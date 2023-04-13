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

package net.william278.huskhomes;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.william278.huskhomes.grpc.GrpcServer;
import net.william278.huskhomes.proto.Definition;

import java.time.Duration;

@Plugin(id = "huskhomes", name = "HuskHomes", version = "1.0.0-SNAPSHOT", authors = "portlek")
public final class VelocityHuskHomes {

    @Inject
    private ProxyServer proxy;

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        this.proxy.getCommandManager().unregister("server");
        try {
            GrpcServer.initiate(this.proxy);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        this.proxy.getScheduler().buildTask(this, GrpcServer::onUpdate)
            .delay(Duration.ofSeconds(1L))
            .repeat(Duration.ofSeconds(1L))
            .schedule();
    }

    @Subscribe
    public void onLeave(final DisconnectEvent event) {
        final var player = event.getPlayer();
        final var name = player.getUsername();
        final var uuid = player.getUniqueId().toString();
        GrpcServer.onLeave(Definition.User.newBuilder().setName(name).setUuid(uuid).build());
    }

    @Subscribe
    public void onJoin(final ServerConnectedEvent event) {
        final var player = event.getPlayer();
        final var name = player.getUsername();
        final var uuid = player.getUniqueId().toString();
        GrpcServer.onJoin(Definition.User.newBuilder().setName(name).setUuid(uuid).build());
    }
}
