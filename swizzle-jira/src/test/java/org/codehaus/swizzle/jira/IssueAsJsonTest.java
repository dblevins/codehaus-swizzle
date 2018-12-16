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
import javax.json.JsonObjectBuilder;
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

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("a", true);
        builder.add("c", true);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final JsonWriter writer = factory.createWriter(out);
        writer.write(builder.build());
        writer.close();

        Assert.assertEquals("", new String(out.toByteArray()));
    }


}
