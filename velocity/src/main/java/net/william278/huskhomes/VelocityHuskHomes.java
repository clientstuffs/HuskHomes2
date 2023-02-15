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
        GrpcServer.initiate(this.proxy);
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
        if (event.getPreviousServer().isEmpty()) {
            return;
        }
        final var player = event.getPlayer();
        final var name = player.getUsername();
        final var uuid = player.getUniqueId().toString();
        GrpcServer.onLeave(Definition.User.newBuilder().setName(name).setUuid(uuid).build());
    }
}
