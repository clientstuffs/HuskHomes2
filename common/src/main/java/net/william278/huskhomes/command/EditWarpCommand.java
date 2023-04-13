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
<<<<<<< HEAD
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.PositionMeta;
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.util.Permission;
=======
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ValidationException;
>>>>>>> master
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EditWarpCommand extends SavedPositionCommand<Warp> {

    public EditWarpCommand(@NotNull HuskHomes plugin) {
        super("editwarp", List.of(), Warp.class, List.of("rename", "description", "relocate"), plugin);
        setOperatorCommand(true);
        addAdditionalPermissions(arguments.stream().collect(HashMap::new, (m, e) -> m.put(e, false), HashMap::putAll));
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length >= 1) {
            final String warpName = args[0];
            final String editOperation = args.length >= 2 ? args[1] : null;
            final String editArgs = getEditArguments(args);

            plugin.getDatabase().getWarp(warpName).thenAcceptAsync(optionalWarp -> {
                if (optionalWarp.isEmpty()) {
                    plugin.getLocales().getLocale("error_warp_invalid", warpName)
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
                editWarp(optionalWarp.get(), onlineUser, editOperation, editArgs);
            });
        } else {
            plugin.getLocales().getLocale("error_invalid_syntax",
                    "/editwarp <name> [" + String.join("|", EDIT_WARP_COMPLETIONS) + "] [args]")
                .ifPresent(onlineUser::sendMessage);
        }
    }

    /**
     * Perform the specified EditOperation on the specified warp
     *
     * @param warp          The warp to edit
     * @param editor        The player who is editing the warp
     * @param editOperation The edit operation to perform
     * @param editArgs      Arguments for the edit operation
     */
    private void editWarp(@NotNull Warp warp, @NotNull OnlineUser editor,
                          @Nullable String editOperation, @Nullable String editArgs) {
        final AtomicBoolean showMenuFlag = new AtomicBoolean(false);

        if (editOperation == null) {
            getWarpEditorWindow(warp, true, editor.hasPermission(Permission.COMMAND_WARP.node))
                .forEach(editor::sendMessage);
=======
    public void execute(@NotNull CommandUser executor, @NotNull Warp warp, @NotNull String[] args) {
        final Optional<String> operation = parseStringArg(args, 0);
        if (operation.isEmpty()) {
            getWarpEditorWindow(warp).forEach(executor::sendMessage);
>>>>>>> master
            return;
        }

        if (!arguments.contains(operation.get().toLowerCase())) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

<<<<<<< HEAD
        switch (editOperation.toLowerCase(Locale.ROOT)) {
            case "rename": {
                if (editArgs == null || editArgs.contains(Pattern.quote(" "))) {
                    plugin.getLocales().getLocale("error_invalid_syntax",
                            "/editwarp <name> rename <new name>")
                        .ifPresent(editor::sendMessage);
                    return;
                }

                final String oldWarpName = warp.meta.name;
                final String newWarpName = editArgs;
                plugin.getSavedPositionManager().updateWarpMeta(warp, new PositionMeta(newWarpName, warp.meta.description))
                    .thenAccept(renameResult -> {
                        final Optional<MineDown> message;
                        switch (renameResult.resultType()) {
                            case SUCCESS: {
                                message = plugin.getLocales().getLocale("edit_warp_update_name", oldWarpName, newWarpName);
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
                        message.ifPresent(editor::sendMessage);
                    });
                break;
            }
            case "description": {
                final String oldWarpDescription = warp.meta.description;
                final String newDescription = editArgs != null ? editArgs : "";

                plugin.getSavedPositionManager().updateWarpMeta(warp, new PositionMeta(warp.meta.name, newDescription))
                    .thenAccept(descriptionUpdateResult -> {
                        final Optional<MineDown> message;
                        switch (descriptionUpdateResult.resultType()) {
                            case SUCCESS: {
                                message = plugin.getLocales().getLocale("edit_warp_update_description",
                                    warp.meta.name,
                                    oldWarpDescription.isBlank() ? plugin.getLocales()
                                        .getRawLocale("item_no_description").orElse("N/A") : oldWarpDescription,
                                    newDescription.isBlank() ? plugin.getLocales()
                                        .getRawLocale("item_no_description").orElse("N/A") : newDescription);
                                break;
                            }
                            case FAILED_DESCRIPTION_LENGTH: {
                                message = plugin.getLocales().getLocale("error_warp_description_length");
                                break;
                            }
                            case FAILED_DESCRIPTION_CHARACTERS: {
                                message = plugin.getLocales().getLocale("error_warp_description_characters");
                                break;
                            }
                            default: {
                                message = plugin.getLocales().getLocale("error_warp_name_characters");
                                break;
                            }
                        }
                        message.ifPresent(editor::sendMessage);
                    });
                break;
            }
            case "relocate": {
                plugin.getSavedPositionManager().updateWarpPosition(warp, editor.getPosition()).thenRun(() -> {
                    editor.sendMessage(plugin.getLocales().getLocale("edit_warp_update_location",
                        warp.meta.name).orElse(new MineDown("")));

                    // Show the menu if the menu flag is set
                    if (showMenuFlag.get()) {
                        getWarpEditorWindow(warp, false, editor.hasPermission(Permission.COMMAND_WARP.node))
                            .forEach(editor::sendMessage);
                    }
                });
                break;
            }
            default: {
                plugin.getLocales().getLocale("error_invalid_syntax",
                        "/editwarp <name> [" + String.join("|", EDIT_WARP_COMPLETIONS) + "] [args]")
                    .ifPresent(editor::sendMessage);
                break;
            }
=======
        switch (operation.get().toLowerCase()) {
            case "rename" -> setWarpName(executor, warp, args);
            case "description" -> setWarpDescription(executor, warp, args);
            case "relocate" -> setWarpPosition(executor, warp);
>>>>>>> master
        }
    }

    private void setWarpName(@NotNull CommandUser executor, @NotNull Warp warp, @NotNull String[] args) {
        final String oldName = warp.getName();
        final Optional<String> optionalName = parseStringArg(args, 1);
        if (optionalName.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax",
                            "/editwarp " + warp.getName() + " rename <name>")
                    .ifPresent(executor::sendMessage);
            return;
        }

        warp.getMeta().setName(optionalName.get());
        plugin.fireEvent(plugin.getWarpEditEvent(warp, executor), (event) -> {
            try {
                plugin.getManager().warps().setWarpName(warp, warp.getName());
            } catch (ValidationException e) {
                e.dispatchWarpError(executor, plugin, warp.getName());
                return;
            }

            plugin.getLocales().getLocale("edit_warp_update_name", oldName, optionalName.get())
                    .ifPresent(executor::sendMessage);
        });
    }

    private void setWarpDescription(@NotNull CommandUser executor, @NotNull Warp warp, @NotNull String[] args) {
        final Optional<String> optionalDescription = parseGreedyArguments(args);
        if (optionalDescription.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax",
                            "/editwarp " + warp.getName() + " description <text>")
                    .ifPresent(executor::sendMessage);
            return;
        }

        warp.getMeta().setDescription(optionalDescription.get());
        plugin.fireEvent(plugin.getWarpEditEvent(warp, executor), (event) -> {
            try {
                plugin.getManager().warps().setWarpDescription(warp, warp.getMeta().getDescription());
            } catch (ValidationException e) {
                e.dispatchWarpError(executor, plugin, warp.getMeta().getDescription());
                return;
            }

            plugin.getLocales().getLocale("edit_warp_update_description", warp.getName(), warp.getMeta().getDescription())
                    .ifPresent(executor::sendMessage);
        });
    }

    private void setWarpPosition(@NotNull CommandUser executor, @NotNull Warp warp) {
        if (!(executor instanceof OnlineUser user)) {
            plugin.getLocales().getLocale("error_in_game_only")
                    .ifPresent(executor::sendMessage);
            return;
        }

        warp.update(user.getPosition());
        plugin.fireEvent(plugin.getWarpEditEvent(warp, executor), (event) -> {
            try {
                plugin.getManager().warps().setWarpPosition(warp, warp);
            } catch (ValidationException e) {
                e.dispatchWarpError(executor, plugin, warp.getName());
                return;
            }

            plugin.getLocales().getLocale("edit_warp_update_location", warp.getName())
                    .ifPresent(executor::sendMessage);
        });
    }

    /**
     * Get a formatted warp editor chat window for a supplied {@link Warp}
     *
     * @param warp The warp to display
     * @return List of {@link MineDown} messages to send to the editor that form the menu
     */
    @NotNull
    private List<MineDown> getWarpEditorWindow(@NotNull Warp warp) {
        return new ArrayList<>() {{
<<<<<<< HEAD
            if (showTitle) {
                plugin.getLocales().getLocale("edit_warp_menu_title", warp.meta.name)
                    .ifPresent(this::add);
            }

            plugin.getLocales().getLocale("edit_warp_menu_metadata",
                    DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm")
                        .format(warp.meta.creationTime.atZone(ZoneId.systemDefault())),
                    warp.uuid.toString().split(Pattern.quote("-"))[0],
                    warp.uuid.toString())
                .ifPresent(this::add);
=======
            plugin.getLocales().getLocale("edit_warp_menu_title", warp.getMeta().getName())
                    .ifPresent(this::add);

            plugin.getLocales().getLocale("edit_warp_menu_metadata",
                            DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm")
                                    .format(warp.getMeta().getCreationTime().atZone(ZoneId.systemDefault())),
                            warp.getUuid().toString().split(Pattern.quote("-"))[0],
                            warp.getUuid().toString())
                    .ifPresent(this::add);
>>>>>>> master

            if (warp.getMeta().getDescription().length() > 0) {
                plugin.getLocales().getLocale("edit_warp_menu_description",
<<<<<<< HEAD
                        warp.meta.description.length() > 50
                            ? warp.meta.description.substring(0, 49).trim() + "…" : warp.meta.description,
                        plugin.getLocales().formatDescription(warp.meta.description))
                    .ifPresent(this::add);
=======
                                plugin.getLocales().truncateText(warp.getMeta().getDescription(), 50),
                                plugin.getLocales().wrapText(warp.getMeta().getDescription(), 40))
                        .ifPresent(this::add);
>>>>>>> master
            }

            if (!plugin.getSettings().doCrossServer()) {
                plugin.getLocales().getLocale("edit_warp_menu_world", warp.getWorld().getName()).ifPresent(this::add);
            } else {
                plugin.getLocales().getLocale("edit_warp_menu_world_server", warp.getWorld().getName(), warp.getServer()).ifPresent(this::add);
            }

            plugin.getLocales().getLocale("edit_warp_menu_coordinates",
<<<<<<< HEAD
                    String.format("%.1f", warp.x), String.format("%.1f", warp.y), String.format("%.1f", warp.z),
                    String.format("%.2f", warp.yaw), String.format("%.2f", warp.pitch))
                .ifPresent(this::add);

            if (showTeleportButton) {
                plugin.getLocales().getLocale("edit_warp_menu_use_buttons", warp.meta.name)
                    .ifPresent(this::add);
            }
            plugin.getLocales().getLocale("edit_warp_menu_manage_buttons", warp.meta.name)
                .ifPresent(this::add);
            plugin.getLocales().getLocale("edit_warp_menu_meta_edit_buttons", warp.meta.name)
                .ifPresent(this::add);
        }};
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        switch (args.length) {
            case 0:
            case 1:
                return plugin.getCache().warps
                    .stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
                    .sorted()
                    .collect(Collectors.toList());
            case 2:
                return Arrays.stream(EDIT_WARP_COMPLETIONS)
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .sorted()
                    .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }
=======
                            String.format("%.1f", warp.getX()), String.format("%.1f", warp.getY()), String.format("%.1f", warp.getZ()),
                            String.format("%.2f", warp.getYaw()), String.format("%.2f", warp.getPitch()))
                    .ifPresent(this::add);

            plugin.getLocales().getLocale("edit_warp_menu_use_buttons", warp.getMeta().getName())
                    .ifPresent(this::add);
            plugin.getLocales().getLocale("edit_warp_menu_manage_buttons", warp.getMeta().getName())
                    .ifPresent(this::add);
            plugin.getLocales().getLocale("edit_warp_menu_meta_edit_buttons", warp.getMeta().getName())
                    .ifPresent(this::add);
        }};
    }

>>>>>>> master
}
