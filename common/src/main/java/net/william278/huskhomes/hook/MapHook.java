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

package net.william278.huskhomes.hook;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.position.SavedPosition;
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * A hook for a mapping plugin, such as Dynmap
 */
public abstract class MapHook extends Hook {

    protected static final String WARP_MARKER_IMAGE_NAME = "warp";
    protected static final String PUBLIC_HOME_MARKER_IMAGE_NAME = "public-home";

<<<<<<< HEAD
    protected MapHook(@NotNull HuskHomes implementor, @NotNull String hookName) {
        super(implementor, hookName);
    }

    @Override
    public final boolean initialize() {
        initializeMap().thenRun(() -> {
            if (plugin.getSettings().publicHomesOnMap) {
                plugin.getDatabase().getLocalPublicHomes(plugin)
                    .thenAcceptAsync(homes -> homes.forEach(this::updateHome));
            }
            if (plugin.getSettings().warpsOnMap) {
                plugin.getDatabase().getLocalWarps(plugin)
                    .thenAcceptAsync(warps -> warps.forEach(this::updateWarp));
            }
        });
        return true;
=======
    protected MapHook(@NotNull HuskHomes plugin, @NotNull String name) {
        super(plugin, name);
>>>>>>> master
    }

    /**
     * Populate the map with public homes and warps
     */
    protected void populateMap() {
        if (plugin.getSettings().doPublicHomesOnMap()) {
            plugin.getDatabase()
                    .getLocalPublicHomes(plugin)
                    .forEach(this::updateHome);
        }
        if (plugin.getSettings().doWarpsOnMap()) {
            plugin.getDatabase()
                    .getLocalWarps(plugin)
                    .forEach(this::updateWarp);
        }
    }

    /**
     * Update a home, adding it to the map if it exists, or updating it on the map if it doesn't
     *
     * @param home the home to update
     */
    public abstract void updateHome(@NotNull Home home);

    /**
     * Removes a home from the map
     *
     * @param home the home to remove
     */
    public abstract void removeHome(@NotNull Home home);

    /**
     * Clears homes owned by a player from the map
     *
     * @param user the player whose homes to clear
     */
    public abstract void clearHomes(@NotNull User user);

    /**
     * Update a warp, adding it to the map if it exists, or updating it on the map if it doesn't
     *
     * @param warp the warp to update
     */
    public abstract void updateWarp(@NotNull Warp warp);

    /**
     * Removes a warp from the map
     *
     * @param warp the warp to remove
     */
    public abstract void removeWarp(@NotNull Warp warp);

    /**
     * Clears all warps from the map
     */
    public abstract void clearWarps();

    /**
     * Returns if the position is valid to be set on this server
     *
     * @param position the position to check
     * @return if the position is valid
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected final boolean isValidPosition(@NotNull SavedPosition position) {
<<<<<<< HEAD
        if (position instanceof Warp && !plugin.getSettings().warpsOnMap) return false;
        if (position instanceof Home && !plugin.getSettings().publicHomesOnMap) return false;

        try {
            return position.server.equals(plugin.getServerName());
        } catch (HuskHomesException e) {
            return plugin.getWorlds().stream()
                .map(world -> world.uuid)
                .anyMatch(uuid -> uuid.equals(position.world.uuid));
=======
        if (position instanceof Warp && !plugin.getSettings().doWarpsOnMap()) {
            return false;
>>>>>>> master
        }
        if (position instanceof Home && !plugin.getSettings().doPublicHomesOnMap()) {
            return false;
        }

        return !plugin.getSettings().doCrossServer() || position.getServer().equals(plugin.getServerName());
    }

    @NotNull
    protected final String getPublicHomesKey() {
        return plugin.getKey(getName().toLowerCase(), "public_home_markers").toString();
    }

    @NotNull
    protected final String getWarpsKey() {
        return plugin.getKey(getName().toLowerCase(), "warp_markers").toString();
    }

    @NotNull
    protected final String getPublicHomesMarkerSetName() {
        return plugin.getLocales().getRawLocale("map_hook_public_homes_marker_set_name")
                .orElse("Public Homes");
    }

    @NotNull
    protected final String getWarpsMarkerSetName() {
        return plugin.getLocales().getRawLocale("map_hook_warps_marker_set_name")
                .orElse("Warps");
    }

}
