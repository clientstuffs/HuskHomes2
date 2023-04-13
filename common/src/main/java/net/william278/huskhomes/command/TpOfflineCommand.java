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
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.user.SavedUser;
import net.william278.huskhomes.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
<<<<<<< HEAD
import java.util.Locale;
import java.util.stream.Collectors;
=======
import java.util.Optional;
>>>>>>> master

public class TpOfflineCommand extends InGameCommand implements UserListTabProvider {

    protected TpOfflineCommand(@NotNull HuskHomes plugin) {
        super("tpoffline", List.of(), "<player>", plugin);
        setOperatorCommand(true);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length != 1) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/tpoffline <player>")
                .ifPresent(onlineUser::sendMessage);
            return;
        }
        final String targetUser = args[0];
        plugin.getDatabase().getUserDataByName(targetUser).thenAccept(userData -> {
            if (userData.isEmpty()) {
                plugin.getLocales().getLocale("error_player_not_found", targetUser)
                    .ifPresent(onlineUser::sendMessage);
                return;
            }
            plugin.getDatabase().getOfflinePosition(userData.get().user()).thenAccept(offlinePosition -> {
                if (offlinePosition.isEmpty()) {
                    plugin.getLocales().getLocale("error_no_offline_position", targetUser)
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
                plugin.getLocales().getLocale("teleporting_offline_player", targetUser)
                    .ifPresent(onlineUser::sendMessage);
                Teleport.builder(plugin, onlineUser)
                    .setTarget(offlinePosition.get())
                    .toTeleport()
                    .thenAccept(teleport -> teleport.execute().thenAccept(result -> {
                        if (result.successful()) {
                            plugin.getLocales().getLocale("teleporting_offline_complete", targetUser)
                                .ifPresent(onlineUser::sendMessage);
                        }
                    }));
            });
        });
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        return args.length <= 1 ? plugin.getCache().players.stream()
            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
            .sorted().collect(Collectors.toList()) : Collections.emptyList();
=======
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        final Optional<String> optionalUser = parseStringArg(args, 0);
        if (optionalUser.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        final Optional<User> targetUserData = plugin.getDatabase()
                .getUserDataByName(optionalUser.get())
                .map(SavedUser::getUser);
        if (targetUserData.isEmpty()) {
            plugin.getLocales().getLocale("error_player_not_found", optionalUser.get())
                    .ifPresent(executor::sendMessage);
            return;
        }

        this.teleportToOfflinePosition(executor, targetUserData.get(), args);
    }

    private void teleportToOfflinePosition(@NotNull OnlineUser user, @NotNull User target, @NotNull String[] args) {
        final Optional<Position> position = plugin.getDatabase().getOfflinePosition(target);
        if (position.isEmpty()) {
            plugin.getLocales().getLocale("error_no_offline_position", target.getUsername())
                    .ifPresent(user::sendMessage);
            return;
        }

        plugin.getLocales().getLocale("teleporting_offline_player", target.getUsername())
                .ifPresent(user::sendMessage);
        try {
            Teleport.builder(plugin)
                    .teleporter(user)
                    .target(position.get())
                    .toTeleport().execute();
        } catch (TeleportationException e) {
            e.displayMessage(user, plugin, args);
        }
>>>>>>> master
    }

}
