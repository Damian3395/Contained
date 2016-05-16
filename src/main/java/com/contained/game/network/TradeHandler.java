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
import net.minecraft.server.MinecraftServer;

public class TradeHandler {
	public TradeHandler(){}
	
	public void transaction(EntityPlayerMP player, String uuid){
		//Find The Trade
		PlayerTrade transTrade = null;
		for(PlayerTrade trade : Contained.getTradeList(player.dimension)){
			if(trade.id.equals(uuid)){
				transTrade = trade;
				break;
			}
		}
		if(transTrade == null){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Trade Not Found!");
			return;
		}
		
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
        if(creator == null){
        	Util.displayMessage(player, Util.errorCode + "[Trade Error] Other Player Is Not Online!");
        	return;
        }
        
        //Check If Both Players Have Available Space in their inventory
        if(player.inventory.getFirstEmptyStack() < 0){
        	Util.displayMessage(player, Util.errorCode + "[Trade Error] Your Inventory Is Full!");
        	return;
        }
		if(creator.inventory.getFirstEmptyStack() < 0){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Other Players' Inventory Is Full");
			Util.displayMessage(creator, Util.errorCode + "[Trade Error] Your Inventory Is Full");
			return;
		}
		
		//Check If Player Has Request Item And Remove It
		int count = transTrade.request.stackSize;
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack item = player.inventory.getStackInSlot(i);
			if(count > 0){
				if(item != null && item.getItem().equals(transTrade.request.getItem()) && !item.isItemDamaged()){
					slots.add(i);
					count -= item.stackSize;
				}
			}else
				break;
		}
		
		//Failed
		if(count > 0){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] You Do Not Fullfil The Trade Request");
			return;
		}
		
		//Success
		count = transTrade.request.stackSize;
		for(int i = 0; i < slots.size(); i++){
			ItemStack item = player.inventory.getStackInSlot(slots.get(i));
			if((count-item.stackSize) > 0){
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
		
		//Removing Trade From Trade List
		Contained.trades.remove(transTrade);
			
		//Update Acceptor
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TRADE_TRANS);
		tradePacket.writeItemStack(transTrade.offer);
		tradePacket.writeItemStack(transTrade.request);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		Util.displayMessage(player, Util.successCode + "[Trade Success] You Have Received: " + transTrade.offer);
		
		//Update Creator
		tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TRADE_TRANS);
		tradePacket.writeItemStack(null);
		tradePacket.writeItemStack(transTrade.request);
		Contained.channel.sendTo(tradePacket.toPacket(), creator);
		
		Util.displayMessage(creator, Util.successCode + "[Trade Success] You Have Received: " + transTrade.request);
		
		//Update All Players
		PacketCustom addTrades = ClientPacketHandlerUtil.packetSyncTrades(transTrade, false);
		Contained.channel.sendToAll(addTrades.toPacket());
			
		//DataLog Trade
		DataLogger.insertTrade("debug", creator.getDisplayName()
				, Util.getDimensionString(player.dimension)
				, Util.getGameID(player.dimension)
				, player.getDisplayName()
				, transTrade.offer.getDisplayName()
				, transTrade.offer.stackSize
				, transTrade.request.getDisplayName()
				, transTrade.request.stackSize
				, Util.getDate());
	}
	
	public void create(EntityPlayerMP player, int slotId, ItemStack offer, ItemStack request){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam playerTeam = PlayerTeam.get(pdata.teamID, player.dimension);
		
		if(offer == null || request == null && slotId != -1){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Invalid Input");
			return;
		}
		
		int slot = slotId + 9;
		if(slot >= player.inventory.mainInventory.length)
			slot -= player.inventory.mainInventory.length;
		
		if(!player.inventory.getStackInSlot(slot).getItem().equals(offer.getItem())){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Offer Item Not Found");
			return;
		}
		
		player.inventory.setInventorySlotContents(slot, null);
		
		//Remove Items From Player
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.REMOVE_ITEM);
		tradePacket.writeInt(slotId);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		//Create New Trade
		PlayerTrade newTrade = new PlayerTrade(player.getDisplayName(), playerTeam.displayName, offer, request);
		Contained.getTradeList(player.dimension).add(newTrade);
		
		Util.displayMessage(player, Util.successCode + "[Trade Success] Trade Created");
		
		//Update All Players
		PacketCustom addTrades = ClientPacketHandlerUtil.packetSyncTrades(newTrade, true);
		Contained.channel.sendToAll(addTrades.toPacket());
	}
	
	public void cancel(EntityPlayerMP player, String uuid){
		if(uuid.isEmpty()){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Trade ID Empty");
			return;
		}
			
		if(player.inventory.getFirstEmptyStack() < 0){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Inventory Is Full");
			return;
		}
			
		PlayerTrade removeTrade = null;
		for(PlayerTrade trade : Contained.getTradeList(player.dimension)){
			if(trade.id.equals(uuid)){
				removeTrade = trade;
				Contained.trades.remove(trade);
				break;
			}
		}
		
		if(removeTrade == null){
			Util.displayMessage(player, Util.errorCode + "[Trade Error] Trade Not Found");
			return;
		}
		
		player.inventory.addItemStackToInventory(removeTrade.offer);
		
		////Add Item Back To Player
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADD_ITEM);
		tradePacket.writeItemStack(removeTrade.offer);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		Util.displayMessage(player, Util.errorCode + "[Trade Success] Trade Offer Was Canceled");
		
		//Update All Players
		PacketCustom removeTrades = ClientPacketHandlerUtil.packetSyncTrades(removeTrade, false);
		Contained.channel.sendToAll(removeTrades.toPacket());
	}
}
