/**
 *
 * Copyright 2006 David Blevins
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codehaus.swizzle.jira;

import org.apache.xmlrpc.client.XmlRpcAhcTransportFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class JiraXmlRpc implements Jira {
    private static Map cacheMetadata = new HashMap();

    static {
        cacheMetadata.put(IssueType.class, new String[] { "id", "name" });
        cacheMetadata.put(Component.class, new String[] { "id", "name" });
        cacheMetadata.put(Priority.class, new String[] { "id", "name" });
        cacheMetadata.put(Resolution.class, new String[] { "id", "name" });
        cacheMetadata.put(Version.class, new String[] { "id", "name" });
        cacheMetadata.put(Status.class, new String[] { "id", "name" });
        cacheMetadata.put(Filter.class, new String[] { "id", "name" });
        cacheMetadata.put(Issue.class, new String[] { "id", "key" });
        cacheMetadata.put(Project.class, new String[] { "id", "key" });
    }

    private final XmlRpcClient client;
    private String token;
    private boolean autofill = true;
    private final Map<String, IssueFiller> issueFillers = new LinkedHashMap<String, IssueFiller>();
    private final Map<String, String> autofillProviders = new HashMap<String, String>();

    public JiraXmlRpc(String endpoint) throws MalformedURLException {
        
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        if (!endpoint.endsWith("/rpc/xmlrpc")) {
            endpoint += "/rpc/xmlrpc";
        }

        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL(endpoint));
        client = new XmlRpcClient();
        client.setTransportFactory( new XmlRpcAhcTransportFactory(client) );
        client.setConfig(clientConfig);

        BasicIssueFiller basicIssueFiller = new BasicIssueFiller(this);
        basicIssueFiller.setEnabled(true);
        issueFillers.put("issue", basicIssueFiller);
        issueFillers.put("project", new ProjectFiller(this));
        autofillProviders.put("issue", BasicIssueFiller.class.getName());
        autofillProviders.put("project", ProjectFiller.class.getName());
        autofillProviders.put("voters", "org.codehaus.swizzle.jira.VotersFiller");
        autofillProviders.put("subtasks", "org.codehaus.swizzle.jira.SubTasksFiller");
        autofillProviders.put("attachments", "org.codehaus.swizzle.jira.AttachmentsFiller");
        autofillProviders.put("comments", "org.codehaus.swizzle.jira.CommentsFiller");
    }

    /**
     * Valid schemes are "issue", "project", "voters", and "attachments" "issues" is enabled by default
     *
     * @param scheme
     * @param enabled
     */
    public void autofill(String scheme, boolean enabled) {
        if (!autofillProviders.containsKey(scheme)) {
            throw new UnsupportedOperationException("Autofill Scheme not supported: " + scheme);
        }

        IssueFiller filler = issueFillers.get(scheme);
        if (filler == null) {
            try {
                ClassLoader classLoader = this.getClass().getClassLoader();
                Class clazz = classLoader.loadClass((String) autofillProviders.get(scheme));
                Constructor constructor = clazz.getConstructor(JiraXmlRpc.class);
                filler = (IssueFiller) constructor.newInstance(this);
                issueFillers.put(scheme, filler);
            } catch (Exception e) {
                System.err.println("Cannot install autofill provider " + scheme);
                e.printStackTrace();
            }
        }
        filler.setEnabled(enabled);
    }

    /**
     * Logs the user into JIRA
     */
    public void login(String username, String password) throws Exception {
        token = (String) call("login", username, password);
    }

    /**
     * remove this token from the list of logged in tokens.
     */
    public boolean logout() throws Exception {
        Boolean value = (Boolean) call("logout");
        return value.booleanValue();
    }

    /**
     * List<{@link Comment}>: Returns all comments associated with the issue
     */
    public List getComments(String issueKey) {
        return cachedList(new Call("getComments", issueKey), Comment.class);
    }

    public List getComments(Issue issue) {
        return getComments(issue.getKey());
    }

    /**
     * Adds a comment to an issue TODO: If someone adds a comment to an issue, we should account for that in our caching
     */
    public boolean addComment(String issueKey, String comment) throws Exception {
        Boolean value = (Boolean) call("addComment", issueKey, comment);
        return value.booleanValue();
    }

    /**
     * Creates an issue in JIRA TODO: If someone creates an issue, we should account for that in our caching
     */
    public Issue createIssue(Issue issue) throws Exception {
        Map data = (Map) call("createIssue", issue.toMap());
        return (autofill) ? fill(new Issue(data)) : new Issue(data);
    }

    /**
     * Updates an issue in JIRA from a HashMap object TODO: If someone updates an issue, we should account for that in our caching
     */
    public Issue updateIssue(String issueKey, Issue issue) throws Exception {
        Map data = (Map) call("updateIssue", issueKey, issue.toMap());
        return (autofill) ? fill(new Issue(data)) : new Issue(data);
    }

    public Issue getIssue(String issueKey) {
        return (Issue) cachedObject(new Call("getIssue", issueKey), Issue.class);
    }

    public List<Issue> getIssuesFromFilter(Filter filter) throws Exception {
        return getIssuesFromFilter(filter.getId());
    }

    public List<Issue> getIssuesFromFilter(String filterName) throws Exception {
        Filter filter = getSavedFilter(filterName);
        if (filter == null) {
            return toList(new Object[] {}, Issue.class);
        } else {
            return getIssuesFromFilter(filter.getId());
        }
    }

    public List<Issue> getIssuesFromFilter(int filterId) throws Exception {
        Object[] vector = (Object[]) call("getIssuesFromFilter", filterId + "");
        return toList(vector, Issue.class);
    }

    public List<Issue> getIssuesFromTextSearch(String searchTerms) throws Exception {
        Object[] vector = (Object[]) call("getIssuesFromTextSearch", searchTerms);
        return toList(vector, Issue.class);
    }

    public List<Issue> getIssuesFromTextSearchWithProject(List projectKeys, String searchTerms, int maxNumResults) throws Exception {
        Object[] vector = (Object[]) call("getIssuesFromTextSearchWithProject", projectKeys.toArray(), searchTerms, new Integer(maxNumResults));
        return toList(vector, Issue.class);
    }

    public List<IssueType> getIssueTypes() {
        return cachedList(new Call("getIssueTypes"), IssueType.class);
    }

    public IssueType getIssueType(String name) {
        Map objects = cachedMap(new Call("getIssueTypes"), IssueType.class, "name");
        return (IssueType) objects.get(name);
    }

    public IssueType getIssueType(int id) {
        Map objects = cachedMap(new Call("getIssueTypes"), IssueType.class, "id");
        return (IssueType) objects.get(id + "");
    }

    // New for JIRA 4.0
    public List getIssueTypesForProject(int projectId) {
        return cachedList(new Call("getIssueTypesForProject", projectId+""), IssueType.class);    	
    }  

    public List getIssueTypesForProject(String projectKey) {
        return cachedList(new Call("getIssueTypesForProject", getProject( projectKey ).getId()+""), IssueType.class);    	
    }      
    
    /**
     * List<{@link Priority}>: Returns all priorities in the system
     */
    public List<Priority> getPriorities() {
        return cachedList(new Call("getPriorities"), Priority.class);
    }

    public Priority getPriority(String name) {
        Map objects = cachedMap(new Call("getPriorities"), Priority.class, "name");
        return (Priority) objects.get(name);
    }

    public Priority getPriority(int id) {
        Map objects = cachedMap(new Call("getPriorities"), Priority.class, "id");
        return (Priority) objects.get(id + "");
    }

    /**
     * List<{@link Project}>: Returns a list of projects available to the user
     */
    public List<Project> getProjects() {
        String versionPrefix = getVersionPrefix( 4 );
        if (versionPrefix.compareTo("3.13") < 0)
            // In Jira < 3.13
            return cachedList(new Call("getProjects"), Project.class);
        else
            // Otherwise
            return cachedList(new Call("getProjectsNoSchemes"), Project.class);
    }

    public Project getProject(String key) {
        Map objects;
        String versionPrefix = getVersionPrefix( 4 );
        if (versionPrefix.compareTo("3.13") < 0)
            // In Jira < 3.13
            objects = cachedMap(new Call("getProjects"), Project.class, "key");
        else
            // Otherwise
            objects = cachedMap(new Call("getProjectsNoSchemes"), Project.class, "key");
        return (Project) objects.get(key);
    }

    public Project getProject(int id) {
        Map objects;
        String versionPrefix = getVersionPrefix( 4 );
        if (versionPrefix.compareTo("3.13") < 0)
            // In Jira < 3.13
            objects = cachedMap(new Call("getProjects"), Project.class, "id");
        else
            // Otherwise
            objects = cachedMap(new Call("getProjectsNoSchemes"), Project.class, "id");
        return (Project) objects.get(id + "");
    }

    /**
     * List<{@link Resolution}>: Returns all resolutions in the system
     */
    public List<Resolution> getResolutions() {
        return cachedList(new Call("getResolutions"), Resolution.class);
    }

    public Resolution getResolution(String name) {
        Map objects = cachedMap(new Call("getResolutions"), Resolution.class, "name");
        return (Resolution) objects.get(name);
    }

    public Resolution getResolution(int id) {
        Map objects = cachedMap(new Call("getResolutions"), Resolution.class, "id");
        return (Resolution) objects.get(id + "");
    }

    /**
     * List<{@link Status}>: Returns all statuses in the system
     */
    public List<Status> getStatuses() {
        return cachedList(new Call("getStatuses"), Status.class);
    }

    public Status getStatus(String name) {
        Map objects = cachedMap(new Call("getStatuses"), Status.class, "name");
        return (Status) objects.get(name);
    }

    public Status getStatus(int id) {
        Map objects = cachedMap(new Call("getStatuses"), Status.class, "id");
        return (Status) objects.get(id + "");
    }

    /**
     * List<{@link Filter}>: Gets all saved filters available for the currently logged in user
     */
    public List<Filter> getSavedFilters() {
        return cachedList(new Call("getSavedFilters"), Filter.class);
    }

    public Filter getSavedFilter(String name) {
        Map objects = cachedMap(new Call("getSavedFilters"), Filter.class, "name");
        return (Filter) objects.get(name);
    }

    public Filter getSavedFilter(int id) {
        Map objects = cachedMap(new Call("getSavedFilters"), Filter.class, "id");
        return (Filter) objects.get(id + "");
    }

    /**
     * Returns the Server information such as baseUrl, version, edition, buildDate, buildNumber.
     */
    public ServerInfo getServerInfo() {
        return (ServerInfo) cachedObject(new Call("getServerInfo"), ServerInfo.class);
    }

    /**
     * List<{@link IssueType}>: Returns all visible subtask issue types in the system
     *
     * @return list of {@link IssueType}
     */
    public List<IssueType> getSubTaskIssueTypes() {
        return cachedList(new Call("getSubTaskIssueTypes"), IssueType.class);
    }

    public IssueType getSubTaskIssueType(String name) {
        Map objects = cachedMap(new Call("getSubTaskIssueTypes"), IssueType.class, "name");
        return (IssueType) objects.get(name);
    }

    public IssueType getSubTaskIssueType(int id) {
        Map objects = cachedMap(new Call("getSubTaskIssueTypes"), IssueType.class, "id");
        return (IssueType) objects.get(id + "");
    }

    /**
     * Returns a user's information given a username
     */
    public User getUser(String username) {
        return (User) cachedObject(new Call("getUser", username), User.class);
    }

    // ---- PROJECT related data ----
    // /////////////////////////////////////////////////////

    /**
     * List<{@link Component}>: Returns all components available in the specified project
     */
    public List<Component> getComponents(String projectKey) {
        return cachedList(new Call("getComponents", projectKey), Component.class);
    }

    public List<Component> getComponents(Project project) {
        return getComments(project.getKey());
    }

    public Component getComponent(String projectKey, String name) {
        Map components = cachedMap(new Call("getComponents", projectKey), Component.class, "name");
        return (Component) components.get(name);
    }

    public Component getComponent(Project project, String name) {
        return getComponent(project.getKey(), name);
    }

    public Component getComponent(String projectKey, int id) {
        Map components = cachedMap(new Call("getComponents", projectKey), Component.class, "id");
        return (Component) components.get(id + "");
    }

    public Component getComponent(Project project, int id) {
        return getComponent(project.getKey(), id);
    }

    /**
     * List<{@link Version}>: Returns all versions available in the specified project
     */
    public List<Version> getVersions(String projectKey) {
        return cachedList(new Call("getVersions", projectKey), Version.class);
    }

    public List<Version> getVersions(Project project) {
        return getVersions(project.getKey());
    }

    public Version getVersion(String projectKey, String name) {
        Map versions = cachedMap(new Call("getVersions", projectKey), Version.class, "name");
        return (Version) versions.get(name);
    }

    public Version getVersion(Project project, String name) {
        return getVersion(project.getKey(), name);
    }

    public Version getVersion(String projectKey, int id) {
        Map versions = cachedMap(new Call("getVersions", projectKey), Version.class, "id");
        return (Version) versions.get(id + "");
    }

    public Version getVersion(Project project, int id) {
        return getVersion(project.getKey(), id);
    }

    public List getFavoriteFilters() {
        return cachedList(new Call("getFavouriteFilters"), Filter.class);
    }
    
    // ---- PROJECT related data ----
    // /////////////////////////////////////////////////////

    private <T> List<T> toList(Object[] vector, Class<T> type) {
        List<T> list = new MapObjectList(vector.length);

        try {
            Constructor constructor = type.getConstructor(Map.class);
            for (int i = 0; i < vector.length; i++) {
                Map data = (Map) vector[i];
                T object = (T) constructor.newInstance(data);
                fill(type, object);
                list.add(object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void fill(Class type, Object object) {
        if (autofill && type == Issue.class) {
            fill((Issue) object);
        }
        if (type == Filter.class) {
            Filter filter = (Filter) object;
            User dest = filter.getAuthor();
            User source = this.getUser(dest.getName());
            dest.merge(source);
        }
    }

    private <T> T toObject(Map data, Class<T> type) {
        try {
            Constructor constructor = type.getConstructor(Map.class);
            Object object = constructor.newInstance(data);
            fill(type, object);
            return (T) object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object call(String command, Object... args) throws Exception {
        Object[] vector;
        if (token != null) {
            vector = new Object[args.length + 1];
            vector[0] = token;
            System.arraycopy(args, 0, vector, 1, args.length);
        } else {
            vector = args;
        }
                
        return client.execute("jira1." + command, vector);
    }

    public Issue fill(Issue issue) {
        for (IssueFiller issueFiller : issueFillers.values()) {
            issueFiller.fill(issue);
        }
        return issue;
    }

    public static class Call {
        public final String command;
        public final Object[] args;

        public Call(String command, Object... args) {
            this.command = command;
            this.args = args;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Call call = (Call) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            if (!Arrays.equals(args, call.args)) return false;
            if (!command.equals(call.command)) return false;

            return true;
        }

        public int hashCode() {
            return command.hashCode();
        }
    }

    private Map<Call, Object> callcache = new HashMap<Call, Object>();

    private List cachedList(Call call, Class type) {
        Object result = cache(call, type);

        Map indexes = (Map) result;
        return (List) indexes.get(List.class);
    }

    private Map cachedMap(Call call, Class type, String field) {
        Object result = cache(call, type);

        Map indexes = (Map) result;
        return (Map) indexes.get(field);
    }

    private Object cachedObject(Call call, Class type) {
        return cache(call, type);
    }

    private <T> T cache(Call call, Class<T> type) {
        Object object = callcache.get(call);
        if (object != null) {
            return (T) object;
        }

        Object result = exec(call);
        if (result instanceof Object[]) {
            List list = toList((Object[]) result, type);
            Map indexes = new HashMap();
            String[] uniqueFields = (String[]) cacheMetadata.get(type);
            for (int i = 0; uniqueFields != null && i < uniqueFields.length; i++) {
                Map<String, MapObject> index = new HashMap<String, MapObject>();
                String field = uniqueFields[i];
                for (int j = 0; j < list.size(); j++) {
                    MapObject mapObject = (MapObject) list.get(j);
                    index.put(mapObject.getString(field), mapObject);
                }
                indexes.put(field, index);
            }
            indexes.put(List.class, new MapObjectList(list));
            // indexes.put(List.class, Collections.unmodifiableList(list));
            result = indexes;
        } else if (result instanceof Map) {
            result = toObject((Map) result, type);
        }

        callcache.put(call, result);
        return (T) result;
    }

    private Object exec(Call call) {
        try {
            Object[] vector;
            if (token != null) {
                vector = new Object[call.args.length + 1];
                vector[0] = token;
                System.arraycopy(call.args, 0, vector, 1, call.args.length);
            } else {
                vector = call.args;
            }
            return client.execute("jira1." + call.command, vector);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getVersionPrefix( int maxLength )
    {
        String fullVersion = getServerInfo().getVersion();
        if ( fullVersion.length() > maxLength )
        {
            return fullVersion.substring( 0, maxLength );
        }
        else
        {
            return fullVersion;
        }
    }    
}
