package com.contained.game.network;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

/**
 * Handling of packets sent from client to server.
 */
public class ServerPacketHandler {

	public static final int UPDATE_CLASS = 1;
	public static final int UPDATE_CLASSLEVEL = 2;
	public static final int OFFSET_XPLEVEL = 3;
	public static final int INVENTORY_REMOVE = 4;
	public static final int INVENTORY_ADD = 5;
	public static final int UPDATE_GUILD_STATUS = 6;

	protected String channelName;
	protected EntityPlayerMP player;

	@SubscribeEvent
	public void handlePacket(ServerCustomPacketEvent event) {		
		channelName = event.packet.channel();
		NetHandlerPlayServer net = (NetHandlerPlayServer)event.handler;
		player = net.playerEntity;

		if (channelName.equals(Resources.MOD_ID) && event.packet.getTarget() == Side.SERVER) {
			PacketCustom packet = new PacketCustom(event.packet.payload());
			
			switch(packet.getType()) {
				case UPDATE_CLASS:
					ExtendedPlayer.get(player).occupationClass = packet.readInt();
				break;
	
				case UPDATE_CLASSLEVEL:
					ExtendedPlayer.get(player).occupationLevel = packet.readInt();
				break;
	
				case OFFSET_XPLEVEL:
					player.experienceLevel += packet.readInt();
				break;
	
				case INVENTORY_REMOVE:
					ItemStack toRemove = packet.readItemStack();
					Util.removeItem(toRemove, player);
				break;
				
				case INVENTORY_ADD:
					ItemStack toSpawn = packet.readItemStack();
					player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY+1, player.posZ, toSpawn));
				break;
				
				case UPDATE_GUILD_STATUS:
					ExtendedPlayer.get(player).guild = packet.readInt();
			}
		}
	}

	public static void sendToServer(FMLProxyPacket packet) {
		Contained.channel.sendToServer(packet);
	}
}
