package net.william278.huskhomes.grpc.service;

import com.google.protobuf.Empty;
import com.velocitypowered.api.proxy.ProxyServer;
import io.grpc.stub.StreamObserver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.william278.huskhomes.proto.Definition;
import net.william278.huskhomes.proto.Queue;
import net.william278.huskhomes.proto.QueueServiceGrpc;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class QueueService extends QueueServiceGrpc.QueueServiceImplBase implements ServiceListener {

    private static final AtomicInteger QUEUE_STARTS = new AtomicInteger(100);
    private static final AtomicReference<Component> ACTIONBAR = new AtomicReference<>();
    private static final AtomicReference<Component> MESSAGE = new AtomicReference<>();
    private static final AtomicReference<Component> OLD_QUEUE_MESSAGE = new AtomicReference<>();
    private static final AtomicReference<Component> FINISH_QUEUE_MESSAGE = new AtomicReference<>();

    private final Map<String, PriorityQueue<QueuedUser>> queue = new ConcurrentHashMap<>();

    private final Map<Definition.User, QueuedUser> users = new ConcurrentHashMap<>();


    @NotNull
    private final ProxyServer proxy;

    public QueueService(@NotNull final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void join(Queue.Join.Request request, StreamObserver<Queue.Join.Response> responseObserver) {
        final var user = request.getUser();
        final var position = request.getPosition();
        final var priority = request.getPriority();
        final var type = request.getType();
        final var existingUser = this.users.get(user);
        final var playerOptional = this.proxy.getPlayer(UUID.fromString(user.getUuid()));
        final var result = existingUser != null ? Queue.Join.Result.REPLACED : Queue.Join.Result.SUCCEED;
        if (existingUser != null) {
            this.queue.computeIfAbsent(existingUser.position.getServer(), __ -> new PriorityQueue<>()).remove(existingUser);
            if (QueueService.OLD_QUEUE_MESSAGE.get() != null) {
                playerOptional.ifPresent(player -> player.sendMessage(QueueService.OLD_QUEUE_MESSAGE.get()));
            }
        }
        final var newUser = new QueuedUser(user, position, priority, type);
        final var queue = this.queue.computeIfAbsent(position.getServer(), __ -> new PriorityQueue<>());
        queue.add(newUser);
        this.users.put(user, newUser);
        if (QueueService.MESSAGE.get() != null) {
            playerOptional.ifPresent(player ->
                player.sendMessage(QueueService.MESSAGE.get().replaceText(builder -> {
                    builder
                        .match("%server_name%").replacement(position.getServer())
                        .match("%queue_type%").replacement(type)
                        .match("%queue_order%").replacement(String.valueOf(queue.size()))
                        .match("%queue_total%").replacement(String.valueOf(queue.size()));
                })));
        }
        responseObserver.onNext(Queue.Join.Response.newBuilder().setResult(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void leave(Queue.Leave.Request request, StreamObserver<Queue.Leave.Response> responseObserver) {
        if (this.users.containsKey(request.getUser())) {
            responseObserver.onNext(Queue.Leave.Response.newBuilder().setResult(Queue.Leave.Result.USER_NOT_FOUND).build());
        } else {
            this.onLeave(request.getUser());
            responseObserver.onNext(Queue.Leave.Response.newBuilder().setResult(Queue.Leave.Result.SUCCEED).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateSettings(Queue.UpdateSettings.Request request, StreamObserver<Empty> responseObserver) {
        QueueService.QUEUE_STARTS.set(request.getQueueStarts());
        if (request.hasActionbar()) {
            QueueService.ACTIONBAR.set(GsonComponentSerializer.gson().deserialize(request.getActionbar()));
        } else {
            QueueService.ACTIONBAR.set(null);
        }
        if (request.hasMessage()) {
            QueueService.MESSAGE.set(GsonComponentSerializer.gson().deserialize(request.getMessage()));
        } else {
            QueueService.MESSAGE.set(null);
        }
        if (request.hasOldQueueMessage()) {
            QueueService.OLD_QUEUE_MESSAGE.set(GsonComponentSerializer.gson().deserialize(request.getOldQueueMessage()));
        } else {
            QueueService.OLD_QUEUE_MESSAGE.set(null);
        }
        if (request.hasFinishQueueMessage()) {
            QueueService.FINISH_QUEUE_MESSAGE.set(GsonComponentSerializer.gson().deserialize(request.getFinishQueueMessage()));
        } else {
            QueueService.FINISH_QUEUE_MESSAGE.set(null);
        }
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void onUpdate() {
        for (final var entry : this.queue.entrySet()) {
            final var server = entry.getKey();
            final var queue = entry.getValue();
            final var serverOptional = this.proxy.getServer(server);
            if (serverOptional.isEmpty()) {
                continue;
            }
            final var registeredServer = serverOptional.get();
            final var ping = registeredServer.ping().join();
            final var players = ping.getPlayers().orElseThrow();
            final var online = players.getOnline();
            if (online >= QueueService.QUEUE_STARTS.get()) {
                continue;
            }
            final var user = queue.poll();
            if (user == null) {
                continue;
            }
            final var playerOptional = this.proxy.getPlayer(UUID.fromString(user.user.getUuid()));
            if (playerOptional.isEmpty()) {
                continue;
            }
            final var player = playerOptional.get();
            if (QueueService.FINISH_QUEUE_MESSAGE.get() != null) {
                player.sendMessage(QueueService.FINISH_QUEUE_MESSAGE.get().replaceText(builder -> {
                    builder.match("%server_name%").replacement(server);
                }));
            }
            player.createConnectionRequest(registeredServer).fireAndForget();
            break;
        }
        if (QueueService.ACTIONBAR.get() != null) {
            for (final var entry : this.queue.entrySet()) {
                final var server = entry.getKey();
                final var queue = entry.getValue();
                final var order = new AtomicInteger();
                for (final var user : queue) {
                    order.getAndIncrement();
                    this.proxy.getPlayer(UUID.fromString(user.user.getUuid())).ifPresent(player -> {
                        player.sendActionBar(QueueService.ACTIONBAR.get().replaceText(builder -> {
                            builder
                                .match("%server_name%").replacement(server)
                                .match("%queue_type%").replacement(user.type)
                                .match("%queue_order%").replacement(String.valueOf(order.get()))
                                .match("%queue_total%").replacement(String.valueOf(queue.size()));
                        }));
                    });
                }
            }
        }
    }

    @Override
    public void onLeave(@NotNull final Definition.User user) {
        final var existingUser = this.users.remove(user);
        if (existingUser != null) {
            this.queue.computeIfAbsent(existingUser.position.getServer(), __ -> new PriorityQueue<>()).remove(existingUser);
        }
    }

    private static final class QueuedUser implements Comparable<QueuedUser> {

        @NotNull
        private final Definition.User user;

        @NotNull
        private final Definition.NetworkPosition position;

        private final int priority;

        @NotNull
        private final String type;

        private QueuedUser(@NotNull final Definition.User user, @NotNull final Definition.NetworkPosition position,
                           final int priority, @NotNull final String type) {
            this.user = user;
            this.position = position;
            this.priority = priority;
            this.type = type;
        }

        @Override
        public int compareTo(@NotNull QueueService.QueuedUser o) {
            if (this.priority != o.priority) {
                return this.priority - o.priority;
            }
            return this.user.getName().compareTo(o.user.getName());
        }
    }
}
