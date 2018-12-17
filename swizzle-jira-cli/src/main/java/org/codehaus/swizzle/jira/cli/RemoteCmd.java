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

import org.tomitribe.crest.api.Command;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

@Command("remote")
public class RemoteCmd {

    @Command
    public void add(final String name, final URI path) throws IOException {

        final File file = Remote.getFile(name);
        if (file.exists()) throw new RemoteAlreadyExistsException(name);
        if (!file.createNewFile()) throw new CannotCreateRemoteFileException(name);

        final Remote remote = Remote.load(name);
        remote.setUrl(path);
        Remote.store(remote);
    }

    @Command
    public void login(final Remote remote, final String username, final String password) throws IOException {

        remote.setUsername(username);
        remote.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
        Remote.store(remote);

    }

}
