package ru.baronessdev.personal.bankdeposit;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.baronessdev.personal.bankdeposit.config.Config;
import ru.baronessdev.personal.bankdeposit.config.Messages;

import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class BankDeposit extends JavaPlugin {
    public static BankDeposit inst;
    private static Economy economy = null;

    public BankDeposit() {
        inst = this;
    }

    @Override
    public void onEnable() {
        new Config();
        new Messages();

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            getLogger().log(Level.INFO, "Disabled due to no Vault dependency found");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            economy = rsp.getProvider();
        }

        getCommand("bank").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length > 1 && args[0].equalsIgnoreCase("deposit")) {
                    int commandNum;
                    int numOfIron = Stream.of(player.getInventory().getContents())
                            .filter(Objects::nonNull)
                            .filter(item -> item.getType() == Material.IRON_INGOT)
                            .mapToInt(ItemStack::getAmount)
                            .sum();

                    if (args[1].matches("[-+]?\\d+")) {
                        commandNum = Integer.parseInt(args[1]);
                    } else if (args[1].equalsIgnoreCase("all")) {
                        commandNum = numOfIron;
                    } else {
                        player.sendMessage(Messages.inst.getMessage("usage"));
                        return true;
                    }

                    commandNum = Math.abs(commandNum);

                    if (numOfIron >= commandNum) {
                        int forRemoval = commandNum;

                        for (int slot = 0; slot < player.getInventory().getSize() && forRemoval > 0; slot++) {
                            ItemStack itemStack = player.getInventory().getItem(slot);

                            if (itemStack != null) {
                                int now = itemStack.getAmount() - forRemoval;

                                if (now > 0) {
                                    itemStack.setAmount(now);
                                    player.getInventory().setItem(slot, itemStack);
                                } else {
                                    player.getInventory().clear(slot);
                                }

                                forRemoval -= itemStack.getAmount();
                            }
                        }

                        double price = Config.inst.getDouble("price") * commandNum;
                        economy.depositPlayer(player, price);

                        player.sendMessage(Messages.inst.getMessage("successfully")
                                .replace("%num", String.valueOf(commandNum))
                                .replace("%price", String.valueOf(price)));
                    } else {
                        player.sendMessage(Messages.inst.getMessage("too-little")
                                .replace("%num", String.valueOf(numOfIron)));
                    }
                } else {
                    player.sendMessage(Messages.inst.getMessage("usage"));
                }
            } else {
                sender.sendMessage(Messages.inst.getMessage("not-player"));
            }

            return true;
        });


    }

    @Override
    public void onDisable() { }
}
