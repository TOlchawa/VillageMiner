package com.memoritta.villageminer.util;

import com.memoritta.villageminer.VillageMinerPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType    ;

public class VillageMinerUtils {

    private final VillageMinerPlugin plugin;

    public VillageMinerUtils(VillageMinerPlugin plugin) {
        this.plugin = plugin;
    }

    public Location calculateTargetLocation(Villager villager) {
        Integer iX = villager.getPersistentDataContainer().get(VillageMinerPlugin.targetXAttributeKey, PersistentDataType.INTEGER);
        Integer iY = villager.getPersistentDataContainer().get(VillageMinerPlugin.targetYAttributeKey, PersistentDataType.INTEGER);
        Integer iZ = villager.getPersistentDataContainer().get(VillageMinerPlugin.targetZAttributeKey, PersistentDataType.INTEGER);
        Location targetLocation = new Location(villager.getWorld(), iX, iY, iZ);
        plugin.getLogger().finest("saved target location: " + targetLocation);
        return targetLocation;
    }

    public Location calculateVillagerLocation(Villager villager) {
        String sX = villager.getPersistentDataContainer().get(VillageMinerPlugin.locationXAttributeKey, PersistentDataType.STRING);
        String sY = villager.getPersistentDataContainer().get(VillageMinerPlugin.locationYAttributeKey, PersistentDataType.STRING);
        String sZ = villager.getPersistentDataContainer().get(VillageMinerPlugin.locationZAttributeKey, PersistentDataType.STRING);

        Double dX = Double.parseDouble(sX);
        Double dY = Double.parseDouble(sY);
        Double dZ = Double.parseDouble(sZ);


        Location location = new Location(villager.getWorld(), dX.doubleValue(), dY.doubleValue(), dZ.doubleValue());
        plugin.getLogger().finest("saved villager location: " + location);

        return location;
    }


}
