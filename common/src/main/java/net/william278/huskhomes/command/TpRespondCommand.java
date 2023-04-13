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
import java.util.Locale;
import java.util.stream.Collectors;
=======
import java.util.Optional;
>>>>>>> master

/**
 * A command used for responding to tp requests - can either be a /tpaccept or /tpdecline command, controlled by the
 * acceptRequestCommand flag
 */
public class TpRespondCommand extends InGameCommand implements UserListTabProvider {

    private final boolean accept;

    protected TpRespondCommand(@NotNull HuskHomes plugin, boolean accept) {
        super(accept ? "tpaccept" : "tpdecline", List.of(), "[player]", plugin);
        this.accept = accept;
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length > 1) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/" + command + " [player]")
                .ifPresent(onlineUser::sendMessage);
=======
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        final Optional<String> requesterName = parseStringArg(args, 0);
        if (requesterName.isPresent()) {
            plugin.getManager().requests().respondToTeleportRequestBySenderName(executor, requesterName.get(), accept);
>>>>>>> master
            return;
        }
        plugin.getManager().requests().respondToTeleportRequest(executor, accept);
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
