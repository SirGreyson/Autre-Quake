/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake;

import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.config.Settings;
import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.lobby.LobbyHandler;
import eu.autremonde.quake.match.Stage;
import eu.autremonde.quake.protocol.ProtocolHandler;
import eu.autremonde.quake.railgun.RailgunHandler;
import eu.autremonde.quake.stats.StatHandler;
import eu.autremonde.quake.util.Messaging;
import eu.autremonde.quake.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventHandler implements Listener {

    private AutreQuake plugin;

    public EventHandler(AutreQuake plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Messaging.printInfo("Events successfully registered!");
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        StatHandler.loadStats(e.getPlayer());
        PlayerUtil.resetPlayer(e.getPlayer(), true, true);
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent e) {
        Lobby lobby = LobbyHandler.getLobbyFromPlayer(e.getPlayer());
        if(lobby != null) lobby.removePlayer(e.getPlayer());
        e.setQuitMessage(null);
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.getDrops().clear();
        e.setDeathMessage(null);
        if(Settings.FORCE_RESPAWN_DELAY.asInt() < 0) return;
        final Player player = e.getEntity();
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if(player == null || !player.isDead()) return;
                ProtocolHandler.forceRespawn(player);
            }
        }, Settings.FORCE_RESPAWN_DELAY.asInt() * 20);
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Lobby lobby = LobbyHandler.getLobbyFromPlayer(e.getPlayer());
        if(lobby == null) e.setRespawnLocation(Bukkit.getWorld(Settings.SPAWN_WORLD.asString()).getSpawnLocation());
        else {
            e.setRespawnLocation(lobby.getActiveArena().getNextSpawnLoc());
            if(lobby.getStage() != Stage.RUNNING) return;
            RailgunHandler.getRailgun("DEFAULT").giveRailGun(e.getPlayer());
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false));
        }
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player) || e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;
        e.setCancelled(true);
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign) {
            Lobby lobby = LobbyHandler.getLobbyFromSign(e.getClickedBlock().getLocation());
            if(lobby != null && lobby.canAddPlayer(e.getPlayer())) lobby.addPlayer(e.getPlayer());

        } else if(e.getAction() == Action.RIGHT_CLICK_AIR && e.getItem() != null)
            if(RailgunHandler.getRailgun("DEFAULT").isSimilar(e.getItem()) && e.getPlayer().getTotalExperience() <= 0)
                RailgunHandler.getRailgun("DEFAULT").shoot(e.getPlayer()); //TODO This is not dynamic
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent e) {
        if(!e.getLine(0).equalsIgnoreCase("[AutreQuake]")) return;
        Lobby lobby = LobbyHandler.getLobby(e.getLine(1));
        if(lobby != null) {
            lobby.addLobbySign(e.getBlock().getLocation());
            Messaging.send(e.getPlayer(), Lang.Messages.LOBBY_SIGN_ADDED.toString().replace("%lobby%", lobby.getLobbyID()));
        } else {
            e.getBlock().breakNaturally();
            Messaging.send(e.getPlayer(), Lang.Messages.INVALID_LOBBY_ID);
        }
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if(!(e.getBlock().getState() instanceof Sign)) return;
        Lobby lobby = LobbyHandler.getLobbyFromSign(e.getBlock().getLocation());
        if(lobby != null) {
            if (!e.getPlayer().hasPermission("autrequake.admin")) e.setCancelled(true);
            else {
                lobby.removeLobbySign(e.getBlock().getLocation());
                Messaging.send(e.getPlayer(), Lang.Messages.LOBBY_SIGN_REMOVED.toString().replace("%lobby%", lobby.getLobbyID()));
            }
        }
    }

    @org.bukkit.event.EventHandler (priority = EventPriority.HIGHEST)
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
