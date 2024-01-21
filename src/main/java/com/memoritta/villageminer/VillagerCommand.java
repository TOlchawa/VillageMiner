package com.memoritta.villageminer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

import static com.memoritta.villageminer.VillageMinerPlugin.MINER;

public class VillagerCommand implements CommandExecutor {

    private final VillageMinerPlugin plugin;
    private final Logger logger;

    public VillagerCommand(VillageMinerPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            Villager villager = player.getWorld().spawn(location, Villager.class);
            villager.setCustomName(player.getDisplayName() + "'s miner");
            villager.setCustomNameVisible(true);
            villager.setProfession(Villager.Profession.NONE);
            villager.setCustomName(ChatColor.GRAY + "Miner");
            villager.setCustomNameVisible(true);
            villager.getPersistentDataContainer().set(VillageMinerPlugin.isMinerAttributeKey, PersistentDataType.STRING, MINER);

            logger.info("start villager: " + villager.toString());

            // Make the villager follow the player
            new BukkitRunnable() {

                public void run() {

                    if (villager.isDead() || !player.isOnline()) {
                        this.cancel();
                        return;
                    }

                }
            }.runTaskTimerAsynchronously(plugin,0L, 2000L); // runs every second

            return true;
        }
        return false;
    }
}
