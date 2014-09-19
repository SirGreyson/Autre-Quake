/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.config;

import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.util.StringUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Lang {

    private static YamlConfiguration c = Configuration.getConfig("lang");

    public enum Messages {

        PREFIX,

        INVALID_LOBBY_ID,
        LOBBY_ID_USED,
        LOBBY_CREATED,
        LOBBY_REMOVED,
        LOBBY_SIGN_ADDED,
        LOBBY_SIGN_REMOVED,

        ALREADY_IN_LOBBY,
        NOT_IN_LOBBY,
        LOBBY_LEFT,
        LOBBY_FULL,
        LOBBY_RUNNING,
        LOBBY_ERROR,

        INVALID_ARENA_ID,
        ARENA_ID_USED,
        ARENA_CREATED,
        ARENA_REMOVED,
        ARENA_SPAWN_SET,
        ARENA_SPAWN_ADDED;

        public String toString() {
            return c.getString("messages." + this.name());
        }
    }

    public enum Broadcasts {

        BROADCAST_PREFIX,

        LOBBY_CANT_START,
        LOBBY_FINISHED,
        LOBBY_ENDING,
        LOBBY_FORCE_ENDING,

        PLAYER_JOINED_LOBBY,
        PLAYER_LEFT_LOBBY,
        PLAYER_KILLED,
        PLAYER_SHOTDOWN;

        public String toString() {
            return c.getString("broadcasts." + this.name());
        }
    }

    public enum Signs {

        WAITING,
        STARTING,
        FORCE_STARTING,
        RUNNING,
        ENDING,
        ERROR;

        public String getLine(int index) {
            return c.getString("signs." + this.name() + "." + index);
        }

        public String fLine(int index, Map<String, String> args) {
            String fLine = getLine(index);
            if(args != null)
                for(String arg : args.keySet())
                    fLine = fLine.replace(arg, args.get(arg) == null ? "NULL" : args.get(arg));
            return StringUtil.colorize(fLine);
        }
    }

    public static class Countdown {

        public static boolean hasMessage(int time) {
            return c.getString("countdown." + time) != null;
        }

        public static String toString(int time) {
            if(!hasMessage(time)) return null;
            return c.getString("countdown." + time).replace("%time%", String.valueOf(time));
        }
    }

    public static class KillCounts {

        public static boolean hasMessage(int kills) {
            return c.getString("killCounts." + kills) != null;
        }

        public static String toString(Player player, int kills) {
            if(!hasMessage(kills)) return null;
            return c.getString("killCounts." + kills).replace("%player%", String.valueOf(player.getName()));
        }
    }

    public static class KillStreaks {

        public static boolean hasMessage(int streak) {
            return c.getString("killstreaks." + streak) != null;
        }

        public static String toString(Player player, int streak) {
            if(!hasMessage(streak)) return null;
            return c.getString("killstreaks." + streak).replace("%player%", String.valueOf(player.getName()));
        }

        public static int getCoins(int streak) {
            if(streak == 5 || streak == 10 || streak == 15 || streak == 20) return streak / 5;
            else if(streak == 26) return 7;
            else if(streak == 27) return 9;
            else if(streak == 28) return 11;
            else if(streak == 29) return 13;
            else if(streak == 30) return 15;
            return 0;
        }
    }

    public static class FreeStyleKills {

        public static boolean hasMessage(int kills) {
            return c.getString("freeStyleKills." + kills) != null;
        }

        public static String toString(Player player, int kills) {
            if(!hasMessage(kills)) return null;
            return c.getString("freeStyleKills." + kills).replace("%player%", String.valueOf(player.getName()));
        }

        public static int getCoins(int kills) {
            return kills + (kills - 1);
        }
    }

    public enum FormatType {

        PLAYER_LOBBY_EVENT,
        COUNTDOWN,
        LOBBY_SIGN;

        public Map<String, String> getVarMap(final Object... args) {
            if(this == PLAYER_LOBBY_EVENT) {
                final Player player = (Player) args[0];
                final Lobby lobby = (Lobby) args[1];
                return new HashMap<String, String>() {{
                    put("%line%", "\n");
                    put("%player%", player == null ? null : player.getName());
                    put("%playersNeeded%", String.valueOf(lobby.getActiveArena().getMinPlayers() - lobby.getPlayerCount()));
                    put("%playerCount%", String.valueOf(lobby.getPlayerCount()));
                    put("%maxPlayers%", String.valueOf(lobby.getActiveArena().getMaxPlayers()));
                    put("%winner%", lobby.getWinner());
                }};
            } else if(this == COUNTDOWN) {
                return new HashMap<String, String>() {{
                    put("%time%", String.valueOf(args[0]));
                }};
            } else if(this == LOBBY_SIGN) {
                final Lobby lobby = (Lobby) args[0];
                return new HashMap<String, String>() {{
                    put("%lobby%", lobby.getLobbyID());
                    put("%arena%", lobby.getActiveArena() == null ? "NONE" : lobby.getActiveArena().getDisplayName(true));
                    put("%playerCount%", String.valueOf(lobby.getPlayerCount()));
                    put("%maxPlayers%", lobby.getActiveArena() == null ? "0" : String.valueOf(lobby.getActiveArena().getMaxPlayers()));
                    put("%time%", String.valueOf(lobby.getCountdown()));
                    put("%winner%", lobby.getWinner());
                }};
            } else return null;
        }
    }
}
