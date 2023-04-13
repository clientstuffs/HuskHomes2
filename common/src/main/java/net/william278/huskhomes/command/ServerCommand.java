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

package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ServerCommand extends InGameCommand {

    protected ServerCommand(@NotNull HuskHomes implementor) {
        super("server", List.of(), "", implementor);
        this.addAdditionalPermissions(Map.of("server", false));
    }

    @Override
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        if (!this.plugin.getSettings().doCrossServer()) {
            return;
        }
        if (args.length == 1) {
            final String serverName = args[0];
            if (executor.getPosition().getServer().equalsIgnoreCase(serverName)) {
                this.plugin.getLocales().getLocale("already_in_same_server").ifPresent(executor::sendMessage);
                return;
            }
            Teleport.builder(this.plugin)
                .executor(executor)
                .target(Position.at(0.0d, 0.0d, 0.0d, 0.0f, 0.0f, World.from("", UUID.randomUUID()), serverName))
                .type(Teleport.Type.SERVER)
                .setQueueType("server")
                .toTimedTeleport()
                .execute();
        } else {
            this.plugin.getLocales().getLocale("error_invalid_syntax", "/server [server_name]")
                .ifPresent(executor::sendMessage);
        }
    }
}
