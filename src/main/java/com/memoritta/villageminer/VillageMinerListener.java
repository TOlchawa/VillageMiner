package com.memoritta.villageminer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public interface VillageMinerListener {
    void register(Villager miner);

    void killMiners(Player owner);
}
