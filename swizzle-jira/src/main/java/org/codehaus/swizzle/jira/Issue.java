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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class Issue extends MapObject implements Comparable {

    private final MapObject fields;

    public Issue() {
        this(new HashMap() {{
            put("fields", new HashMap<String, Object>());
        }});
    }

    public Issue(Map data) {
        super(data);

        final Object fields = data.get("fields");
        if (fields == null) throw new IllegalStateException("No 'fields' entry found");
        if (fields.getClass().isAssignableFrom(Map.class)) throw new IllegalStateException("No 'fields' entry should be a map: found " + fields.getClass());
        this.fields = new MapObject(Map.class.cast(fields), false);

        xmlrpcRefs.put(IssueType.class, "id");
        xmlrpcRefs.put(Status.class, "id");
        xmlrpcRefs.put(User.class, "name");
        xmlrpcRefs.put(Project.class, "key");
        xmlrpcRefs.put(Priority.class, "id");
        xmlrpcRefs.put(Resolution.class, "id");

        xmlrpcNoSend.add("customFieldValues");
        xmlrpcNoSend.add("link");
        xmlrpcNoSend.add("voters");
        xmlrpcNoSend.add("subtasks");
        xmlrpcNoSend.add("parentTask");
        xmlrpcNoSend.add("attachments");
        xmlrpcNoSend.add("comments");
    }


    /**
     *
     */
    public Project getProject() {
        return (Project) fields.getMapObject("project", Project.class);
    }

    public void setProject(Project project) {
        fields.setMapObject("project", project);
    }

    public IssueType getType() {
        return (IssueType) fields.getMapObject("issuetype", IssueType.class);
    }

    public void setType(IssueType type) {
        fields.setMapObject("issuetype", type);
    }

    /**
     * example: 2005-10-11 06:10:39.115
     */
    public Date getCreated() {
        return fields.getDate("created");
    }

    public void setCreated(Date created) {
        fields.setDate("created", created);
    }

    /**
     *
     */
    public String getSummary() {
        return fields.getString("summary");
    }

    public void setSummary(String summary) {
        fields.setString("summary", summary);
    }

    /**
     * This data is not available via interface except scraping the html from the web interface. If you know of another way to get it, please let us know.
     *
     * @return List<User>
     */
    public List<User> getVoters() {
        if (!hasField("voters")) {
            List votes = new MapObjectList();
            for (int i = getInt("votes"); i > 0; i--) {
                votes.add(new User());
            }
            fields.setMapObjects("voters", votes);
        }
        return fields.getMapObjects("voters", User.class);
    }

    public void setVoters(List users) {
        fields.setMapObjects("voters", users);
    }

    /**
     *
     */
    public int getVotes() {
        return getVoters().size();
    }

    /**
     * List of something
     */
    public List<CustomFieldValue> getCustomFieldValues() {
        return fields.getMapObjects("customFieldValues", CustomFieldValue.class);
    }

    public void setCustomFieldValues(List customFieldValues) {
        fields.setMapObjects("customFieldValues", customFieldValues);
    }

    /**
     * List of Comments
     */
    public List<Comment> getComments() {
        return fields.getMapObjects("comments", Comment.class);
    }

    /**
     * List of Components
     */
    public List<Component> getComponents() {
        return fields.getMapObjects("components", Component.class);
    }

    public void setComponents(List components) {
        fields.setMapObjects("components", components);
    }

    // I think this is a type as it's plural but only adding one component.
    // I'll leave it for backward compat.
    @Deprecated
    public void addComponents(Component component) {
        getComponents().add(component);
    }

    public void addComponent(Component component) {
        getComponents().add(component);
    }

    public void removeComponents(Component component) {
        getComponents().remove(component);
    }

    /**
     * List of Labels
     */
    public List<String> getLabels() {
        return fields.getList("labels");
    }

    public void setLabels(List labels) {
        fields.setList("labels", labels);
    }

    public void addLabel(String label) {
        final List labels = (List) fields.fields.computeIfAbsent("labels", o -> new ArrayList<>());
        labels.add(label);
    }

    public void removeLabel(String label) {
        final List labels = (List) fields.fields.computeIfAbsent("labels", o -> new ArrayList<>());
        labels.remove(label);
    }

    /**
     * List of Versions
     */
    public List<Version> getAffectsVersions() {
        return fields.getMapObjects("affectsVersions", Version.class);
    }

    public void setAffectsVersions(List affectsVersions) {
        fields.setMapObjects("affectsVersions", affectsVersions);
    }

    public void addAffectsVersion(Version version) {
        getAffectsVersions().add(version);
    }

    public void removeAffectsVersion(Version version) {
        getAffectsVersions().remove(version);
    }

    /**
     * 28093
     */
    public int getId() {
        return getInt("id");
    }

    public void setId(int id) {
        setInt("id", id);
    }

    /**
     * 6
     */
    public Status getStatus() {
        return (Status) fields.getMapObject("status", Status.class);
    }

    public void setStatus(Status status) {
        fields.setMapObject("status", status);
    }

    public Resolution getResolution() {
        return (Resolution) fields.getMapObject("resolution", Resolution.class);
    }

    public void setResolution(Resolution resolution) {
        fields.setMapObject("resolution", resolution);
    }

    /**
     * List
     */
    public List<Version> getFixVersions() {
        return fields.getMapObjects("fixVersions", Version.class);
    }

    public void setFixVersions(List fixVersions) {
        fields.setMapObjects("fixVersions", fixVersions);
    }

    public void addFixVersion(Version version) {
        getFixVersions().add(version);
    }

    public void removeFixVersion(Version version) {
        getFixVersions().remove(version);
    }

    public List<Issue> getSubTasks() {
        return fields.getMapObjects("subtasks", Issue.class);
    }

    public void setSubTasks(List subTasks) {
        fields.setMapObjects("subtasks", subTasks);
    }

    public void addSubTask(Issue issue) {
        getSubTasks().add(issue);
    }

    public void removeSubTask(Issue issue) {
        getSubTasks().remove(issue);
    }

    @Deprecated
    protected Issue getParentTask() {
        return getParent();
    }

    @Deprecated
    protected void setParentTask(Issue parentTask) {
        setParent(parentTask);
    }

    public Issue getParent() {
        return (Issue) fields.getMapObject("parent", Issue.class);
    }

    public void setParent(Issue parentTask) {
        fields.setMapObject("parent", parentTask);
        if (parentTask != null) {
            setType(IssueType.SUBTASK);
        }
    }

    /**
     *
     */
    public String getDescription() {
        return fields.getString("description");
    }

    public void setDescription(String description) {
        fields.setString("description", description);
    }

    public User getReporter() {
        return (User) fields.getMapObject("reporter", User.class);
    }

    public void setReporter(User reporter) {
        fields.setMapObject("reporter", reporter);
    }

    /**
     *
     */
    public Date getUpdated() {
        return fields.getDate("updated");
    }

    public void setUpdated(Date updated) {
        fields.setDate("updated", updated);
    }

    /**
     *
     */
    public Date getDuedate() {
        return fields.getDate("duedate");
    }

    public void setDuedate(Date duedate) {
        fields.setDate("duedate", duedate);
    }

    public User getAssignee() {
        return (User) fields.getMapObject("assignee", User.class);
    }

    public void setAssignee(User assignee) {
        fields.setMapObject("assignee", assignee);
    }

    /**
     *
     */
    public String getEnvironment() {
        return fields.getString("environment");
    }

    public void setEnvironment(String environment) {
        fields.setString("environment", environment);
    }

    public Priority getPriority() {
        return (Priority) fields.getMapObject("priority", Priority.class);
    }

    public void setPriority(Priority priority) {
        fields.setMapObject("priority", priority);
    }

    /**
     *
     */
    public String getKey() {
        return getString("key");
    }

    public void setKey(String key) {
        setString("key", key);
    }

    /**
     * Only available via the RSS source Not available via XML-RPC source
     */
    public String getLink() {
        return fields.getString("link");
    }

    public void setLink(String link) {
        fields.setString("link", link);
    }

    public List<Attachment> getAttachments() {
        return fields.getMapObjects("attachments", Attachment.class);
    }

    public void setAttachments(List attachments) {
        fields.setMapObjects("attachments", attachments);
    }

    public Map toMap2() {
        return super.toMap2();
    }

    public Map toMap() {
        // It's unlikely that you can even update the votes via xml-rpc
        // till we know for sure, best to make sure the tally is current
        fields.setInt("votes", getVoters().size());
        return super.toMap();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Issue issue = (Issue) o;

        if (getId() != issue.getId()) return false;
        if (getKey() != null ? !getKey().equals(issue.getKey()) : issue.getKey() != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = getId();
        result = 29 * result + (getKey() != null ? getKey().hashCode() : 0);
        return result;
    }

    public int compareTo(Object object) {
        if (object instanceof Issue) {
            Issue that = (Issue) object;
            int a = this.getId();
            int b = that.getId();
            if (a > b) {
                return 1;
            } else if (a < b) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public static Issue fromKey(final String key) {
        final Issue issue = new Issue();
        issue.setKey(key);
        return issue;
    }
}
