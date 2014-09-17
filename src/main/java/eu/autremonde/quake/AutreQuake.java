/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake;

import eu.autremonde.quake.arena.ArenaHandler;
import eu.autremonde.quake.cmd.CommandHandler;
import eu.autremonde.quake.config.Configuration;
import eu.autremonde.quake.lobby.LobbyHandler;
import eu.autremonde.quake.protocol.ProtocolHandler;
import eu.autremonde.quake.railgun.RailgunHandler;
import eu.autremonde.quake.stats.StatHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class AutreQuake extends JavaPlugin {

    private CommandHandler commandHandler;
    private Configuration configuration;
    private EventHandler eventHandler;
    private ProtocolHandler protocolHandler;

    public void onEnable() {
        getConfiguration().loadConfigurations();
        ArenaHandler.loadArenas();
        LobbyHandler.loadLobbies();
        RailgunHandler.loadRailguns();
        getCommandHandler().registerCommands();
        getEventHandler().registerEvents();
        getProtocolHandler().registerListeners();
        getLogger().info("has been enabled");
    }

    public void onDisable() {
        StatHandler.saveStats();
        LobbyHandler.handleDisable();
        ArenaHandler.saveArenas();
        LobbyHandler.saveLobbies();
        getConfiguration().saveConfigurations();
        getLogger().info("has been disabled");
    }

    public static AutreQuake getPlugin() {
        return (AutreQuake) Bukkit.getPluginManager().getPlugin("AutreQuake");
    }

    public CommandHandler getCommandHandler() {
        if(commandHandler == null) commandHandler = new CommandHandler(this);
        return commandHandler;
    }

    public Configuration getConfiguration() {
        if(configuration == null) configuration = new Configuration(this);
        return configuration;
    }

    public EventHandler getEventHandler() {
        if(eventHandler == null) eventHandler = new EventHandler(this);
        return eventHandler;
    }

    public ProtocolHandler getProtocolHandler() {
        if(protocolHandler == null) protocolHandler = new ProtocolHandler(this);
        return protocolHandler;
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        return getCommandHandler().onCommand(sender, cmd, commandLabel, args);
    }
}
