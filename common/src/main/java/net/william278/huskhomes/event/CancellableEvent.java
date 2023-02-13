package net.william278.huskhomes.event;

/**
 * Base interface for a cancellable event
 */
public interface CancellableEvent {

    /**
     * Check whether the event is cancelled
     *
     * @return {@code true} if the event is cancelled
     */
    boolean isCancelled();

    /**
     * Set whether the event should be cancelled
     *
     * @param cancelled {@code true} if the event should be cancelled
     */
    void setCancelled(boolean cancelled);

}
