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
import net.william278.huskhomes.config.Locales;
import net.william278.huskhomes.hook.EconomyHook;
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EditHomeCommand extends SavedPositionCommand<Home> {

    public EditHomeCommand(@NotNull HuskHomes plugin) {
        super("edithome", List.of(), Home.class, List.of("rename", "description", "relocate", "privacy"), plugin);
        addAdditionalPermissions(arguments.stream().collect(HashMap::new, (m, e) -> m.put(e, false), HashMap::putAll));
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length >= 1) {
            final String homeName = args[0];
            final String editOperation = args.length >= 2 ? args[1] : null;
            final String editArgs = getEditArguments(args);

            RegexUtil.matchDisambiguatedHomeIdentifier(homeName).ifPresentOrElse(
                homeIdentifier -> plugin.getDatabase().getUserDataByName(homeIdentifier.ownerName())
                    .thenAcceptAsync(optionalUserData -> optionalUserData.ifPresentOrElse(userData -> {
                            if (!userData.getUserUuid().equals(onlineUser.uuid)) {
                                if (!onlineUser.hasPermission(Permission.COMMAND_EDIT_HOME_OTHER.node)) {
                                    plugin.getLocales().getLocale("error_no_permission")
                                        .ifPresent(onlineUser::sendMessage);
                                    return;
                                }
                            }
                            plugin.getDatabase().getHome(userData.user(), homeIdentifier.homeName()).thenAcceptAsync(optionalHome -> {
                                if (optionalHome.isEmpty()) {
                                    plugin.getLocales().getLocale("error_home_invalid_other",
                                            homeIdentifier.ownerName(), homeIdentifier.homeName())
                                        .ifPresent(onlineUser::sendMessage);
                                    return;
                                }
                                editHome(optionalHome.get(), onlineUser, editOperation, editArgs);
                            });

                        },
                        () -> plugin.getLocales().getLocale("error_home_invalid_other",
                                homeIdentifier.ownerName(), homeIdentifier.homeName())
                            .ifPresent(onlineUser::sendMessage))),
                () -> plugin.getDatabase().getHome(onlineUser, homeName).thenAcceptAsync(optionalHome -> {
                    if (optionalHome.isEmpty()) {
                        plugin.getLocales().getLocale("error_home_invalid", homeName)
                            .ifPresent(onlineUser::sendMessage);
                        return;
                    }
                    editHome(optionalHome.get(), onlineUser, editOperation, editArgs);
                })
            );
        } else {
            plugin.getLocales().getLocale("error_invalid_syntax",
                    "/edithome <name> [" + String.join("|", EDIT_HOME_COMPLETIONS) + "] [args]")
                .ifPresent(onlineUser::sendMessage);
        }
    }

    /**
     * Perform the specified EditOperation on the specified home
     *
     * @param home          The home to edit
     * @param editor        The player who is editing the home
     * @param editOperation The edit operation to perform
     * @param editArgs      Arguments for the edit operation
     */
    private void editHome(@NotNull Home home, @NotNull OnlineUser editor,
                          @Nullable String editOperation, @Nullable String editArgs) {
        final AtomicBoolean showMenuFlag = new AtomicBoolean(false);
        final boolean otherOwner = !editor.equals(home.owner);

        if (editOperation == null) {
            getHomeEditorWindow(home, true, otherOwner,
                !otherOwner || editor.hasPermission(Permission.COMMAND_HOME_OTHER.node),
                editor.hasPermission(Permission.COMMAND_EDIT_HOME_PRIVACY.node))
                .forEach(editor::sendMessage);
=======
    public void execute(@NotNull CommandUser executor, @NotNull Home home, @NotNull String[] args) {
        final boolean ownerEditing = home.getOwner().equals(executor);
        if (!ownerEditing && !executor.hasPermission(getOtherPermission())) {
            plugin.getLocales().getLocale("error_no_permission")
                    .ifPresent(executor::sendMessage);
>>>>>>> master
            return;
        }

        final Optional<String> operation = parseStringArg(args, 0);
        if (operation.isEmpty()) {
            getHomeEditorWindow(home, !ownerEditing,
                    !ownerEditing || executor.hasPermission(getOtherPermission()),
                    executor.hasPermission(getPermission("privacy")))
                    .forEach(executor::sendMessage);
            return;
        }

<<<<<<< HEAD
        switch (editOperation.toLowerCase(Locale.ROOT)) {
            case "rename": {
                if (editArgs == null || editArgs.contains(Pattern.quote(" "))) {
                    plugin.getLocales().getLocale("error_invalid_syntax",
                            "/edithome <name> rename <new name>")
                        .ifPresent(editor::sendMessage);
                    return;
                }

                final String oldHomeName = home.meta.name;
                final String newHomeName = editArgs;
                plugin.getSavedPositionManager().updateHomeMeta(home, new PositionMeta(newHomeName, home.meta.description))
                    .thenAccept(renameResult -> {
                        final Optional<MineDown> message;
                        switch (renameResult.resultType()) {
                            case SUCCESS: {
                                if (home.owner.equals(editor)) {
                                    message = plugin.getLocales().getLocale("edit_home_update_name",
                                        oldHomeName, newHomeName);
                                } else {
                                    message = plugin.getLocales().getLocale("edit_home_update_name_other",
                                        home.owner.username, oldHomeName, newHomeName);
                                }
                                break;
                            }
                            case FAILED_DUPLICATE: {
                                message = plugin.getLocales().getLocale("error_home_name_taken");
                                break;
                            }
                            case FAILED_NAME_LENGTH: {
                                message = plugin.getLocales().getLocale("error_home_name_length");
                                break;
                            }
                            case FAILED_NAME_CHARACTERS: {
                                message = plugin.getLocales().getLocale("error_home_name_characters");
                                break;
                            }
                            default: {
                                message = plugin.getLocales().getLocale("error_home_description_characters");
                                break;
                            }
                        }
                        message.ifPresent(editor::sendMessage);
                    });
                break;
            }
            case "description": {
                final String oldHomeDescription = home.meta.description;
                final String newDescription = editArgs != null ? editArgs : "";

                plugin.getSavedPositionManager().updateHomeMeta(home, new PositionMeta(home.meta.name, newDescription))
                    .thenAccept(descriptionUpdateResult -> {
                        final Optional<MineDown> message;
                        switch (descriptionUpdateResult.resultType()) {
                            case SUCCESS: {
                                if (home.owner.equals(editor)) {
                                    message = plugin.getLocales().getLocale("edit_home_update_description",
                                        home.meta.name,
                                        oldHomeDescription.isBlank() ? plugin.getLocales()
                                            .getRawLocale("item_no_description").orElse("N/A") : oldHomeDescription,
                                        newDescription.isBlank() ? plugin.getLocales()
                                            .getRawLocale("item_no_description").orElse("N/A") : newDescription);
                                } else {
                                    message = plugin.getLocales().getLocale("edit_home_update_description_other",
                                        home.owner.username,
                                        home.meta.name,
                                        oldHomeDescription.isBlank() ? plugin.getLocales()
                                            .getRawLocale("item_no_description").orElse("N/A") : oldHomeDescription,
                                        newDescription.isBlank() ? plugin.getLocales()
                                            .getRawLocale("item_no_description").orElse("N/A") : newDescription);
                                }
                                break;
                            }
                            case FAILED_DESCRIPTION_LENGTH: {
                                message = plugin.getLocales().getLocale("error_home_description_length");
                                break;
                            }
                            case FAILED_DESCRIPTION_CHARACTERS: {
                                message = plugin.getLocales().getLocale("error_home_description_characters");
                                break;
                            }
                            default: {
                                message = plugin.getLocales().getLocale("error_home_name_characters");
                                break;
                            }
                        }
                        message.ifPresent(editor::sendMessage);
                    });
                break;
            }
            case "relocate":
                plugin.getSavedPositionManager().updateHomePosition(home, editor.getPosition()).thenRun(() -> {
                    if (home.owner.equals(editor)) {
                        editor.sendMessage(plugin.getLocales().getLocale("edit_home_update_location",
                            home.meta.name).orElse(new MineDown("")));
                    } else {
                        editor.sendMessage(plugin.getLocales().getLocale("edit_home_update_location_other",
                            home.owner.username, home.meta.name).orElse(new MineDown("")));
                    }

                    // Show the menu if the menu flag is set
                    if (showMenuFlag.get()) {
                        getHomeEditorWindow(home, false, otherOwner,
                            !otherOwner || editor.hasPermission(Permission.COMMAND_HOME_OTHER.node),
                            editor.hasPermission(Permission.COMMAND_EDIT_HOME_PRIVACY.node))
                            .forEach(editor::sendMessage);
                    }
                });
            case "privacy": {
                if (!editor.hasPermission(Permission.COMMAND_EDIT_HOME_PRIVACY.node)) {
                    plugin.getLocales().getLocale("error_no_permission")
                        .ifPresent(editor::sendMessage);
                    return;
                }
                final AtomicBoolean newIsPublic = new AtomicBoolean(!home.isPublic);
                if (editArgs != null && !editArgs.isBlank()) {
                    if (editArgs.equalsIgnoreCase("private")) {
                        newIsPublic.set(false);
                    } else if (editArgs.equalsIgnoreCase("public")) {
                        newIsPublic.set(true);
                    } else {
                        plugin.getLocales().getLocale("error_invalid_syntax",
                                "/edithome <name> privacy [private|public]")
                            .ifPresent(editor::sendMessage);
                        return;
                    }
                }
                final String privacyKeyedString = newIsPublic.get() ? "public" : "private";
                if (newIsPublic.get() == home.isPublic) {
                    plugin.getLocales().getLocale(
                            "error_edit_home_privacy_already_" + privacyKeyedString)
                        .ifPresent(editor::sendMessage);
                    return;
                }

                // Get the homes of the editor
                plugin.getDatabase().getHomes(editor).thenAccept(editorHomes -> {
                    // Perform checks if making the home public
                    if (newIsPublic.get() && !otherOwner) {
                        // Check against maximum public homes
                        final List<Home> existingPublicHomes = editorHomes.stream()
                            .filter(existingHome -> existingHome.isPublic).collect(Collectors.toList());
                        final int maxPublicHomes = editor.getMaxPublicHomes(plugin.getSettings().maxPublicHomes,
                            plugin.getSettings().stackPermissionLimits);
                        if (existingPublicHomes.size() >= maxPublicHomes) {
                            plugin.getLocales().getLocale("error_edit_home_maximum_public_homes",
                                    Integer.toString(maxPublicHomes))
                                .ifPresent(editor::sendMessage);
                            return;
                        }

                        // Check against economy
                        if (!plugin.validateEconomyCheck(editor, Settings.EconomyAction.MAKE_HOME_PUBLIC)) {
                            return;
                        }
                    }

                    // Execute the update
                    plugin.getSavedPositionManager().updateHomePrivacy(home, newIsPublic.get()).thenRun(() -> {
                        if (home.owner.equals(editor)) {
                            editor.sendMessage(plugin.getLocales().getLocale(
                                "edit_home_privacy_" + privacyKeyedString + "_success",
                                home.meta.name).orElse(new MineDown("")));
                        } else {
                            editor.sendMessage(plugin.getLocales().getLocale(
                                "edit_home_privacy_" + privacyKeyedString + "_success_other",
                                home.owner.username, home.meta.name).orElse(new MineDown("")));
                        }

                        // Perform necessary economy transaction
                        plugin.performEconomyTransaction(editor, Settings.EconomyAction.MAKE_HOME_PUBLIC);

                        // Show the menu if the menu flag is set
                        if (showMenuFlag.get()) {
                            getHomeEditorWindow(home, false, otherOwner,
                                !otherOwner || editor.hasPermission(Permission.COMMAND_HOME_OTHER.node),
                                editor.hasPermission(Permission.COMMAND_EDIT_HOME_PRIVACY.node))
                                .forEach(editor::sendMessage);
                        }
                    });
                });
                break;
            }
            default: {
                plugin.getLocales().getLocale("error_invalid_syntax",
                        "/edithome <name> [" + String.join("|", EDIT_HOME_COMPLETIONS) + "] [args]")
                    .ifPresent(editor::sendMessage);
                break;
            }
=======
        if (!arguments.contains(operation.get().toLowerCase())) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        switch (operation.get().toLowerCase()) {
            case "rename" -> setHomeName(executor, home, ownerEditing, args);
            case "description" -> setHomeDescription(executor, home, ownerEditing, args);
            case "relocate" -> setHomePosition(executor, home, ownerEditing);
            case "privacy" -> setHomePrivacy(executor, home, ownerEditing, args);
>>>>>>> master
        }
    }

    private void setHomeName(@NotNull CommandUser executor, @NotNull Home home, boolean ownerEditing, @NotNull String[] args) {
        final String oldName = home.getName();
        final Optional<String> optionalName = parseStringArg(args, 1);
        if (optionalName.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax",
                            "/edithome " + home.getName() + " rename <name>")
                    .ifPresent(executor::sendMessage);
            return;
        }

        home.getMeta().setName(optionalName.get());
        plugin.fireEvent(plugin.getHomeEditEvent(home, executor), (event) -> {
            final String newName = event.getHome().getName();
            try {
                plugin.getManager().homes().setHomeName(home, newName);
            } catch (ValidationException e) {
                e.dispatchHomeError(executor, false, plugin, newName);
                return;
            }

            if (ownerEditing) {
                plugin.getLocales().getLocale("edit_home_update_name", oldName, newName)
                        .ifPresent(executor::sendMessage);
            } else {
                plugin.getLocales().getLocale("edit_home_update_name_other", home.getOwner().getUsername(),
                                oldName, newName)
                        .ifPresent(executor::sendMessage);
            }
        });
    }

    private void setHomeDescription(@NotNull CommandUser executor, @NotNull Home home, boolean ownerEditing, @NotNull String[] args) {
        final String oldDescription = home.getMeta().getDescription();
        final Optional<String> optionalDescription = parseGreedyArguments(args);
        if (optionalDescription.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax",
                            "/edithome " + home.getName() + " description <text>")
                    .ifPresent(executor::sendMessage);
            return;
        }

        home.getMeta().setDescription(optionalDescription.get());
        plugin.fireEvent(plugin.getHomeEditEvent(home, executor), (event) -> {
            final String newDescription = event.getHome().getMeta().getDescription();
            try {
                plugin.getManager().homes().setHomeDescription(home, newDescription);
            } catch (ValidationException e) {
                e.dispatchHomeError(executor, false, plugin, newDescription);
                return;
            }

            if (ownerEditing) {
                plugin.getLocales().getLocale("edit_home_update_description", home.getName(), oldDescription, newDescription)
                        .ifPresent(executor::sendMessage);
            } else {
                plugin.getLocales().getLocale("edit_home_update_description_other", home.getOwner().getUsername(),
                                home.getName(), oldDescription, newDescription)
                        .ifPresent(executor::sendMessage);
            }
        });
    }

    private void setHomePosition(@NotNull CommandUser executor, @NotNull Home home, boolean ownerEditing) {
        if (!(executor instanceof OnlineUser user)) {
            plugin.getLocales().getLocale("error_in_game_only")
                    .ifPresent(executor::sendMessage);
            return;
        }

        home.update(user.getPosition());
        plugin.fireEvent(plugin.getHomeEditEvent(home, executor), (event) -> {
            try {
                plugin.getManager().homes().setHomePosition(home, home);
            } catch (ValidationException e) {
                e.dispatchHomeError(executor, false, plugin, home.getName());
                return;
            }

            if (ownerEditing) {
                plugin.getLocales().getLocale("edit_home_update_location", home.getName())
                        .ifPresent(executor::sendMessage);
            } else {
                plugin.getLocales().getLocale("edit_home_update_location_other", home.getOwner().getUsername(), home.getName())
                        .ifPresent(executor::sendMessage);
            }
        });
    }

    private void setHomePrivacy(@NotNull CommandUser executor, @NotNull Home home, boolean ownerEditing, @NotNull String[] args) {
        if (!executor.hasPermission(getPermission("privacy"))) {
            plugin.getLocales().getLocale("error_no_permission")
                    .ifPresent(executor::sendMessage);
            return;
        }

        // Check against economy
        if (executor instanceof OnlineUser user && !plugin.validateEconomyCheck(user, EconomyHook.Action.MAKE_HOME_PUBLIC)) {
            return;
        }

        // Set the home privacy
        home.setPublic(parseStringArg(args, 1)
                .map(String::toLowerCase).map("public"::equals)
                .orElse(!home.isPublic()));

        plugin.fireEvent(plugin.getHomeEditEvent(home, executor), (event) -> {
            try {
                plugin.getManager().homes().setHomePrivacy(event.getHome(), home.isPublic());
            } catch (ValidationException e) {
                int maxHomes = plugin.getManager().homes().getMaxPublicHomes(executor instanceof OnlineUser user ? user : null);
                e.dispatchHomeError(executor, false, plugin, Integer.toString(maxHomes));
                return;
            }

            // Perform transaction
            if (executor instanceof OnlineUser user) {
                plugin.performEconomyTransaction(user, EconomyHook.Action.MAKE_HOME_PUBLIC);
            }

            final String privacy = home.isPublic() ? "public" : "private";
            if (ownerEditing) {
                plugin.getLocales().getLocale("edit_home_privacy_" + privacy + "_success",
                                home.getMeta().getName())
                        .ifPresent(executor::sendMessage);
            } else {
                plugin.getLocales().getLocale("edit_home_privacy_" + privacy + "_success_other",
                                home.getOwner().getUsername(), home.getMeta().getName())
                        .ifPresent(executor::sendMessage);
            }
        });
    }

    /**
     * Get a formatted home editor chat window for a supplied {@link Home}
     *
     * @param home                    The home to display
     * @param otherViewer             If the viewer of the editor is not the homeowner
     * @param showTeleportButton      Whether to show the teleport "use" button
     * @param showPrivacyToggleButton Whether to show the home privacy toggle button
     * @return List of {@link MineDown} messages to send to the editor that form the menu
     */
    @NotNull
    private List<MineDown> getHomeEditorWindow(@NotNull Home home, boolean otherViewer,
                                               boolean showTeleportButton, boolean showPrivacyToggleButton) {
        return new ArrayList<>() {{
<<<<<<< HEAD
            if (showTitle) {
                if (!otherViewer) {
                    plugin.getLocales().getLocale("edit_home_menu_title", home.meta.name)
                        .ifPresent(this::add);
                } else {
                    plugin.getLocales().getLocale("edit_home_menu_title_other", home.owner.username, home.meta.name)
                        .ifPresent(this::add);
                }
            }

            plugin.getLocales().getLocale("edit_home_menu_metadata_" + (!home.isPublic ? "private" : "public"),
                    DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm")
                        .format(home.meta.creationTime.atZone(ZoneId.systemDefault())),
                    home.uuid.toString().split(Pattern.quote("-"))[0],
                    home.uuid.toString())
                .ifPresent(this::add);

            if (home.meta.description.length() > 0) {
                plugin.getLocales().getLocale("edit_home_menu_description",
                        home.meta.description.length() > 50
                            ? home.meta.description.substring(0, 49).trim() + "…" : home.meta.description,
                        plugin.getLocales().formatDescription(home.meta.description))
                    .ifPresent(this::add);
            }

            if (!plugin.getSettings().crossServer) {
                plugin.getLocales().getLocale("edit_home_menu_world", home.world.name)
                    .ifPresent(this::add);
            } else {
                plugin.getLocales().getLocale("edit_home_menu_world_server", home.world.name, home.server.name)
                    .ifPresent(this::add);
            }

            plugin.getLocales().getLocale("edit_home_menu_coordinates",
                    String.format("%.1f", home.x), String.format("%.1f", home.y), String.format("%.1f", home.z),
                    String.format("%.2f", home.yaw), String.format("%.2f", home.pitch))
                .ifPresent(this::add);
=======
            if (!otherViewer) {
                plugin.getLocales().getLocale("edit_home_menu_title", home.getMeta().getName())
                        .ifPresent(this::add);
            } else {
                plugin.getLocales().getLocale("edit_home_menu_title_other", home.getOwner().getUsername(), home.getMeta().getName())
                        .ifPresent(this::add);
            }

            plugin.getLocales().getLocale("edit_home_menu_metadata_" + (!home.isPublic() ? "private" : "public"),
                            DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm")
                                    .format(home.getMeta().getCreationTime().atZone(ZoneId.systemDefault())),
                            home.getUuid().toString().split(Pattern.quote("-"))[0],
                            home.getUuid().toString())
                    .ifPresent(this::add);

            if (home.getMeta().getDescription().length() > 0) {
                plugin.getLocales().getLocale("edit_home_menu_description",
                                plugin.getLocales().truncateText(home.getMeta().getDescription(), 50),
                                plugin.getLocales().wrapText(home.getMeta().getDescription(), 40))
                        .ifPresent(this::add);
            }

            if (!plugin.getSettings().doCrossServer()) {
                plugin.getLocales().getLocale("edit_home_menu_world", home.getWorld().getName())
                        .ifPresent(this::add);
            } else {
                plugin.getLocales().getLocale("edit_home_menu_world_server", home.getWorld().getName(), home.getServer())
                        .ifPresent(this::add);
            }

            plugin.getLocales().getLocale("edit_home_menu_coordinates",
                            String.format("%.1f", home.getX()), String.format("%.1f", home.getY()), String.format("%.1f", home.getZ()),
                            String.format("%.2f", home.getYaw()), String.format("%.2f", home.getPitch()))
                    .ifPresent(this::add);
>>>>>>> master

            final String formattedName = home.getOwner().getUsername() + "." + home.getMeta().getName();
            if (showTeleportButton) {
                plugin.getLocales().getLocale("edit_home_menu_use_buttons",
                        formattedName)
                    .ifPresent(this::add);
            }
            final String escapedName = Locales.escapeText(formattedName);
            plugin.getLocales().getRawLocale("edit_home_menu_manage_buttons", escapedName,
<<<<<<< HEAD
                    showPrivacyToggleButton ? plugin.getLocales()
                        .getRawLocale("edit_home_menu_privacy_button_"
                                      + (home.isPublic ? "private" : "public"), escapedName)
                        .orElse("") : "")
                .map(MineDown::new).ifPresent(this::add);
=======
                            showPrivacyToggleButton ? plugin.getLocales()
                                    .getRawLocale("edit_home_menu_privacy_button_"
                                            + (home.isPublic() ? "private" : "public"), escapedName)
                                    .orElse("") : "")
                    .map(MineDown::new).ifPresent(this::add);
>>>>>>> master
            plugin.getLocales().getLocale("edit_home_menu_meta_edit_buttons",
                    formattedName)
                .ifPresent(this::add);
        }};
    }

<<<<<<< HEAD
    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        if (user == null) {
            return Collections.emptyList();
        }
        switch (args.length) {
            case 0:
            case 1:
                return plugin.getCache().homes.getOrDefault(user.uuid, new ArrayList<>())
                    .stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
                    .sorted()
                    .collect(Collectors.toList());
            case 2:
                return Arrays.stream(EDIT_HOME_COMPLETIONS)
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .sorted()
                    .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }
=======
>>>>>>> master
}
