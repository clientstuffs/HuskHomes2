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

package net.william278.huskhomes.teleport;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.hook.EconomyHook;
import net.william278.huskhomes.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleportBuilder {
    private final HuskHomes plugin;
<<<<<<< HEAD

    /**
     * The executor of the teleport; the one who triggered the teleport to happen;
     * whom may not necessarily the person being <i>teleported</i>
     */
    @NotNull
    private final OnlineUser executor;
    /**
     * List of {@link Settings.EconomyAction}s to check against.
     * <p>
     * Note that these are checked against the <i>{@link #executor executor}</i> of the teleport;
     * not necessarily the one doing the teleporting
     */
    private final Set<Settings.EconomyAction> economyActions = new HashSet<>();
    /**
     * The teleporter; the one who is being teleported, to be resolved before construction
     */
    private CompletableFuture<User> teleporter;
    /**
     * The target position for the teleporter, to be resolved before construction
     */
    private CompletableFuture<Position> target;
    /**
     * The type of teleport. Defaults to {@link TeleportType#TELEPORT}
     */
    private TeleportType type = TeleportType.TELEPORT;

    /**
     * Whether this teleport should update the user's last position (i.e. their {@code /back} position)
     */
=======
    private OnlineUser executor;
    private Teleportable teleporter;
    private Target target;
>>>>>>> master
    private boolean updateLastPosition = true;
    private Teleport.Type type = Teleport.Type.TELEPORT;
    private List<EconomyHook.Action> economyActions = List.of();

<<<<<<< HEAD
    @Nullable
    private String queueType;

    protected TeleportBuilder(@NotNull HuskHomes plugin, @NotNull OnlineUser executor) {
=======
    protected TeleportBuilder(@NotNull HuskHomes plugin) {
>>>>>>> master
        this.plugin = plugin;
    }

    @NotNull
    public Teleport toTeleport() throws IllegalStateException {
        validateTeleport();
        return new Teleport(executor, teleporter, target, type, updateLastPosition, economyActions, plugin);
    }

    @NotNull
    public TimedTeleport toTimedTeleport() throws TeleportationException, IllegalStateException {
        validateTeleport();
        if (!(teleporter instanceof OnlineUser onlineTeleporter)) {
            throw new IllegalStateException("Teleporter must be an OnlineUser for timed teleportation");
        }
        return new TimedTeleport(executor, onlineTeleporter, target, type,
                plugin.getSettings().getTeleportWarmupTime(), updateLastPosition, economyActions, plugin);
    }

    private void validateTeleport() throws TeleportationException {
        if (teleporter == null) {
            throw new TeleportationException(TeleportationException.Type.TELEPORTER_NOT_FOUND);
        }
        if (executor == null) {
            if (teleporter instanceof OnlineUser onlineUser) {
                executor = onlineUser;
            } else {
                executor = ((Username) teleporter).findLocally(plugin)
                        .orElseThrow(() -> new TeleportationException(TeleportationException.Type.TELEPORTER_NOT_FOUND));
            }
        }
        if (target == null) {
            throw new TeleportationException(TeleportationException.Type.TARGET_NOT_FOUND);
        }
    }

    @NotNull
    public TeleportBuilder executor(@NotNull OnlineUser executor) {
        this.executor = executor;
        return this;
    }

<<<<<<< HEAD
    /**
     * Set the person being teleported as the username of a player, which will attempt to be resolved
     * into a user at the time of construction
     *
     * @param teleporterUsername The username of the player who is doing the teleporting
     * @return The {@link TeleportBuilder} instance
     */
    public TeleportBuilder setTeleporter(@NotNull String teleporterUsername) {
        this.teleporter = CompletableFuture.supplyAsync(() -> plugin
            .findOnlinePlayer(teleporterUsername)
            .map(onlineUser -> (User) onlineUser)
            .or(() -> {
                if (plugin.getSettings().crossServer) {
                    return plugin.getMessenger()
                        .findPlayer(executor, teleporterUsername).join()
                        .map(username -> new User(UUID.randomUUID(), username));
                }
                return Optional.empty();
            })
            .orElse(null));
=======
    @NotNull
    public TeleportBuilder teleporter(@NotNull Teleportable teleporter) {
        this.teleporter = teleporter;
>>>>>>> master
        return this;
    }

    @NotNull
    public TeleportBuilder teleporter(@NotNull String teleporter) {
        this.teleporter = Teleportable.username(teleporter);
        return this;
    }

    @NotNull
    public TeleportBuilder target(@NotNull Target target) {
        this.target = target;
        return this;
    }

    @NotNull
    public TeleportBuilder target(@NotNull String target) {
        this.target = Target.username(target);
        return this;
    }

    @NotNull
    public TeleportBuilder updateLastPosition(boolean updateLastPosition) {
        this.updateLastPosition = updateLastPosition;
        return this;
    }

<<<<<<< HEAD
    public TeleportBuilder setQueueType(@NotNull final String queueType) {
        this.queueType = queueType;
        return this;
    }

    /**
     * Resolve the teleporter and target, and construct as an instantly-completing {@link Teleport}
     *
     * @return The constructed {@link Teleport}
     */
    public CompletableFuture<Teleport> toTeleport() {
        return CompletableFuture.supplyAsync(() -> {
            final User teleporter = this.teleporter.join();
            final Position target = this.target.join();

            return new Teleport(teleporter, executor, target, type, economyActions, updateLastPosition, queueType, plugin);
        }).exceptionally(e -> {
            plugin.getLoggingAdapter().log(Level.SEVERE, "Failed to create teleport", e);
            return null;
        });
    }

    /**
     * Resolve the teleporter and target, and construct as a {@link TimedTeleport}
     *
     * @return The constructed {@link TimedTeleport}
     */
    public CompletableFuture<TimedTeleport> toTimedTeleport() {
        return CompletableFuture.supplyAsync(() -> {
            final User teleporter = this.teleporter.join();
            final Position target = this.target.join();
            final int warmupTime = plugin.getSettings().teleportWarmupTime;

            if (!(teleporter instanceof OnlineUser)) {
                throw new IllegalStateException("Timed teleports can only be executed for local users");
            }
            final var onlineUser = (OnlineUser) teleporter;

            return new TimedTeleport(onlineUser, executor, target, type, warmupTime, economyActions, updateLastPosition, queueType, plugin);
        }).exceptionally(e -> {
            plugin.getLoggingAdapter().log(Level.SEVERE, "Failed to create timed teleport", e);
            return null;
        });
    }

    /**
     * Gets the position of a player by their username, including players on other servers
     *
     * @param playerName the username of the player being requested
     * @return future optionally supplying the player's position, if the player could be found
     */
    private CompletableFuture<Optional<Position>> getPlayerPosition(@NotNull String playerName) {
        final Optional<OnlineUser> localPlayer = plugin.findOnlinePlayer(playerName);
        if (localPlayer.isPresent()) {
            return CompletableFuture.supplyAsync(() -> Optional.of(localPlayer.get().getPosition()));
        }
        if (plugin.getSettings().crossServer) {
            return plugin.getMessenger()
                .findPlayer(executor, playerName)
                .thenApplyAsync(foundPlayer -> {
                    if (foundPlayer.isEmpty()) {
                        return Optional.empty();
                    }
                    return Request.builder()
                        .withType(Request.MessageType.POSITION_REQUEST)
                        .withTargetPlayer(playerName)
                        .build().send(executor, plugin)
                        .thenApply(reply -> reply.map(message -> message.getPayload().position)).join();
                });
        }
        return CompletableFuture.supplyAsync(Optional::empty);
    }

=======
    @NotNull
    public TeleportBuilder economyActions(@NotNull EconomyHook.Action... economyActions) {
        this.economyActions = List.of(economyActions);
        return this;
    }

    @NotNull
    public TeleportBuilder type(@NotNull Teleport.Type type) {
        this.type = type;
        return this;
    }
>>>>>>> master
}
