package net.william278.huskhomes.event;

import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.Home;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeListEvent extends Event implements IHomeListEvent, Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    @NotNull
    private final List<Home> homes;
    @NotNull
    private final OnlineUser onlineUser;
    private final boolean isPublicHomeList;
    private boolean cancelled;

    public HomeListEvent(@NotNull List<Home> homes, @NotNull OnlineUser onlineUser, boolean isPublicHomeList) {
        this.homes = homes;
        this.onlineUser = onlineUser;
        this.isPublicHomeList = isPublicHomeList;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    @NotNull
    public List<Home> getHomes() {
        return homes;
    }

    @Override
    @NotNull
    public OnlineUser getOnlineUser() {
        return onlineUser;
    }

    @Override
    public boolean getIsPublicHomeList() {
        return isPublicHomeList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
