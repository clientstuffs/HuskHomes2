package net.william278.huskhomes.queue;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.grpc.GrpcClient;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.proto.Definition;
import net.william278.huskhomes.proto.Queue;
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.teleport.TimedTeleport;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class TeleportQueue {

    @NotNull
    private final HuskHomes implementor;

    public TeleportQueue(@NotNull final HuskHomes implementor) {
        this.implementor = implementor;
    }

    public void initialize() {
        GrpcClient.initiateQueue();
        final var requestBuilder = Queue.UpdateSettings.Request
            .newBuilder()
            .setQueueStarts(this.implementor.getSettings().queueStarts);
        if (this.implementor.getSettings().queueReminderActionbar) {
            this.implementor.getLocales()
                .getLocale("queue_reminder_action_bar")
                .map(MineDown::toComponent)
                .map(GsonComponentSerializer.gson()::serialize)
                .ifPresent(requestBuilder::setActionbar);
        }
        if (this.implementor.getSettings().queueReminderMessage) {
            this.implementor.getLocales()
                .getLocale("queue_reminder")
                .map(MineDown::toComponent)
                .map(GsonComponentSerializer.gson()::serialize)
                .ifPresent(requestBuilder::setMessage);
        }
        if (this.implementor.getSettings().queueReminderOldQueue) {
            this.implementor.getLocales()
                .getLocale("queue_canceled_overlapping_queue")
                .map(MineDown::toComponent)
                .map(GsonComponentSerializer.gson()::serialize)
                .ifPresent(requestBuilder::setOldQueueMessage);
        }
        if (this.implementor.getSettings().queueReminderFinishQueue) {
            this.implementor.getLocales()
                .getLocale("queue_finish_success")
                .map(MineDown::toComponent)
                .map(GsonComponentSerializer.gson()::serialize)
                .ifPresent(requestBuilder::setFinishQueueMessage);
        }
        GrpcClient.queue().updateSettings(requestBuilder.build());
    }

    public void join(@NotNull final Teleport teleport, @NotNull final String type) {
        final var destination = Objects.requireNonNull(teleport.target, "Something went wrong!");
        final var onlineUser = Objects.requireNonNull((OnlineUser) teleport.teleporter, "Something went wrong!");
        onlineUser.putMetadata(TimedTeleport.CANCEL_METADATA_KEY, true);
        this.implementor.getDatabase().setCurrentTeleport(onlineUser, teleport);
        final var user = Definition.User.newBuilder().setName(onlineUser.username).setUuid(onlineUser.uuid.toString()).build();
        final var positionBuilder = Definition.NetworkPosition.newBuilder()
            .setServer(destination.server.name);
        if (destination.world != null) {
            positionBuilder
                .setWorld(destination.world.name)
                .setX(destination.x)
                .setY(destination.y)
                .setZ(destination.z)
                .setYaw(destination.yaw)
                .setPitch(destination.pitch);
        }
        final var position = positionBuilder.build();
        final var priority = onlineUser
            .getEffectivePermissionCount(Permission.QUEUE_PRIORITY.formatted(destination.server.name, "{}"))
            .orElse(0);
        final var allPriority = onlineUser
            .getEffectivePermissionCount(Permission.QUEUE_PRIORITY.formatted("*", "{}"))
            .orElse(0);
        final var request = Queue.Join.Request.newBuilder()
            .setUser(user)
            .setPosition(position)
            .setPriority(Math.max(priority, allPriority))
            .setType(type)
            .build();
        GrpcClient.queue().join(request);
    }

    public void leave(@NotNull final OnlineUser onlineUser) {
        onlineUser.putMetadata(TimedTeleport.CANCEL_METADATA_KEY, true);
        this.implementor.getDatabase().setCurrentTeleport(onlineUser, null);
        final var user = Definition.User.newBuilder().setName(onlineUser.username).setUuid(onlineUser.uuid.toString()).build();
        final var request = Queue.Leave.Request.newBuilder().setUser(user).build();
        GrpcClient.queue().leave(request)
            .thenAccept(response -> {
                switch (response.getResult()) {
                    case SUCCEED: {
                        this.implementor.getLocales().getLocale("queue_leave_success").ifPresent(onlineUser::sendMessage);
                        break;
                    }
                    case USER_NOT_FOUND: {
                        this.implementor.getLocales().getLocale("queue_leave_fail_not_in_queue").ifPresent(onlineUser::sendMessage);
                        break;
                    }
                    default:
                        throw new RuntimeException("Something went wrong!");
                }
            })
            .whenComplete((__, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            });
    }
}
