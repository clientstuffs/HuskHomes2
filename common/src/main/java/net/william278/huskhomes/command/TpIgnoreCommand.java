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

import de.themoep.minedown.adventure.MineDown;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.manager.RequestsManager;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.user.SavedUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TpIgnoreCommand extends InGameCommand {

    protected TpIgnoreCommand(@NotNull HuskHomes plugin) {
        super("tpignore", List.of(), "", plugin);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length != 0) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/tpignore")
                .ifPresent(onlineUser::sendMessage);
            return;
        }
=======
    public void execute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        final RequestsManager manager = plugin.getManager().requests();
        final boolean isIgnoringRequests = !manager.isIgnoringRequests(onlineUser);
>>>>>>> master

        plugin.editUserData(onlineUser, (SavedUser user) -> user.setIgnoringTeleports(isIgnoringRequests));

<<<<<<< HEAD
        // Update value on the database and send a message | todo: Clean this up
        plugin.getDatabase().getUserData(onlineUser.uuid)
            .thenAcceptAsync(userData -> userData.ifPresent(data -> plugin.getDatabase()
                .updateUserData(new UserData(onlineUser, data.homeSlots(), isIgnoringRequests, data.rtpCooldown()))
                .thenRun(() -> plugin.getLocales().getRawLocale("tpignore_toggle_" + (isIgnoringRequests ? "on" : "off"),
                        plugin.getLocales().getRawLocale("tpignore_toggle_button").orElse(""))
                    .ifPresent(locale -> onlineUser.sendMessage(new MineDown(locale))))));
=======
        plugin.getLocales().getRawLocale("tpignore_toggle_" + (isIgnoringRequests ? "on" : "off"),
                        plugin.getLocales().getRawLocale("tpignore_toggle_button").orElse(""))
                .map(MineDown::new)
                .ifPresent(onlineUser::sendMessage);
>>>>>>> master
    }
}
