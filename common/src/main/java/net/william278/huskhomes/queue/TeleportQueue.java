package net.william278.huskhomes.queue;

import net.william278.huskhomes.player.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TeleportQueue {

    void initialize();

    @NotNull
    CompletableFuture<?> joinFor(@NotNull OnlineUser onlineUser, @NotNull String serverName);
}
