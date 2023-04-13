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

package net.william278.huskhomes.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.william278.huskhomes.grpc.client.QueueClient;
import net.william278.huskhomes.proto.QueueServiceGrpc;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class GrpcClient {

    private static final AtomicReference<ManagedChannel> CHANNEL = new AtomicReference<>();

    private static final AtomicReference<QueueClient> QUEUE_CLIENT = new AtomicReference<>();

    public static void initiate(@NotNull final String host) {
        GrpcClient.CHANNEL.set(ManagedChannelBuilder.forAddress(host, 548).usePlaintext().build());
    }

    public static void initiateQueue() {
        GrpcClient.QUEUE_CLIENT.set(new QueueClient(QueueServiceGrpc.newStub(GrpcClient.channel())));
    }

    @NotNull
    public static QueueClient queue() {
        return Objects.requireNonNull(GrpcClient.QUEUE_CLIENT.get(), "Enable cross-server.queue!");
    }

    @NotNull
    private static ManagedChannel channel() {
        return Objects.requireNonNull(GrpcClient.CHANNEL.get(), "Initiate gRPC first!");
    }
}
