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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class IssueType extends BasicObject {

    public IssueType(Map data) {
        super(data);
    }

    public IssueType() {
        super();
    }

    public static final IssueType BUG = fromName("Bug");
    public static final IssueType IMPROVEMENT = fromName("Improvement");
    public static final IssueType NEW_FEATURE = fromName("New Feature");
    public static final IssueType TEST = fromName("Test");
    public static final IssueType TASK = fromName("Task");
    public static final IssueType SUBTASK = fromName("Sub-task");

    public static IssueType fromName(final String name) {
        final List<IssueType> types = Arrays.asList(BUG, IMPROVEMENT, NEW_FEATURE, TEST, TASK, SUBTASK);
        for (final IssueType type : types) {
            if (type.getName().equalsIgnoreCase(name)) return type;
        }

        if ("subtask".equalsIgnoreCase(name)) return SUBTASK;
        if ("feature".equalsIgnoreCase(name)) return NEW_FEATURE;
        if ("newfeature".equalsIgnoreCase(name)) return NEW_FEATURE;

        final IssueType issueType = new IssueType();
        issueType.setName(name);
        return issueType;
    }
}
