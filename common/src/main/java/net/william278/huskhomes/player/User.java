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

import java.util.UUID;

/**
 * Represents a user who has data saved in the database
 */
public class User {

    @NotNull
    public final UUID uuid;

    @NotNull
    public final String username;

    /**
     * Get a user from a {@link UUID} and username
     *
     * @param uuid     Minecraft account {@link UUID} of the player
     * @param username Username of the player
     */
    public User(@NotNull UUID uuid, @NotNull String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        if (obj instanceof User) {
            final var user = (User) obj;

            return user.uuid.equals(uuid);
        }
        return super.equals(obj);
    }
}
