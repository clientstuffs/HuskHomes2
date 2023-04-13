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
import net.william278.desertwell.AboutMenu;
import net.william278.desertwell.UpdateChecker;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.config.Locales;
import net.william278.huskhomes.user.CommandUser;
import net.william278.paginedown.PaginatedList;
import org.jetbrains.annotations.NotNull;

<<<<<<< HEAD
import java.util.*;
import java.util.logging.Level;
=======
import java.util.List;
import java.util.Map;
>>>>>>> master
import java.util.stream.Collectors;

public class HuskHomesCommand extends Command implements TabProvider {

    private static final Map<String, Boolean> SUB_COMMANDS = Map.of(
            "about", false,
            "help", false,
            "reload", true,
            "update", true
    );

    private final UpdateChecker updateChecker;
    private final AboutMenu aboutMenu;

    protected HuskHomesCommand(@NotNull HuskHomes plugin) {
        super("huskhomes", List.of(), "[" + String.join("|", SUB_COMMANDS.keySet()) + "]", plugin);
        addAdditionalPermissions(SUB_COMMANDS);

        this.updateChecker = plugin.getUpdateChecker();
        this.aboutMenu = AboutMenu.create("HuskHomes")
<<<<<<< HEAD
            .withDescription("A powerful, intuitive and flexible teleportation suite")
            .withVersion(implementor.getPluginVersion())
            .addAttribution("Author",
                AboutMenu.Credit.of("William278").withDescription("Click to visit website").withUrl("https://william278.net"))
            .addAttribution("Contributors",
                AboutMenu.Credit.of("imDaniX").withDescription("Code, refactoring"),
                AboutMenu.Credit.of("Log1x").withDescription("Code"))
            .addAttribution("Translators",
                AboutMenu.Credit.of("SnivyJ").withDescription("Simplified Chinese (zh-cn)"),
                AboutMenu.Credit.of("ApliNi").withDescription("Simplified Chinese (zh-cn)"),
                AboutMenu.Credit.of("TonyPak").withDescription("Traditional Chinese (zh-tw)"),
                AboutMenu.Credit.of("davgo0103").withDescription("Traditional Chinese (zh-tw)"),
                AboutMenu.Credit.of("Villag3r_").withDescription("Italian (it-it)"),
                AboutMenu.Credit.of("ReferTV").withDescription("Polish (pl)"),
                AboutMenu.Credit.of("anchelthe").withDescription("Spanish (es-es)"),
                AboutMenu.Credit.of("Chiquis2005").withDescription("Spanish (es-es)"),
                AboutMenu.Credit.of("Ceddix").withDescription("German, (de-de)"),
                AboutMenu.Credit.of("Pukejoy_1").withDescription("Bulgarian (bg-bg)"))
            .addButtons(
                AboutMenu.Link.of("https://william278.net/docs/huskhomes").withText("Documentation").withIcon("⛏"),
                AboutMenu.Link.of("https://github.com/WiIIiam278/HuskHomes2/issues").withText("Issues").withIcon("❌").withColor("#ff9f0f"),
                AboutMenu.Link.of("https://discord.gg/tVYhJfyDWG").withText("Discord").withIcon("⭐").withColor("#6773f5"));
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length == 0) {
            sendAboutMenu(onlineUser);
            return;
        }
        if (args.length > 2) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/huskhomes [about|help|reload|update]")
                .ifPresent(onlineUser::sendMessage);
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "about": {
                sendAboutMenu(onlineUser);
                break;
            }
            case "help": {
                if (!onlineUser.hasPermission(Permission.COMMAND_HUSKHOMES_HELP.node)) {
                    plugin.getLocales().getLocale("error_no_permission")
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                        plugin.getLocales().getLocale("error_invalid_syntax", "/huskhomes help <page>")
                            .ifPresent(onlineUser::sendMessage);
                        return;
                    }
                }
                onlineUser.sendMessage(plugin.getCache().getCommandList(onlineUser,
                    plugin.getLocales(), plugin.getCommands(), plugin.getSettings().listItemsPerPage, page));
                break;
            }
            case "reload": {
                if (!onlineUser.hasPermission(Permission.COMMAND_HUSKHOMES_RELOAD.node)) {
                    plugin.getLocales().getLocale("error_no_permission")
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
                if (!plugin.reload()) {
                    onlineUser.sendMessage(new MineDown("[Error:](#ff3300) [Failed to reload the plugin. Check console for errors.](#ff7e5e)"));
                    return;
                }
                onlineUser.sendMessage(new MineDown("[HuskHomes](#00fb9a bold) &#00fb9a&| Reloaded config & message files."));
                break;
            }
            case "update": {
                if (!onlineUser.hasPermission(Permission.COMMAND_HUSKHOMES_UPDATE.node)) {
                    plugin.getLocales().getLocale("error_no_permission")
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
                plugin.getLatestVersionIfOutdated().thenAccept(newestVersion ->
                    newestVersion.ifPresentOrElse(
                        newVersion -> onlineUser.sendMessage(
                            new MineDown("[HuskHomes](#00fb9a bold) [| A new version of HuskHomes is available!"
                                         + " (v" + newVersion + " (Running: v" + plugin.getPluginVersion() + ")](#00fb9a)")),
                        () -> onlineUser.sendMessage(
                            new MineDown("[HuskHomes](#00fb9a bold) [| HuskHomes is up-to-date."
                                         + " (Running: v" + plugin.getPluginVersion() + ")](#00fb9a)"))));
                break;
            }
            case "migrate": {
                plugin.getLocales().getLocale("error_console_command_only")
                    .ifPresent(onlineUser::sendMessage);
                break;
            }
            default: {
                plugin.getLocales().getLocale("error_invalid_syntax", "/huskhomes [about|help|reload|update]")
                    .ifPresent(onlineUser::sendMessage);
                break;
            }
        }
    }

    @Override
    public void onConsoleExecute(@NotNull String[] args) {
        if (args.length == 0) {
            Arrays.stream(aboutMenu.toString().split("\n")).forEach(message ->
                plugin.getLoggingAdapter().log(Level.INFO, message));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "about": {
                Arrays.stream(aboutMenu.toString().split("\n")).forEach(message ->
                    plugin.getLoggingAdapter().log(Level.INFO, message));
                break;
            }
            case "help": {
                plugin.getLoggingAdapter().log(Level.INFO, "List of enabled console-executable commands:");
                plugin.getCommands()
                    .stream().filter(command -> command instanceof ConsoleExecutable)
                    .forEach(command -> plugin.getLoggingAdapter().log(Level.INFO,
                        command.command +
                        (command.command.length() < 16 ? " ".repeat(16 - command.command.length()) : "")
                        + " - " + command.getDescription()));
                break;
            }
            case "reload": {
                if (!plugin.reload()) {
                    plugin.getLoggingAdapter().log(Level.SEVERE, "Failed to reload the plugin.");
                    return;
                }
                plugin.getLoggingAdapter().log(Level.INFO, "Reloaded config & message files.");
                break;
            }
            case "update": {
                plugin.getLatestVersionIfOutdated().thenAccept(newestVersion ->
                    newestVersion.ifPresentOrElse(newVersion -> plugin.getLoggingAdapter().log(Level.WARNING,
                            "An update is available for HuskHomes, v" + newVersion
                            + " (Running v" + plugin.getPluginVersion() + ")"),
                        () -> plugin.getLoggingAdapter().log(Level.INFO,
                            "HuskHomes is up to date" +
                            " (Running v" + plugin.getPluginVersion() + ")")));
                break;
            }
            case "migrate": {
                if (args.length < 2) {
                    plugin.getLoggingAdapter().log(Level.INFO,
                        "Please choose a migrator, then run \"huskhomes migrate <migrator>\"");
                    logMigratorsList();
                    return;
                }
                final Optional<Migrator> selectedMigrator = plugin.getMigrators().stream().filter(availableMigrator ->
                    availableMigrator.getIdentifier().equalsIgnoreCase(args[1])).findFirst();
                selectedMigrator.ifPresentOrElse(migrator -> {
                    if (args.length < 3) {
                        plugin.getLoggingAdapter().log(Level.INFO, migrator.getHelpMenu());
                        return;
                    }
                    switch (args[2]) {
                        case "start":
                            migrator.start().thenAccept(succeeded -> {
                                if (succeeded) {
                                    plugin.getLoggingAdapter().log(Level.INFO, "Migration completed successfully!");
                                } else {
                                    plugin.getLoggingAdapter().log(Level.WARNING, "Migration failed!");
                                }
                            });
                        case "set":
                            migrator.handleConfigurationCommand(Arrays.copyOfRange(args, 3, args.length));
                        default:
                            plugin.getLoggingAdapter().log(Level.INFO,
                                "Invalid syntax. Console usage: \"huskhomes migrate " + args[1] + " <start/set>");
                    }
                }, () -> {
                    plugin.getLoggingAdapter().log(Level.INFO,
                        "Please specify a valid migrator.\n" +
                        "If a migrator is not available, please verify that you meet the prerequisites to use it.");
                    logMigratorsList();
                });
                break;
            }
        }
    }

    private void logMigratorsList() {
        plugin.getLoggingAdapter().log(Level.INFO,
            "List of available migrators:\nMigrator ID / Migrator Name:\n" +
            plugin.getMigrators().stream()
                .map(migrator -> migrator.getIdentifier() + " - " + migrator.getName())
                .collect(Collectors.joining("\n")));
    }

    private void sendAboutMenu(@NotNull OnlineUser onlineUser) {
        if (!onlineUser.hasPermission(Permission.COMMAND_HUSKHOMES_ABOUT.node)) {
            plugin.getLocales().getLocale("error_no_permission")
                .ifPresent(onlineUser::sendMessage);
=======
                .withDescription("A powerful, intuitive and flexible teleportation suite")
                .withVersion(plugin.getVersion())
                .addAttribution("Author",
                        AboutMenu.Credit.of("William278").withDescription("Click to visit website").withUrl("https://william278.net"))
                .addAttribution("Contributors",
                        AboutMenu.Credit.of("imDaniX").withDescription("Code, refactoring"),
                        AboutMenu.Credit.of("Log1x").withDescription("Code"))
                .addAttribution("Translators",
                        AboutMenu.Credit.of("SnivyJ").withDescription("Simplified Chinese (zh-cn)"),
                        AboutMenu.Credit.of("ApliNi").withDescription("Simplified Chinese (zh-cn)"),
                        AboutMenu.Credit.of("Wtq_").withDescription("Simplified Chinese (zh-cn)"),
                        AboutMenu.Credit.of("TonyPak").withDescription("Traditional Chinese (zh-tw)"),
                        AboutMenu.Credit.of("davgo0103").withDescription("Traditional Chinese (zh-tw)"),
                        AboutMenu.Credit.of("Villag3r_").withDescription("Italian (it-it)"),
                        AboutMenu.Credit.of("ReferTV").withDescription("Polish (pl)"),
                        AboutMenu.Credit.of("anchelthe").withDescription("Spanish (es-es)"),
                        AboutMenu.Credit.of("Chiquis2005").withDescription("Spanish (es-es)"),
                        AboutMenu.Credit.of("Ceddix").withDescription("German, (de-de)"),
                        AboutMenu.Credit.of("Pukejoy_1").withDescription("Bulgarian (bg-bg)"))
                .addButtons(
                        AboutMenu.Link.of("https://william278.net/docs/huskhomes").withText("Documentation").withIcon("⛏"),
                        AboutMenu.Link.of("https://github.com/WiIIiam278/HuskHomes2/issues").withText("Issues").withIcon("❌").withColor("#ff9f0f"),
                        AboutMenu.Link.of("https://discord.gg/tVYhJfyDWG").withText("Discord").withIcon("⭐").withColor("#6773f5"));
    }

    @Override
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        final String action = parseStringArg(args, 0).orElse("about");
        if (SUB_COMMANDS.containsKey(action) && !executor.hasPermission(getPermission(action))) {
            plugin.getLocales().getLocale("error_no_permission")
                    .ifPresent(executor::sendMessage);
>>>>>>> master
            return;
        }

        switch (action.toLowerCase()) {
            case "about" -> executor.sendMessage(aboutMenu.toMineDown());
            case "help" -> executor.sendMessage(getCommandList(executor)
                    .getNearestValidPage(parseIntArg(args, 1).orElse(1)));
            case "reload" -> {
                if (!plugin.loadConfigs()) {
                    executor.sendMessage(new MineDown("[Error:](#ff3300) [Failed to reload the plugin. Check console for errors.](#ff7e5e)"));
                    return;
                }
                executor.sendMessage(new MineDown("[HuskHomes](#00fb9a bold) &#00fb9a&| Reloaded config & message files."));
            }
            case "update" -> updateChecker.isUpToDate().thenAccept(upToDate -> {
                if (upToDate) {
                    plugin.getLocales().getLocale("up_to_date", plugin.getVersion().toString())
                            .ifPresent(executor::sendMessage);
                    return;
                }
                updateChecker.getLatestVersion().thenAccept(latest -> plugin.getLocales()
                        .getLocale("update_available", latest.toString(), plugin.getVersion().toString())
                        .ifPresent(executor::sendMessage));
            });
            default -> plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
        }
    }

    @NotNull
    private PaginatedList getCommandList(@NotNull CommandUser user) {
        return PaginatedList.of(plugin.getCommands().stream()
                        .filter(command -> user.hasPermission(command.getPermission()))
                        .map(command -> plugin.getLocales().getRawLocale("command_list_item",
                                        Locales.escapeText(command.getName()),
                                        Locales.escapeText(plugin.getLocales()
                                                .truncateText(command.getDescription(), 50)),
                                        Locales.escapeText(plugin.getLocales()
                                                .wrapText(command.getUsage() + "\n" + command.getDescription(), 40)))
                                .orElse(command.getName()))
                        .collect(Collectors.toList()),
                plugin.getLocales().getBaseList(Math.min(plugin.getSettings().getListItemsPerPage(), 6))
                        .setHeaderFormat(plugin.getLocales().getRawLocale("command_list_title").orElse(""))
                        .setItemSeparator("\n").setCommand("/huskhomes:huskhomes help")
                        .build());
    }

    @Override
<<<<<<< HEAD
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        if (args.length == 0 || args.length == 1) {
            return Arrays.stream(SUB_COMMANDS)
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
                .sorted().collect(Collectors.toList());
        }
        return Collections.emptyList();
=======
    @NotNull
    public List<String> suggest(@NotNull CommandUser user, @NotNull String[] args) {
        return args.length < 2 ? SUB_COMMANDS.keySet().stream().sorted().toList() : List.of();
>>>>>>> master
    }

}
