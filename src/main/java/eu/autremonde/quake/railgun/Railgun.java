/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.railgun;

import eu.autremonde.quake.AutreQuake;
import eu.autremonde.quake.FireworkColor;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.lobby.LobbyHandler;
import eu.autremonde.quake.stats.StatHandler;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Railgun {

    private ItemStack gunStack;
    private String shotSound;
    private String hitSound;
    private FireworkEffect.Type fireworkType;
    private List<Color> fireworkColor;

    private HashSet<Byte> transparent = new HashSet<Byte>();

    private Map<UUID, BukkitTask> taskMap = new HashMap<UUID, BukkitTask>();

    public Railgun(Material gunType, String displayName, List<String> lore, String ench, String shotSound, String hitSound, FireworkEffect.Type fireworkType, List<Color> fireworkColor) {
        this.gunStack = RailgunHandler.metaStack(gunType, displayName, lore, ench);
        this.shotSound = shotSound;
        this.hitSound = hitSound;
        this.fireworkType = fireworkType;
        this.fireworkColor = fireworkColor;
        for (Material material : Material.values()) {
            if (material.isTransparent()) {
                transparent.add((byte) material.getId());
            }
        }
    }

    public boolean isSimilar(ItemStack itemStack) {
        return itemStack.isSimilar(gunStack);
    }

    public void giveRailGun(Player player) {
        player.getInventory().addItem(gunStack);
    }

    private void playSound(String sound, Location loc) {
        String[] args = sound.split(":");
        loc.getWorld().playSound(loc, Sound.valueOf(args[0]), Float.valueOf(args[1]), Float.valueOf(args[2]));
    }

    private Firework createFirework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(fireworkType).withColor(fireworkColor).build());
        firework.setFireworkMeta(meta);
        return firework;
    }

    public void shoot(Player shooter) {
        doCooldown(shooter);
        playSound(shotSound, shooter.getLocation());
        List<UUID> hitPlayers = new ArrayList<UUID>();
        List<Block> lineOfSight = shooter.getLineOfSight(transparent, 50);
        for (Block block : lineOfSight) {
            if (block.getType() != Material.AIR && block.getType().isSolid()) break;
            Firework trail = block.getWorld().spawn(block.getLocation(), Firework.class);
            Player player = getNearestPlayer(trail);
            trail.remove();
            if (player != null && !player.isDead() && player != shooter && !hitPlayers.contains(player.getUniqueId()) && !RailgunHandler.isSpawnProtected(player)) {
                if (RailgunHandler.handleHit(player, shooter)) {
                    hitPlayers.add(player.getUniqueId());
                    playSound(hitSound, player.getLocation());
                    doHit(player.getLocation());
                }
            }
        }
        if(LobbyHandler.getLobbyFromPlayer(shooter) != null && Lang.FreeStyleKills.hasMessage(hitPlayers.size()))
            Messaging.broadcast(LobbyHandler.getLobbyFromPlayer(shooter), Lang.FreeStyleKills.toString(shooter, hitPlayers.size()));
        if (hitPlayers.size() > 0) StatHandler.giveCoins(shooter, hitPlayers.size() + 1);
    }

    private Player getNearestPlayer(Firework firework) {
        for(Entity ent : firework.getNearbyEntities(0.5, 0.5, 0.5))
            if(ent instanceof Player) return (Player) ent;
        return null;
    }

    private void doHit(Location loc) {
        final Firework firework = createFirework(loc);
        Bukkit.getScheduler().runTaskLater(AutreQuake.getPlugin(), new Runnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }, 5);
    }

    private void doCooldown(final Player player) {
        player.setTotalExperience(0);
        player.giveExp(16);
        if(taskMap.containsKey(player.getUniqueId())) taskMap.get(player.getUniqueId()).cancel();
        taskMap.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(AutreQuake.getPlugin(), new Runnable() {
            @Override
                public void run() {
                if(player == null || player.isDead() || player.getTotalExperience() <= 0) taskMap.get(player.getUniqueId()).cancel();
                else player.giveExp(-1);
            }
        }, 0, 2));
    }

    public static Railgun deserialize(ConfigurationSection c) {
        return new Railgun(Material.valueOf(c.getString("MATERIAL")), c.getString("DISPLAY_NAME"), c.getStringList("ITEM_LORE"), c.getString("ENCHANTMENT"),
                c.getString("SHOT_SOUND"), c.getString("HIT_SOUND"), FireworkEffect.Type.valueOf(c.getString("FIREWORK_TYPE")), FireworkColor.getColors(c.getStringList("FIREWORK_COLOR")));
    }
}
