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

package net.william278.huskhomes.queue;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.grpc.GrpcClient;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.proto.Definition;
import net.william278.huskhomes.proto.Queue;
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.teleport.TimedTeleport;
import net.william278.huskhomes.user.OnlineUser;
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
        final var destination = ((Position) Objects.requireNonNull(teleport.getTarget(), "Something went wrong!"));
        final var onlineUser = Objects.requireNonNull((OnlineUser) teleport.getTeleporter(), "Something went wrong!");
        onlineUser.putMetadata(TimedTeleport.CANCEL_METADATA_KEY, true);
        this.implementor.getDatabase().setCurrentTeleport(onlineUser, teleport);
        final var user = Definition.User.newBuilder().setName(onlineUser.getUsername()).setUuid(onlineUser.getUuid().toString()).build();
        final var positionBuilder = Definition.NetworkPosition.newBuilder()
            .setServer(destination.getServer());
        if (destination.getWorld() != null) {
            positionBuilder
                .setWorld(destination.getWorld().getName())
                .setX(destination.getX())
                .setY(destination.getY())
                .setZ(destination.getZ())
                .setYaw(destination.getYaw())
                .setPitch(destination.getPitch());
        }
        final var position = positionBuilder.build();
        final var priority = onlineUser
            .getEffectivePermissionCount("huskhomes.queue.priority."+destination.getServer()+".{}")
            .orElse(0);
        final var allPriority = onlineUser
            .getEffectivePermissionCount("huskhomes.queue.priority.*.{}")
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
        final var user = Definition.User.newBuilder().setName(onlineUser.getUsername()).setUuid(onlineUser.getUuid().toString()).build();
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
