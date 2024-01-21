package com.memoritta.villageminer;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VillageMinerPlugin extends JavaPlugin {

    public static NamespacedKey isMinerAttributeKey;
    public static NamespacedKey miningTimeAttributeKey;
    public static NamespacedKey targetXAttributeKey;
    public static NamespacedKey targetYAttributeKey;
    public static NamespacedKey targetZAttributeKey;
    public static NamespacedKey locationXAttributeKey;
    public static NamespacedKey locationYAttributeKey;
    public static NamespacedKey locationZAttributeKey;
    public static final String MINER = "miner";

    private Logger logger = this.getLogger();


    @Override
    public void onEnable() {

        isMinerAttributeKey = new NamespacedKey(this, "miner_type");
        miningTimeAttributeKey = new NamespacedKey(this, "is_digging");
        targetXAttributeKey = new NamespacedKey(this, "target_X");
        targetYAttributeKey = new NamespacedKey(this, "target_Y");
        targetZAttributeKey = new NamespacedKey(this, "target_Z");
        locationXAttributeKey = new NamespacedKey(this, "location_X");
        locationYAttributeKey = new NamespacedKey(this, "location_Y");
        locationZAttributeKey = new NamespacedKey(this, "location_Z");

        logger.setLevel(Level.INFO);

        logger.info("VillageMiner has been enabled!");
        this.getCommand("spawnvillager").setExecutor(new VillagerCommand(this, logger));
        new VillagerPickupTask(this).runTaskTimer(this, 0L, 40L);

        logger.info("Plugin enabled.");

    }

    @Override
    public void onDisable() {
        getLogger().info("VillageMiner has been disabled.");
    }
}
