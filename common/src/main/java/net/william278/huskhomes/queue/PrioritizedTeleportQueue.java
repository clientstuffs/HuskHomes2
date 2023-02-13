package net.william278.huskhomes.queue;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.network.Messenger;
import org.jetbrains.annotations.NotNull;

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
    }
}
