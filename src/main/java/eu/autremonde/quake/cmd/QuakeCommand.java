/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.cmd;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import eu.autremonde.quake.railgun.RailgunHandler;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuakeCommand {

    @Command(aliases = {"railgun"}, desc = "Railgun spawning command", usage = "<ID>", min = 1, max = 1)
    public static void spawnRailgun(CommandContext args, CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) Messaging.send(sender, "&cThis command cannot be run from the console!");
        else if(RailgunHandler.getRailgun(args.getString(0)) == null) Messaging.send(sender, "&cThere is no configured Railgun with that ID!");
        else RailgunHandler.getRailgun(args.getString(0)).giveRailGun((Player) sender);
    }
}
