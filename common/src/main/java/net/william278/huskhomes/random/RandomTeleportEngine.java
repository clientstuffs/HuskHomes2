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

package net.william278.huskhomes.random;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an engine for generating random position targets
 */
public abstract class RandomTeleportEngine {

<<<<<<< HEAD
    /**
     * The name of the random teleport engine
     */
    @NotNull
    public final String name;
    @NotNull
    protected final HuskHomes plugin;
    /**
     * How many attempts to allow {@link #getRandomPosition} lookups before timing out
     */
    public long randomTimeout = 8;
=======
    protected final HuskHomes plugin;
    public final String name;
    public long maxAttempts = 12;
>>>>>>> master

    /**
     * Constructor for a random teleport engine
     *
     * @param plugin The HuskHomes plugin instance
     * @param name   The name of the implementing random teleport engine
     */
    protected RandomTeleportEngine(@NotNull HuskHomes plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @NotNull
    public final String getName() {
        return name;
    }

    /**
     * Get the origin position (spawn) of this server
     *
     * @return The origin position
     */
<<<<<<< HEAD
    protected final Position getOrigin(@NotNull World world) {
        return plugin.getLocalCachedSpawn()
            .flatMap(spawn -> {
                if (!spawn.worldUuid.equals(world.uuid.toString())) {
                    return Optional.empty();
                }
                return spawn.getPosition(plugin.getServerName());
            })
            .orElse(new Position(0d, 128d, 0d, 0f, 0f,
                world, plugin.getServerName()));
=======
    @NotNull
    protected Position getCenterPoint(@NotNull World world) {
        return plugin.getServerSpawn()
                .map(s -> s.getPosition(plugin.getServerName()))
                .orElse(Position.at(0d, 128d, 0d, 0f, 0f,
                        world, plugin.getServerName()));
>>>>>>> master
    }

    /**
     * Gets a random position in the {@link World}, or {@link Optional#empty()} if no position could be found in
     * the configured number of attempts
     *
     * @param world The world to find a random position in
     * @param args  The arguments to pass to the random teleport engine
     * @return The position, optionally, which will be empty if the random teleport engine timed out after a
     * {@link #maxAttempts configured number of attempts}
     */
<<<<<<< HEAD
    protected abstract Optional<Position> generatePosition(@NotNull World world, @NotNull String[] args);

    /**
     * Gets a random position in the {@link World}, supplying a future to be completed with the optional position
     * is found, or empty if the operation times out after a number of attempts.
     *
     * @param world The world to find a random position in
     * @param args  The arguments to pass to the random teleport engine
     * @return A {@link CompletableFuture} containing the random position, if one is found in
     * {@link #randomTimeout configured number of attempts}, or if the operation times out after 10 seconds
     */
    public CompletableFuture<Optional<Position>> getRandomPosition(@NotNull World world, @NotNull String[] args) {
        return CompletableFuture.supplyAsync(() -> generatePosition(world, args))
            .orTimeout(10, TimeUnit.SECONDS)
            .exceptionally(e -> Optional.empty());
    }
=======
    public abstract CompletableFuture<Optional<Position>> getRandomPosition(@NotNull World world, @NotNull String[] args);
>>>>>>> master

}
