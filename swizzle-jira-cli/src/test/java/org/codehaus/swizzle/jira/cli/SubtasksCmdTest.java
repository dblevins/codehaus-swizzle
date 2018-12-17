package org.codehaus.swizzle.jira.cli;

import org.junit.Test;
import org.tomitribe.crest.cmds.Cmd;
import org.tomitribe.crest.cmds.processors.Commands;

public class SubtasksCmdTest {

    @Test
    public void testAdd() throws Exception {
        final Cmd add = Commands.get(SubtasksCmd.class).get("subtasks");

        add.exec("add",
                "apache",
                "OPENEJB-2139",
                "Testing sjira command-line",
                "--label=foo",
                "--label=bar"
        );
    }
}