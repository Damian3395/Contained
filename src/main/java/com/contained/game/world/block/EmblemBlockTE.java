package com.contained.game.world.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class EmblemBlockTE extends TileEntity {

	public boolean isActive = false;
	public String teamID = null;
	public int color = 0;
	
	public EmblemBlockTE() {
		super();
	}
	
	public EmblemBlockTE(int color, boolean active) {
		super();
		this.isActive = active;
		this.color = color;
	}
	
	public EmblemBlockTE(int color, boolean active, String teamID) {
		super();
		this.isActive = active;
		this.teamID = teamID;
		this.color = color;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound ntc) {
		super.writeToNBT(ntc);
		ntc.setBoolean("active", this.isActive);
		ntc.setInteger("color", this.color);
		if (teamID != null)
			ntc.setString("team", teamID);
		else if (ntc.hasKey("team"))
			ntc.removeTag("team");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound ntc) {
		super.readFromNBT(ntc);
		this.isActive = ntc.getBoolean("active");
		this.color = ntc.getInteger("color");
		if (ntc.hasKey("team"))
			this.teamID = ntc.getString("team");
		else
			this.teamID = null;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}
}
