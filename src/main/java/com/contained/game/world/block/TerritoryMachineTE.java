package com.contained.game.world.block;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.item.ItemTerritory;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.util.ErrorCase;

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

	private int claimDelay = 20*10; //TODO: Add this to config file.
	private int claimRadius = 2;    //TODO: Add this to config file.
	public int tickTimer = 0;
	public boolean shouldClaim; //Is this is claim mode, or remove mode?
	public String teamID;		 //The team this machine belongs to.
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
	}
	
	@Override
	public void updateEntity() {
		if (this.teamID != null || !this.shouldClaim)
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
							if (ItemTerritory.canRemove(null, toRemove, probe) == ErrorCase.Error.NONE)
								candidates.add(toRemove);
						}
					}
					
					//Remove a random territory block from the candidate set.
					if (candidates.size() > 0) {
						sendParticlePacket("crit"); //success
						Collections.shuffle(candidates);
						Point toRemove = candidates.get(0);
						Contained.territoryData.remove(toRemove);
						ClientPacketHandler.packetRemoveTerrBlock(toRemove.x, toRemove.y).sendToClients();
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
						Contained.territoryData.put(toClaim, this.teamID);
						ClientPacketHandler.packetAddTerrBlock(this.teamID, toClaim.x, toClaim.y).sendToClients();
					} else
						sendParticlePacket("smoke"); //fail
				}
			}
			
			// Once every second, sync the state of this machine with the
			// clients to update rendering.
			if (tickTimer % 20 == 0) {
				PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandler.TMACHINE_STATE);
				packet.writeInt(this.xCoord);
				packet.writeInt(this.yCoord);
				packet.writeInt(this.zCoord);
				packet.writeInt(this.tickTimer);
				if (this.teamID == null)
					packet.writeString("");
				else
					packet.writeString(this.teamID);
				packet.writeBoolean(this.shouldClaim);
				packet.sendPacketToAllAround(this.xCoord, this.yCoord, this.zCoord, 50, this.worldObj.provider.dimensionId);
			}
		} else {
			if (tickTimer >= claimDelay)
				tickTimer = 0;
		}
		
		//Periodically check for team color changes.
		if (this.teamID != null && Math.random() <= 1.0/20.0) {
			PlayerTeam team = PlayerTeam.get(this.teamID);
			if (team != null)
				this.renderColor = team.getColor();
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
	
	public float getProgress() {
		return Math.min(1f, (float)tickTimer/(float)claimDelay);
	}
	
	public void sendParticlePacket(String type) {
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandler.TE_PARTICLE);
		packet.writeInt(this.xCoord);
		packet.writeInt(this.yCoord);
		packet.writeInt(this.zCoord);
		packet.writeString(type);
		packet.sendPacketToAllAround(this.xCoord, this.yCoord, this.zCoord, 50, this.worldObj.provider.dimensionId);
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