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

package net.william278.huskhomes.grpc.client;

import net.william278.huskhomes.proto.Queue;
import net.william278.huskhomes.proto.QueueServiceGrpc;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class QueueClient {

    @NotNull
    private final QueueServiceGrpc.QueueServiceStub stub;

    public QueueClient(@NotNull final QueueServiceGrpc.QueueServiceStub stub) {
        this.stub = stub;
    }

    @NotNull
    public CompletableFuture<Queue.Join.Response> join(@NotNull final Queue.Join.Request request) {
        return ObserverToFuture.future(observer -> this.stub.join(request, observer));
    }

    @NotNull
    public CompletableFuture<Queue.Leave.Response> leave(@NotNull final Queue.Leave.Request request) {
        return ObserverToFuture.future(observer -> this.stub.leave(request, observer));
    }

    public void updateSettings(@NotNull final Queue.UpdateSettings.Request request) {
        this.stub.updateSettings(request, NoopObserver.create());
    }
}
