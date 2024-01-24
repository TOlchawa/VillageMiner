package com.memoritta.villageminer;

import com.memoritta.villageminer.controller.VillageMinerController;
import com.memoritta.villageminer.manager.VillagerCommand;
import com.memoritta.villageminer.util.VillageMinerUtils;
import com.memoritta.villageminer.util.VillagerPickupTask;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VillageMinerPlugin extends JavaPlugin {

    public static NamespacedKey isMinerAttributeKey;
    public static NamespacedKey ownerMinerAttributeKey;
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
        ownerMinerAttributeKey = new NamespacedKey(this, "miner_owner");
        miningTimeAttributeKey = new NamespacedKey(this, "is_digging");
        targetXAttributeKey = new NamespacedKey(this, "target_X");
        targetYAttributeKey = new NamespacedKey(this, "target_Y");
        targetZAttributeKey = new NamespacedKey(this, "target_Z");
        locationXAttributeKey = new NamespacedKey(this, "location_X");
        locationYAttributeKey = new NamespacedKey(this, "location_Y");
        locationZAttributeKey = new NamespacedKey(this, "location_Z");

        logger.setLevel(Level.INFO);

        logger.finest("VillageMiner has been enabled!");
        VillageMinerUtils utils = new VillageMinerUtils(this);
        VillageMinerController villageMinerController = new VillageMinerController(this, utils);
        VillagerCommand commandsExecutor = new VillagerCommand(this, logger, villageMinerController);
        this.getCommand("spawnminer").setExecutor(commandsExecutor);
        this.getCommand("killminer").setExecutor(commandsExecutor);
        new VillagerPickupTask(this, villageMinerController, utils).runTaskTimer(this, 0L, 40L);
        villageMinerController.runTaskTimer(this, 0L, 5L);


        logger.finest("Plugin enabled.");

    }

    @Override
    public void onDisable() {
        getLogger().finest("VillageMiner has been disabled.");
    }
}
