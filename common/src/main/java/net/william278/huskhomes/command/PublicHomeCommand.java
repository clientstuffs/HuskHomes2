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
import net.william278.huskhomes.user.CommandUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
<<<<<<< HEAD
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
=======
>>>>>>> master

public class PublicHomeCommand extends HomeCommand implements TabProvider {

    protected PublicHomeCommand(@NotNull HuskHomes plugin) {
        super("phome", List.of("publichome"), plugin);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        switch (args.length) {
            case 0: {
                plugin.getDatabase().getPublicHomes().thenAcceptAsync(publicHomes -> {
                    // Display the public home list if there are any public home set
                    if (publicHomes.size() == 0) {
                        plugin.getLocales().getLocale("error_no_public_homes_set").ifPresent(onlineUser::sendMessage);
                        return;
                    }

                    plugin.getCache().getPublicHomeList(onlineUser, plugin.getLocales(),
                            publicHomes, plugin.getSettings().listItemsPerPage, 1)
                        .ifPresent(onlineUser::sendMessage);
                });
                break;
            }
            case 1: {
                final String homeName = args[0];
                // Match the input to a home identifier and teleport
                RegexUtil.matchDisambiguatedHomeIdentifier(homeName).ifPresentOrElse(
                    homeIdentifier -> plugin.getDatabase().getUserDataByName(homeIdentifier.ownerName())
                        .thenAccept(optionalUserData -> optionalUserData.ifPresentOrElse(
                            userData -> teleportToNamedHome(onlineUser, userData.user(), homeIdentifier.homeName()),
                            () -> plugin.getLocales().getLocale("error_home_invalid_other", homeIdentifier.ownerName(), homeIdentifier.homeName())
                                .ifPresent(onlineUser::sendMessage))),
                    () -> plugin.getDatabase().getPublicHomes().thenAccept(publicHomes -> {
                        // If the identifier format was not used, attempt to teleport the player to the closest match
                        final List<Home> homeMatches = publicHomes.stream()
                            .filter(home -> home.meta.name.equalsIgnoreCase(homeName)).collect(Collectors.toList());
                        if ((long) homeMatches.size() == 1) {
                            Teleport.builder(plugin, onlineUser)
                                .setTarget(homeMatches.get(0))
                                .toTimedTeleport().thenAccept(TimedTeleport::execute);
                        } else {
                            plugin.getLocales().getLocale("error_invalid_syntax", "/publichome [<owner_name>.<home_name>]")
                                .ifPresent(onlineUser::sendMessage);
                        }
                    }));
                break;
            }
            default: {
                plugin.getLocales().getLocale("error_invalid_syntax", "/publichome [<owner_name>.<home_name>]")
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
                Teleport.builder(plugin, teleporter)
                    .setTarget(home)
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
            plugin.getLoggingAdapter().log(Level.WARNING, "Invalid syntax. Usage: publichome <player> <[owner_name].[home_name]>");
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
                () -> matchedHome.set(plugin.getDatabase().getPublicHomes().join()
                    .stream()
                    .filter(home -> home.owner.equals(playerToTeleport) && home.meta.name.equalsIgnoreCase(args[1]))
                    .findFirst()
                    .orElse(null)));

            final Home home = matchedHome.get();
            if (home == null) {
                plugin.getLoggingAdapter().log(Level.WARNING, "Could not find public home '" + args[1] + "'");
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
        // Return every public home name as username.home_name from the cache
        final List<String> publicHomes = new ArrayList<>();
        plugin.getCache().publicHomes.forEach((ownerName, homeNames) ->
            homeNames.forEach(homeName -> publicHomes.add(ownerName + "." + homeName)));
        return args.length > 1 ? Collections.emptyList() : publicHomes
            .stream()
            .filter(publicHomeIdentifier -> publicHomeIdentifier.split(Pattern.quote("."))[1].toLowerCase(Locale.ROOT)
                .startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
            .sorted()
            .collect(Collectors.toList());
=======
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        // Display the public home list if no arguments are provided
        if (args.length == 0) {
            plugin.getCommand(PublicHomeListCommand.class)
                    .ifPresent(command -> command.showPublicHomeList(executor, 1));
            return;
        }
        super.execute(executor, args);
    }

    @Override
    @NotNull
    public List<String> suggest(@NotNull CommandUser executor, @NotNull String[] args) {
        if (args.length <= 2) {
            return filter(plugin.getManager().homes().getPublicHomeNames(), args);
        }
        return List.of();
>>>>>>> master
    }

}