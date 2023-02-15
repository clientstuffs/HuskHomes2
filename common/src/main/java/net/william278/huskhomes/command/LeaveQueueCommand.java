package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LeaveQueueCommand extends CommandBase {

    protected LeaveQueueCommand(@NotNull HuskHomes implementor) {
        super("leavequeue", Permission.COMMAND_LEAVE_QUEUE, implementor);
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (!this.plugin.getSettings().crossServer || !this.plugin.getSettings().queue) {
            return;
        }
        this.plugin.getTeleportQueue().leave(onlineUser);
    }
}
