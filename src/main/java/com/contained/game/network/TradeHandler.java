package com.contained.game.network;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTrade;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class TradeHandler {
	public TradeHandler(){}
	
	public void transaction(EntityPlayerMP player, String uuid){
		//Find The Trade
		PlayerTrade transTrade = null;
		for(PlayerTrade trade : Contained.trades){
			if(trade.id.equals(uuid)){
				transTrade = trade;
				break;
			}
		}
		if(transTrade == null)
			return;
		
		//Find The Creator of the Trade
		EntityPlayerMP creator = null;
		List list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (Object playerMP : list) {
            EntityPlayerMP p = (EntityPlayerMP) playerMP;
            if (p.getDisplayName().equals(transTrade.displayName)){
              creator = p;
              break;
            }
        }
        if(creator == null)
        	return;
		
        //Check If Both Players Have Available Space in their inventory
		if(player.inventory.getFirstEmptyStack() < 0 || creator.inventory.getFirstEmptyStack() < 0)
			return;
		
		//Check If Player Has Request Item And Remove It
		int count = transTrade.request.stackSize;
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack item = player.inventory.getStackInSlot(i);
			if(item.equals(transTrade.request)){
				if((count-item.stackSize) >= 0){
					slots.add(i);
					count -= item.stackSize;
				}else{
					slots.add(i);
					count = 0;
					break;
				}
			}
		}
		
		//Failed
		if(count != 0)
			return;
		
		//Success
		count = transTrade.request.stackSize;
		for(int i = 0; i < slots.size(); i++){
			ItemStack item = player.inventory.getStackInSlot(slots.get(i));
			if((count-item.stackSize) >= 0){
				player.inventory.setInventorySlotContents(slots.get(i), null);
				count -= item.stackSize;
			}else{
				player.inventory.decrStackSize(slots.get(i), count);
				break;
			}
		}
		
		//Add Offer ItemStack For Player Acceptor
		player.inventory.addItemStackToInventory(transTrade.offer);

		//Add Request ItemStack For Creator
		creator.inventory.addItemStackToInventory(transTrade.request);
		
		Contained.trades.remove(transTrade);
		
		//Update Acceptor
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TRADE_TRANS);
		tradePacket.writeItemStack(transTrade.offer);
		tradePacket.writeItemStack(transTrade.request);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		//Update Creator
		tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TRADE_TRANS);
		tradePacket.writeItemStack(null);
		tradePacket.writeItemStack(transTrade.request);
		Contained.channel.sendTo(tradePacket.toPacket(), creator);
		
		//Update All Players
		PacketCustom addTrades = ClientPacketHandlerUtil.packetSyncTrades(transTrade, false);
		Contained.channel.sendToAll(addTrades.toPacket());
		
		//DataLog Trade
		String world = player.dimension == 0 ? "Normal" : "Nether";
		DataLogger.insertTrade("debug", creator.getDisplayName()
				, world
				, player.getDisplayName()
				, transTrade.offer.getDisplayName()
				, transTrade.offer.stackSize
				, transTrade.request.getDisplayName()
				, transTrade.request.stackSize
				, Util.getDate());
	}
	
	public void create(EntityPlayerMP player, int slotId, ItemStack offer, ItemStack request){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam playerTeam = PlayerTeam.get(pdata.teamID);
		
		if(offer == null || request == null && slotId != -1)
			return;
		
		int slot = slotId + 9;
		if(slot >= player.inventory.mainInventory.length)
			slot -= player.inventory.mainInventory.length;
		
		player.inventory.setInventorySlotContents(slot, null);
		
		//Remove Items From Player
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.REMOVE_ITEM);
		tradePacket.writeInt(slotId);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		//Update All Players
		PlayerTrade newTrade = new PlayerTrade(player.getDisplayName(), playerTeam.displayName, offer, request);
		PacketCustom addTrades = ClientPacketHandlerUtil.packetSyncTrades(newTrade, true);
		Contained.channel.sendToAll(addTrades.toPacket());
	}
	
	public void cancel(EntityPlayerMP player, String uuid){
		if(uuid.isEmpty())
			return;
		
		if(player.inventory.getFirstEmptyStack() < 0)
			return;
		
		PlayerTrade removeTrade = null;
		for(PlayerTrade trade : Contained.trades){
			if(trade.id.equals(uuid)){
				removeTrade = trade;
				Contained.trades.remove(trade);
				break;
			}
		}
		
		player.inventory.addItemStackToInventory(removeTrade.offer);
		
		////Add Item Back To Player
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADD_ITEM);
		tradePacket.writeItemStack(removeTrade.offer);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		//Update All Players
		if(removeTrade != null){
			PacketCustom removeTrades = ClientPacketHandlerUtil.packetSyncTrades(removeTrade, false);
			Contained.channel.sendToAll(removeTrades.toPacket());
		}
	}
}
