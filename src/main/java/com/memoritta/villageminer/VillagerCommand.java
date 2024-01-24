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
    private final VillageMinerListener villageMinerListener;

    public VillagerCommand(VillageMinerPlugin plugin, Logger logger, VillageMinerListener villageMinerListener) {
        this.plugin = plugin;
        this.logger = logger;
        this.villageMinerListener = villageMinerListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getLogger().finest("command: " + command.getName());
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if ("killminer".equalsIgnoreCase(command.getName())) {
                plugin.getLogger().finest("killing ...");
                villageMinerListener.killMiners(player);
                return true;

            } else {

                villageMinerListener.killMiners(player);

                Location location = player.getLocation();
                Villager villager = player.getWorld().spawn(location, Villager.class);
                villager.setCustomName(player.getDisplayName() + "'s miner");
                villager.setCustomNameVisible(true);
                villager.setProfession(Villager.Profession.NONE);
                villager.setCustomName(ChatColor.GRAY + "Miner");
                villager.setCustomNameVisible(true);
                villager.getPersistentDataContainer().set(VillageMinerPlugin.isMinerAttributeKey, PersistentDataType.STRING, MINER);
                villager.getPersistentDataContainer().set(VillageMinerPlugin.ownerMinerAttributeKey, PersistentDataType.STRING, player.getUniqueId().toString());

                villageMinerListener.register(villager);

                logger.finest("start villager: " + villager.toString());

                return true;
            }
        }
        return false;
    }
}
