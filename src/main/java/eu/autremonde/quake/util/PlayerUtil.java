/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.util;

import eu.autremonde.quake.config.Settings;
import eu.autremonde.quake.protocol.ProtocolHandler;
import eu.autremonde.quake.stats.StatHandler;
import eu.autremonde.quake.stats.StatPack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerUtil {

    public static Player getPlayer(String player) {
        return Bukkit.getPlayer(player);
    }

    public static List<Player> getPlayers(List<UUID> players) {
        List<Player> output = new ArrayList<Player>();
        for(UUID player : players)
            if(Bukkit.getPlayer(player) != null) output.add(Bukkit.getPlayer(player));
        return output;
    }

    public static void resetPlayer(Player player, boolean doTeleport, boolean eraseBoard) {
        if(player.isDead()) ProtocolHandler.forceRespawn(player);
        player.setHealth(player.getMaxHealth());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        for(PotionEffect pe : player.getActivePotionEffects())
            player.removePotionEffect(pe.getType());
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        if(doTeleport) player.teleport(Bukkit.getWorld(Settings.SPAWN_WORLD.asString()).getSpawnLocation());
        if(eraseBoard) player.setScoreboard(getPlayerBoard(player));
    }

    public static void resetPlayers(List<Player> players, boolean doTeleport, boolean eraseBoard) {
        for(Player player : players) resetPlayer(player, doTeleport, eraseBoard);
    }

    public static Scoreboard getPlayerBoard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj =  board.registerNewObjective("main", "dummy");
        obj.setDisplayName(StringUtil.colorize("&c&lStatistiques"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore(ChatColor.GOLD + "Pièces:").setScore(StatHandler.getStats(player).getCoinCount());
        obj.getScore(ChatColor.AQUA + "Kills:").setScore(StatHandler.getStats(player).getKillCount());
        obj.getScore(ChatColor.GREEN + "Wins:").setScore(StatHandler.getStats(player).getWinCount());
        return board;
    }

    public static boolean updatePlayerBoard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.getObjective("main") == null || !scoreboard.getObjective("main").getDisplayName().equalsIgnoreCase(StringUtil.colorize("&c&lStatistiques")))
            return false;
        Objective obj = scoreboard.getObjective("main");
        StatPack stats = StatHandler.getStats(player);
        obj.getScore(ChatColor.GOLD + "Pièces:").setScore(stats.getCoinCount());
        obj.getScore(ChatColor.AQUA + "Kills:").setScore(stats.getKillCount());
        obj.getScore(ChatColor.GREEN + "Wins:").setScore(stats.getWinCount());
        System.out.println("Loaded Stats{player=" + player.getName() + ",coins=" + stats.getCoinCount() + ",kills=" + stats.getKillCount() + ",wins=" + stats.getWinCount() + "}");
        return true;
    }
}
