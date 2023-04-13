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

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.user.User;
import net.william278.huskhomes.util.ThrowingConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Hook to display warps and public homes on <a href="https://github.com/BlueMap-Minecraft/BlueMap">BlueMap</a> maps
 */
public class BlueMapHook extends MapHook {

    private Map<String, MarkerSet> publicHomesMarkerSets;
    private Map<String, MarkerSet> warpsMarkerSets;

    public BlueMapHook(@NotNull HuskHomes plugin) {
        super(plugin, "BlueMap");
    }

    @Override
<<<<<<< HEAD
    protected CompletableFuture<Void> initializeMap() {
        final CompletableFuture<Void> initializedFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> BlueMapAPI.onEnable(blueMapAPI -> {
            // Create marker sets
            plugin.getWorlds().forEach(world -> blueMapAPI.getWorld(world.uuid)
                .ifPresent(blueMapWorld -> blueMapWorld.getMaps().forEach(map -> {
                    if (plugin.getSettings().publicHomesOnMap) {
                        map.getMarkerSets().put(blueMapWorld.getId() + ":" + PUBLIC_HOMES_MARKER_SET_ID,
                            MarkerSet.builder().label("Public Homes").build());
                    }
                    if (plugin.getSettings().warpsOnMap) {
                        map.getMarkerSets().put(blueMapWorld.getId() + ":" + WARPS_MARKER_SET_ID,
                            MarkerSet.builder().label("Warps").build());
                    }
                })));

            // Create marker icons
            try {
                publicHomeMarkerIconPath = blueMapAPI.getWebApp().createImage(
                    ImageIO.read(Objects.requireNonNull(plugin.getResource("markers/50x/" + PUBLIC_HOME_MARKER_IMAGE_NAME + ".png"))),
                    "huskhomes/" + PUBLIC_HOMES_MARKER_SET_ID + ".png");
                warpMarkerIconPath = blueMapAPI.getWebApp().createImage(
                    ImageIO.read(Objects.requireNonNull(plugin.getResource("markers/50x/" + WARP_MARKER_IMAGE_NAME + ".png"))),
                    "huskhomes/" + WARP_MARKER_IMAGE_NAME + ".png");
            } catch (IOException e) {
                plugin.getLoggingAdapter().log(Level.SEVERE, "Failed to create warp marker image", e);
=======
    public void initialize() {
        BlueMapAPI.onEnable(api -> {
            this.publicHomesMarkerSets = new HashMap<>();
            this.warpsMarkerSets = new HashMap<>();

            for (World world : plugin.getWorlds()) {
                this.editMapWorld(world, (mapWorld -> {
                    final MarkerSet publicHomeMarkers = MarkerSet.builder().label(getPublicHomesMarkerSetName()).build();
                    final MarkerSet warpsMarkers = MarkerSet.builder().label(getWarpsMarkerSetName()).build();

                    for (BlueMapMap map : mapWorld.getMaps()) {
                        map.getMarkerSets().put(getPublicHomesKey(), publicHomeMarkers);
                        map.getMarkerSets().put(getWarpsKey(), warpsMarkers);
                    }

                    publicHomesMarkerSets.put(world.getName(), publicHomeMarkers);
                    warpsMarkerSets.put(world.getName(), warpsMarkers);
                }));
>>>>>>> master
            }

            this.populateMap();
        });
    }

    @Override
    public void updateHome(@NotNull Home home) {
        if (!isValidPosition(home)) {
            return;
        }

        this.editPublicHomesMarkerSet(home.getWorld(), (markerSet -> {
            final String markerId = home.getOwner().getUuid() + ":" + home.getUuid();
            markerSet.remove(markerId);
            markerSet.put(markerId, POIMarker.builder()
                    .label("/phome " + home.getOwner().getUsername() + "." + home.getName())
                    .position(home.getX(), home.getY(), home.getZ())
                    .maxDistance(5000)
                    .icon(getIcon(PUBLIC_HOME_MARKER_IMAGE_NAME), 25, 25)
                    .build());
        }));
    }

    @Override
    public void removeHome(@NotNull Home home) {
        this.editPublicHomesMarkerSet(home.getWorld(), markerSet -> markerSet
                .remove(home.getOwner().getUuid() + ":" + home.getUuid()));

<<<<<<< HEAD
        return removeHome(home).thenRun(() -> BlueMapAPI.getInstance().flatMap(
            blueMapAPI -> getBlueMapWorld(blueMapAPI, home.world)).ifPresent(blueMapWorld -> blueMapWorld.getMaps()
            .forEach(blueMapMap -> blueMapMap.getMarkerSets()
                .computeIfPresent(blueMapWorld.getId() + ":" + PUBLIC_HOMES_MARKER_SET_ID, (s, markerSet) -> {
                    markerSet.getMarkers().put(home.owner.uuid + ":" + home.uuid,
                        POIMarker.toBuilder()
                            .label("/phome" + home.owner.username + "." + home.meta.name)
                            .position((int) home.x, (int) home.y, (int) home.z)
                            .icon(publicHomeMarkerIconPath, Vector2i.from(25, 25))
                            .maxDistance(5000)
                            .build());
                    return markerSet;
                }))));
    }

    @Override
    public CompletableFuture<Void> removeHome(@NotNull Home home) {
        if (!isValidPosition(home)) return CompletableFuture.completedFuture(null);

        BlueMapAPI.getInstance().flatMap(blueMapAPI -> getBlueMapWorld(blueMapAPI, home.world))
            .ifPresent(blueMapWorld -> blueMapWorld.getMaps().forEach(blueMapMap -> blueMapMap.getMarkerSets()
                .computeIfPresent(blueMapWorld.getId() + ":" + PUBLIC_HOMES_MARKER_SET_ID, (s, markerSet) -> {
                    markerSet.getMarkers().remove(home.owner.uuid + ":" + home.uuid);
                    return markerSet;
                })));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> clearHomes(@NotNull User user) {
        BlueMapAPI.getInstance().ifPresent((BlueMapAPI blueMapAPI) -> blueMapAPI.getWorlds()
            .forEach(blueMapWorld -> blueMapWorld.getMaps()
                .forEach(blueMapMap -> blueMapMap.getMarkerSets()
                    .computeIfPresent(blueMapWorld.getId() + ":" + PUBLIC_HOMES_MARKER_SET_ID, (s, markerSet) -> {
                        markerSet.getMarkers().keySet().removeIf(key -> key.startsWith(user.uuid.toString()));
                        return markerSet;
                    }))));

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateWarp(@NotNull Warp warp) {
        if (!isValidPosition(warp)) return CompletableFuture.completedFuture(null);

        return removeWarp(warp).thenRun(() -> BlueMapAPI.getInstance().flatMap(blueMapAPI -> getBlueMapWorld(blueMapAPI, warp.world))
            .ifPresent(blueMapWorld -> blueMapWorld.getMaps().forEach(blueMapMap -> blueMapMap.getMarkerSets()
                .computeIfPresent(blueMapWorld.getId() + ":" + WARPS_MARKER_SET_ID, (s, markerSet) -> {
                    markerSet.getMarkers().put(warp.uuid.toString(),
                        POIMarker.toBuilder()
                            .label("/warp " + warp.meta.name)
                            .position((int) warp.x, (int) warp.y, (int) warp.z)
                            .icon(warpMarkerIconPath, Vector2i.from(25, 25))
                            .maxDistance(10000)
                            .build());
                    return markerSet;
                }))));
    }

    @Override
    public CompletableFuture<Void> removeWarp(@NotNull Warp warp) {
        if (!isValidPosition(warp)) return CompletableFuture.completedFuture(null);

        BlueMapAPI.getInstance().flatMap(blueMapAPI -> getBlueMapWorld(blueMapAPI, warp.world))
            .ifPresent(blueMapWorld -> blueMapWorld.getMaps().forEach(blueMapMap -> blueMapMap.getMarkerSets()
                .computeIfPresent(blueMapWorld.getId() + ":" + WARPS_MARKER_SET_ID, (s, markerSet) -> {
                    markerSet.getMarkers().remove(warp.uuid.toString());
                    return markerSet;
                })));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> clearWarps() {
        BlueMapAPI.getInstance().ifPresent((BlueMapAPI blueMapAPI) -> blueMapAPI.getWorlds()
            .forEach(blueMapWorld -> blueMapWorld.getMaps()
                .forEach(blueMapMap -> blueMapMap.getMarkerSets()
                    .computeIfPresent(blueMapWorld.getId() + ":" + WARPS_MARKER_SET_ID, (s, markerSet) -> {
                        markerSet.getMarkers().clear();
                        return markerSet;
                    }))));

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Get the {@link BlueMapWorld} for a world
     *
     * @param world The {@link World} to get the {@link BlueMapWorld} for
     * @return The {@link BlueMapWorld} of the world
     */
    @NotNull
    private Optional<BlueMapWorld> getBlueMapWorld(@NotNull BlueMapAPI blueMapAPI, @NotNull World world) {
        if (world.uuid.equals(new UUID(0, 0))) {
            return blueMapAPI.getWorld(world.name);
        } else {
            return blueMapAPI.getWorld(world.uuid);
=======
    }

    @Override
    public void clearHomes(@NotNull User user) {
        if (publicHomesMarkerSets != null) {
            publicHomesMarkerSets.values().forEach(markerSet -> markerSet.getMarkers().keySet()
                    .removeIf(markerId -> markerId.startsWith(user.getUuid().toString())));
>>>>>>> master
        }
    }

    @Override
    public void updateWarp(@NotNull Warp warp) {
        if (!isValidPosition(warp)) {
            return;
        }

        this.editWarpsMarkerSet(warp.getWorld(), (markerSet -> {
            final String markerId = warp.getUuid().toString();
            markerSet.remove(markerId);
            markerSet.put(markerId, POIMarker.builder()
                    .label("/warp " + warp.getName())
                    .position(warp.getX(), warp.getY(), warp.getZ())
                    .maxDistance(5000)
                    .icon(getIcon(WARP_MARKER_IMAGE_NAME), 25, 25)
                    .build());
        }));
    }

    @Override
    public void removeWarp(@NotNull Warp warp) {
        editWarpsMarkerSet(warp.getWorld(), markerSet -> markerSet.remove(warp.getUuid().toString()));
    }

    @Override
    public void clearWarps() {
        if (warpsMarkerSets != null) {
            warpsMarkerSets.values().forEach(markerSet -> markerSet.getMarkers().clear());
        }
    }

    @Nullable
    private String getIcon(@NotNull String iconName) {
        return BlueMapAPI.getInstance().map(api -> {
            final Path icons = api.getWebApp().getWebRoot().resolve("icons").resolve("huskhomes");
            if (!icons.toFile().exists() && !icons.toFile().mkdirs()) {
                plugin.log(Level.WARNING, "Failed to create BlueMap icons directory");
            }

            final String iconFileName = iconName + ".png";
            final File iconFile = icons.resolve(iconFileName).toFile();
            if (!iconFile.exists()) {
                try (InputStream readIcon = plugin.getResource("markers/50x/" + iconFileName)) {
                    if (readIcon == null) {
                        throw new FileNotFoundException("Could not find icon resource: " + iconFileName);
                    }
                    Files.copy(readIcon, iconFile.toPath());
                } catch (IOException e) {
                    plugin.log(Level.WARNING, "Failed to load icon for BlueMap hook", e);
                }
            }

            return "icons/huskhomes/" + iconFileName;
        }).orElse(null);
    }

    private void editPublicHomesMarkerSet(@NotNull World world, @NotNull ThrowingConsumer<MarkerSet> editor) {
        editMapWorld(world, (mapWorld -> {
            if (publicHomesMarkerSets != null) {
                editor.accept(publicHomesMarkerSets.get(world.getName()));
            }
        }));
    }

    private void editWarpsMarkerSet(@NotNull World world, @NotNull ThrowingConsumer<MarkerSet> editor) {
        editMapWorld(world, (mapWorld -> {
            if (warpsMarkerSets != null) {
                editor.accept(warpsMarkerSets.get(world.getName()));
            }
        }));
    }

    private void editMapWorld(@NotNull World world, @NotNull ThrowingConsumer<BlueMapWorld> editor) {
        BlueMapAPI.getInstance().flatMap(api -> api.getWorld(world.getName())).ifPresent(editor);
    }

}
