package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.Server;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.Teleport;
import net.william278.huskhomes.teleport.TeleportType;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerCommand extends CommandBase implements TabCompletable {

    protected ServerCommand(@NotNull HuskHomes implementor) {
        super("server", Permission.COMMAND_SERVER, implementor);
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (!this.plugin.getSettings().crossServer) {
            return;
        }
        if (args.length == 1) {
            final String serverName = args[0];
            final var server = new Server(serverName);
            Teleport.builder(this.plugin, onlineUser)
                .setTarget(new Position(0.0d, 0.0d, 0.0d, 0.0f, 0.0f, new World("", UUID.randomUUID()), server))
                .setType(TeleportType.SERVER)
                .toTimedTeleport()
                .thenAccept(teleport -> {
                    final var target = teleport.target;
                    if (target == null) {
                        teleport.execute();
                    } else {
                        final var bypass = onlineUser.hasPermission(Permission.QUEUE_BYPASS_ALL.node) ||
                                           onlineUser.hasPermission(Permission.QUEUE_BYPASS.formatted(target.server.name));
                        if (!this.plugin.getSettings().queue || bypass) {
                            teleport.execute();
                        } else {
                            this.plugin.getTeleportQueue().join(teleport, "server");
                        }
                    }
                });
        } else {
            this.plugin.getLocales().getLocale("error_invalid_syntax", "/server [server_name]")
                .ifPresent(onlineUser::sendMessage);
        }
    }

    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull String[] args, @Nullable OnlineUser user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return args.length > 1 ? Collections.emptyList() : this.plugin.getCache().onlineServers
            .stream()
            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args.length == 1 ? args[0].toLowerCase(Locale.ROOT) : ""))
            .sorted()
            .collect(Collectors.toList());
    }
}
