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
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.teleport.Teleportable;
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

<<<<<<< HEAD
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.Collectors;
=======
import java.util.List;
import java.util.Optional;
>>>>>>> master

public abstract class HomeCommand extends SavedPositionCommand<Home> {

    protected HomeCommand(@NotNull String name, @NotNull List<String> aliases, @NotNull HuskHomes plugin) {
        super(name, aliases, Home.class, List.of("player"), plugin);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        switch (args.length) {
            case 0: {
                plugin.getDatabase().getHomes(onlineUser).thenAcceptAsync(homes -> {
                    // Send the home list if they have homes set. If they have just one home set, teleport the player
                    switch (homes.size()) {
                        case 0: {
                            plugin.getLocales().getLocale("error_no_homes_set").ifPresent(onlineUser::sendMessage);
                            break;
                        }
                        case 1: {
                            Teleport.builder(this.plugin, onlineUser)
                                .setTarget(homes.get(0))
                                .setQueueType("home")
                                .toTimedTeleport()
                                .thenAccept(TimedTeleport::execute);
                            break;
                        }
                        default: {
                            plugin.getCache().getHomeList(onlineUser, onlineUser,
                                    plugin.getLocales(), homes, plugin.getSettings().listItemsPerPage, 1)
                                .ifPresent(onlineUser::sendMessage);
                            break;
                        }
                    }
                });
                break;
            }
            case 1: {
                // Parse the home name input and teleport the player to the home
                final String homeName = args[0];
                RegexUtil.matchDisambiguatedHomeIdentifier(homeName).ifPresentOrElse(
                    homeIdentifier -> plugin.getDatabase().getUserDataByName(homeIdentifier.ownerName())
                        .thenAccept(optionalUserData -> optionalUserData.ifPresentOrElse(
                            userData -> teleportToNamedHome(onlineUser, userData.user(), homeIdentifier.homeName()),
                            () -> plugin.getLocales().getLocale("error_home_invalid_other", homeIdentifier.ownerName(), homeIdentifier.homeName())
                                .ifPresent(onlineUser::sendMessage))),
                    () -> teleportToNamedHome(onlineUser, onlineUser, homeName));
                break;
            }
            default: {
                plugin.getLocales().getLocale("error_invalid_syntax", "/home [name]")
                    .ifPresent(onlineUser::sendMessage);
                break;
            }
        }
    }

    private void teleportToNamedHome(@NotNull OnlineUser teleporter, @NotNull User owner, @NotNull String homeName) {
        final boolean otherHome = !owner.equals(teleporter);
        plugin.getDatabase()
            .getHome(owner, homeName)
            .thenAccept(homeResult -> homeResult.ifPresentOrElse(home -> {
                if (otherHome && !home.isPublic) {
                    if (!teleporter.hasPermission(Permission.COMMAND_HOME_OTHER.node)) {
                        plugin.getLocales().getLocale("error_no_permission")
                            .ifPresent(teleporter::sendMessage);
                        return;
                    }
                }
                Teleport.builder(this.plugin, teleporter)
                    .setTarget(home)
                    .setQueueType("home")
                    .toTimedTeleport()
                    .thenAccept(TimedTeleport::execute);
            }, () -> {
                if (otherHome) {
                    plugin.getLocales().getLocale("error_home_invalid_other", owner.username, homeName)
                        .ifPresent(teleporter::sendMessage);
                } else {
                    plugin.getLocales().getLocale("error_home_invalid", homeName)
                        .ifPresent(teleporter::sendMessage);
                }
            }));
    }

    @Override
    public void onConsoleExecute(@NotNull String[] args) {
        if (args.length != 2) {
            plugin.getLoggingAdapter().log(Level.WARNING, "Invalid syntax. Usage: home <player> <home>");
            return;
        }
        CompletableFuture.runAsync(() -> {
            final OnlineUser playerToTeleport = plugin.findOnlinePlayer(args[0]).orElse(null);
            if (playerToTeleport == null) {
                plugin.getLoggingAdapter().log(Level.WARNING, "Player not found: " + args[0]);
                return;
            }
            final AtomicReference<Home> matchedHome = new AtomicReference<>(null);
            RegexUtil.matchDisambiguatedHomeIdentifier(args[1]).ifPresentOrElse(
                identifier -> matchedHome.set(plugin.getDatabase().getUserDataByName(identifier.ownerName()).join()
                    .flatMap(user -> plugin.getDatabase().getHome(user.user(), identifier.homeName()).join())
                    .orElse(null)),
                () -> matchedHome.set(plugin.getDatabase().getUserDataByName(playerToTeleport.username).join()
                    .flatMap(user -> plugin.getDatabase().getHome(user.user(), args[1]).join())
                    .orElse(null)));

            final Home home = matchedHome.get();
            if (home == null) {
                plugin.getLoggingAdapter().log(Level.WARNING, "Could not find home '" + args[1] + "'");
                return;
            }

            plugin.getLoggingAdapter().log(Level.INFO, "Teleporting " + playerToTeleport.username + " to "
                                                       + home.owner.username + "." + home.meta.name);
            Teleport.builder(plugin, playerToTeleport)
                .setTarget(home)
                .toTeleport()
                .thenAccept(Teleport::execute);
        });
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return args.length > 1 ? Collections.emptyList() : plugin.getCache().homes
            .getOrDefault(user.uuid, new ArrayList<>())
            .stream()
            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
            .sorted()
            .collect(Collectors.toList());
=======
    public void execute(@NotNull CommandUser executor, @NotNull Home home, @NotNull String[] args) {
        final Optional<Teleportable> optionalTeleporter = resolveTeleporter(executor, args);
        if (optionalTeleporter.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        if (executor instanceof OnlineUser user && !user.hasPermission(getOtherPermission())
                && (!home.getOwner().equals(user) && !home.isPublic())) {
            plugin.getLocales().getLocale("error_public_home_invalid",
                            home.getOwner().getUsername(), home.getName())
                    .ifPresent(executor::sendMessage);
            return;
        }

        this.teleport(executor, optionalTeleporter.get(), home);
>>>>>>> master
    }

}
