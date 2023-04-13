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
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelHomeCommand extends SavedPositionCommand<Home> {

    public DelHomeCommand(@NotNull HuskHomes plugin) {
        super("delhome", List.of(), Home.class, List.of(), plugin);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length == 0) {
            plugin.getDatabase().getHomes(onlineUser).thenAccept(homes -> {
                if (homes.size() == 1) {
                    homes.stream().findFirst().ifPresent(home -> deletePlayerHome(onlineUser, onlineUser, home.meta.name, false));
                } else {
                    plugin.getLocales().getLocale("error_invalid_syntax", "/delhome <name>")
                        .ifPresent(onlineUser::sendMessage);
                }
            });
            return;
        }

        if (args.length <= 2) {
            final String homeName = args[0];
            final boolean confirm = args.length == 2 && args[1].equalsIgnoreCase("confirm");
            RegexUtil.matchDisambiguatedHomeIdentifier(homeName).ifPresentOrElse(homeIdentifier ->
                    plugin.getDatabase().getUserDataByName(homeIdentifier.ownerName()).thenAccept(
                        optionalUserData -> optionalUserData.ifPresentOrElse(userData -> {
                                if (!userData.getUserUuid().equals(onlineUser.uuid)) {
                                    if (!onlineUser.hasPermission(Permission.COMMAND_DELETE_HOME_OTHER.node)) {
                                        plugin.getLocales().getLocale("error_no_permission")
                                            .ifPresent(onlineUser::sendMessage);
                                        return;
                                    }
                                }
                                deletePlayerHome(onlineUser, userData.user(), homeIdentifier.homeName(), confirm);
                            },
                            () -> plugin.getLocales().getLocale("error_home_invalid_other",
                                homeIdentifier.ownerName(), homeIdentifier.homeName()).ifPresent(onlineUser::sendMessage))),
                () -> deletePlayerHome(onlineUser, onlineUser, homeName, confirm));
            return;
        }

        plugin.getLocales().getLocale("error_invalid_syntax", "/delhome <name>")
            .ifPresent(onlineUser::sendMessage);
    }

    /**
     * Delete a player's home
     *
     * @param deleter           the player who is deleting the home
     * @param homeOwner         The player who owns the home
     * @param homeName          The home name to delete
     * @param delHomeAllConfirm Whether to skip the confirmation prompt for deleting all homes
     */
    private void deletePlayerHome(@NotNull OnlineUser deleter, @NotNull User homeOwner, @NotNull String homeName,
                                  final boolean delHomeAllConfirm) {
        plugin.getSavedPositionManager().deleteHome(homeOwner, homeName).thenAccept(deleted -> {
            if (deleter.equals(homeOwner)) {
                if (deleted) {
                    plugin.getLocales().getLocale("home_deleted", homeName).ifPresent(deleter::sendMessage);
                    return;
                }
                if (homeName.equalsIgnoreCase("all")) {
                    deleteAllHomes(deleter, homeOwner, delHomeAllConfirm);
                    return;
                }
                plugin.getLocales().getLocale("error_home_invalid", homeName).ifPresent(deleter::sendMessage);
            } else {
                if (deleted) {
                    plugin.getLocales().getLocale("home_deleted_other", homeOwner.username, homeName).ifPresent(deleter::sendMessage);
                    return;
                }
                if (homeName.equalsIgnoreCase("all")) {
                    deleteAllHomes(deleter, homeOwner, delHomeAllConfirm);
                    return;
                }
                plugin.getLocales().getLocale("error_home_invalid_other", homeOwner.username, homeName).ifPresent(deleter::sendMessage);
            }
        });
    }

    /**
     * Delete all of a player's homes
     *
     * @param deleter   the player who is deleting the homes
     * @param homeOwner the player who owns the homes
     * @param confirm   whether to skip the confirmation prompt
     */
    private void deleteAllHomes(@NotNull OnlineUser deleter, @NotNull User homeOwner,
                                final boolean confirm) {
        if (!confirm) {
            plugin.getLocales().getLocale("delete_all_homes_confirm")
                .ifPresent(deleter::sendMessage);
            return;
        }

        plugin.getSavedPositionManager().deleteAllHomes(homeOwner).thenAccept(deleted -> {
            if (deleted == 0) {
                plugin.getLocales().getLocale("error_no_warps_set")
                    .ifPresent(deleter::sendMessage);
                return;
            }

            plugin.getLocales().getLocale("delete_all_homes_success", Integer.toString(deleted))
                .ifPresent(deleter::sendMessage);
=======
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        if (executor instanceof OnlineUser user && handleDeleteAll(user, args)) {
            return;
        }
        super.execute(executor, args);
    }

    @Override
    public void execute(@NotNull CommandUser executor, @NotNull Home home, @NotNull String[] args) {
        if (executor instanceof OnlineUser user && !home.getOwner().equals(user) && !user.hasPermission(getOtherPermission())) {
            plugin.getLocales().getLocale("error_no_permission")
                    .ifPresent(user::sendMessage);
            return;
        }

        plugin.fireEvent(plugin.getHomeDeleteEvent(home, executor), (event) -> {
            try {
                plugin.getManager().homes().deleteHome(home);
            } catch (ValidationException e) {
                e.dispatchHomeError(executor, !home.getOwner().equals(executor), plugin, home.getName());
                return;
            }
            plugin.getLocales().getLocale("home_deleted", home.getName())
                    .ifPresent(executor::sendMessage);
>>>>>>> master
        });
    }

    private boolean handleDeleteAll(@NotNull OnlineUser user, @NotNull String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("all")) {
            if (!parseStringArg(args, 1)
                    .map(confirm -> confirm.equalsIgnoreCase("confirm"))
                    .orElse(false)) {
                plugin.getLocales().getLocale("delete_all_homes_confirm")
                        .ifPresent(user::sendMessage);
                return true;
            }

            plugin.fireEvent(plugin.getDeleteAllHomesEvent(user, user), (event) -> {
                final int deleted = plugin.getManager().homes().deleteAllHomes(user);
                if (deleted == 0) {
                    plugin.getLocales().getLocale("error_no_homes_set")
                            .ifPresent(user::sendMessage);
                    return;
                }

                plugin.getLocales().getLocale("delete_all_homes_success", Integer.toString(deleted))
                        .ifPresent(user::sendMessage);
            });
            return true;
        }
<<<<<<< HEAD
        return args.length > 1 ? Collections.emptyList() : plugin.getCache().homes
            .getOrDefault(user.uuid, new ArrayList<>())
            .stream()
            .filter(s -> s.startsWith(args.length == 1 ? args[0] : ""))
            .sorted()
            .collect(Collectors.toList());
=======
        return false;
>>>>>>> master
    }

}
