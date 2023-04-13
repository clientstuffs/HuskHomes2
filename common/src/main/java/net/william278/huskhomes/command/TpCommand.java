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
import net.william278.huskhomes.teleport.*;
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TpCommand extends Command implements TabProvider {

    protected TpCommand(@NotNull HuskHomes plugin) {
        super("tp", List.of("tpo"), "[<player|position>] [target]", plugin);
        addAdditionalPermissions(Map.of("coordinates", true));
        setOperatorCommand(true);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        // Ensure a valid target was found
        final Optional<TeleportTarget> teleportTargetOptional = getTeleportTarget(args, onlineUser);
        if (teleportTargetOptional.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/tp <target> [destination]")
                .ifPresent(onlineUser::sendMessage);
            return;
        }

        // Determine the player to teleport
        final var teleportTarget = teleportTargetOptional.get();

        final String targetPlayerToTeleport = ((teleportTarget instanceof TargetPlayer) && args.length == 2)
            ? args[0] : ((teleportTarget instanceof TargetPosition)
            ? args.length > 3 ? (isCoordinate(args[1]) && isCoordinate(args[2]) && isCoordinate(args[3]) ? args[0] : onlineUser.username)
            : onlineUser.username : onlineUser.username);

        // Find the online user to teleport
        plugin.getCache().updatePlayerListCache(plugin, onlineUser).thenRun(() -> {
            // Get the list of potential teleports, filtered against vanished players, but ensuring the executor is in the list
            final Set<String> players = plugin.getCache().players;
            players.add(onlineUser.username);

            // Find the player to teleport
            final String playerToTeleport = players.stream()
                .filter(user -> user.equalsIgnoreCase(targetPlayerToTeleport)).findFirst()
                .or(() -> Optional.ofNullable(targetPlayerToTeleport.equals("@s") ? onlineUser.username : null))
                .or(() -> plugin.getCache().players.stream().filter(user -> user.toLowerCase(Locale.ROOT).startsWith(targetPlayerToTeleport)).findFirst())
                .orElse(null);

            // Ensure the player to teleport exists
            if (playerToTeleport == null) {
                plugin.getLocales().getLocale("error_player_not_found", targetPlayerToTeleport)
                    .ifPresent(onlineUser::sendMessage);
                return;
            }

            // Ensure the user has permission to teleport the player to teleport
            if (!playerToTeleport.equals(onlineUser.username)) {
                if (!onlineUser.hasPermission(Permission.COMMAND_TP_OTHER.node)) {
                    plugin.getLocales().getLocale("error_no_permission")
                        .ifPresent(onlineUser::sendMessage);
                    return;
                }
            }

            // Execute the teleport
            if (teleportTarget instanceof TargetPlayer) {
                final var targetPlayer = (TargetPlayer) teleportTarget;

                // Carry out the teleport to a player, by name
                Teleport.builder(plugin, onlineUser)
                    .setTeleporter(playerToTeleport)
                    .setTarget(targetPlayer.playerName)
                    .toTeleport()
                    .thenAccept(teleport -> teleport.execute().thenAccept(result -> {
                        if (result.successful()) {
                            result.getTeleporter()
                                .flatMap(teleporter -> plugin.getLocales().getLocale("teleporting_other_complete",
                                    teleporter.username, targetPlayer.playerName))
                                .ifPresent(onlineUser::sendMessage);
                        } else {
                            plugin.getLocales().getLocale("error_player_not_found", targetPlayer.playerName)
                                .ifPresent(onlineUser::sendMessage);
                        }
                    }));
            } else if (teleportTarget instanceof TargetPosition) {
                final var targetPosition = (TargetPosition) teleportTarget;

                // Handle coordinate teleport targets
                if (!onlineUser.hasPermission(Permission.COMMAND_TP_TO_COORDINATES.node)) {
                    plugin.getLocales().getLocale("error_no_permission").ifPresent(onlineUser::sendMessage);
                    return;
                }

                // Carry out the teleport to a position
                Teleport.builder(plugin, onlineUser)
                    .setTeleporter(playerToTeleport)
                    .setTarget(targetPosition.position)
                    .toTeleport()
                    .thenAccept(teleport -> teleport.execute().thenAccept(result -> {
                        if (!result.successful() || playerToTeleport.equalsIgnoreCase(onlineUser.username) || result.getDestination().isEmpty()) {
                            return;
                        }
                        final Position destination = result.getDestination().get();
                        result.getTeleporter()
                            .flatMap(teleporter -> plugin.getLocales()
                                .getLocale("teleporting_other_complete_position", teleporter.username,
                                    Integer.toString((int) destination.x),
                                    Integer.toString((int) destination.y),
                                    Integer.toString((int) destination.z)))
                            .ifPresent(onlineUser::sendMessage);
                    }));
=======
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                if (!(executor instanceof OnlineUser user)) {
                    plugin.getLocales().getLocale("error_in_game_only")
                            .ifPresent(executor::sendMessage);
                    return;
                }

                this.execute(executor, user, Target.username(args[0]), args);
>>>>>>> master
            }
            case 2 -> this.execute(executor, Teleportable.username(args[0]), Target.username(args[1]), args);
            default -> {
                final Position basePosition = getBasePosition(executor);
                Optional<Position> target = executor.hasPermission(getPermission("coordinates"))
                        ? parsePositionArgs(basePosition, args, 0) : Optional.empty();
                if (target.isPresent()) {
                    if (!(executor instanceof OnlineUser user)) {
                        plugin.getLocales().getLocale("error_in_game_only")
                                .ifPresent(executor::sendMessage);
                        return;
                    }

                    this.execute(executor, user, target.get(), args);
                    return;
                }

                target = executor.hasPermission(getPermission("coordinates"))
                        ? parsePositionArgs(basePosition, args, 1) : Optional.empty();
                if (target.isPresent() && args.length >= 1) {
                    this.execute(executor, Teleportable.username(args[0]), target.get(), args);
                    return;
                }

                plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                        .ifPresent(executor::sendMessage);
            }
        }
    }

<<<<<<< HEAD
    /**
     * Determines the teleport target from a set of arguments, which may be comprised of usernames and/or position
     * coordinates with a world and server.
     *
     * @param args       The arguments to parse.
     * @param relativeTo The position to use as a relative reference. This is used for relative coordinate handling
     *                   (e.g. {@code ~-10 ~ ~20})
     * @return The teleport target, if it could be parsed, otherwise and empty {@link Optional}.
     */
    @NotNull
    private Optional<TeleportTarget> getTeleportTarget(@NotNull String[] args, @NotNull OnlineUser relativeTo) {
        if (args.length == 1 || args.length == 2) {
            return Optional.of(new TargetPlayer(args[args.length - 1]));
        }
        if (args.length > 2 && args.length < 7) {
            final Optional<TeleportTarget> targetPosition = Position.parse(args, relativeTo.getPosition())
                .map(TargetPosition::new);
            return targetPosition.or(() -> Position.parse(Arrays.copyOfRange(args, 1, args.length),
                relativeTo.getPosition()).map(TargetPosition::new));
        }
        return Optional.empty();
    }

    /**
     * Determines if a string is a valid (relative) position coordinate double target.
     *
     * @param coordinate The string to check.
     * @return {@code true} if the string is a valid coordinate, otherwise {@code false}.
     */
    private boolean isCoordinate(@NotNull String coordinate) {
=======
    // Execute a teleport
    private void execute(@NotNull CommandUser executor, @NotNull Teleportable teleportable, @NotNull Target target,
                         @NotNull String[] args) {
        // Build and execute the teleport
        final TeleportBuilder builder = Teleport.builder(plugin)
                .teleporter(teleportable)
                .target(target);
>>>>>>> master
        try {
            if (executor instanceof OnlineUser user) {
                builder.executor(user);
            }
            builder.toTeleport().execute();
        } catch (TeleportationException e) {
            e.displayMessage(executor, plugin, args);
            return;
        }

        // Display teleport completion message
        final String teleporterName = teleportable instanceof OnlineUser user
                ? user.getUsername() : ((Username) teleportable).name();
        if (target instanceof Position position) {
            plugin.getLocales().getLocale("teleporting_other_complete_position", teleporterName,
                            Integer.toString((int) position.getX()), Integer.toString((int) position.getY()),
                            Integer.toString((int) position.getZ()))
                    .ifPresent(executor::sendMessage);
        } else {
            plugin.getLocales().getLocale("teleporting_other_complete", teleporterName, ((Username) target).name())
                    .ifPresent(executor::sendMessage);
        }
    }

    @Override
<<<<<<< HEAD
    public void onConsoleExecute(@NotNull String[] args) {
        if (args.length < 2 || args.length > 6) {
            plugin.getLoggingAdapter().log(Level.WARNING, "Invalid syntax. Usage: tp <player> <destination>");
            return;
        }
        final OnlineUser playerToTeleport = plugin.findOnlinePlayer(args[0]).orElse(null);
        if (playerToTeleport == null) {
            plugin.getLoggingAdapter().log(Level.WARNING, "Player not found: " + args[0]);
            return;
        }
        final TeleportTarget teleportTarget;
        if (args.length == 2) {
            teleportTarget = new TargetPlayer(args[1]);
        } else {
            try {
                teleportTarget = new TargetPosition(new Position(
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2]),
                    Double.parseDouble(args[3]),
                    0f, 0f,
                    args.length >= 5 ? new World(args[4], UUID.randomUUID()) : plugin.getWorlds().get(0),
                    args.length == 6 ? new Server(args[5]) : plugin.getServerName()));
            } catch (NumberFormatException e) {
                plugin.getLoggingAdapter().log(Level.WARNING, "Invalid syntax. Usage: tp <player> <x> <y> <z> [world] [server]");
                return;
            }
        }

        // Execute the console teleport
        final TeleportBuilder builder = Teleport.builder(plugin, playerToTeleport)
            .setTeleporter(playerToTeleport.username);
        if (teleportTarget instanceof TargetPlayer) {
            final var targetPlayer = (TargetPlayer) teleportTarget;

            builder.setTarget(targetPlayer.playerName);
        } else {
            builder.setTarget(((TargetPosition) teleportTarget).position);
        }
        builder.toTeleport().thenAccept(teleport -> teleport.execute().thenAccept(result -> {
            if (result.successful()) {
                plugin.getLoggingAdapter().log(Level.INFO, "Successfully teleported " + playerToTeleport.username);
            } else {
                plugin.getLoggingAdapter().log(Level.WARNING, "Failed to teleport " + playerToTeleport.username + " to " + teleportTarget);
            }
        }));
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        final boolean serveCoordinateCompletions = user != null && user.hasPermission(Permission.COMMAND_TP_TO_COORDINATES.node);
=======
    @NotNull
    public final List<String> suggest(@NotNull CommandUser user, @NotNull String[] args) {
        final Position basePosition = getBasePosition(user);
        final boolean serveCoordinateCompletions = user.hasPermission(getPermission("coordinates"));
>>>>>>> master
        switch (args.length) {
            case 0:
            case 1: {
                final ArrayList<String> completions = new ArrayList<>();
                completions.addAll(serveCoordinateCompletions
<<<<<<< HEAD
                    ? List.of("~", "~ ~", "~ ~ ~",
                    Integer.toString((int) user.getPosition().x),
                    ((int) user.getPosition().x + " " + (int) user.getPosition().y),
                    ((int) user.getPosition().x + " " + (int) user.getPosition().y + " " + (int) user.getPosition().z))
                    : Collections.emptyList());
                completions.addAll(plugin.getCache().players);
=======
                        ? List.of("~", "~ ~", "~ ~ ~",
                        Integer.toString((int) basePosition.getX()),
                        ((int) basePosition.getX() + " " + (int) basePosition.getY()),
                        ((int) basePosition.getX() + " " + (int) basePosition.getY() + " " + (int) basePosition.getZ()))
                        : List.of());
                completions.addAll(plugin.getPlayerList());
>>>>>>> master
                return completions.stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
                    .sorted().collect(Collectors.toList());
            }
            case 2: {
                final ArrayList<String> completions = new ArrayList<>();
                if (isCoordinate(args, 0)) {
                    completions.addAll(List.of("~", Integer.toString((int) basePosition.getY())));
                    completions.addAll(List.of("~ ~", (int) basePosition.getY() + " " + (int) basePosition.getZ()));
                } else {
                    completions.addAll(serveCoordinateCompletions
<<<<<<< HEAD
                        ? List.of("~", "~ ~", "~ ~ ~",
                        Integer.toString((int) user.getPosition().x),
                        ((int) user.getPosition().x + " " + (int) user.getPosition().y),
                        ((int) user.getPosition().x + " " + (int) user.getPosition().y + " " + (int) user.getPosition().z))
                        : Collections.emptyList());
                    completions.addAll(plugin.getCache().players);
=======
                            ? List.of("~", "~ ~", "~ ~ ~",
                            Integer.toString((int) basePosition.getX()),
                            ((int) basePosition.getX() + " " + (int) basePosition.getY()),
                            ((int) basePosition.getX() + " " + (int) basePosition.getY() + " " + (int) basePosition.getZ()))
                            : List.of());
                    completions.addAll(plugin.getPlayerList());
>>>>>>> master
                }
                return completions.stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .sorted().collect(Collectors.toList());
            }
            case 3: {
                final ArrayList<String> completions = new ArrayList<>();
                if (isCoordinate(args, 1) && isCoordinate(args, 2)) {
                    if (!serveCoordinateCompletions) {
                        return completions;
                    }
                    completions.addAll(List.of("~", Integer.toString((int) basePosition.getZ())));
                } else if (isCoordinate(args, 1)) {
                    if (!serveCoordinateCompletions) {
                        return completions;
                    }
                    completions.addAll(List.of("~", Integer.toString((int) basePosition.getY())));
                    completions.addAll(List.of("~ ~", (int) basePosition.getY() + " " + (int) basePosition.getZ()));
                }
                return completions.stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .sorted().collect(Collectors.toList());
            }
            case 4: {
                final ArrayList<String> completions = new ArrayList<>();
                if (isCoordinate(args, 1) && isCoordinate(args, 2) && !isCoordinate(args, 0)) {
                    if (!serveCoordinateCompletions) {
                        return completions;
                    }
                    completions.addAll(List.of("~", Integer.toString((int) basePosition.getZ())));
                }
                return completions.stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[3].toLowerCase(Locale.ROOT)))
                    .sorted().collect(Collectors.toList());
            }
<<<<<<< HEAD
            default:
                return Collections.emptyList();
=======
            default -> {
                return List.of();
            }
>>>>>>> master
        }
    }

    private boolean isCoordinate(@NotNull String[] args, int index) {
        return parseCoordinateArg(args, index, 0d).isPresent();
    }

}
