package com.contained.game.world.block;

import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * The remnants of an ore block that has already been harvested. Over time,
 * will regenerate the original ore.
 */
public class HarvestedOreTE extends TileEntity {

	public Block blockToRespawn = Blocks.coal_ore;
	public long expireTime;
	
	public HarvestedOreTE() {
		super();
		expireTime = System.currentTimeMillis()+1000L*Util.randomRange(Resources.minOreRegen, Resources.maxOreRegen);
	}
	
	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote && System.currentTimeMillis() > expireTime) {
			// After the required amount of time has passed, revert back to the
			// original block.
			this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, blockToRespawn);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound ntc) {
		super.writeToNBT(ntc);
		ntc.setLong("expireTime", this.expireTime);
		ntc.setInteger("blockType", Block.getIdFromBlock(this.blockToRespawn));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound ntc) {
		super.readFromNBT(ntc);
		this.expireTime = ntc.getLong("expireTime");
		this.blockToRespawn = Block.getBlockById(ntc.getInteger("blockType"));
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
