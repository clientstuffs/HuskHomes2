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
import net.william278.huskhomes.network.Message;
import net.william278.huskhomes.network.Payload;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TpAllCommand extends InGameCommand {

    protected TpAllCommand(@NotNull HuskHomes plugin) {
        super("tpall", List.of(), "", plugin);
        setOperatorCommand(true);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length != 0) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/tpaall")
                .ifPresent(onlineUser::sendMessage);
            return;
        }

        // Determine players to teleport and teleport them
        plugin.getCache().updatePlayerListCache(plugin, onlineUser).thenAccept(fetchedPlayers -> {
            final List<String> players = fetchedPlayers.stream()
                .filter(userName -> !userName.equalsIgnoreCase(onlineUser.username)).collect(Collectors.toList());
            if (players.isEmpty()) {
                plugin.getLocales().getLocale("error_no_players_online").ifPresent(onlineUser::sendMessage);
                return;
=======
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        if (plugin.getGlobalPlayerList().size() <= 1) {
            plugin.getLocales().getLocale("error_no_players_online")
                    .ifPresent(executor::sendMessage);
            return;
        }

        final Position targetPosition = executor.getPosition();
        try {
            for (OnlineUser user : plugin.getOnlineUsers()) {
                Teleport.builder(plugin)
                        .teleporter(user)
                        .target(targetPosition)
                        .toTeleport().execute();
>>>>>>> master
            }
        } catch (TeleportationException e) {
            e.displayMessage(executor, plugin, args);
            return;
        }

<<<<<<< HEAD
            // Send a message
            plugin.getLocales().getLocale("teleporting_all_players", Integer.toString(players.size()))
                .ifPresent(onlineUser::sendMessage);

            // Teleport every player
            final Position targetPosition = onlineUser.getPosition();
            players.forEach(playerName -> Teleport.builder(plugin, onlineUser)
                .setTeleporter(playerName)
                .setTarget(targetPosition)
                .toTeleport()
                .thenAccept(Teleport::execute));
        });
=======
        if (plugin.getSettings().doCrossServer()) {
            Message.builder()
                    .target(Message.TARGET_ALL)
                    .type(Message.Type.TELEPORT_TO_POSITION)
                    .payload(Payload.withPosition(targetPosition))
                    .build().send(plugin.getMessenger(), executor);
        }
>>>>>>> master

        plugin.getLocales().getLocale("teleporting_all_players")
                .ifPresent(executor::sendMessage);
    }

}
