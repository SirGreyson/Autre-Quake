/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.cmd;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import eu.autremonde.quake.arena.ArenaHandler;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.lobby.LobbyHandler;
import eu.autremonde.quake.util.Messaging;
import eu.autremonde.quake.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand {

    @Command(aliases = {"create"}, desc = "Arena creation command", usage = "<ID> [-d <displayName>]", flags = "d:", min = 1)
    public static void createArena(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(LobbyHandler.lobbyExists(args.getString(0))) Messaging.send(sender, Lang.Messages.ARENA_ID_USED);
        else {
            ArenaHandler.createArena(args.getString(0), args.hasFlag('d') ? args.getFlag('d') : args.getString(0), ((Player) sender).getLocation());
            Messaging.send(sender, Lang.Messages.ARENA_CREATED.toString().replace("%arena%", args.getString(0)));
        }
    }

    @Command(aliases = {"remove"}, desc = "Arena removal command", usage = "<ID>", min = 1, max = 1)
    public static void removeArena(CommandContext args, CommandSender sender) throws CommandException {
        if(!ArenaHandler.arenaExists(args.getString(0))) Messaging.send(sender, Lang.Messages.INVALID_ARENA_ID);
        else {
            ArenaHandler.removeArena(args.getString(0));
            Messaging.send(sender, Lang.Messages.ARENA_REMOVED.toString().replace("%arena%", args.getString(0)));
        }
    }

    @Command(aliases = {"list"}, desc = "Arena listing command", max = 0)
    public static void listArenas(CommandContext args, CommandSender sender) throws CommandException {
        String arenaList = StringUtil.colorize("&9===  &6Arenas  &9===");
        for(String arenaID : ArenaHandler.getArenas().keySet())
            arenaList += StringUtil.colorize("\n&8[" + ArenaHandler.getArena(arenaID).getActiveLobbyID() + "&8]&7 " + arenaID);
        arenaList += StringUtil.colorize("\n&9===  ===  ===  ===");
        Messaging.send(sender, arenaList);
    }

    @Command(aliases = {"setspawn"}, desc = "Arena main spawn assignment command", usage = "<ID>", min = 1, max = 1)
    public static void setArenaSpawn(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(!ArenaHandler.arenaExists(args.getString(0))) Messaging.send(sender, Lang.Messages.INVALID_ARENA_ID);
        else {
            ArenaHandler.getArena(args.getString(0)).setMainSpawn(((Player) sender).getLocation());
            Messaging.send(sender, Lang.Messages.ARENA_SPAWN_SET);
        }
    }

    @Command(aliases = {"addspawn"}, desc = "Arena spawn point addition command", usage = "<ID>", min = 1, max = 1)
    public static void addArenaSpawnPoint(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(!ArenaHandler.arenaExists(args.getString(0))) Messaging.send(sender, Lang.Messages.INVALID_ARENA_ID);
        else {
            ArenaHandler.getArena(args.getString(0)).addSpawnLocation(((Player) sender).getLocation());
            Messaging.send(sender, Lang.Messages.ARENA_SPAWN_ADDED);
        }
    }
}
