package net.william278.huskhomes.hook;

import com.flowpowered.math.vector.Vector2i;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.User;
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.position.World;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Hook to display warps and public homes on <a href="https://github.com/BlueMap-Minecraft/BlueMap">BlueMap</a> maps
 */
public class BlueMapHook extends MapHook {

    private String publicHomeMarkerIconPath;
    private String warpMarkerIconPath;

    public BlueMapHook(@NotNull HuskHomes implementor) {
        super(implementor, "BlueMap");
    }

    @Override
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
            }

            initializedFuture.complete(null);
        }));
        return initializedFuture;
    }

    @Override
    public CompletableFuture<Void> updateHome(@NotNull Home home) {
        if (!isValidPosition(home)) return CompletableFuture.completedFuture(null);

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
        }
    }
}
