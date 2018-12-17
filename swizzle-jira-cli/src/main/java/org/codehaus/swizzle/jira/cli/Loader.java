/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package org.codehaus.swizzle.jira.cli;

import org.tomitribe.crest.cmds.processors.Commands;

import java.util.Arrays;
import java.util.Iterator;

public class Loader implements Commands.Loader {

    @Override
    public Iterator<Class<?>> iterator() {
        return Arrays.asList(
                IssueCmd.class,
                RemoteCmd.class,
                SubtasksCmd.class
        ).iterator();
    }
}
