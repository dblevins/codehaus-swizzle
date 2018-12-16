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

import java.util.List;

public interface Jira {
    List getComments(String issueKey);

    List getComments(Issue issue);

    boolean addComment(String issueKey, String comment) throws Exception;

    Issue createIssue(Issue issue) throws Exception;

    Issue updateIssue(String issueKey, Issue issue) throws Exception;

    Issue getIssue(String issueKey);

    List<Issue> getIssuesFromFilter(Filter filter) throws Exception;

    List<Issue> getIssuesFromFilter(String filterName) throws Exception;

    List<Issue> getIssuesFromFilter(int filterId) throws Exception;

    List<Issue> getIssuesFromTextSearch(String searchTerms) throws Exception;

    List<Issue> getIssuesFromTextSearchWithProject(List projectKeys, String searchTerms, int maxNumResults) throws Exception;

    List<IssueType> getIssueTypes();

    IssueType getIssueType(String name);

    IssueType getIssueType(int id);

    // New for JIRA 4.0
    List getIssueTypesForProject(int projectId);

    List getIssueTypesForProject(String projectKey);

    List<Priority> getPriorities();

    Priority getPriority(String name);

    Priority getPriority(int id);

    List<Project> getProjects();

    Project getProject(String key);

    Project getProject(int id);

    List<Resolution> getResolutions();

    Resolution getResolution(String name);

    Resolution getResolution(int id);

    List<Status> getStatuses();

    Status getStatus(String name);

    Status getStatus(int id);

    List<Filter> getSavedFilters();

    Filter getSavedFilter(String name);

    Filter getSavedFilter(int id);

    ServerInfo getServerInfo();

    List<IssueType> getSubTaskIssueTypes();

    IssueType getSubTaskIssueType(String name);

    IssueType getSubTaskIssueType(int id);

    User getUser(String username);

    List<Component> getComponents(String projectKey);

    List<Component> getComponents(Project project);

    Component getComponent(String projectKey, String name);

    Component getComponent(Project project, String name);

    Component getComponent(String projectKey, int id);

    Component getComponent(Project project, int id);

    List<Version> getVersions(String projectKey);

    List<Version> getVersions(Project project);

    Version getVersion(String projectKey, String name);

    Version getVersion(Project project, String name);

    Version getVersion(String projectKey, int id);

    Version getVersion(Project project, int id);

    List getFavoriteFilters();

    Issue fill(Issue issue);
}
