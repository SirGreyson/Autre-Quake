package eu.autremonde.quake.match;/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

import eu.autremonde.quake.arena.Arena;
import eu.autremonde.quake.arena.ArenaHandler;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.config.Settings;
import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.protocol.ProtocolHandler;
import eu.autremonde.quake.railgun.RailgunHandler;
import eu.autremonde.quake.stats.StatHandler;
import eu.autremonde.quake.util.Messaging;
import eu.autremonde.quake.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Match {

    private Lobby lobby;

    private MatchBoard matchBoard;
    private MatchTimer matchTimer;

    private Arena activeArena;
    private List<UUID> players = new ArrayList<UUID>();
    private Map<UUID, Integer> killStreaks = new HashMap<UUID, Integer>();

    public Match(Lobby lobby) {
        this.lobby = lobby;
        this.matchBoard = new MatchBoard(this);
        this.matchTimer = new MatchTimer(this);
    }

    public Lobby getLobby() {
        return lobby;
    }

    public MatchBoard getMatchBoard() {
        return matchBoard;
    }

    public Stage getStage() {
        return matchTimer.getCurrentStage();
    }

    public void setStage(Stage stage) {
        matchTimer.setCurrentStage(stage);
        if(stage == Stage.DISABLING) return;
        lobby.updateLobbySigns();
    }

    public int getCountdown() {
        return matchTimer.getCountdown();
    }

    public void setCountdown(int countdown) {
        matchTimer.setCountdown(countdown);
    }

    public Arena getActiveArena() {
        return activeArena;
    }

    public void setActiveArena(Arena nextArena) {
        if(activeArena != null) activeArena.reset();
        this.activeArena = nextArena;
        if(activeArena != null) {
            activeArena.setActiveLobby(lobby);
            matchBoard.loadMatchBoard();
        } else {
            Messaging.printErr("Tried to set Arena for Lobby " + lobby.getLobbyID() + ", but Arena was null!");
            setStage(Stage.ERROR);
        }
    }

    public void resetKillStreak(Player player) {
        if(killStreaks.containsKey(player.getUniqueId()))
            killStreaks.remove(player.getUniqueId());
    }

    public int getKillStreak(Player player) {
        if(!killStreaks.containsKey(player.getUniqueId())) return 0;
        return killStreaks.get(player.getUniqueId());
    }

    public void addKill(Player player) {
        killStreaks.put(player.getUniqueId(), getKillStreak(player) + 1);
        StatHandler.getStats(player).addKill();
        if(Lang.KillStreaks.hasMessage(getKillStreak(player))) Messaging.broadcast(this, Lang.KillStreaks.toString(player, getKillStreak(player)));
        if(Lang.KillStreaks.getCoins(getKillStreak(player)) != 0) StatHandler.giveCoins(player, Lang.KillStreaks.getCoins(getKillStreak(player)));
        for(Player p : player.getWorld().getPlayers()) ProtocolHandler.sendCustomSound(p, "killstreaks." + getKillStreak(player));
        if(matchBoard.addPoint(player) >= Settings.POINTS_TO_WIN.asInt()) finishGame(false);
        else setCountdown(Settings.KILL_CHECK_COUNTDOWN.asInt());
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        return PlayerUtil.getPlayers(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public String getWinner() {
        return matchBoard.getWinner();
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        matchBoard.addPlayer(player);
        if(getStage() == Stage.WAITING && canStart()) setStage(Stage.STARTING);
        else if(isFull() && getCountdown() > Settings.MAX_PLAYER_COUNTDOWN.asInt() + 1) setCountdown(Settings.MAX_PLAYER_COUNTDOWN.asInt() + 1);
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        matchBoard.removePlayer(player);
        if(getStage() == Stage.RUNNING && players.size() <= 1) finishGame(false);
    }

    public boolean canStart() {
        return players.size() >= activeArena.getMinPlayers();
    }

    public boolean isFull() {
        return players.size() >= activeArena.getMaxPlayers();
    }

    public void startGame() {
        if(Lang.Countdown.hasMessage(0)) Messaging.broadcast(this, Lang.Countdown.toString(0));
        setStage(Stage.RUNNING);
        for(Player player : getPlayers()) {
            player.teleport(activeArena.getNextSpawnLoc());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true));
            RailgunHandler.getRailgun("DEFAULT").giveRailGun(player);
            ProtocolHandler.sendCustomSound(player, activeArena.getJoinSound());
        }
    }

    public void finishGame(boolean isDisabling) {
        setStage(isDisabling ? Stage.DISABLING : Stage.ENDING);
        if(isDisabling) resetGame();
        else {
            PlayerUtil.resetPlayers(PlayerUtil.getPlayers(players), false, false);
            Messaging.broadcastNoPrefix(this, Lang.Broadcasts.LOBBY_FINISHED, Lang.FormatType.PLAYER_LOBBY_EVENT.getVarMap(null, lobby));
            Messaging.broadcast(this, Lang.Broadcasts.LOBBY_ENDING.toString().replace("%time%", String.valueOf(Settings.END_GAME_COUNTDOWN.asInt())));
            if(!getWinner().equalsIgnoreCase("NONE")) {
                if (matchBoard.getPoints(PlayerUtil.getPlayer(getWinner())) >= 10) {
                    StatHandler.getStats(PlayerUtil.getPlayer(getWinner())).addCoins(Settings.WINNER_COINS.asInt());
                    StatHandler.getStats(PlayerUtil.getPlayer(getWinner())).addWin();
                    if (PlayerUtil.getPlayer(getWinner()) != null)
                        Messaging.send(PlayerUtil.getPlayer(getWinner()), Lang.Messages.LOBBY_WON);
                }
            }
        }
    }

    public void resetGame() {
        PlayerUtil.resetPlayers(PlayerUtil.getPlayers(players), true, true);
        this.players = new ArrayList<UUID>();
        this.killStreaks = new HashMap<UUID, Integer>();
        if(getStage() == Stage.DISABLING) this.matchBoard.unregister();
        else {
            setActiveArena(ArenaHandler.getOpenArena());
            if(activeArena != null) setStage(Stage.WAITING);
        }
    }
}
