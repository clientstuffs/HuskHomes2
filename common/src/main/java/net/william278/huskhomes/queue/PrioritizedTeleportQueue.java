package net.william278.huskhomes.queue;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.network.Messenger;
import net.william278.huskhomes.player.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class PrioritizedTeleportQueue implements TeleportQueue {

    @NotNull
    private final HuskHomes implementor;

    @NotNull
    private final Messenger messenger;

    public PrioritizedTeleportQueue(@NotNull HuskHomes implementor, @NotNull Messenger messenger) {
        this.implementor = implementor;
        this.messenger = messenger;
    }

    @Override
    public void initialize() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            () -> {

            },
            0L,
            1L,
            TimeUnit.SECONDS
        );
    }

    @NotNull
    @Override
    public CompletableFuture<?> joinFor(@NotNull OnlineUser onlineUser, @NotNull String serverName) {
        return CompletableFuture.completedFuture(null);
    }
}
