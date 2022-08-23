package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class WarpCommand extends CommandBase implements TabCompletable, ConsoleExecutable {

    protected WarpCommand(@NotNull HuskHomes implementor) {
        super("warp", Permission.COMMAND_WARP, implementor);
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        switch (args.length) {
            case 0 -> plugin.getDatabase().getWarps().thenAccept(warps -> {
                if (warps.isEmpty()) {
                    plugin.getLocales().getLocale("error_no_warps_set").ifPresent(onlineUser::sendMessage);
                    return;
                }
                onlineUser.sendMessage(plugin.getCache().getWarpList(onlineUser, plugin.getLocales(), warps,
                        plugin.getSettings().permissionRestrictWarps, plugin.getSettings().listItemsPerPage, 1));
            });
            case 1 -> {
                final String warpName = args[0];
                plugin.getTeleportManager().teleportToWarpByName(onlineUser, warpName);
            }
            default -> plugin.getLocales().getLocale("error_invalid_syntax", "/warp [name]")
                    .ifPresent(onlineUser::sendMessage);
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        return plugin.getCache().warps.stream()
                .filter(s -> s.toLowerCase().startsWith(args.length >= 1 ? args[0].toLowerCase() : ""))
                .sorted().collect(Collectors.toList());
    }

    @Override
    public void onConsoleExecute(@NotNull String[] args) {
        //todo
    }
}
