package net.william278.huskhomes.grpc.service;

import net.william278.huskhomes.proto.Definition;
import org.jetbrains.annotations.NotNull;

public interface ServiceListener {

    /**
     * Runs every seconds.
     */
    default void onUpdate() {
    }

    /**
     * Runs whenever the user leaves from the server(proxy).
     *
     * @param user The user to run.
     */
    default void onLeave(@NotNull final Definition.User user) {
    }
}
