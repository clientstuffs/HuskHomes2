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

import com.velocitypowered.api.proxy.ProxyServer;
import io.grpc.ServerBuilder;
import net.william278.huskhomes.grpc.service.QueueService;
import net.william278.huskhomes.grpc.service.ServiceListener;
import net.william278.huskhomes.proto.Definition;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public final class GrpcServer {

    private static final Collection<ServiceListener> LISTENABLE_SERVICES = ConcurrentHashMap.newKeySet();

    public static void initiate(
        @NotNull final ProxyServer proxy
    ) throws IOException {
        final var queueService = new QueueService(proxy);
        GrpcServer.LISTENABLE_SERVICES.add(queueService);
        ServerBuilder.forPort(548)
            .addService(queueService)
            .build()
            .start();
    }

    public static void onUpdate() {
        GrpcServer.LISTENABLE_SERVICES.forEach(ServiceListener::onUpdate);
    }

    public static void onLeave(@NotNull final Definition.User user) {
        GrpcServer.LISTENABLE_SERVICES.forEach(s -> s.onLeave(user));
    }

    public static void onJoin(@NotNull final Definition.User user) {
        GrpcServer.LISTENABLE_SERVICES.forEach(s -> s.onJoin(user));
    }
}
