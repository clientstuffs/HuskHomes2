package net.william278.huskhomes.player;

import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import net.william278.huskhomes.position.Location;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.teleport.TeleportResult;
import net.william278.huskhomes.util.Permission;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A cross-platform representation of a logged-in {@link User}
 */
public abstract class OnlineUser extends User {

    public OnlineUser(@NotNull UUID uuid, @NotNull String username) {
        super(uuid, username);
    }


    /**
     * Returns the current {@link Position} of this player
     *
     * @return the player's current {@link Position}
     */
    public abstract Position getPosition();

    /**
     * Returns the player's current bed or respawn anchor {@link Position}
     *
     * @return an optional with the player's current bed or respawn anchor {@link Position} if it has been set,
     * otherwise an {@link Optional#empty()}
     */
    public abstract Optional<Position> getBedSpawnPosition();

    /**
     * Returns the health of this player
     *
     * @return the player's health points
     */
    public abstract double getHealth();

    /**
     * Returns if the player has the permission node
     *
     * @param node The permission node string
     * @return {@code true} if the player has the node; {@code false} otherwise
     */
    public abstract boolean hasPermission(@NotNull @Pattern(Permission.PERMISSION_PATTERN) String node);

    /**
     * Returns a {@link Map} of a player's permission nodes
     *
     * @return a {@link Map} of all permissions this player has to their set values
     */
    @NotNull
    public abstract Map<String, Boolean> getPermissions();

    @NotNull
    public abstract OptionalInt getEffectivePermissionCount(@NotNull String permissionFormat);

    /**
     * Dispatch a MineDown-formatted title or subtitle to the player
     *
     * @param mineDown the parsed {@link MineDown} to send
     * @param subTitle whether to send the title as a subtitle ({@code true} for a subtitle, {@code false} for a title)
     */
    public final void sendTitle(@NotNull MineDown mineDown, boolean subTitle) {
        getAudience().sendTitlePart(subTitle ? TitlePart.SUBTITLE : TitlePart.TITLE, mineDown
            .disable(MineDownParser.Option.SIMPLE_FORMATTING)
            .replace().toComponent());
    }

    /**
     * Dispatch a MineDown-formatted action bar message to this player
     *
     * @param mineDown the parsed {@link MineDown} to send
     */
    public final void sendActionBar(@NotNull MineDown mineDown) {
        getAudience().sendActionBar(mineDown
            .disable(MineDownParser.Option.SIMPLE_FORMATTING)
            .replace().toComponent());
    }


    /**
     * Dispatch a MineDown-formatted chat message to this player
     *
     * @param mineDown the parsed {@link MineDown} to send
     */
    public final void sendMessage(@NotNull MineDown mineDown) {
        getAudience().sendMessage(mineDown
            .disable(MineDownParser.Option.SIMPLE_FORMATTING)
            .replace().toComponent());
    }

    /**
     * Dispatch a Minecraft translatable keyed-message to this player
     *
     * @param translationKey the translation key of the message to send
     * @implNote This method is intended for use with Minecraft's built-in translation keys. If the key is invalid,
     * it will be substituted with {@code minecraft:block.minecraft.spawn.not_valid}
     */
    public final void sendTranslatableMessage(@Subst(Key.MINECRAFT_NAMESPACE + "block.minecraft.spawn.not_valid")
                                              @NotNull String translationKey) {
        getAudience().sendMessage(Component.translatable(translationKey));
    }

    /**
     * Play the specified sound to this player
     *
     * @param soundEffect the sound effect to play. If the sound name is invalid, the sound will not play
     * @implNote If the key is invalid, it will be substituted with {@code minecraft:block.note_block.banjo}
     */
    public final void playSound(@Subst(Key.MINECRAFT_NAMESPACE + "block.note_block.banjo")
                                @NotNull String soundEffect) throws IllegalArgumentException {
        try {
            getAudience().playSound(Sound.sound(Key.key(soundEffect), Sound.Source.PLAYER,
                1.0f, 1.0f), Sound.Emitter.self());
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid sound effect name: " + soundEffect);
        }
    }

    /**
     * Get the adventure {@link Audience} for this player
     *
     * @return the adventure {@link Audience} for this player
     */
    @NotNull
    protected abstract Audience getAudience();

    /**
     * Teleport a player to the specified local {@link Location}
     *
     * @param location     the {@link Location} to teleport the player to
     * @param asynchronous if the teleport should be asynchronous
     */
    public abstract CompletableFuture<TeleportResult> teleportLocally(@NotNull Location location, boolean asynchronous);

    /**
     * Returns if a player is moving (i.e. they have momentum)
     *
     * @return {@code true} if the player is moving; {@code false} otherwise
     */
    public abstract boolean isMoving();

    /**
     * Returns if the player is tagged as being "vanished" by a /vanish plugin
     *
     * @return {@code true} if the player is tagged as being "vanished" by a /vanish plugin; {@code false} otherwise
     */
    public abstract boolean isVanished();

    public abstract Optional<Object> getMetadata(@NotNull final String metadata);
    public abstract void putMetadata(@NotNull final String metadata, @NotNull final Object value);

    /**
     * Get the maximum number of homes this user may set
     *
     * @param defaultMaxHomes the default maximum number of homes if the user has not set a custom value
     * @param stack           whether to stack numerical permissions that grant the user extra max homes
     * @return the maximum number of homes this user may set
     */
    public final int getMaxHomes(final int defaultMaxHomes, final boolean stack) {
        final List<Integer> homes = getNumericalPermissions("huskhomes.max_homes.");
        if (homes.isEmpty()) {
            return defaultMaxHomes;
        }
        if (stack) {
            return defaultMaxHomes + homes.stream().reduce(0, Integer::sum);
        } else {
            return homes.get(0);
        }
    }

    /**
     * Get the number of homes this user may make public
     *
     * @param defaultPublicHomes the default number of homes this user may make public
     * @param stack              whether to stack numerical permissions that grant the user extra public homes
     * @return the number of public home slots this user may set
     */
    public int getMaxPublicHomes(final int defaultPublicHomes, final boolean stack) {
        final List<Integer> homes = getNumericalPermissions("huskhomes.max_public_homes.");
        if (homes.isEmpty()) {
            return defaultPublicHomes;
        }
        if (stack) {
            return defaultPublicHomes + homes.stream().reduce(0, Integer::sum);
        } else {
            return homes.get(0);
        }
    }

    /**
     * Get the number of free home slots this user may set
     *
     * @param defaultFreeHomes the default number of free home slots to give this user
     * @param stack            whether to stack numerical permissions that grant the user extra free homes
     * @return the number of free home slots this user may set
     */
    public int getFreeHomes(final int defaultFreeHomes, final boolean stack) {
        final List<Integer> homes = getNumericalPermissions("huskhomes.free_homes.");
        if (homes.isEmpty()) {
            return defaultFreeHomes;
        }
        if (stack) {
            return defaultFreeHomes + homes.stream().reduce(0, Integer::sum);
        } else {
            return homes.get(0);
        }
    }

    /**
     * Gets a list of numbers from the prefixed permission nodes
     *
     * @param nodePrefix the prefix of the permission nodes to get
     * @return a list of numbers from the prefixed permission nodes, sorted by size
     */
    private List<Integer> getNumericalPermissions(@NotNull String nodePrefix) {
        return getPermissions().entrySet().stream()
            .filter(Map.Entry::getValue)
            .filter(permission -> permission.getKey().startsWith(nodePrefix))
            .filter(permission -> {
                try {
                    // Remove node prefix from the permission and parse as an integer
                    Integer.parseInt(permission.getKey().substring(nodePrefix.length()));
                } catch (final NumberFormatException e) {
                    return false;
                }
                return true;
            })
            .map(permission -> Integer.parseInt(permission.getKey().substring(nodePrefix.length())))
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList());
    }
}