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

import org.junit.Assert;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class IssueAsJsonTest {

    @Test
    public void testToJson() throws Exception {

        final JsonWriterFactory factory = Json.createWriterFactory(new HashMap<String, Object>() {{
            put(JsonGenerator.PRETTY_PRINTING, true);
        }});

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


        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final JsonWriter writer = factory.createWriter(out);
        writer.write(JiraRest.toJsonObject(issue));
        writer.close();

        Assert.assertEquals("{\n" +
                "  \"fields\":{\n" +
                "    \"affectsVersions\":[\n" +
                "      {\n" +
                "        \"name\":\"1.0.3\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"components\":[\n" +
                "      {\n" +
                "        \"name\":\"Shapes\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"description\":\"Description of foo 123\",\n" +
                "    \"labels\":[\n" +
                "      \"generated\"\n" +
                "    ],\n" +
                "    \"summary\":\"Do the thing with the thing\"\n" +
                "  },\n" +
                "  \"key\":\"FOO-123\"\n" +
                "}", new String(out.toByteArray()));
    }


}
