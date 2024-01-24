package com.memoritta.villageminer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VillageMinerController extends BukkitRunnable implements VillageMinerListener {

    private final VillageMinerPlugin villageMinerPlugin;
    private final VillageMinerUtils utils;
    private static final List<Villager> miners = new LinkedList<>();

    public VillageMinerController(VillageMinerPlugin villageMinerPlugin, VillageMinerUtils utils) {
        this.villageMinerPlugin = villageMinerPlugin;
        this.utils = utils;
    }

    @Override
    public void run() {
        miners.forEach(miner -> {
            if (miner.isDead()) {
                miners.remove(miner);
            } else {
                Integer time = miner.getPersistentDataContainer().get(VillageMinerPlugin.miningTimeAttributeKey, PersistentDataType.INTEGER);
                if (time != null && time > 0) {
                    Location minerLocation = utils.calculateVillagerLocation(miner);
                    miner.teleport(minerLocation);
                }
            }
        });
    }

    @Override
    public void register(Villager miner) {
        miners.add(miner);
    }

    @Override
    public void killMiners(Player owner) {
        Set<Villager> killed = new HashSet<>();
        UUID playerUniqueId = owner.getUniqueId();
        villageMinerPlugin.getLogger().finest("killing all miners for: " + playerUniqueId);
        miners.forEach(miner -> {
            String minerOwnerUUID = miner.getPersistentDataContainer().get(VillageMinerPlugin.ownerMinerAttributeKey, PersistentDataType.STRING);

            if (minerOwnerUUID == null || playerUniqueId.toString().equalsIgnoreCase(minerOwnerUUID)) {
                killed.add(miner);
                miner.remove();
            }
        });
        miners.removeAll(killed);
    }
}
