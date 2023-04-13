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
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
<<<<<<< HEAD
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
=======
>>>>>>> master

public class TpaAllCommand extends InGameCommand {

    protected TpaAllCommand(@NotNull HuskHomes plugin) {
        super("tpaall", List.of(), "", plugin);
        setOperatorCommand(true);
    }

    @Override
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        if (plugin.getGlobalPlayerList().size() <= 1) {
            plugin.getLocales().getLocale("error_no_players_online")
                    .ifPresent(executor::sendMessage);
            return;
        }

        if (plugin.getManager().requests().isIgnoringRequests(executor)) {
            plugin.getLocales().getLocale("error_ignoring_teleport_requests")
<<<<<<< HEAD
                .ifPresent(onlineUser::sendMessage);
            return;
        }

        if (args.length != 0) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/tpaall")
                .ifPresent(onlineUser::sendMessage);
            return;
        }

        // Determine players to teleport and teleport them
        plugin.getCache().updatePlayerListCache(plugin, onlineUser).thenAccept(playerList -> {
            final List<String> players = plugin.getCache().players.stream()
                .filter(userName -> !userName.equalsIgnoreCase(onlineUser.username)).collect(Collectors.toList());
            if (players.isEmpty()) {
                plugin.getLocales().getLocale("error_no_players_online").ifPresent(onlineUser::sendMessage);
                return;
            }

            // Send a teleport request to every player
            final AtomicInteger counter = new AtomicInteger(0);
            final List<CompletableFuture<Void>> sentRequestsFuture = new ArrayList<>();
            players.forEach(playerName -> sentRequestsFuture.add(plugin.getRequestManager()
                .sendTeleportRequest(onlineUser, playerName, TeleportRequest.RequestType.TPA_HERE)
                .thenAccept(sent -> counter.addAndGet(sent.isPresent() ? 1 : 0))));

            // Send a message when all requests have been sent
            CompletableFuture.allOf(sentRequestsFuture.toArray(new CompletableFuture[0])).thenRun(() -> {
                if (counter.get() == 0) {
                    plugin.getLocales().getLocale("error_no_players_online")
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
                plugin.getLocales().getLocale("tpaall_request_sent", Integer.toString(counter.get()))
                    .ifPresent(onlineUser::sendMessage);
            });
        });
=======
                    .ifPresent(executor::sendMessage);
            return;
        }

        plugin.getManager().requests().sendTeleportAllRequest(executor);
        plugin.getLocales().getLocale("tpaall_request_sent")
                .ifPresent(executor::sendMessage);
>>>>>>> master
    }

}
