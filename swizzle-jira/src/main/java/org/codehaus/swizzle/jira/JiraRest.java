/*
 * Copyright 2011 David Blevins
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

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.json.JsonValue.ValueType.ARRAY;
import static javax.json.JsonValue.ValueType.FALSE;
import static javax.json.JsonValue.ValueType.NULL;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.OBJECT;
import static javax.json.JsonValue.ValueType.STRING;
import static javax.json.JsonValue.ValueType.TRUE;

public class JiraRest implements Jira {

    private final URI base;
    private final AsyncHttpClient client;

    public JiraRest(final String base) {
        this.base = URI.create(base);
        this.client = new DefaultAsyncHttpClient();
    }

    public List getComments(final String issueKey) {
        return null;
    }

    public List getComments(final Issue issue) {
        return null;
    }

    public boolean addComment(final String issueKey, final String comment) throws Exception {
        return false;
    }

    public Issue createIssue(final Issue issue) throws Exception {
        try {

            final Map map = issue.toMap();

            final URI issueUri = base.resolve("issue");

            final Response response = client.preparePost(issueUri.toASCIIString())
                    .execute().get();
            final JsonProvider provider = JsonProvider.provider();
            final JsonReader reader = provider.createReader(response.getResponseBodyAsStream());
            final JsonObject jsonObject = reader.readObject();

            return new Issue(toMap(jsonObject));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Issue updateIssue(final String issueKey, final Issue issue) throws Exception {
        return null;
    }

    public Issue getIssue(final String issueKey) {
        try {
            final URI issueUri = base.resolve("issue/" + issueKey);

            final Response response = client.prepareGet(issueUri.toASCIIString()).execute().get();
            final JsonProvider provider = JsonProvider.provider();
            final JsonReader reader = provider.createReader(response.getResponseBodyAsStream());
            final JsonObject jsonObject = reader.readObject();

            return new Issue(toMap(jsonObject));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Map<String, Object> toMap(final JsonObject jsonObject) {
        final Map<String, Object> map = new HashMap<>();

        for (final Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            final String key = entry.getKey();
            final JsonValue value = entry.getValue();

            map.put(key, toJava(value));
        }

        return map;
    }

    private static Object toJava(final JsonValue value) {
        final JsonValue.ValueType valueType = value.getValueType();
        if (STRING.equals(valueType)) return asString(value);
        else if (OBJECT.equals(valueType)) return toMap(value.asJsonObject());
        else if (FALSE.equals(valueType)) return false;
        else if (TRUE.equals(valueType)) return true;
        else if (NUMBER.equals(valueType)) return value.toString();
        else if (NULL.equals(valueType)) return null;
        else if (ARRAY.equals(valueType)) {
            final List<Object> list = new ArrayList<Object>();
            for (final JsonValue jsonValue : value.asJsonArray()) {
                list.add(toJava(jsonValue));
            }
            return list;
        }
        throw new IllegalStateException("Unknown JsonValue type: " + valueType);
    }

    private static String asString(final JsonValue value) {
        final String s = value.toString();
        return s.substring(1, s.length() - 1);
    }

    public static JsonObject toJsonObject(final MapObject mapObject) {
        final Map<String, Object> map = mapObject.toMap2();
        return toJsonObject(map);
    }

    private static JsonObject toJsonObject(final Map<String, Object> map) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            builder.add(entry.getKey(), toJsonObject(entry.getValue()));
        }
        return builder.build();
    }

    private static JsonValue toJsonObject(final Object value) {
        if (value == null) return JsonValue.NULL;
        if (value instanceof String) return Json.createValue((String) value);
        if (value instanceof Boolean) return ((Boolean) value) ? JsonValue.TRUE : JsonValue.FALSE;
        if (value instanceof Byte) return Json.createValue(((Byte) value).byteValue());
        if (value instanceof Short) return Json.createValue(((Short) value).shortValue());
        if (value instanceof Integer) return Json.createValue(((Integer) value).intValue());
        if (value instanceof Long) return Json.createValue(((Long) value).longValue());
        if (value instanceof Float) return Json.createValue(((Float) value).floatValue());
        if (value instanceof Double) return Json.createValue(((Double) value).doubleValue());
        if (value instanceof BigDecimal) return Json.createValue(((BigDecimal) value));
        if (value instanceof BigInteger) return Json.createValue(((BigInteger) value));
        if (value instanceof Map) return toJsonObject((Map) value);
        if (value instanceof List) {
            final JsonArrayBuilder array = Json.createArrayBuilder();
            for (final Object o : (List) value) {
                array.add(toJsonObject(o));
            }
            return array.build();
        }
        throw new IllegalStateException("Unsupported type: " + value);
    }


    public List<Issue> getIssuesFromFilter(final Filter filter) throws Exception {
        return null;
    }

    public List<Issue> getIssuesFromFilter(final String filterName) throws Exception {
        return null;
    }

    public List<Issue> getIssuesFromFilter(final int filterId) throws Exception {
        return null;
    }

    public List<Issue> getIssuesFromTextSearch(final String searchTerms) throws Exception {
        return null;
    }

    public List<Issue> getIssuesFromTextSearchWithProject(final List projectKeys, final String searchTerms, final int maxNumResults) throws Exception {
        return null;
    }

    public List<IssueType> getIssueTypes() {
        return null;
    }

    public IssueType getIssueType(final String name) {
        return null;
    }

    public IssueType getIssueType(final int id) {
        return null;
    }

    public List getIssueTypesForProject(final int projectId) {
        return null;
    }

    public List getIssueTypesForProject(final String projectKey) {
        return null;
    }

    public List<Priority> getPriorities() {
        return null;
    }

    public Priority getPriority(final String name) {
        return null;
    }

    public Priority getPriority(final int id) {
        return null;
    }

    public List<Project> getProjects() {
        return null;
    }

    public Project getProject(final String key) {
        return null;
    }

    public Project getProject(final int id) {
        return null;
    }

    public List<Resolution> getResolutions() {
        return null;
    }

    public Resolution getResolution(final String name) {
        return null;
    }

    public Resolution getResolution(final int id) {
        return null;
    }

    public List<Status> getStatuses() {
        return null;
    }

    public Status getStatus(final String name) {
        return null;
    }

    public Status getStatus(final int id) {
        return null;
    }

    public List<Filter> getSavedFilters() {
        return null;
    }

    public Filter getSavedFilter(final String name) {
        return null;
    }

    public Filter getSavedFilter(final int id) {
        return null;
    }

    public ServerInfo getServerInfo() {
        return null;
    }

    public List<IssueType> getSubTaskIssueTypes() {
        return null;
    }

    public IssueType getSubTaskIssueType(final String name) {
        return null;
    }

    public IssueType getSubTaskIssueType(final int id) {
        return null;
    }

    public User getUser(final String username) {
        return null;
    }

    public List<Component> getComponents(final String projectKey) {
        return null;
    }

    public List<Component> getComponents(final Project project) {
        return null;
    }

    public Component getComponent(final String projectKey, final String name) {
        return null;
    }

    public Component getComponent(final Project project, final String name) {
        return null;
    }

    public Component getComponent(final String projectKey, final int id) {
        return null;
    }

    public Component getComponent(final Project project, final int id) {
        return null;
    }

    public List<Version> getVersions(final String projectKey) {
        return null;
    }

    public List<Version> getVersions(final Project project) {
        return null;
    }

    public Version getVersion(final String projectKey, final String name) {
        return null;
    }

    public Version getVersion(final Project project, final String name) {
        return null;
    }

    public Version getVersion(final String projectKey, final int id) {
        return null;
    }

    public Version getVersion(final Project project, final int id) {
        return null;
    }

    public List getFavoriteFilters() {
        return null;
    }

    public Issue fill(final Issue issue) {
        return null;
    }
}
