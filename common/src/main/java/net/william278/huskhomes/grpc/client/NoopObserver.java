package net.william278.huskhomes.grpc.client;

import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.NotNull;

public final class NoopObserver<T> implements StreamObserver<T> {

    private NoopObserver() {
    }

    @NotNull
    public static <T> NoopObserver<T> create() {
        return new NoopObserver<>();
    }

    @Override
    public void onNext(T value) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {

    }
}
