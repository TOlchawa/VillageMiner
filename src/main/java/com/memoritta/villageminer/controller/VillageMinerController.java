package com.memoritta.villageminer.controller;

import com.memoritta.villageminer.VillageMinerPlugin;
import com.memoritta.villageminer.util.VillageMinerUtils;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.FOLLOW_LEADER;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.FORGOT_TARGET;

public class VillageMinerController extends BukkitRunnable implements VillageMinerListener {

    public static final String FOLLOW_MODE = "FOLLOW";
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
                String mode = miner.getPersistentDataContainer().get(VillageMinerPlugin.modeAttributeKey, PersistentDataType.STRING);
                if (!FOLLOW_MODE.equalsIgnoreCase(mode)) {
                    Integer time = miner.getPersistentDataContainer().get(VillageMinerPlugin.miningTimeAttributeKey, PersistentDataType.INTEGER);
                    if (time != null && time > 0) {
                        Location minerLocation = utils.calculateVillagerLocation(miner);
                        miner.teleport(minerLocation);
                    }
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
        String playerUniqueId = owner.getUniqueId().toString();
        villageMinerPlugin.getLogger().finest("killing all miners for: " + playerUniqueId);
        miners.forEach(miner -> {
            String minerOwnerUniqueId = miner.getPersistentDataContainer().get(VillageMinerPlugin.ownerMinerAttributeKey, PersistentDataType.STRING);

            if (minerOwnerUniqueId == null || playerUniqueId.equalsIgnoreCase(minerOwnerUniqueId)) {
                killed.add(miner);
                miner.remove();
            }
        });
        miners.removeAll(killed);
    }


    public void followOwner(Player owner) {
        String playerName = owner.getName();
        villageMinerPlugin.getLogger().info("killing all miners for: " + playerName);
        miners.forEach(miner -> {
            String minerOwnerUniqueId = miner.getPersistentDataContainer().get(VillageMinerPlugin.ownerMinerAttributeKey, PersistentDataType.STRING);
            villageMinerPlugin.getLogger().info("minerOwnerUniqueId: " + minerOwnerUniqueId);
            if (minerOwnerUniqueId == null || playerName.equalsIgnoreCase(minerOwnerUniqueId)) {
                if (miner instanceof CraftVillager && owner instanceof CraftPlayer) {
                    villageMinerPlugin.getLogger().info("execute follow");
                    CraftVillager craftVillager = (CraftVillager) miner;
                    EntityPlayer entityPlayer = ((CraftPlayer) owner).getHandle();
                    craftVillager.getHandle().setTarget(entityPlayer, FOLLOW_LEADER, true);
                    miner.getPersistentDataContainer().set(VillageMinerPlugin.modeAttributeKey, PersistentDataType.STRING, FOLLOW_MODE);
                }
            }
        });
    }

    public void unfollowOwner(Player owner) {
        String playerName = owner.getName();
        villageMinerPlugin.getLogger().info("killing all miners for: " + playerName);
        miners.forEach(miner -> {
            String minerOwnerUniqueId = miner.getPersistentDataContainer().get(VillageMinerPlugin.ownerMinerAttributeKey, PersistentDataType.STRING);
            villageMinerPlugin.getLogger().info("minerOwnerUniqueId: " + minerOwnerUniqueId);
            if (minerOwnerUniqueId == null || playerName.equalsIgnoreCase(minerOwnerUniqueId)) {
                if (miner instanceof CraftVillager && owner instanceof CraftPlayer) {
                    villageMinerPlugin.getLogger().info("execute unfollow");
                    CraftVillager craftVillager = (CraftVillager) miner;
                    craftVillager.getHandle().setTarget(null, FOLLOW_LEADER, true);
                    miner.getPersistentDataContainer().set(VillageMinerPlugin.modeAttributeKey, PersistentDataType.STRING, "WORK");
                }
            }
        });
    }

}
