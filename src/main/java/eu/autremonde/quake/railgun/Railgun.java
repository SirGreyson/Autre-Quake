/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.railgun;

import eu.autremonde.quake.AutreQuake;
import eu.autremonde.quake.FireworkColor;
import eu.autremonde.quake.config.Lang;
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
    private FireworkColor fireworkColor;
    private FireworkEffect.Type fireworkType;

    private Map<UUID, BukkitTask> taskMap = new HashMap<UUID, BukkitTask>();

    public Railgun(Material gunType, String displayName, List<String> lore, String ench, String shotSound, String hitSound, FireworkColor fireworkColor, FireworkEffect.Type fireworkType) {
        this.gunStack = RailgunHandler.metaStack(gunType, displayName, lore, ench);
        this.shotSound = shotSound;
        this.hitSound = hitSound;
        this.fireworkColor = fireworkColor;
        this.fireworkType = fireworkType;
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
        meta.addEffect(FireworkEffect.builder().with(fireworkType).withColor(fireworkColor.getColor()).build());
        firework.setFireworkMeta(meta);
        return firework;
    }

    public void shoot(Player shooter) {
        playSound(shotSound, shooter.getLocation());
        doCooldown(shooter);
        List<UUID> hitPlayers = new ArrayList<UUID>();
        List<Block> lineOfSight = shooter.getLineOfSight(null, 50);
        for (Block block : lineOfSight) {
            if(block.getType() != Material.AIR) break;
            Firework trail = block.getWorld().spawn(block.getLocation(), Firework.class);
            Player player = getNearestPlayer(trail);
            trail.remove();
            if (player != null && player != shooter && !hitPlayers.contains(player.getUniqueId())) {
                hitPlayers.add(player.getUniqueId());
                playSound(hitSound, player.getLocation());
                doHit(player.getLocation());
                RailgunHandler.handleHit(player, shooter);
            }
        }
        if(Lang.FreeStyleKills.hasMessage(hitPlayers.size())) Messaging.send(shooter, Lang.FreeStyleKills.toString(shooter, hitPlayers.size()));
        if(hitPlayers.size() > 0) StatHandler.giveCoins(shooter, hitPlayers.size());
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
        player.giveExp(16);
        taskMap.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(AutreQuake.getPlugin(), new Runnable() {
            @Override
                public void run() {
                if(player == null || player.getTotalExperience() <= 0) taskMap.get(player.getUniqueId()).cancel();
                else player.giveExp(-4);
            }
        }, 0, 10));
    }

    public static Railgun deserialize(ConfigurationSection c) {
        return new Railgun(Material.valueOf(c.getString("MATERIAL")), c.getString("DISPLAY_NAME"), c.getStringList("ITEM_LORE"), c.getString("ENCHANTMENT"),
                c.getString("SHOT_SOUND"), c.getString("HIT_SOUND"), FireworkColor.valueOf(c.getString("FIREWORK_COLOR")), FireworkEffect.Type.valueOf(c.getString("FIREWORK_TYPE")));
    }
}
