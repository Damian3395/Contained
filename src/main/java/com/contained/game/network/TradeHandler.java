package com.contained.game.network;

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
	
	public void transaction(EntityPlayerMP player, NBTTagCompound data){
		PlayerTrade trade = new PlayerTrade(data);
		EntityPlayerMP creator = null;
		
		List list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (Object playerMP : list) {
            EntityPlayerMP p = (EntityPlayerMP) playerMP;
            if (p.getDisplayName().equals(trade.displayName))
              creator = p;
        }
        
        if(creator == null)
        	return;
		
        //Check If Both Players Have Available Space in their inventory
		if(player.inventory.getFirstEmptyStack() == -1 || creator.inventory.getFirstEmptyStack() == -1){
			return;
		}else{
			//Remove Request ItemStack From Player Acceptor
			int count = 0;
			for(int i = 0; i < player.inventory.getSizeInventory(); i++){
				ItemStack curSlot = player.inventory.getStackInSlot(i);
				if(curSlot.equals(trade.request)){
					if((curSlot.stackSize + count) <= trade.request.stackSize){
						count += curSlot.stackSize;
						player.inventory.setInventorySlotContents(i, null);
					}else{
						player.inventory.decrStackSize(i, trade.request.stackSize-count);
						i = player.inventory.getSizeInventory();
					}
				}
			}
			//Add Offer ItemStack For Player Acceptor
			player.inventory.addItemStackToInventory(trade.offer);
			
			//Remove Offer ItemStack From Creator
			count = 0;
			for(int i = 0; i < creator.inventory.getSizeInventory(); i++){
				ItemStack curSlot = creator.inventory.getStackInSlot(i);
				if(curSlot.equals(trade.offer)){
					if((curSlot.stackSize + count) <= trade.offer.stackSize){
						count += curSlot.stackSize;
						creator.inventory.setInventorySlotContents(i, null);
					}else{
						creator.inventory.decrStackSize(i, trade.offer.stackSize-count);
						i = creator.inventory.getSizeInventory();
					}
				}
			}
			//Add Request ItemStack For Creator
			creator.inventory.addItemStackToInventory(trade.request);
		}
		
		//Update Acceptor
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TRADE_TRANS);
		tradePacket.writeBoolean(false);
		tradePacket.writeItemStack(trade.offer);
		tradePacket.writeItemStack(trade.request);
		Contained.channel.sendTo(tradePacket.toPacket(), player);
		
		//Update Creator
		tradePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TRADE_TRANS);
		tradePacket.writeBoolean(true);
		tradePacket.writeItemStack(trade.offer);
		tradePacket.writeItemStack(trade.request);
		Contained.channel.sendTo(tradePacket.toPacket(), creator);
		
		//DataLog Trade
		String world = player.dimension == 0 ? "Normal" : "Nether";
		DataLogger.insertTrade("debug", creator.getDisplayName()
				, world
				, player.getDisplayName()
				, trade.offer.getDisplayName()
				, trade.offer.stackSize
				, trade.request.getDisplayName()
				, trade.request.stackSize
				, Util.getDate());
	}
	
	public void create(EntityPlayerMP player, int slotId, ItemStack offer, ItemStack request){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam playerTeam = PlayerTeam.get(pdata.teamID);
		
		if(offer == null || request == null && slotId != -1)
			return;
		
		player.inventory.setInventorySlotContents(slotId, null);
		
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
		
		PlayerTrade removeTrade = null;
		for(int i = 0; i < Contained.trades.size(); i++)
			if(Contained.trades.get(i).id.equals(uuid)){
				removeTrade = Contained.trades.get(i);
				Contained.trades.remove(removeTrade);
			}
		
		if(removeTrade != null){
			PacketCustom removeTrades = ClientPacketHandlerUtil.packetSyncTrades(removeTrade, false);
			Contained.channel.sendToAll(removeTrades.toPacket());
		}
	}
}
