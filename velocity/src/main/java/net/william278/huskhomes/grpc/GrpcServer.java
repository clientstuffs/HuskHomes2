package net.william278.huskhomes.grpc;

import com.velocitypowered.api.proxy.ProxyServer;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.ManagedChannelBuilder;
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
