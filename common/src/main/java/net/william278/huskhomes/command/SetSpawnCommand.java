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
<<<<<<< HEAD
import net.william278.huskhomes.position.PositionMeta;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand extends CommandBase {
=======
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetSpawnCommand extends InGameCommand {
>>>>>>> master

    protected SetSpawnCommand(@NotNull HuskHomes plugin) {
        super("setspawn", List.of(), "", plugin);
        setOperatorCommand(true);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length > 0) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/setspawn")
                .ifPresent(onlineUser::sendMessage);
            return;
        }

        final Position position = onlineUser.getPosition();
        if (plugin.getSettings().crossServer && plugin.getSettings().globalSpawn) {
            plugin.getDatabase().getWarp(plugin.getSettings().globalSpawnName).thenApply(warp -> {
                if (warp.isPresent()) {
                    return plugin.getSavedPositionManager().updateWarpPosition(warp.get(), position);
                } else {
                    return plugin.getSavedPositionManager().setWarp(new PositionMeta(plugin.getSettings().globalSpawnName,
                            plugin.getLocales().getRawLocale("spawn_warp_default_description").orElse("")), position)
                        .thenApply(result -> result.resultType().successful);
                }
            }).thenAccept(result -> result.thenAccept(successful -> {
                if (successful) {
                    plugin.getLocales().getLocale("set_spawn_success")
                        .ifPresent(onlineUser::sendMessage);
                }
            }));
        } else {
            plugin.setServerSpawn(position);
            plugin.getLocales().getLocale("set_spawn_success")
                .ifPresent(onlineUser::sendMessage);
        }
=======
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        final Position position = executor.getPosition();
        try {
            if (plugin.getSettings().doCrossServer() && plugin.getSettings().isGlobalSpawn()) {
                final String warpName = plugin.getSettings().getGlobalSpawnName();
                plugin.getManager().warps().createWarp(warpName, position, true);
                plugin.getLocales().getRawLocale("spawn_warp_default_description")
                        .ifPresent(description -> plugin.getManager().warps().setWarpDescription(warpName, description));
            } else {
                plugin.setServerSpawn(position);
            }
        } catch (ValidationException e) {
            e.dispatchWarpError(executor, plugin, plugin.getSettings().getGlobalSpawnName());
            return;
        }

        plugin.getLocales().getLocale("set_spawn_success")
                .ifPresent(executor::sendMessage);
>>>>>>> master
    }

}
