package org.codehaus.swizzle.jira;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IssueTest {

    @Test
    public void testSummary() throws Exception {
        final Issue issue = getIssue("TOMEE-2339");

        assertEquals("Describe Workflow for Contribution", issue.getSummary());
        assertEquals("Describe the workflow when contributing as discussed [here|http://tomee-openejb.979440.n4.nabble.com/How-to-find-out-that-someone-else-is-already-working-on-a-task-td4685855.html]", issue.getDescription());
        assertEquals(13203209, issue.getId());
    }

    @Test
    public void testCreated() throws Exception {
        final Issue issue = getIssue("TOMEE-339");

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        format.setTimeZone(utc);
        assertEquals("2012-07-24T19:16:23.633+0000", format.format(issue.getCreated()));
    }

    @Test
    public void testUpdated() throws Exception {
        final Issue issue = getIssue("TOMEE-5");

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        format.setTimeZone(utc);
        assertEquals("2011-09-27T11:31:15.937+0000", format.format(issue.getCreated()));
    }

    @Test
    public void testLabels() throws Exception {
        final Issue issue = getIssue("TOMEE-2339");

        final List<String> labels = issue.getLabels();
        assertEquals(1, labels.size());
        assertEquals("pull-request-available", labels.get(0));
    }

    @Test
    public void testTypeDocumentation() throws Exception {
        final Issue issue = getIssue("TOMEE-2339");

        // Assert Issue Type
        final IssueType type = issue.getType();
        assertEquals("Documentation", type.getName());
        assertEquals(20, type.getId());
        assertEquals("https://issues.apache.org/jira/images/icons/issuetypes/documentation.png", type.getIcon());
    }

    @Test
    public void testTypeBug() throws Exception {
        final Issue issue = getIssue("TOMEE-2000");

        final IssueType type = issue.getType();
        assertEquals("Bug", type.getName());
        assertEquals(1, type.getId());
    }


    @Test
    public void testComponents() throws Exception {
        final Issue issue = getIssue("TOMEE-2339");

        // Assert Component
        final List<Component> components = issue.getComponents();
        assertEquals(1, components.size());
        final Component component = components.get(0);
        assertEquals("Examples and Documentation", component.getName());
        assertEquals(12332465, component.getId());
    }

    @Test
    public void testFixVersion() throws Exception {
        final Issue issue = getIssue("TOMEE-2000");

        final IssueType type = issue.getType();
        assertEquals("Bug", type.getName());
        assertEquals(1, type.getId());

        final List<Version> fixVersions = issue.getFixVersions();
        assertEquals(1, fixVersions.size());

        final Version version = fixVersions.get(0);
        assertEquals("7.0.3", version.getName());
        assertEquals(12338670, version.getId());
        assertEquals(false, version.getArchived());
        assertEquals(true, version.getReleased());
        assertEquals(0, version.getSequence());

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2017-03-13 00:00:00", format.format(version.getReleaseDate()));
    }


    @Test
    public void testResolution() throws Exception {
        final Issue issue = getIssue("TOMEE-123");

        final Resolution resolution = issue.getResolution();
        assertEquals("Fixed", resolution.getName());
        assertEquals("A fix for this issue is checked into the tree and tested.", resolution.getDescription());
        assertEquals(1, resolution.getId());
    }

    @Test
    public void testStatus() throws Exception {
        final Issue issue = getIssue("TOMEE-123");

        final Status status = issue.getStatus();
        assertEquals("Closed", status.getName());
        assertEquals("The issue is considered finished, the resolution is correct. Issues which are not closed can be reopened.", status.getDescription());
        assertEquals(6, status.getId());
    }

    @Test
    public void testPriority() throws Exception {
        final Issue issue = getIssue("TOMEE-12");

        final Priority priority = issue.getPriority();
        assertEquals("Minor", priority.getName());
        assertEquals(4, priority.getId());
    }

    @Test
    public void testReporter() throws Exception {
        final Issue issue = getIssue("TOMEE-122");

        final User user = issue.getReporter();
        assertEquals("dblevins", user.getName());
        assertEquals("David Blevins", user.getFullname());
        assertEquals("David Blevins", user.getDisplayName());
        assertEquals(null, user.getEmail());
    }

    @Test
    public void testAssignee() throws Exception {
        final Issue issue = getIssue("OPENEJB-100");

        final User user = issue.getAssignee();
        assertEquals("dblevins", user.getName());
        assertEquals("David Blevins", user.getFullname());
        assertEquals("David Blevins", user.getDisplayName());
        assertEquals(null, user.getEmail());
    }

    @Test
    public void testSubtasks() throws Exception {
        final Issue issue = getIssue("OPENEJB-98");

        final List<Issue> subTasks = issue.getSubTasks();
        assertEquals(9, subTasks.size());

        assertTrue(subTasks instanceof MapObjectList);

        final MapObjectList<Issue> issues = (MapObjectList<Issue>) subTasks;
        final Issue openejb100 = issues
                .matches("key", "OPENEJB-100")
                .get(0);

        assertEquals("OPENEJB-100", openejb100.getKey());
        assertEquals("Dependency Injection: Setter", openejb100.getSummary());
    }


    private static Issue getIssue(final String key) {
        final JiraRest jiraRest = new JiraRest("https://issues.apache.org/jira/rest/api/2/");
        final Issue issue = jiraRest.getIssue(key);
        assertEquals(key, issue.getKey());
        return issue;
    }
}