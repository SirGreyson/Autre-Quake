/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.cmd;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.lobby.Lobby;
import eu.autremonde.quake.lobby.LobbyHandler;
import eu.autremonde.quake.match.Stage;
import eu.autremonde.quake.util.Messaging;
import eu.autremonde.quake.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand {

    @Command(aliases = {"create"}, desc = "Lobby creation command", usage = "<ID>", min = 1, max = 1)
    @CommandPermissions("autrequake.admin")
    public static void createLobby(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(LobbyHandler.lobbyExists(args.getString(0))) Messaging.send(sender, Lang.Messages.LOBBY_ID_USED);
        else {
            LobbyHandler.createLobby(args.getString(0));
            Messaging.send(sender, Lang.Messages.LOBBY_CREATED.toString().replace("%lobby%", args.getString(0)));
        }
    }

    @Command(aliases = {"remove"}, desc = "Lobby removal command", usage = "<ID>", min = 1, max = 1)
    @CommandPermissions("autrequake.admin")
    public static void removeLobby(CommandContext args, CommandSender sender) throws CommandException {
        if(!LobbyHandler.lobbyExists(args.getString(0))) Messaging.send(sender, Lang.Messages.INVALID_LOBBY_ID);
        else {
            LobbyHandler.removeLobby(args.getString(0));
            Messaging.send(sender, Lang.Messages.LOBBY_REMOVED.toString().replace("%lobby%", args.getString(0)));
        }
    }

    @Command(aliases = {"list"}, desc = "Lobby listing command", max = 0)
    @CommandPermissions("autrequake.admin")
    public static void listLobbies(CommandContext args, CommandSender sender) throws CommandException {
        String lobbyList = StringUtil.colorize("&9===  &6Lobbies  &9===");
        for(String lobbyID : LobbyHandler.getLobbies().keySet())
            lobbyList += StringUtil.colorize("\n&8[" + LobbyHandler.getLobby(lobbyID).getStage().toString() + "&8]&7 " + lobbyID);
        lobbyList += StringUtil.colorize("\n&9===  ===  ===  ===");
        Messaging.send(sender, lobbyList);
    }

    @Command(aliases = {"forcestart"}, desc = "Lobby force-starting command", max = 0)
    @CommandPermissions("autrequake.admin")
    public static void forceStartLobby(CommandContext args, CommandSender sender) throws CommandException {
        Lobby lobby = sender instanceof Player ? LobbyHandler.getLobbyFromPlayer((Player) sender) : null;
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(lobby == null) Messaging.send(sender, Lang.Messages.NOT_IN_LOBBY);
        else if(lobby.getStage() != Stage.WAITING) Messaging.send(sender, Lang.Messages.LOBBY_RUNNING);
        else lobby.setStage(Stage.FORCE_STARTING);
    }

    @Command(aliases = {"join"}, desc = "Lobby joining command", usage = "<ID>", min = 1, max = 1)
    public static void joinLobby(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(!LobbyHandler.lobbyExists(args.getString(0))) Messaging.send(sender, Lang.Messages.INVALID_LOBBY_ID);
        else if(LobbyHandler.getLobby(args.getString(0)).canAddPlayer((Player) sender)) LobbyHandler.getLobby(args.getString(0)).addPlayer((Player) sender);
    }

    @Command(aliases = {"leave"}, desc = "Lobby leaving command", max = 0)
    public static void leaveLobby(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else {
            Lobby lobby = LobbyHandler.getLobbyFromPlayer((Player) sender);
            if(lobby == null) Messaging.send(sender, Lang.Messages.NOT_IN_LOBBY);
            else lobby.removePlayer((Player) sender);
        }
    }
}
