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
