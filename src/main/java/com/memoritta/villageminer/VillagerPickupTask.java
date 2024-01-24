package com.memoritta.villageminer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VillagerPickupTask extends BukkitRunnable {

    public static final int INITIAL_TIME_TO_DIGGING = 1;
    private final VillageMinerPlugin plugin;
    private final Map<UUID,AtomicInteger> minerAxeDurability = new HashMap<>();
    private final VillageMinerListener villageMinerListener;
    private final VillageMinerUtils utils;

    public VillagerPickupTask(VillageMinerPlugin plugin, VillageMinerListener villageMinerListener, VillageMinerUtils utils) {
        this.plugin = plugin;
        this.villageMinerListener = villageMinerListener;
        this.utils = utils;
    }

    @Override
    public void run() {

        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {

            if (isViallageMinerProfession(entity)) {
                Villager villager = (Villager) entity;

                UUID uuid = villager.getUniqueId();
                if (minerAxeDurability.containsKey(uuid)) {
                    AtomicInteger durability = minerAxeDurability.get(uuid);
                    if (durability != null && durability.get() > 0) {
                        if (isMiningInProgress(villager)) {
                            processMining(villager);
                        } else {
                            startMining(villager);
                        }
                    } else {
                        processGatheringPickaxe(villager);
                    }
                } else {
                    minerAxeDurability.put(uuid, new AtomicInteger(0));
                }


            }
        }
    }

    private void processMining(Villager villager) {
        plugin.getLogger().finest("Process mining");
//        Location villagerLocation = utils.calculateVillagerLocation(villager);
//        plugin.getLogger().finest("Mining location: " + villagerLocation);
//        villager.teleport(villagerLocation);

        Integer time = villager.getPersistentDataContainer().get(VillageMinerPlugin.miningTimeAttributeKey, PersistentDataType.INTEGER);
        if (time > 1) {
            int remainingTime = time.intValue() - 1;
            plugin.getLogger().finest("remaining time: " + remainingTime);
            villager.getPersistentDataContainer().set(VillageMinerPlugin.miningTimeAttributeKey, PersistentDataType.INTEGER, Integer.valueOf(remainingTime));

        } else {

            Location targetLocation = utils.calculateTargetLocation(villager);
            Block block = targetLocation.getWorld().getBlockAt(targetLocation);

            if (block.getType() == Material.STONE) {
                plugin.getLogger().finest("mining DONE!");
                block.setType(Material.AIR);


                ItemStack cobblestoneItem = new ItemStack(Material.COBBLESTONE);
                World world = targetLocation.getWorld();
                Item droppedItem = world.dropItem(new Location(world, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ()), cobblestoneItem);
                droppedItem.setPickupDelay(20 * 3);
            }



            villager.getPersistentDataContainer().remove(VillageMinerPlugin.miningTimeAttributeKey);
            startMining(villager);
        }
    }

    private boolean isMiningInProgress(Villager villager) {
        Integer miningTime = villager.getPersistentDataContainer().get(VillageMinerPlugin.miningTimeAttributeKey, PersistentDataType.INTEGER);
        if (miningTime != null) {
            return true;
        }
        return false;
    }

    private void startMining(Villager villager) {
        UUID uniqueId = villager.getUniqueId();
        int durability = minerAxeDurability.get(uniqueId).get();
        if (durability > 0) {
            digg(villager, 1);
        } else {
            minerAxeDurability.remove(uniqueId);
        }
    }

    private void digg(Villager villager, int range) {
        Location villagerLocation = villager.getLocation();


        for (int y = range+1; y >= -range; y--) {
            for (int z = range; z >= -range; z--) {
                for (int x = -range; x <= range; x++) {

                    if (x == 0 && y == 0 && z == -1) {
                        continue;
                    }

                    if (checkBlock(villager, x, y, z, villagerLocation)) return;
                }
            }
        }
    }

    private boolean checkBlock(Villager villager, int x, int y, int z, Location villagerLocation) {
        plugin.getLogger().finest("checking "+ x +","+ y +","+ z +" ...");

        Location targetLocation = new Location(villager.getWorld(), villager.getLocation().getX(), villager.getLocation().getY(), villager.getLocation().getZ());
        targetLocation = targetLocation.add(x, y, z);
        Block block = targetLocation.getWorld().getBlockAt(targetLocation);
        if (block.getType() == Material.STONE) {

            villager.getPersistentDataContainer().set(VillageMinerPlugin.locationXAttributeKey, PersistentDataType.STRING, Double.toString(villagerLocation.getX()));
            villager.getPersistentDataContainer().set(VillageMinerPlugin.locationYAttributeKey, PersistentDataType.STRING, Double.toString(villagerLocation.getY()));
            villager.getPersistentDataContainer().set(VillageMinerPlugin.locationZAttributeKey, PersistentDataType.STRING, Double.toString(villagerLocation.getZ()));

            villager.getPersistentDataContainer().set(VillageMinerPlugin.miningTimeAttributeKey, PersistentDataType.INTEGER, Integer.valueOf(INITIAL_TIME_TO_DIGGING));
            villager.getPersistentDataContainer().set(VillageMinerPlugin.targetXAttributeKey, PersistentDataType.INTEGER, Integer.valueOf(targetLocation.getBlockX()));
            villager.getPersistentDataContainer().set(VillageMinerPlugin.targetYAttributeKey, PersistentDataType.INTEGER, Integer.valueOf(targetLocation.getBlockY()));
            villager.getPersistentDataContainer().set(VillageMinerPlugin.targetZAttributeKey, PersistentDataType.INTEGER, Integer.valueOf(targetLocation.getBlockZ()));

            UUID uniqueId = villager.getUniqueId();
            int durability = minerAxeDurability.get(uniqueId).decrementAndGet();
            plugin.getLogger().finest("new durability is: " + durability);

            return true;
        }
        return false;
    }

    private void processGatheringPickaxe(Villager villager) {
        plugin.getLogger().finest("Check for items around the villager" );
        for (Entity nearbyEntity : villager.getNearbyEntities(5, 5, 5)) { // Adjust the range as needed
            if (nearbyEntity instanceof Item) {
                Item item = (Item) nearbyEntity;
                ItemStack itemStack = item.getItemStack();

                if (isPickaxe(itemStack) && isItemUndamaged(itemStack)) {
                    simulateVillagerPickupBreath(villager, item);
                }
            }

        }
    }

    private boolean isPickaxe(ItemStack itemStack) {
        boolean resut;
        Material type = itemStack.getType();
        switch (type) {
            case NETHERITE_PICKAXE, DIAMOND_PICKAXE, IRON_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, WOODEN_PICKAXE:
                resut = true;
                break;
            default:
                resut = false;
                break;
        }
        return resut;
    }

    private void simulateVillagerPickupBreath(Villager villager, Item item) {

        plugin.getLogger().finest("villager: " + villager.getProfession() + " pickup " + item.getName());
        int durability = calculateDurability(item.getItemStack().getType());
        minerAxeDurability.put(villager.getUniqueId(), new AtomicInteger(durability));
        item.remove();

    }

    private int calculateDurability(Material type) {
        int result = 0;
        switch (type) {
            case NETHERITE_PICKAXE:
                result = 2031;
            break;
            case DIAMOND_PICKAXE:
                result = 1561;
            break;
            case IRON_PICKAXE:
                result = 250;
            break;
            case STONE_PICKAXE:
                result = 131;
            break;
            case GOLDEN_PICKAXE:
                result = 32;
            break;
            case WOODEN_PICKAXE:
                result = 59;
            break;
            default:
            break;
        }
        return result;
    }

    public boolean isItemUndamaged(ItemStack itemStack) {
        plugin.getLogger().finest("itemStack: " + itemStack);
        plugin.getLogger().finest("itemStack.hasItemMeta(): " + itemStack.hasItemMeta());
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            plugin.getLogger().finest("meta: " + meta);
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                plugin.getLogger().finest("damageable: " + damageable);
                return damageable.getDamage() == 0;
            }
        }
        return true;
    }

    private boolean isViallageMinerProfession(Entity entity) {
        boolean result = false;
        if (entity instanceof Villager) {
            Villager villager = (Villager) entity;
            plugin.getLogger().finest("Villager: " + villager);
            result = Villager.Profession.NONE.equals(villager.getProfession())
                    && VillageMinerPlugin.MINER.equals(villager.getPersistentDataContainer().get(VillageMinerPlugin.isMinerAttributeKey, PersistentDataType.STRING));
            if (result) {
                plugin.getLogger().finest("isViallageMinerProfession: " + result);
            } else {
                plugin.getLogger().finest("isViallageMinerProfession: " + result);
            }
        }
        return result;
    }
}
