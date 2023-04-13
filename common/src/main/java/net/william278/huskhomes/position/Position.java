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

package net.william278.huskhomes.position;

import com.google.gson.annotations.Expose;
import net.william278.huskhomes.teleport.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a position - a {@link Location} somewhere on the proxy network or server
 */
public class Position extends Location implements Target {

    @Expose
    private String server;

    protected Position(double x, double y, double z, float yaw, float pitch, @NotNull World world, @NotNull String server) {
        super(x, y, z, yaw, pitch, world);
        this.setServer(server);
    }

    protected Position(@NotNull Location location, @NotNull String server) {
        super(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld());
        this.setServer(server);
    }

<<<<<<< HEAD
    public Position(@NotNull final Server server) {
        this.server = server;
    }

    @SuppressWarnings("unused")
    public Position() {
=======
    @SuppressWarnings("unused")
    private Position() {
    }

    @NotNull
    public static Position at(double x, double y, double z, float yaw, float pitch, @NotNull World world, @NotNull String server) {
        return new Position(x, y, z, yaw, pitch, world, server);
    }

    @NotNull
    public static Position at(double x, double y, double z, @NotNull World world, @NotNull String server) {
        return Position.at(x, y, z, 0, 0, world, server);
    }

    @NotNull
    public static Position at(@NotNull Location location, @NotNull String server) {
        return new Position(location, server);
    }

    @Override
    public void update(@NotNull Position newPosition) {
        super.update(newPosition);
        this.setServer(newPosition.getServer());
>>>>>>> master
    }

    /**
     * The name of the server the position is on
     */
    @NotNull
    public String getServer() {
        return server;
    }

    public void setServer(@NotNull String server) {
        this.server = server;
    }

<<<<<<< HEAD
    /**
     * Update the position to match that of another position
     *
     * @param newPosition The position to update to
     */
    public void update(@NotNull Position newPosition) {
        this.x = newPosition.x;
        this.y = newPosition.y;
        this.z = newPosition.z;
        this.yaw = newPosition.yaw;
        this.pitch = newPosition.pitch;
        this.world = newPosition.world;
        this.server = newPosition.server;
    }
=======
    @Override
    public String toString() {
        return "x: " + (int) getX() + ", " +
                "y: " + (int) getY() + ", " +
                "z: " + (int) getZ() + " " +
                "(" + getWorld().getName() + " / " + getServer() + ")";
    }

>>>>>>> master
}
