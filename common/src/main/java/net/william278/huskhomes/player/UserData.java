package net.william278.huskhomes.player;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents data about a player on the server
 */
public final class UserData {

    @NotNull
    private final User user;
    private final int homeSlots;
    private final boolean ignoringTeleports;
    @NotNull
    private final Instant rtpCooldown;

    public UserData(@NotNull User user, int homeSlots, boolean ignoringTeleports, @NotNull Instant rtpCooldown) {
        this.user = user;
        this.homeSlots = homeSlots;
        this.ignoringTeleports = ignoringTeleports;
        this.rtpCooldown = rtpCooldown;
    }

    public User user() {
        return user;
    }

    public int homeSlots() {
        return homeSlots;
    }

    public boolean ignoringTeleports() {
        return ignoringTeleports;
    }

    public Instant rtpCooldown() {
        return rtpCooldown;
    }

    @NotNull
    public UUID getUserUuid() {
        return user.uuid;
    }

    public String getUsername() {
        return user.username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (homeSlots != userData.homeSlots) return false;
        if (ignoringTeleports != userData.ignoringTeleports) return false;
        if (!user.equals(userData.user)) return false;
        return rtpCooldown.equals(userData.rtpCooldown);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + homeSlots;
        result = 31 * result + (ignoringTeleports ? 1 : 0);
        result = 31 * result + rtpCooldown.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserData{" +
               "user=" + user +
               ", homeSlots=" + homeSlots +
               ", ignoringTeleports=" + ignoringTeleports +
               ", rtpCooldown=" + rtpCooldown +
               '}';
    }
}
