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
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
<<<<<<< HEAD
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
=======
import java.util.Optional;
>>>>>>> master

public class TpHereCommand extends InGameCommand implements UserListTabProvider {

    protected TpHereCommand(@NotNull HuskHomes plugin) {
        super("tphere", List.of("tpohere"), "<player>", plugin);
        setOperatorCommand(true);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        CompletableFuture.runAsync(() -> {
            if (args.length != 1) {
                plugin.getLocales().getLocale("error_invalid_syntax", "/tphere <player>")
                    .ifPresent(onlineUser::sendMessage);
                return;
            }
            final String targetPlayerName = args[0];
            plugin.findPlayer(onlineUser, targetPlayerName).thenAccept(teleporterName -> {
                if (teleporterName.isEmpty()) {
                    plugin.getLocales().getLocale("error_player_not_found", targetPlayerName)
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }

                Teleport.builder(plugin, onlineUser)
                    .setTeleporter(teleporterName.get())
                    .setTarget(onlineUser.getPosition())
                    .toTeleport()
                    .thenAccept(teleport -> teleport.execute().thenAccept(result -> {
                        if (result.successful()) {
                            result.getTeleporter()
                                .flatMap(teleporter -> plugin.getLocales().getLocale("teleporting_other_complete",
                                    teleporter.username, onlineUser.username))
                                .ifPresent(onlineUser::sendMessage);
                        }
                    }));
            });
=======
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        final Optional<String> optionalTarget = parseStringArg(args, 0);
        if (optionalTarget.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        try {
            Teleport.builder(plugin)
                    .executor(executor)
                    .teleporter(optionalTarget.get())
                    .target(executor.getPosition())
                    .toTeleport().execute();
>>>>>>> master

            plugin.getLocales().getLocale("teleporting_other_complete",
                    optionalTarget.get(), executor.getUsername());
        } catch (TeleportationException e) {
            e.displayMessage(executor, plugin, args);
        }
    }

<<<<<<< HEAD
    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        return args.length <= 1 ? plugin.getCache().players.stream()
            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
            .sorted().collect(Collectors.toList()) : Collections.emptyList();
    }
=======
>>>>>>> master
}
