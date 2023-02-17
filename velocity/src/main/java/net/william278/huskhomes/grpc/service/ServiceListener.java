package net.william278.huskhomes.grpc.service;

import net.william278.huskhomes.proto.Definition;
import org.jetbrains.annotations.NotNull;

public interface ServiceListener {

    default void onUpdate() {}

    default void onLeave(@NotNull final Definition.User user) {}

    default void onJoin(@NotNull final Definition.User user) {}
}
