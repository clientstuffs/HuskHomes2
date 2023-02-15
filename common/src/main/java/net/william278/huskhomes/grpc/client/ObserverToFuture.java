package net.william278.huskhomes.grpc.client;

import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

final class ObserverToFuture {

    @NotNull
    static <T> CompletableFuture<T> future(@NotNull final Consumer<StreamObserver<T>> consumer) {
        final var observer = new Observer<T>();
        consumer.accept(observer);
        return observer.future;
    }

    private static final class Observer<T> implements StreamObserver<T> {

        @NotNull
        private final CompletableFuture<T> future = new CompletableFuture<>();

        private final AtomicReference<T> value = new AtomicReference<>();

        @Override
        public void onNext(T value) {
            this.value.set(value);
        }

        @Override
        public void onError(Throwable t) {
            future.completeExceptionally(t);
        }

        @Override
        public void onCompleted() {
            future.complete(this.value.get());
        }
    }
}
