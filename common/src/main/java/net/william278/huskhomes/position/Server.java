/*
 * This file is part of HuskHomes, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
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

package net.william278.huskhomes.position;

import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Represents a server on a proxied network
 */
@YamlFile(header = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                   "┃ Server ID cache. Must match  ┃\n" +
                   "┃ server name in proxy config. ┃\n" +
                   "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛")
public class Server {

    /**
     * Default (unknown) server identifier
     */
    public static String getDefaultServerName() {
        try {
            final Path serverDirectory = Path.of(System.getProperty("user.dir"));
            return serverDirectory.getFileName().toString().trim();
        } catch (Exception e) {
            return "server";
        }
    }

    /**
     * Proxy-defined name of this server
     */
    @YamlKey("server_name")
    public String name = getDefaultServerName();

    public Server(@NotNull String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    private Server() {
    }

    @Override
    public boolean equals(@NotNull Object other) {
        // If the name of this server matches another, the servers are the same.
        if (other instanceof Server) {
            final var server = (Server) other;

            return server.name.equalsIgnoreCase(this.name);
        }
        return super.equals(other);
    }

}
