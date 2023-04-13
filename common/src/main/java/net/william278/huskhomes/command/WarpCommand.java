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
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.teleport.Teleportable;
import net.william278.huskhomes.user.CommandUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
<<<<<<< HEAD
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;
=======
import java.util.Optional;
>>>>>>> master

public class WarpCommand extends SavedPositionCommand<Warp> {

    protected WarpCommand(@NotNull HuskHomes plugin) {
        super("warp", List.of(), Warp.class, List.of("player"), plugin);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        switch (args.length) {
            case 0: {
                plugin.getDatabase().getWarps()
                    .thenApply(warps -> warps.stream()
                        .filter(warp -> warp.hasPermission(plugin.getSettings().permissionRestrictWarps, onlineUser))
                        .collect(Collectors.toList()))
                    .thenAccept(warps -> {
                        if (warps.isEmpty()) {
                            plugin.getLocales().getLocale("error_no_warps_set")
                                .ifPresent(onlineUser::sendMessage);
                            return;
                        }
                        plugin.getCache().getWarpList(onlineUser, plugin.getLocales(), warps,
                                plugin.getSettings().listItemsPerPage, 1)
                            .ifPresent(onlineUser::sendMessage);
                    });
                break;
            }
            case 1: {
                final String warpName = args[0];
                plugin.getDatabase()
                    .getWarp(warpName)
                    .thenAccept(warpResult -> warpResult.ifPresentOrElse(warp -> {
                            // Handle permission restrictions
                            if (!warp.hasPermission(plugin.getSettings().permissionRestrictWarps, onlineUser)) {
                                plugin.getLocales().getLocale("error_no_permission")
                                    .ifPresent(onlineUser::sendMessage);
                                return;
                            }

                            Teleport.builder(this.plugin, onlineUser)
                                .setTarget(warp)
                                .setQueueType("warp")
                                .toTimedTeleport()
                                .thenAccept(TimedTeleport::execute);
                        },
                        () -> plugin.getLocales().getLocale("error_warp_invalid", warpName)
                            .ifPresent(onlineUser::sendMessage)));
                break;
            }
            default: {
                plugin.getLocales().getLocale("error_invalid_syntax", "/warp [name]")
                    .ifPresent(onlineUser::sendMessage);
                break;
            }
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        return plugin.getCache().warps.stream()
            .filter(s -> user == null || Warp.hasPermission(plugin.getSettings().permissionRestrictWarps, user, s))
            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length >= 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public void onConsoleExecute(@NotNull String[] args) {
        if (args.length != 2) {
            plugin.getLoggingAdapter().log(Level.WARNING, "Invalid syntax. Usage: warp <player> <warp>");
            return;
        }
        final OnlineUser playerToTeleport = plugin.findOnlinePlayer(args[0]).orElse(null);
        if (playerToTeleport == null) {
            plugin.getLoggingAdapter().log(Level.WARNING, "Player not found: " + args[0]);
=======
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        if (args.length == 0) {
            plugin.getCommand(WarpListCommand.class)
                    .ifPresent(command -> command.showWarpList(executor, 1));
>>>>>>> master
            return;
        }
        super.execute(executor, args);
    }

    @Override
    public void execute(@NotNull CommandUser executor, @NotNull Warp warp, @NotNull String[] args) {
        if (plugin.getSettings().doPermissionRestrictWarps()) {
            if (!executor.hasPermission(warp.getPermission()) && !executor.hasPermission(Warp.getWildcardPermission())) {
                plugin.getLocales().getLocale("error_no_permission")
                        .ifPresent(executor::sendMessage);
                return;
            }
        }

<<<<<<< HEAD
            plugin.getLoggingAdapter().log(Level.INFO, "Teleporting " + playerToTeleport.username + " to " + warp.meta.name);
            Teleport.builder(plugin, playerToTeleport)
                .setTarget(warp)
                .toTimedTeleport()
                .thenAccept(TimedTeleport::execute);
        });
=======
        final Optional<Teleportable> optionalTeleporter = resolveTeleporter(executor, args);
        if (optionalTeleporter.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        this.teleport(executor, optionalTeleporter.get(), warp);
>>>>>>> master
    }
}
