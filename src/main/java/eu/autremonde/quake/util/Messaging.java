/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.util;

import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.match.Match;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Logger;

public class Messaging {

    public static Logger log = Logger.getLogger("AutreQuake");
    public static String PREFIX = Lang.Messages.PREFIX.toString();
    public static String BROADCAST_PREFIX = Lang.Broadcasts.BROADCAST_PREFIX.toString();

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(StringUtil.colorize(PREFIX + " " + message));
    }

    public static void send(CommandSender sender, Lang.Messages message) {
        send(sender, message.toString());
    }

    public static void broadcast(Match match, String message) {
        for(Player player : match.getPlayers())
            player.sendMessage(StringUtil.colorize(BROADCAST_PREFIX + " " + message));
    }

    public static void broadcast(Lobby lobby, String message) {
        for(Player player : lobby.getPlayers())
            player.sendMessage(StringUtil.colorize(BROADCAST_PREFIX + " " + message));
    }

    public static void broadcast(Match match, Lang.Broadcasts message, Map<String, String> args) {
        String msg = message.toString();
        if(args != null)
            for(String arg : args.keySet()) msg = msg.replace(arg, args.get(arg) == null ? "NULL" : args.get(arg));
        broadcast(match, msg);
    }

    public static void broadcast(Lobby lobby, Lang.Broadcasts message, Map<String, String> args) {
        String msg = message.toString();
        if(args != null)
            for(String arg : args.keySet()) msg = msg.replace(arg, args.get(arg) == null ? "NULL" : args.get(arg));
        broadcast(lobby, msg);
    }

    public static void broadcastNoPrefix(Match match, Lang.Broadcasts message, Map<String, String> args) {
        String msg = message.toString();
        if(args != null)
            for(String arg : args.keySet()) msg = msg.replace(arg, args.get(arg) == null ? "NULL" : args.get(arg));
        for(Player player : match.getPlayers()) player.sendMessage(StringUtil.colorize(msg));
    }

    public static void printInfo(String message) {
        log.info("§e" + message);
    }

    public static void printErr(String message) {
        log.severe("§c" + message);
    }

}
