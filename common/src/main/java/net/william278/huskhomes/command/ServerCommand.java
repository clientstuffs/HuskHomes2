package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.Server;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerCommand extends CommandBase implements TabCompletable {

    protected ServerCommand(@NotNull HuskHomes implementor) {
        super("server", Permission.COMMAND_SERVER, implementor);
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length == 1) {
            final String serverName = args[0];
            this.plugin.getMessenger().sendPlayer(onlineUser, new Server(serverName));
        } else {
            this.plugin.getLocales().getLocale("error_invalid_syntax", "/home [name]")
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
            .filter(s -> s.toLowerCase().startsWith(args.length == 1 ? args[0].toLowerCase() : ""))
            .sorted()
            .collect(Collectors.toList());
    }
}
