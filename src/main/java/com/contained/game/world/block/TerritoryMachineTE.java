package com.contained.game.world.block;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.item.ItemTerritory;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerTeam;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.util.ErrorCase;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Controls the action of the territory machine automatically claiming/removing 
 * a nearby block of territory after a set period of time elapses.
 */
public class TerritoryMachineTE extends TileEntity {

	private int claimDelay;
	private int claimRadius; 
	public int tickTimer = 0;
	public boolean shouldClaim; //Is this is claim mode, or remove mode?
	public String teamID;		 //The team this machine is linked to.
	public int renderColor;
	public String displayParticle = null;
	
	// Minecraft requires TileEntities to have default constructors.
	public TerritoryMachineTE() {
		this(false);
	}
	
	public TerritoryMachineTE(boolean mode) {
		this.shouldClaim = mode;
		this.teamID = null;
		this.renderColor = 0xFFFFFF;
		
		if (shouldClaim) {
			this.claimDelay = Contained.configs.claimDelay*20;
			this.claimRadius = Contained.configs.claimRadius;
		} else {
			this.claimDelay = Contained.configs.antiClaimDelay*20;
			this.claimRadius = Contained.configs.antiClaimRadius;
		}
	}
	
	@Override
	public void updateEntity() {
		if (this.teamID != null)
			tickTimer++;
		
		if (!this.worldObj.isRemote) {			
			//Full amount of time has elapsed, time to perform the territory action.
			if (tickTimer >= claimDelay) {
				tickTimer = 0;
				
				if (!this.shouldClaim) {
					// [[REMOVE TERRITORY MODE]]
				    // Find all claimed territory within the machine's radius.
					ArrayList<Point> candidates = new ArrayList<Point>();
					Point probe = new Point();
					for(int i=-claimRadius; i<=claimRadius; i++) {
						for(int j=-claimRadius; j<=claimRadius; j++) {
							probe.x = this.xCoord+i;
							probe.y = this.zCoord+j;
							Point toRemove = new Point(probe.x, probe.y);
							if (ItemTerritory.canRemove(this.teamID, toRemove, probe, this.worldObj.provider.dimensionId) == ErrorCase.Error.NONE)
								candidates.add(toRemove);
						}
					}
					
					//Remove a random territory block from the candidate set.
					if (candidates.size() > 0) {
						sendParticlePacket("crit"); //success
						Collections.shuffle(candidates);
						Point toRemove = candidates.get(0);
						Contained.territoryData.remove(toRemove);
						Contained.channel.sendToAll(ClientPacketHandlerUtil.packetRemoveTerrBlock(toRemove.x, toRemove.y).toPacket());
					} else
						sendParticlePacket("smoke"); //fail
				} else {
					// [[CLAIM TERRITORY MODE]]
				    // Find all unclaimed, eligible territory within the machine's radius.
					ArrayList<Point> candidates = new ArrayList<Point>();
					Point probe = new Point();
					for(int i=-claimRadius; i<=claimRadius; i++) {
						for(int j=-claimRadius; j<=claimRadius; j++) {
							probe.x = this.xCoord+i;
							probe.y = this.zCoord+j;
							Point toClaim = new Point(probe.x, probe.y);
							if (ItemTerritory.canClaim(this.teamID, toClaim, probe) == ErrorCase.Error.NONE)
								candidates.add(toClaim);
						}
					}
					
					//Claim a random territory block from the candidate set.
					if (candidates.size() > 0) {
						sendParticlePacket("crit"); //success
						Collections.shuffle(candidates);
						Point toClaim = candidates.get(0);
						Contained.getTerritoryMap(this.worldObj.provider.dimensionId).put(toClaim, this.teamID);
						Contained.channel.sendToAll(ClientPacketHandlerUtil.packetAddTerrBlock(this.teamID, toClaim.x, toClaim.y).toPacket());
					} else
						sendParticlePacket("smoke"); //fail
				}
			}
			
			// Once every second, sync the state of this machine with the
			// clients to update rendering.
			if (tickTimer % 20 == 0)
				sendInfoPacket();
		} else {
			if (tickTimer >= claimDelay)
				tickTimer = 0;
		}
		
		//Spawn some particle effects on action completion for feedback.
		if (this.worldObj.isRemote && displayParticle != null) {
			for (int i = 0; i < 20; i++) {
				double d0 = this.xCoord + this.worldObj.rand.nextDouble();
				double d1 = this.yCoord + this.worldObj.rand.nextDouble();
				double d2 = this.zCoord + this.worldObj.rand.nextDouble();
				double d3 = Util.randomBoth(1.0);
				double d4 = Util.randomBoth(1.0);
				double d5 = Util.randomBoth(1.0);
				this.worldObj.spawnParticle(displayParticle, d0, d1, d2, d3, d4, d5);
			}
			displayParticle = null;
		}
	}
	
	public void refreshColor() {
		if (this.teamID != null) {
			PlayerTeam team = PlayerTeam.get(this.teamID, this.worldObj.provider.dimensionId);
			if (team != null)
				this.renderColor = team.getColor();
		}
	}
	
	public void sendInfoPacket() {
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TMACHINE_STATE);
		packet.writeInt(this.xCoord);
		packet.writeInt(this.yCoord);
		packet.writeInt(this.zCoord);
		packet.writeInt(this.tickTimer);
		if (this.teamID == null)
			packet.writeString("");
		else
			packet.writeString(this.teamID);
		packet.writeBoolean(this.shouldClaim);
		Contained.channel.sendToAllAround(packet.toPacket(), new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 50));
	}
	
	public float getProgress() {
		return Math.min(1f, (float)tickTimer/(float)claimDelay);
	}
	
	public void sendParticlePacket(String type) {
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.TE_PARTICLE);
		packet.writeInt(this.xCoord);
		packet.writeInt(this.yCoord);
		packet.writeInt(this.zCoord);
		packet.writeString(type);
		Contained.channel.sendToAllAround(packet.toPacket(), new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 50));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound ntc) {
		super.writeToNBT(ntc);
		ntc.setInteger("tick", this.tickTimer);
		ntc.setBoolean("mode", this.shouldClaim);
		if (this.teamID != null)
			ntc.setString("team", this.teamID);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound ntc) {
		super.readFromNBT(ntc);
		this.tickTimer = ntc.getInteger("tick");
		this.shouldClaim = ntc.getBoolean("mode");
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
