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
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ValidationException;
import org.jetbrains.annotations.NotNull;

<<<<<<< HEAD
import java.util.Optional;

public class SetWarpCommand extends CommandBase {
=======
public class SetWarpCommand extends SetPositionCommand {
>>>>>>> master

    protected SetWarpCommand(@NotNull HuskHomes plugin) {
        super("setwarp", plugin);
        setOperatorCommand(true);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length == 1) {
            setWarp(onlineUser, args[0]);
        } else {
            plugin.getLocales().getLocale("error_invalid_syntax", "/setwarp <name>")
                .ifPresent(onlineUser::sendMessage);
        }
    }

    private void setWarp(@NotNull OnlineUser onlineUser, @NotNull String warpName) {
        plugin.getSavedPositionManager().setWarp(
            new PositionMeta(warpName, ""), onlineUser.getPosition()).thenAccept(setResult -> {
            final Optional<MineDown> message;
            switch (setResult.resultType()) {
                case SUCCESS: {
                    assert setResult.savedPosition().isPresent();
                    message = plugin.getLocales().getLocale("set_warp_success",
                        setResult.savedPosition().get().meta.name);
                    break;
                }
                case SUCCESS_OVERWRITTEN: {
                    assert setResult.savedPosition().isPresent();
                    message = plugin.getLocales().getLocale("edit_warp_update_location",
                        setResult.savedPosition().get().meta.name);
                    break;
                }
                case FAILED_DUPLICATE: {
                    message = plugin.getLocales().getLocale("error_warp_name_taken");
                    break;
                }
                case FAILED_NAME_LENGTH: {
                    message = plugin.getLocales().getLocale("error_warp_name_length");
                    break;
                }
                case FAILED_NAME_CHARACTERS: {
                    message = plugin.getLocales().getLocale("error_warp_name_characters");
                    break;
                }
                default: {
                    message = plugin.getLocales().getLocale("error_warp_description_characters");
                    break;
                }
            }
            message.ifPresent(onlineUser::sendMessage);
        });
    }

=======
    protected void execute(@NotNull OnlineUser setter, @NotNull String name) {
        plugin.fireEvent(plugin.getWarpCreateEvent(name, setter.getPosition(), setter), (event) -> {
            try {
                plugin.getManager().warps().createWarp(event.getName(), event.getPosition());
            } catch (ValidationException e) {
                e.dispatchWarpError(setter, plugin, event.getName());
                return;
            }
            plugin.getLocales().getLocale("set_warp_success", event.getName())
                    .ifPresent(setter::sendMessage);
        });
    }
>>>>>>> master
}
