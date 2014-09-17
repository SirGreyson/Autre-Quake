/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.lobby;

import eu.autremonde.quake.arena.Arena;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.match.Match;
import eu.autremonde.quake.match.Stage;
import eu.autremonde.quake.util.Messaging;
import eu.autremonde.quake.util.PlayerUtil;
import eu.autremonde.quake.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class Lobby {

    private String lobbyID;
    private Set<Location> lobbySigns;

    private Match lobbyMatch;

    public Lobby(String lobbyID, Set<Location> lobbySigns) {
        this.lobbyID = lobbyID;
        this.lobbySigns = lobbySigns;
        this.lobbyMatch = new Match(this);
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public boolean isLobbySign(Location loc) {
        return lobbySigns.contains(loc) && loc.getBlock().getState() instanceof Sign;
    }

    public void addLobbySign(Location signLoc) {
        lobbySigns.add(signLoc);
        updateLobbySign((Sign) signLoc.getBlock().getState());
    }

    public void removeLobbySign(Location signLoc) {
        lobbySigns.remove(signLoc);
    }

    public void updateLobbySigns() {
        for(Location signLoc : lobbySigns)
            if(isLobbySign(signLoc)) updateLobbySign((Sign) signLoc.getBlock().getState());
    }

    private void updateLobbySign(Sign lobbySign) {
        for(int index = 0; index < 4; index++)
            lobbySign.setLine(index, Lang.Signs.valueOf(lobbyMatch.getStage().name()).fLine(index, Lang.FormatType.LOBBY_SIGN.getVarMap(this)));
        lobbySign.update();
    }

    public Match getMatch() {
        return lobbyMatch;
    }

    public Stage getStage() {
        return lobbyMatch.getStage();
    }

    public void setStage(Stage stage) {
        lobbyMatch.setStage(stage);
    }

    public int getCountdown() { return lobbyMatch.getCountdown(); }

    public Arena getActiveArena() {
        return lobbyMatch.getActiveArena();
    }

    public void setActiveArena(Arena activeArena) {
        lobbyMatch.setActiveArena(activeArena);
        updateLobbySigns();
    }

    public boolean hasPlayer(Player player) {
        return lobbyMatch.hasPlayer(player);
    }

    public List<Player> getPlayers() {
        return lobbyMatch.getPlayers();
    }

    public int getPlayerCount() {
        return lobbyMatch.getPlayerCount();
    }

    public String getWinner() {
        return lobbyMatch.getWinner();
    }

    public boolean canAddPlayer(Player player) {
        if(LobbyHandler.getLobbyFromPlayer(player) != null) Messaging.send(player, Lang.Messages.ALREADY_IN_LOBBY);
        else if(getStage() == Stage.ERROR) Messaging.send(player, Lang.Messages.LOBBY_ERROR);
        else if(lobbyMatch.isFull()) Messaging.send(player, Lang.Messages.LOBBY_FULL);
        else if(!getStage().isJoinable()) Messaging.send(player, Lang.Messages.LOBBY_RUNNING);
        else return true;
        return false;
    }

    public void addPlayer(Player player) {
        PlayerUtil.resetPlayer(player, false, true);
        lobbyMatch.addPlayer(player);
        Messaging.broadcast(this, Lang.Broadcasts.PLAYER_JOINED_LOBBY, Lang.FormatType.PLAYER_LOBBY_EVENT.getVarMap(player, this));
        player.teleport(getActiveArena().getMainSpawn());
        updateLobbySigns();
    }

    public void removePlayer(Player player) {
        Messaging.broadcast(this, Lang.Broadcasts.PLAYER_LEFT_LOBBY, Lang.FormatType.PLAYER_LOBBY_EVENT.getVarMap(player, this));
        lobbyMatch.removePlayer(player);
        updateLobbySigns();
        if(!player.isOnline()) return;
        Messaging.send(player, Lang.Messages.LOBBY_LEFT);
        PlayerUtil.resetPlayer(player, true, true);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> output = new HashMap<String, Object>();
        output.put("lobbySigns", StringUtil.parseLocList(new ArrayList<Location>(lobbySigns)));
        return output;
    }

    public static Lobby deserialize(ConfigurationSection c) {
        return new Lobby(c.getName(), new HashSet<Location>(StringUtil.parseLocStringList(c.getStringList("lobbySigns"))));
    }
}
