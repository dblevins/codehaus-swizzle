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
import org.codehaus.swizzle.jira.Priority;
import org.codehaus.swizzle.jira.Project;
import org.codehaus.swizzle.jira.User;
import org.codehaus.swizzle.jira.Version;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Option;

import java.util.List;

@Command("issue")
public class IssueCmd {

    @Command
    public String create(
            @Option("fix-version") final List<Version> fixVersions,
            @Option("component") final List<Component> components,
            @Option("label") final List<String> labels,
            @Option("parent") final Issue parent,
            @Option("priority") final Priority priority,
            @Option("assignee") final User assignee,
            @Option("reporter") final User reporter,
            @Option("description") final String description,
            final Project project,
            final IssueType type,
            final String summary
    ) {

        final Issue issue = new Issue();
        issue.setSummary(summary);
        issue.setType(type);
        issue.setProject(project);

        if (priority != null) issue.setPriority(priority);
        if (assignee != null) issue.setAssignee(assignee);
        if (reporter != null) issue.setReporter(reporter);
        if (description != null) issue.setDescription(description);
        if (priority != null) issue.setFixVersions(fixVersions);
        if (priority != null) issue.setComponents(components);
        if (priority != null) issue.setLabels(labels);
        if (priority != null) issue.setParent(parent);

        return "done";
    }
}
