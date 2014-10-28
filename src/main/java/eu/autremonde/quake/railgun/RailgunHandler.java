/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.railgun;

import eu.autremonde.quake.config.Configuration;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.lobby.LobbyHandler;
import eu.autremonde.quake.match.Stage;
import eu.autremonde.quake.util.Messaging;
import eu.autremonde.quake.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RailgunHandler {

    private static YamlConfiguration gunConfig = Configuration.getConfig("railguns");

    private static Map<String, Railgun> loadedRailguns = new TreeMap<String, Railgun>(String.CASE_INSENSITIVE_ORDER);
    private static Map<UUID, Long> respawnTimes = new HashMap<UUID, Long>();

    public static void loadRailguns() {
        for(String gunID : gunConfig.getKeys(false))
            loadedRailguns.put(gunID, Railgun.deserialize(gunConfig.getConfigurationSection(gunID)));
        Messaging.printInfo("Railguns successfully loaded!");
    }

    public static Railgun getRailgun(String gunID) {
        return loadedRailguns.get(gunID);
    }

    public static ItemStack metaStack(Material material, String displayName, List<String> lore, String ench) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta stackMeta = itemStack.getItemMeta();
        stackMeta.setDisplayName(StringUtil.colorize(displayName));
        if(lore != null && !lore.isEmpty()) stackMeta.setLore(StringUtil.colorizeList(lore));
        if(ench != null && !ench.equalsIgnoreCase("none")) stackMeta.addEnchant(Enchantment.getByName(ench.split(":")[0]), StringUtil.asInt(ench.split(":")[1]), true);
        itemStack.setItemMeta(stackMeta);
        return itemStack;
    }

    public static boolean handleHit(Player killed, Player killer) {
        Lobby lobby = LobbyHandler.getLobbyFromPlayer(killed);
        if (lobby == null || lobby.getStage() != Stage.RUNNING) return false;
        killed.setHealth(0);
        if(lobby.getMatch().getKillStreak(killed) >= 5)
            Messaging.broadcast(lobby, Lang.Broadcasts.PLAYER_SHOTDOWN.toString().replace("%player%", killed.getName()).replace("%killer%", killer.getName()));
        lobby.getMatch().resetKillStreak(killed);
        Messaging.broadcast(lobby, Lang.Broadcasts.PLAYER_KILLED.toString().replace("%player%", killed.getName()).replace("%killer%", killer.getName()));
        lobby.getMatch().addKill(killer);
        return true;
    }

    public static boolean isSpawnProtected(Player player) {
        return respawnTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - respawnTimes.get(player.getUniqueId()) < 3000;
    }

    public static void setRespawnTime(Player player) {
        respawnTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
