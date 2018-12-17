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

import org.codehaus.swizzle.jira.Issue;
import org.codehaus.swizzle.jira.JiraRest;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Base64;

@Command("subtasks")
public class SubtasksCmd {

    @Command
    public StreamingOutput list(final Remote remote, final Issue issue) {
        try (final JiraRest jira = new JiraRest(remote.getUrl())){
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
}
