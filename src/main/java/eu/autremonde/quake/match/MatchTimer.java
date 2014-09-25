/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.match;

import eu.autremonde.quake.AutreQuake;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.config.Settings;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MatchTimer {

    private AutreQuake plugin;
    private Match match;
    private Stage currentStage;

    private BukkitTask gameTask;
    private int countdown;

    public MatchTimer(Match match) {
        this.plugin = AutreQuake.getPlugin();
        this.match = match;
        this.currentStage = Stage.WAITING;
    }

    public void run() {
        // TASK CONFIGURATION FOR STAGE(S) STARTING & FORCE_STARTING
        if(currentStage == Stage.STARTING || currentStage == Stage.FORCE_STARTING) {
            this.countdown = currentStage == Stage.STARTING ? Settings.MIN_PLAYER_COUNTDOWN.asInt() + 1 : Settings.MAX_PLAYER_COUNTDOWN.asInt() + 1;
            this.gameTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if(currentStage != Stage.FORCE_STARTING && !match.canStart()) {
                        setCurrentStage(Stage.WAITING);
                        Messaging.broadcast(match, Lang.Broadcasts.LOBBY_CANT_START, Lang.FormatType.PLAYER_LOBBY_EVENT.getVarMap(null, match.getLobby()));

                    } else {
                        countdown -= 1;
                        if(countdown <= 0) match.startGame();
                        else if(Lang.Countdown.hasMessage(countdown)) Messaging.broadcast(match, Lang.Countdown.toString(countdown));
                        match.getLobby().updateLobbySigns();
                    }
                }
            }.runTaskTimer(plugin, 20, 20);

        // TASK CONFIGURATION FOR STAGE RUNNING
        } else if(currentStage == Stage.RUNNING) {
            this.countdown = Settings.KILL_CHECK_COUNTDOWN.asInt();
            this.gameTask = new BukkitRunnable() {
                @Override
                public void run() {
                    countdown -= 1;
                    if(countdown > 0) return;
                    Messaging.broadcast(match, Lang.Broadcasts.LOBBY_FORCE_ENDING.toString());
                    match.resetGame();
                }
            }.runTaskTimer(plugin, 20, 20);

        // TASK CONFIGURATION FOR STAGE ENDING
        } else if(currentStage == Stage.ENDING) {
            this.countdown = Settings.END_GAME_COUNTDOWN.asInt() + 1;
            this.gameTask = new BukkitRunnable() {
                @Override
                public void run() {
                    countdown -= 1;
                    Player winner = Bukkit.getPlayer(match.getWinner());
                    if(countdown <= 0) match.resetGame();
                    else if(countdown % 5 == 0 && winner != null) launchFirework(winner.getLocation());
                }
            }.runTaskTimer(plugin, 20, 20);
        }
    }

    private void cancel() {
        gameTask.cancel();
        this.gameTask = null;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        if(gameTask != null) cancel();
        this.currentStage = currentStage;
        this.countdown = 0;
        if(currentStage.isRunnable()) run();
        if(currentStage != Stage.DISABLING) match.getLobby().updateLobbySigns();
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    private void launchFirework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).build());
        firework.setFireworkMeta(meta);
    }
}
