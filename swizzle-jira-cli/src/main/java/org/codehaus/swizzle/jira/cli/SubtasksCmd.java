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
package org.codehaus.swizzle.jira.cli;

import org.codehaus.swizzle.jira.Component;
import org.codehaus.swizzle.jira.Issue;
import org.codehaus.swizzle.jira.IssueType;
import org.codehaus.swizzle.jira.JiraRest;
import org.codehaus.swizzle.jira.Priority;
import org.codehaus.swizzle.jira.Project;
import org.codehaus.swizzle.jira.RequestFailedException;
import org.codehaus.swizzle.jira.User;
import org.codehaus.swizzle.jira.Version;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;

import java.io.PrintStream;
import java.util.Base64;
import java.util.List;

@Command("subtasks")
public class SubtasksCmd {

    @Command
    public StreamingOutput list(final Remote remote, final Issue issue) {
        try (final JiraRest jira = new JiraRest(remote.getUrl())) {
            if (remote.getUsername() != null) {
                jira.login(remote.getUsername(), new String(Base64.getDecoder().decode(remote.getPassword())));
            }

            final Issue loaded = jira.getIssue(issue.getKey());

            return outputStream -> {
                final PrintStream out = new PrintStream(outputStream);

                for (final Issue subtask : loaded.getSubTasks()) {
                    out.printf(" - [%s] %s%n", subtask.getKey(), subtask.getSummary());
                }
            };
        }
    }

    @Command
    public String add(
            @Option("fix-version") final List<Version> fixVersions,
            @Option("component") final List<Component> components,
            @Option("label") final List<String> labels,
            @Option("priority") final Priority priority,
            @Option("assignee") final User assignee,
            @Option("reporter") final User reporter,
            @Option("description") final String description,
            final Remote remote, final Issue parent, final String summary) {

        try (final JiraRest jira = new JiraRest(remote.getUrl())) {
            if (remote.getUsername() != null) {
                jira.login(remote.getUsername(), new String(Base64.getDecoder().decode(remote.getPassword())));
            }

            final Issue issue = new Issue();
            if (priority != null) issue.setPriority(priority);
            if (assignee != null) issue.setAssignee(assignee);
            if (reporter != null) issue.setReporter(reporter);
            if (description != null) issue.setDescription(description);
            if (fixVersions != null) issue.setFixVersions(fixVersions);
            if (components != null) issue.setComponents(components);
            if (labels != null) issue.setLabels(labels);

            issue.setSummary(summary);
            issue.setType(IssueType.SUBTASK);
            issue.setParent(parent);
            issue.setProject(Project.fromKey(parent.getKey().replaceAll("-.*", "")));

            final Issue loaded = jira.createIssue(issue);

            return String.format(" - [%s] %s", loaded.getKey(), loaded.getSummary());
        } catch (RequestFailedException e) {
            throw new JiraException("Add subtask failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new JiraException("Add subtask failed", e);
        }
    }
}
