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
