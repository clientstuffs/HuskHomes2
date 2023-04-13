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
import net.william278.huskhomes.command.BackCommand;
import net.william278.huskhomes.config.Settings;
<<<<<<< HEAD
import net.william278.huskhomes.network.Payload;
import net.william278.huskhomes.network.Request;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.player.User;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.util.Permission;
=======
import net.william278.huskhomes.event.ITeleportEvent;
import net.william278.huskhomes.hook.EconomyHook;
import net.william278.huskhomes.network.Message;
import net.william278.huskhomes.network.Payload;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ThrowingConsumer;
>>>>>>> master
import org.jetbrains.annotations.NotNull;

<<<<<<< HEAD
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
=======
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
>>>>>>> master

public class Teleport {

<<<<<<< HEAD
    public static final String CANCEL_METADATA_KEY = "TeleportCancel";

    /**
     * The {@link User} who is doing the teleporting
     */
    @Nullable
    public final User teleporter;
    /**
     * The {@link OnlineUser} on this server executing the teleport.
     * </p>
     * Can be the same as the {@link #teleporter}, but not necessarily
     */
    @NotNull
    public final OnlineUser executor;
    /**
     * The target {@link Position} the teleporter should be teleported to
     */
    @Nullable
    public final Position target;
    /**
     * The {@link TeleportType type} of the teleport
     */
    @NotNull
    public final TeleportType type;
    /**
     * {@link Settings.EconomyAction}s to be checked against the {@link #executor executor}'s balance
     */
    @NotNull
    public final Set<Settings.EconomyAction> economyActions;
    /**
     * Whether to update the {@link #teleporter teleporter}'s last position (i.e. their {@code /back} position)
     */
    public final boolean updateLastPosition;

    @Nullable
    public final String queueType;

    /**
     * <b>Internal</b> - Instance of the implementing HuskHomes plugin
     */
    @NotNull
    private final HuskHomes plugin;

    /**
     * <b>Internal</b> - use TeleportBuilder to instantiate a teleport
     */
    protected Teleport(@Nullable User teleporter, @NotNull OnlineUser executor, @Nullable Position target,
                       @NotNull TeleportType type, @NotNull Set<Settings.EconomyAction> economyActions,
                       final boolean updateLastPosition, @Nullable final String queueType, @NotNull HuskHomes plugin) {
        this.teleporter = teleporter;
=======
    protected final HuskHomes plugin;
    protected final OnlineUser executor;
    protected final Teleportable teleporter;
    protected final Target target;
    protected final Type type;
    protected final List<EconomyHook.Action> economyActions;
    private final boolean async;
    protected final boolean updateLastPosition;

    protected Teleport(@NotNull OnlineUser executor, @NotNull Teleportable teleporter, @NotNull Target target,
                       @NotNull Type type, boolean updateLastPosition, @NotNull List<EconomyHook.Action> actions,
                       @NotNull HuskHomes plugin) {
        this.plugin = plugin;
>>>>>>> master
        this.executor = executor;
        this.teleporter = teleporter;
        this.target = target;
        this.type = type;
<<<<<<< HEAD
        this.economyActions = economyActions;
        this.plugin = plugin;
        this.updateLastPosition = updateLastPosition;
        this.queueType = queueType;
=======
        this.economyActions = actions;
        this.async = plugin.getSettings().doAsynchronousTeleports();
        this.updateLastPosition = updateLastPosition && plugin.getCommand(BackCommand.class)
                .map(command -> executor.hasPermission(command.getPermission()))
                .orElse(false);
>>>>>>> master
    }

    @NotNull
    public static TeleportBuilder builder(@NotNull HuskHomes plugin) {
        return new TeleportBuilder(plugin);
    }

<<<<<<< HEAD
    /**
     * Execute this teleport
     *
     * @return A {@link CompletableFuture} that completes when the teleport is finished
     */
    public CompletableFuture<CompletedTeleport> execute() {
        // Validate the teleporter
        if (teleporter == null) {
            return CompletableFuture.completedFuture(TeleportResult.FAILED_TELEPORTER_NOT_RESOLVED)
                .thenApply(resultState -> CompletedTeleport.from(resultState, this));
        }

        // Validate the target
        if (target == null) {
            return CompletableFuture.completedFuture(TeleportResult.FAILED_TARGET_NOT_RESOLVED)
                .thenApply(resultState -> CompletedTeleport.from(resultState, this));
        }

        // Check economy actions
        for (Settings.EconomyAction economyAction : economyActions) {
            if (!plugin.validateEconomyCheck(executor, economyAction)) {
                return CompletableFuture.completedFuture(TeleportResult.CANCELLED_ECONOMY)
                    .thenApply(resultState -> CompletedTeleport.from(resultState, this));
=======
    public void execute() throws TeleportationException {
        final Optional<OnlineUser> localTeleporter = resolveLocalTeleporter();

        // Validate economy actions
        validateEconomyActions();

        // Teleport a user on another server
        if (localTeleporter.isEmpty()) {
            final Username teleporter = (Username) this.teleporter;
            if (!plugin.getSettings().doCrossServer()) {
                throw new TeleportationException(TeleportationException.Type.TELEPORTER_NOT_FOUND);
>>>>>>> master
            }

            fireEvent((event) -> {
                executeEconomyActions();
                if (target instanceof Username username) {
                    Message.builder()
                            .type(Message.Type.TELEPORT_TO_NETWORKED_USER)
                            .target(teleporter.name())
                            .payload(Payload.withString(username.name()))
                            .build().send(plugin.getMessenger(), executor);
                    return;
                }

                Message.builder()
                        .type(Message.Type.TELEPORT_TO_POSITION)
                        .target(teleporter.name())
                        .payload(Payload.withPosition((Position) target))
                        .build().send(plugin.getMessenger(), executor);
            });
            return;
        }

<<<<<<< HEAD
        if (shouldCancel()) {
            return CompletableFuture.completedFuture(CompletedTeleport.from(TeleportResult.CANCELLED, this));
        }

        if (this.queue()) {
            return CompletableFuture.completedFuture(TeleportResult.COMPLETED_CROSS_SERVER)
                .thenApply(resultState -> CompletedTeleport.from(resultState, this));
        }

        // Run the teleport and apply economy actions
        return run(teleporter)
            .thenApply(resultState -> CompletedTeleport.from(resultState, this))
            .thenApply(result -> {
                if (teleporter instanceof OnlineUser) {
                    final var user = (OnlineUser) teleporter;

                    finish(user, result);
                }
                return result;
            });
    }

    protected boolean queue() {
        if (!this.plugin.getSettings().queue) {
            return false;
        }
        if (this.target == null) {
            return false;
        }
        if (this.queueType == null || !(this.teleporter instanceof OnlineUser)) {
            return false;
        }
        final var onlineUser = (OnlineUser) this.teleporter;
        if (onlineUser.getPosition().server.equals(target.server)) {
            return false;
        }
        if (onlineUser.hasPermission(Permission.QUEUE_BYPASS_ALL.node) ||
            onlineUser.hasPermission(Permission.QUEUE_BYPASS.formatted(target.server.name))) {
            return false;
        }
        this.plugin.getTeleportQueue().join(this, this.queueType);
        return true;
    }

    /**
     * Run the teleport, either locally or over the network
     *
     * @param teleporter The {@link User} who is doing the teleporting
     * @return A {@link CompletableFuture} that completes when the teleport is finished
     */
    private CompletableFuture<TeleportResult> run(@NotNull User teleporter) {
        return teleporter instanceof OnlineUser
            ? teleportLocalUser((OnlineUser) teleporter)
            : teleportNetworkedUser();
    }

    /**
     * Finish the teleport, finalize economy transactions if successful and send message
     *
     * @param teleporter The {@link OnlineUser} who was teleported
     * @param result     The {@link TeleportResult} of the teleport
     */
    private void finish(@NotNull OnlineUser teleporter, @NotNull CompletedTeleport result) {
        if (result.successful()) {
            // Complete economy transactions
            economyActions.forEach(economyAction -> plugin.performEconomyTransaction(executor, economyAction));

            // Play sound effect if successful
            plugin.getSettings().getSoundEffect(Settings.SoundEffectAction.TELEPORTATION_COMPLETE)
                .ifPresent(teleporter::playSound);
        }

        // Send result message
        result.sendResultMessage(plugin.getLocales(), teleporter);
    }

    /**
     * Teleport a local user
     *
     * @param teleporter The teleporter
     * @return A {@link CompletableFuture} that completes when the teleport is finished
     */
    private CompletableFuture<TeleportResult> teleportLocalUser(@NotNull OnlineUser teleporter) {
        assert target != null;

        // Dispatch the teleport event and update the player's last position
        plugin.getEventDispatcher().dispatchTeleportEvent(this);
        if (updateLastPosition && !plugin.getSettings().backCommandSaveOnTeleportEvent && type == TeleportType.TELEPORT) {
            plugin.getDatabase().setLastPosition(teleporter, teleporter.getPosition());
        }

        // If the target position is local, finalize economy transactions and teleport the player
        if (!plugin.getSettings().crossServer || target.server.equals(plugin.getServerName())) {
            return teleporter.teleportLocally(target, plugin.getSettings().asynchronousTeleports);
        }

        // If the target position is on another server, execute a cross-server teleport
        final CompletableFuture<TeleportResult> teleportFuture = new CompletableFuture<>();
        plugin.getDatabase()
            .setCurrentTeleport(teleporter, this)
            .thenApply(ignored -> plugin
                .getMessenger()
                .sendPlayer(teleporter, target.server)
                .thenApply(completed -> completed
                    ? TeleportResult.COMPLETED_CROSS_SERVER
                    : TeleportResult.FAILED_INVALID_SERVER))
            .orTimeout(10, TimeUnit.SECONDS)
            .exceptionally(throwable -> {
                plugin.getLoggingAdapter().log(Level.WARNING, "Cross-server teleport timed out for " + teleporter.username);
                plugin.getDatabase().setCurrentTeleport(teleporter, null);
                return CompletableFuture.completedFuture(TeleportResult.FAILED_INVALID_SERVER);
            })
            .thenAccept(result -> result.thenAcceptAsync(teleportFuture::complete));
        return teleportFuture;
    }

    /**
     * Teleport a user not on this server but on the proxy network
     *
     * @return A {@link CompletableFuture} that completes when the teleport is finished.
     * Successful cross-server teleports will return {@link TeleportResult#COMPLETED_CROSS_SERVER}.
     * @implNote Cross-server teleports will return with a {@link TeleportResult#FAILED_INVALID_SERVER} result if the
     * target server is not online, or connection timed out
     */
    private CompletableFuture<TeleportResult> teleportNetworkedUser() {
        assert target != null;
        assert teleporter != null;

        // Send a network message to a user on another server to teleport them to the target position
        return Request.builder()
            .withType(Request.MessageType.TELEPORT_TO_POSITION_REQUEST)
            .withPayload(Payload.withPosition(target))
            .withTargetPlayer(teleporter.username)
            .build().send(executor, plugin)
            .thenApply(result -> {
                if (result.isPresent()) {
                    final Request reply = result.get();
                    if (reply.getPayload().resultState != null) {
                        return reply.getPayload().resultState;
                    }
                }
                return TeleportResult.FAILED_TELEPORTER_NOT_RESOLVED;
            });
    }

    protected boolean shouldCancel() {
        if (teleporter == null) {
            return false;
        }
        if (!(teleporter instanceof OnlineUser)) {
            return false;
        }
        final var user = (OnlineUser) teleporter;
        return user.getMetadata(CANCEL_METADATA_KEY).filter(Boolean.class::isInstance).map(Boolean.class::cast).orElse(false);
    }
}
=======
        // Teleport a local user
        final OnlineUser teleporter = localTeleporter.get();
        if (target instanceof Username username) {
            final Optional<OnlineUser> localTarget = username.name().equals("@s")
                    ? Optional.of(executor) : username.findLocally(plugin);
            if (localTarget.isPresent()) {
                fireEvent((event) -> {
                    executeEconomyActions();
                    if (updateLastPosition) {
                        plugin.getDatabase().setLastPosition(teleporter, teleporter.getPosition());
                    }

                    teleporter.teleportLocally(localTarget.get().getPosition(), async);
                    this.displayTeleportingComplete(teleporter);
                });
                return;
            }

            if (plugin.getSettings().doCrossServer()) {
                fireEvent((event) -> {
                    executeEconomyActions();
                    Message.builder()
                            .type(Message.Type.TELEPORT_TO_NETWORKED_POSITION)
                            .target(username.name())
                            .build().send(plugin.getMessenger(), executor);
                });
                return;
            }

            throw new TeleportationException(TeleportationException.Type.TARGET_NOT_FOUND);
        }

        fireEvent((event) -> {
            executeEconomyActions();
            if (updateLastPosition) {
                plugin.getDatabase().setLastPosition(teleporter, teleporter.getPosition());
            }

            final Position target = (Position) this.target;
            if (!plugin.getSettings().doCrossServer() || target.getServer().equals(plugin.getServerName())) {
                teleporter.teleportLocally(target, async);
                this.displayTeleportingComplete(teleporter);
                return;
            }

            plugin.getDatabase().setCurrentTeleport(teleporter, this);
            plugin.getMessenger().changeServer(teleporter, target.getServer());
        });
    }

    @NotNull
    private Optional<OnlineUser> resolveLocalTeleporter() throws TeleportationException {
        if (this.teleporter instanceof Username username) {
            return username.findLocally(plugin);
        }
        return Optional.of((OnlineUser) this.teleporter);
    }

    public void displayTeleportingComplete(@NotNull OnlineUser teleporter) {
        plugin.getLocales().getLocale("teleporting_complete")
                .ifPresent(teleporter::sendMessage);
        plugin.getSettings().getSoundEffect(Settings.SoundEffectAction.TELEPORTATION_COMPLETE)
                .ifPresent(teleporter::playSound);
    }

    // Fire the teleport event
    private void fireEvent(@NotNull ThrowingConsumer<ITeleportEvent> afterFired) {
        plugin.fireEvent(plugin.getTeleportEvent(this), afterFired);
    }

    // Check economy actions
    protected void validateEconomyActions() throws TeleportationException {
        if (economyActions.stream()
                .map(action -> plugin.validateEconomyCheck(executor, action))
                .anyMatch(result -> !result)) {
            throw new TeleportationException(TeleportationException.Type.ECONOMY_ACTION_FAILED);
        }
    }

    // Perform transactions on economy actions
    private void executeEconomyActions() {
        economyActions.forEach(action -> plugin.performEconomyTransaction(executor, action));
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @NotNull
    public Teleportable getTeleporter() {
        return teleporter;
    }

    @NotNull
    public Target getTarget() {
        return target;
    }

    /**
     * Represents the type of teleport being used
     */
    public enum Type {
        TELEPORT(0),
        RESPAWN(1),
        BACK(2);

        private final int typeId;

        Type(final int typeId) {
            this.typeId = typeId;
        }

        /**
         * Returns a {@link Type} by its type id.
         *
         * @param typeId The type id of the {@link Type} to return.
         * @return The {@link Type} of the given type id.
         */
        public static Optional<Type> getTeleportType(int typeId) {
            return Arrays.stream(values())
                    .filter(type -> type.getTypeId() == typeId)
                    .findFirst();
        }

        public int getTypeId() {
            return typeId;
        }
    }
}
>>>>>>> master
