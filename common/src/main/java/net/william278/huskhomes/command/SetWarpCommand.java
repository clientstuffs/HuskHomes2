package net.william278.huskhomes.command;

import de.themoep.minedown.adventure.MineDown;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.PositionMeta;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SetWarpCommand extends CommandBase {

    protected SetWarpCommand(@NotNull HuskHomes implementor) {
        super("setwarp", Permission.COMMAND_SET_WARP, implementor);
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length == 1) {
            setWarp(onlineUser, args[0]);
        } else {
            plugin.getLocales().getLocale("error_invalid_syntax", "/setwarp <name>")
                .ifPresent(onlineUser::sendMessage);
        }
    }

    private void setWarp(@NotNull OnlineUser onlineUser, @NotNull String warpName) {
        plugin.getSavedPositionManager().setWarp(
            new PositionMeta(warpName, ""), onlineUser.getPosition()).thenAccept(setResult -> {
            final Optional<MineDown> message;
            switch (setResult.resultType()) {
                case SUCCESS: {
                    assert setResult.savedPosition().isPresent();
                    message = plugin.getLocales().getLocale("set_warp_success",
                        setResult.savedPosition().get().meta.name);
                    break;
                }
                case SUCCESS_OVERWRITTEN: {
                    assert setResult.savedPosition().isPresent();
                    message = plugin.getLocales().getLocale("edit_warp_update_location",
                        setResult.savedPosition().get().meta.name);
                    break;
                }
                case FAILED_DUPLICATE: {
                    message = plugin.getLocales().getLocale("error_warp_name_taken");
                    break;
                }
                case FAILED_NAME_LENGTH: {
                    message = plugin.getLocales().getLocale("error_warp_name_length");
                    break;
                }
                case FAILED_NAME_CHARACTERS: {
                    message = plugin.getLocales().getLocale("error_warp_name_characters");
                    break;
                }
                default: {
                    message = plugin.getLocales().getLocale("error_warp_description_characters");
                    break;
                }
            }
            message.ifPresent(onlineUser::sendMessage);
        });
    }

}
