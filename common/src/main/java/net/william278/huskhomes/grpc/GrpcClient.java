package net.william278.huskhomes.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import net.william278.huskhomes.grpc.client.QueueClient;
import net.william278.huskhomes.proto.QueueServiceGrpc;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class GrpcClient {

    private static final AtomicReference<ManagedChannel> CHANNEL = new AtomicReference<>();

    private static final AtomicReference<QueueClient> QUEUE_CLIENT = new AtomicReference<>();

    public static void initiate(@NotNull final String host) {
        GrpcClient.CHANNEL.set(Grpc.newChannelBuilderForAddress(host, 443, InsecureChannelCredentials.create()).build());
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
