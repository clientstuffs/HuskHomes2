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
        return observer.future.whenComplete((__, t) -> {
            if (t != null) {
                t.printStackTrace();
            }
        });
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
