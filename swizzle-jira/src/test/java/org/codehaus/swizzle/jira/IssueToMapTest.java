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

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IssueToMapTest {

    @Test
    public void testToMap() throws Exception {

        final Issue issue = new Issue() {{
            setKey("FOO-123");
            setSummary("Do the thing with the thing");
            setDescription("Description of foo 123");
            addComponent(new Component() {{
                setName("Shapes");
            }});
            addLabel("generated");
            addAffectsVersion(new Version() {{
                setName("1.0.3");
            }});
        }};

        final Map map = issue.toMap2();

        assertEquals(2, map.size());
        assertEquals("FOO-123", map.get("key"));
        assertTrue(map.get("fields") instanceof TreeMap);
        final Map fields = (Map) map.get("fields");
        assertEquals(5, fields.size());

        {
            final List<Map<String, Object>> affectsVersions = (List<Map<String, Object>>) fields.get("affectsVersions");
            assertNotNull(affectsVersions);
            assertEquals(1, affectsVersions.size());

            final Map<String, Object> version = affectsVersions.get(0);
            assertEquals(1, version.size());
            assertEquals("1.0.3", version.get("name"));
        }

        {
            final List<Map<String, Object>> components = (List<Map<String, Object>>) fields.get("components");
            assertNotNull(components);
            assertEquals(1, components.size());

            final Map<String, Object> component = components.get(0);
            assertEquals(1, component.size());
            assertEquals("Shapes", component.get("name"));
        }

        {
            final List<String> labels = (List<String>) fields.get("labels");
            assertNotNull(labels);
            assertEquals(1, labels.size());
            assertEquals("generated", labels.get(0));
        }
    }


}
