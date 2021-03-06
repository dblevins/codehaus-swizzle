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

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class JiraTest extends SwizzleJiraTestCase {

    // Date is retrieved without timezone from xmlrpc (@codehaus?)
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");

    public void testJira() throws Exception {
        Jira jira = getJira();

        Issue issue = jira.getIssue("SWIZZLE-1");
        assertEquals("Issue.getCreated()", "Fri Aug 04 20:05:13 2006", formatter.format(issue.getCreated()));
        assertEquals("Issue.getSummary()", "Unit Test Summary", issue.getSummary());
        assertEquals("Issue.getType()", 2, issue.getType().getId());
        assertEquals("Issue.getEnvironment()", "Unit Test Environment", issue.getEnvironment());
        assertEquals("Issue.getStatus()", 6, issue.getStatus().getId());
        assertEquals("Issue.getUpdated()", "Fri Aug 04 21:33:48 2006", formatter.format(issue.getUpdated()));
        assertEquals("Issue.getId()", 40099, issue.getId());
        assertEquals("Issue.getKey()", "SWIZZLE-1", issue.getKey());
        assertEquals("Issue.getDescription()", "Unit Test Description", issue.getDescription());
        assertEquals("Issue.getDuedate()", "Sun Aug 06 00:00:00 2006", formatter.format(issue.getDuedate()));
        assertEquals("Issue.getReporter()", "dblevins", issue.getReporter().getName());
        assertEquals("Issue.getProject()", "SWIZZLE", issue.getProject().getKey());
        assertEquals("Issue.getResolution()", 1, issue.getResolution().getId());
        assertEquals("Issue.getVotes()", 1, issue.getVotes());
        assertEquals("Issue.getAssignee()", "dblevins", issue.getAssignee().getName());
        assertEquals("Issue.getPriority()", 1, issue.getPriority().getId());
        assertEquals("Issue.getLink() - " + issue.getLink(), "https://jira.codehaus.org/browse/SWIZZLE-1", issue.getLink());

        assertEquals("Issue.getFixVersions().size()", 1, issue.getFixVersions().size());
        assertTrue("FixVersion instance of Version", issue.getFixVersions().get(0) instanceof Version);
        Version version = (Version) issue.getFixVersions().get(0);
        assertEquals("Version.getName()", "Test Version", version.getName());
        assertEquals("Version.getReleased()", false, version.getReleased());
        assertEquals("Version.getArchived()", false, version.getArchived());
        assertEquals("Version.getReleaseDate()", "Sun Aug 06 00:00:00 2006", formatter.format(version.getReleaseDate()));
        assertEquals("Version.getSequence()", 5, version.getSequence());
        assertEquals("Version.getId()", 12831, version.getId());

        assertEquals("Issue.getAffectsVersions().size()", 1, issue.getAffectsVersions().size());
        assertTrue("AffectsVersion instance of Version", issue.getAffectsVersions().get(0) instanceof Version);
        version = (Version) issue.getAffectsVersions().get(0);
        assertEquals("Version.getName()", "Test Version", version.getName());
        assertEquals("Version.getReleased()", false, version.getReleased());
        assertEquals("Version.getArchived()", false, version.getArchived());
        assertEquals("Version.getReleaseDate()", "Sun Aug 06 00:00:00 2006", formatter.format(version.getReleaseDate()));
        assertEquals("Version.getSequence()", 5, version.getSequence());
        assertEquals("Version.getId()", 12831, version.getId());

        assertEquals("Issue.getComponents().size()", 1, issue.getComponents().size());
        assertTrue("Issue.getComponents instance of Component", issue.getComponents().get(0) instanceof Component);
        Component component = (Component) issue.getComponents().get(0);
        assertEquals("Component.getName()", "swizzle-jira", component.getName());
        assertEquals("Component.getId()", 12312, component.getId());

        Map data = issue.toMap();

        assertEquals("issue.Created", "2006-08-04 20:05:13.157", data.get("created"));
        assertEquals("issue.Summary", "Unit Test Summary", data.get("summary"));
        assertEquals("issue.Type", "2", data.get("type"));
        assertEquals("issue.Environment", "Unit Test Environment", data.get("environment"));
        assertEquals("issue.Status", "6", data.get("status"));
        assertEquals("issue.Updated", "2006-08-04 21:33:48.108", data.get("updated"));
        assertEquals("issue.Id", "40099", data.get("id"));
        assertEquals("issue.Key", "SWIZZLE-1", data.get("key"));
        assertEquals("issue.Description", "Unit Test Description", data.get("description"));
        assertEquals("issue.Duedate", "2006-08-06 00:00:00.0", data.get("duedate"));
        assertEquals("issue.Reporter", "dblevins", data.get("reporter"));
        assertEquals("issue.Project", "SWIZZLE", data.get("project"));
        assertEquals("issue.Resolution", "1", data.get("resolution"));
        assertEquals("issue.Votes", "1", data.get("votes"));
        assertEquals("issue.Assignee", "dblevins", data.get("assignee"));
        assertEquals("issue.Priority", "1", data.get("priority"));
        assertEquals("issue.Link", null, data.get("link"));

    }

    public void testFill() throws Exception {
        Jira jira = getJira();

        Issue issue = jira.getIssue("SWIZZLE-1");
        issue = jira.fill(issue);

        assertEquals("Issue.getCreated()", "Fri Aug 04 20:05:13 2006", formatter.format(issue.getCreated()));
        assertEquals("Issue.getSummary()", "Unit Test Summary", issue.getSummary());
        assertEquals("Issue.getType()", "New Feature", issue.getType().getName());
        assertEquals("Issue.getEnvironment()", "Unit Test Environment", issue.getEnvironment());
        assertEquals("Issue.getStatus()", "Closed", issue.getStatus().getName());
        assertEquals("Issue.getUpdated()", "Fri Aug 04 21:33:48 2006", formatter.format(issue.getUpdated()));
        assertEquals("Issue.getId()", 40099, issue.getId());
        assertEquals("Issue.getKey()", "SWIZZLE-1", issue.getKey());
        assertEquals("Issue.getDescription()", "Unit Test Description", issue.getDescription());
        assertEquals("Issue.getDuedate()", "Sun Aug 06 00:00:00 2006", formatter.format(issue.getDuedate()));
        assertEquals("Issue.getReporter()", "David Blevins", issue.getReporter().getFullname());
        assertEquals("Issue.getProject()", "SWIZZLE", issue.getProject().getKey());
        assertEquals("Issue.getResolution()", "Fixed", issue.getResolution().getName());
        assertEquals("Issue.getVotes()", 1, issue.getVotes());
        assertEquals("Issue.getAssignee()", "David Blevins", issue.getAssignee().getFullname());
        assertEquals("Issue.getPriority()", "Blocker", issue.getPriority().getName());
        assertEquals("Issue.getLink()", "https://jira.codehaus.org/browse/SWIZZLE-1", issue.getLink());

        Map data = issue.toMap();

        assertEquals("issue.Created", "2006-08-04 20:05:13.157", data.get("created"));
        assertEquals("issue.Summary", "Unit Test Summary", data.get("summary"));
        assertEquals("issue.Type", "2", data.get("type"));
        assertEquals("issue.Environment", "Unit Test Environment", data.get("environment"));
        assertEquals("issue.Status", "6", data.get("status"));
        assertEquals("issue.Updated", "2006-08-04 21:33:48.108", data.get("updated"));
        assertEquals("issue.Id", "40099", data.get("id"));
        assertEquals("issue.Key", "SWIZZLE-1", data.get("key"));
        assertEquals("issue.Description", "Unit Test Description", data.get("description"));
        assertEquals("issue.Duedate", "2006-08-06 00:00:00.0", data.get("duedate"));
        assertEquals("issue.Reporter", "dblevins", data.get("reporter"));
        assertEquals("issue.Project", "SWIZZLE", data.get("project"));
        assertEquals("issue.Resolution", "1", data.get("resolution"));
        assertEquals("issue.Votes", "1", data.get("votes"));
        assertEquals("issue.Assignee", "dblevins", data.get("assignee"));
        assertEquals("issue.Priority", "1", data.get("priority"));
        assertEquals("issue.Link", null, data.get("link"));

    }

    public void testAutofillProject() throws Exception {
        JiraXmlRpc jira = getJira();
        jira.autofill("project", true);

        Issue issue = jira.getIssue("SWIZZLE-1");
        Project project = issue.getProject();

        assertEquals("Project.getId()", 11230, project.getId());
        assertEquals("Project.getKey()", "SWIZZLE", project.getKey());
        assertEquals("Project.getName()", "Swizzle", project.getName());
        assertEquals("Project.getLead()", "dblevins", project.getLead());
        assertEquals("Project.getProjectUrl() - " + project.getProjectUrl(), "http://swizzle.codehaus.org", project.getProjectUrl());
        assertEquals("Project.getUrl() - " + project.getUrl(), "https://jira.codehaus.org/browse/SWIZZLE", project.getUrl());
    }

}
