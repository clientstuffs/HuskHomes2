package net.william278.huskhomes.util;

import net.kyori.adventure.key.Key;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@YamlFile(header = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                   "┃      Unsafe /rtp Blocks      ┃\n" +
                   "┃    Developed by William278   ┃\n" +
                   "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛")
public class UnsafeBlocks {

    @YamlKey("unsafe_blocks")
    public List<String> unsafeBlocks;

    @SuppressWarnings("unused")
    public UnsafeBlocks() {
    }

    /**
     * Returns if the block, by provided identifier, is unsafe
     *
     * @param blockId The block identifier (e.g. {@code minecraft:stone})
     * @return {@code true} if the block is on the unsafe blocks list, {@code false} otherwise
     */
    public boolean isUnsafe(@NotNull String blockId) {
        if (!blockId.startsWith(Key.MINECRAFT_NAMESPACE + ":")) {
            blockId = Key.MINECRAFT_NAMESPACE + ":" + blockId;
        }
        return unsafeBlocks.contains(blockId);
    }

}
