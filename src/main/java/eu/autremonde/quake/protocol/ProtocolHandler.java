/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import eu.autremonde.quake.AutreQuake;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolHandler {

    private AutreQuake plugin;
    private ProtocolManager protocolManager;

    public ProtocolHandler(AutreQuake plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void registerListeners() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(event.getPacket().getStrings().read(0).equalsIgnoreCase("fireworks.launch")) event.setCancelled(true);
            }
        });
    }

    public static void forceRespawn(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Client.CLIENT_COMMAND);
        packet.getClientCommands().write(0, EnumWrappers.ClientCommand.PERFORM_RESPAWN);
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendCustomSound(Player player, String soundName) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.NAMED_SOUND_EFFECT);
        packet.getStrings().write(0, soundName);
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
