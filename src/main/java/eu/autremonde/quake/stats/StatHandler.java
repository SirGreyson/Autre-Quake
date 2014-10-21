/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.stats;

import eu.autremonde.quake.config.Configuration;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatHandler {

    private static YamlConfiguration c = Configuration.getConfig("stats");

    private static Map<UUID, StatPack> loadedStats = new HashMap<UUID, StatPack>();

    public static void loadStats() {
        for (String uuid : c.getKeys(false))
            loadedStats.put(UUID.fromString(uuid), StatPack.deserialize(c.getConfigurationSection(uuid)));
        Messaging.printInfo("Stats successfully loaded!");
    }

    /*public static void loadStats(Player player) {
        ConfigurationSection s = c.getConfigurationSection(player.getUniqueId().toString());
        if(s == null) loadedStats.put(player.getUniqueId(), new StatPack());
        else loadedStats.put(player.getUniqueId(), StatPack.deserialize(s));
    }

    public static void saveStats(Player player) {
        System.out.println("Saved Stats{player=" + player.getName() + ",coins=" + getStats(player).getCoinCount() + ",kills=" + getStats(player).getKillCount() + ",wins=" + getStats(player).getWinCount() + "}");
        c.set(player.getUniqueId().toString(), loadedStats.get(player.getUniqueId()).serialize());
        loadedStats.remove(player.getUniqueId());
    }*/

    public static void saveStats() {
        for(UUID uuid : loadedStats.keySet())
            c.set(uuid.toString(), loadedStats.get(uuid).serialize());
        Messaging.printInfo("Stats successfully saved!");
    }

    public static StatPack getStats(Player player) {
        if (!loadedStats.containsKey(player.getUniqueId()))
            loadedStats.put(player.getUniqueId(), new StatPack());
        return loadedStats.get(player.getUniqueId());
    }

    public static void giveCoins(Player player, int amount) {
        if(amount <= 0) return;
        getStats(player).addCoins(amount);
        Messaging.send(player, ChatColor.GOLD + "+ " + amount + (amount > 1 ? " pièces!" : " pièce!"));
    }
}
