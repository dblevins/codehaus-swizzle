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

import org.tomitribe.util.Files;
import org.tomitribe.util.IO;
import org.tomitribe.util.collect.ObjectMap;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

public class Remote {

    private final String name;

    private Remote(final String name) {
        this.name = name;
    }

    private URI url;
    private String username;
    private String password;

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public static File getFile(final String name) {
        final String home = System.getProperty("user.home");
        final File dir = new File(home, ".sjira");
        Files.mkdirs(dir);
        return new File(dir, name + ".properties");
    }

    public static Remote load(final String name) throws IOException {
        final File file = getFile(name);
        if (!file.exists()) throw new NoSuchRemoteException(name);

        final Remote remote = new Remote(name);
        final ObjectMap map = new ObjectMap(remote);

        final Properties properties = new Properties();
        properties.load(IO.read(file));
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue());
        }
        return remote;
    }

    public static void store(final Remote remote) throws IOException {
        final File file = getFile(remote.name);

        final Properties properties = new Properties();
        final ObjectMap map = new ObjectMap(remote);
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) continue;
            if (entry.getKey().equals("class")) continue;
            properties.put(entry.getKey(), entry.getValue().toString());
        }

        try (final OutputStream write = IO.write(file)) {
            properties.store(write, "# configuration");
        }
    }

}
