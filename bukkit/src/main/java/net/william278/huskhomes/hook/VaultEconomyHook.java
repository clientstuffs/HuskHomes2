package net.william278.huskhomes.hook;

import net.milkbowl.vault.economy.Economy;
import net.william278.huskhomes.BukkitHuskHomes;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.HuskHomesInitializationException;
import net.william278.huskhomes.player.BukkitPlayer;
import net.william278.huskhomes.player.OnlineUser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * A hook that hooks into the Vault API to provide economy features
 */
public class VaultEconomyHook extends EconomyHook {

    protected Economy economy;

    public VaultEconomyHook(@NotNull HuskHomes implementor) {
        super(implementor, "Vault (Economy)");
    }

    @Override
    public boolean initialize() throws HuskHomesInitializationException {
        final RegisteredServiceProvider<Economy> economyProvider = ((BukkitHuskHomes) plugin).getServer()
            .getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            return true;
        }
        return false;
    }

    @Override
    public double getPlayerBalance(@NotNull OnlineUser player) {
        return economy.getBalance(((BukkitPlayer) player).getPlayer());
    }

    @Override
    public void changePlayerBalance(@NotNull OnlineUser player, double amount) {
        if (amount != 0d) {
            final Player bukkitPlayer = ((BukkitPlayer) player).getPlayer();
            final double currentBalance = getPlayerBalance(player);
            final double amountToChange = Math.abs(currentBalance - Math.max(0d, currentBalance + amount));
            if (amount < 0d) {
                economy.withdrawPlayer(bukkitPlayer, amountToChange);
            } else {
                economy.depositPlayer(bukkitPlayer, amountToChange);
            }
        }
    }

    @Override
    public String formatCurrency(double amount) {
        return economy.format(amount);
    }
}
