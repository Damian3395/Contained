package com.contained.game.user;

import java.util.ArrayList;
import java.util.UUID;

import com.contained.game.Contained;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerTrade {
	public String displayName;
	public String teamID;
	public ItemStack offer, request;
	public String id;
	
	public PlayerTrade(String playerName, String id, ItemStack offer, ItemStack request){
		this.displayName = playerName;
		this.teamID = id;
		this.offer = offer;
		this.request = request;
		this.id = UUID.randomUUID().toString();
	}
	
	public PlayerTrade(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setString("displayName", this.displayName);
		ntc.setString("teamID", this.teamID);
		ntc.setString("id", this.id);

		NBTTagCompound offerTag = new NBTTagCompound();
		offer.writeToNBT(offerTag);
		ntc.setTag("offer", offerTag);

		NBTTagCompound requestTag = new NBTTagCompound();
		request.writeToNBT(requestTag);
		ntc.setTag("request", requestTag);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		this.displayName = ntc.getString("displayName");
		this.teamID = ntc.getString("teamID");
		this.id = ntc.getString("id");
		
		this.offer = ItemStack.loadItemStackFromNBT(ntc.getCompoundTag("offer"));
		this.request = ItemStack.loadItemStackFromNBT(ntc.getCompoundTag("request"));
	}
	
	public static PlayerTrade get(ArrayList<PlayerTrade> trades, Object comp) {
		for (int i=0; i<trades.size(); i++) {
			if (trades.get(i).equals(comp))
				return trades.get(i);
		}
		return null;
	}
	
	public static PlayerTrade get(Object comp) {
		return PlayerTrade.get(Contained.trades, comp);
	}
	
}
