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

package net.william278.huskhomes.command;

import de.themoep.minedown.adventure.MineDown;
import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.user.CommandUser;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.util.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
<<<<<<< HEAD
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
=======
>>>>>>> master

public class SetHomeCommand extends SetPositionCommand {

    protected SetHomeCommand(@NotNull HuskHomes plugin) {
        super("sethome", plugin);
    }

    @Override
<<<<<<< HEAD
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        plugin.getDatabase().getHomes(onlineUser).thenAcceptAsync(homes -> {
            switch (args.length) {
                case 0: {
                    if (homes.isEmpty()) {
                        setHome(onlineUser, "home", homes);
                    } else {
                        plugin.getLocales().getLocale("error_invalid_syntax", "/sethome <name>")
                            .ifPresent(onlineUser::sendMessage);
                    }
                    break;
                }
                case 1: {
                    setHome(onlineUser, args[0], homes);
                    break;
                }
                default: {
                    plugin.getLocales().getLocale("error_invalid_syntax", "/sethome <name>")
                        .ifPresent(onlineUser::sendMessage);
                    break;
                }
=======
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        if (executor instanceof OnlineUser user && args.length == 0 && createDefaultHome(user)) {
            return;
        }
        super.execute(executor, args);
    }

    @Override
    protected void execute(@NotNull OnlineUser setter, @NotNull String name) {
        plugin.fireEvent(plugin.getHomeCreateEvent(setter, name, setter.getPosition(), setter), (event) -> {
            try {
                plugin.getManager().homes().createHome(setter, event.getName(), event.getPosition());
            } catch (ValidationException e) {
                e.dispatchHomeError(setter, false, plugin, event.getName());
                return;
>>>>>>> master
            }
            plugin.getLocales().getLocale("set_home_success", event.getName())
                    .ifPresent(setter::sendMessage);
        });
    }

<<<<<<< HEAD
    /**
     * Attempts to set a home by given name for the {@link OnlineUser}.
     * <p>
     * A number of validation checks will take place before the home is set. If these checks fail, the home won't be set.
     * <ul>
     *     <li>The user's currentHomes must not exceed the permissive maximum home limit</li>
     *     <li>If economy features are on and the user does not have enough home slots, they must have sufficient funds to buy another</li>
     *     <li>The home name must not already exist</li>
     *     <li>The home name must meet the length and character criteria</li>
     * </ul>
     *
     * @param onlineUser   The {@link OnlineUser} to set the home for
     * @param homeName     The name of the home to set
     * @param currentHomes The current homes of the {@link OnlineUser}
     */
    private void setHome(@NotNull OnlineUser onlineUser, @NotNull String homeName, @NotNull List<Home> currentHomes) {
        // Check against maximum homes
        final int maxHomes = onlineUser.getMaxHomes(plugin.getSettings().maxHomes, plugin.getSettings().stackPermissionLimits);
        if (currentHomes.size() >= maxHomes) {
            plugin.getLocales().getLocale("error_set_home_maximum_homes", Integer.toString(maxHomes))
                .ifPresent(onlineUser::sendMessage);
            return;
        }

        // Get their user data
        plugin.getDatabase().getUserData(onlineUser.uuid).thenAccept(fetchedData -> {
            // Check against economy if needed
            final AtomicBoolean newSlotNeeded = new AtomicBoolean(false);
            final AtomicReference<UserData> userDataToUpdate = new AtomicReference<>(null);
            if (plugin.getSettings().economy) {
                final int freeHomes = onlineUser.getFreeHomes(plugin.getSettings().freeHomeSlots,
                    plugin.getSettings().stackPermissionLimits);
                if (fetchedData.isPresent()) {
                    final Settings.EconomyAction action = Settings.EconomyAction.ADDITIONAL_HOME_SLOT;
                    newSlotNeeded.set((currentHomes.size() + 1) > (freeHomes + fetchedData.get().homeSlots()));

                    // If a new slot is needed, validate the user has enough funds to purchase one
                    if (newSlotNeeded.get()) {
                        if (!plugin.validateEconomyCheck(onlineUser, action)) {
                            return;
                        }
                        userDataToUpdate.set(new UserData(onlineUser, (currentHomes.size() + 1) - freeHomes,
                            fetchedData.get().ignoringTeleports(), fetchedData.get().rtpCooldown()));
                    } else {
                        if (currentHomes.size() == freeHomes) {
                            plugin.getEconomyHook()
                                .flatMap(economyHook -> plugin.getSettings().getEconomyCost(action)
                                    .map(economyHook::formatCurrency))
                                .flatMap(formatted -> plugin.getLocales().getLocale("set_home_used_free_slots",
                                    Integer.toString(freeHomes), formatted))
                                .ifPresent(onlineUser::sendMessage);
                        }
                    }
                }
            }

            // Set the home in the saved position manager
            plugin.getSavedPositionManager()
                .setHome(new PositionMeta(homeName, ""), onlineUser, onlineUser.getPosition())
                .thenAccept(setResult -> {
                    // Display feedback of the result of the set operation
                    final Optional<MineDown> message;

                    switch (setResult.resultType()) {
                        case SUCCESS: {
                            assert setResult.savedPosition().isPresent();

                            // If the user needed to buy a new slot, perform the transaction and update their data
                            if (newSlotNeeded.get()) {
                                plugin.performEconomyTransaction(onlineUser, Settings.EconomyAction.ADDITIONAL_HOME_SLOT);
                                plugin.getDatabase().updateUserData(userDataToUpdate.get());
                            }
                            message = plugin
                                .getLocales().getLocale("set_home_success", setResult.savedPosition().get().meta.name);
                            break;
                        }
                        case SUCCESS_OVERWRITTEN: {
                            assert setResult.savedPosition().isPresent();
                            message = plugin.getLocales().getLocale("edit_home_update_location",
                                setResult.savedPosition().get().meta.name);
                            break;
                        }
                        case FAILED_DUPLICATE: {
                            message = plugin.getLocales().getLocale("error_home_name_taken");
                            break;
                        }
                        case FAILED_NAME_LENGTH: {
                            message = plugin.getLocales().getLocale("error_home_name_length");
                            break;
                        }
                        case FAILED_NAME_CHARACTERS: {
                            message = plugin.getLocales().getLocale("error_home_name_characters");
                            break;
                        }
                        default: {
                            message = plugin.getLocales().getLocale("error_home_description_characters");
                            break;
                        }
                    }
                    message.ifPresent(onlineUser::sendMessage);
                });


        });
=======
    private boolean createDefaultHome(@NotNull OnlineUser user) {
        final List<Home> homes = plugin.getDatabase().getHomes(user);
        final Optional<String> name = homes.isEmpty() ? Optional.of("home") :
                (homes.size() == 1 && plugin.getSettings().doOverwriteExistingHomesWarps())
                        ? Optional.of(homes.get(0).getName()) : Optional.empty();
        if (name.isPresent()) {
            this.execute(user, "home");
            return true;
        }
        return false;
>>>>>>> master
    }

}
