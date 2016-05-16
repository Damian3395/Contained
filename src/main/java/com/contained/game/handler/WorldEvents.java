package com.contained.game.handler;

import java.awt.Point;

import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedLivingBase;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Load;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Save;
import com.contained.game.util.Util;
import com.contained.game.world.GenerateWorld;
import com.contained.game.world.WorldGenDecoration;
import com.contained.game.world.biome.WastelandBiome;
import com.contained.game.world.block.WastelandBlock;
import com.contained.game.world.block.WastelandBush;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldEvents {	
	@SubscribeEvent
	//Make all chunks within a specified distance from spawn be empty wasteland.
	public void biomeControl(ChunkProviderEvent.ReplaceBiomeBlocks event) {
		boolean readyToGen = event.world.playerEntities != null 
								&& event.world.playerEntities.size() > 0;
		
		if (Util.isOverworld(event.world.provider.dimensionId) && readyToGen && !event.world.isRemote) {
			float wasteAmount = Util.isWasteland(event.world, event.chunkX, event.chunkZ);
			BiomeGenBase biomeOverride = null;
			if (wasteAmount > 0) {
				for (int i=0; i<event.blockArray.length; i++) {
					if (Util.isSolidBlock(event.blockArray[i])) {
						if (Math.random() <= wasteAmount)
							event.blockArray[i] = WastelandBlock.instance;
					}
				}
				if (wasteAmount == 1.0f)
					biomeOverride = WastelandBiome.biome;
			}
			if (biomeOverride == null) {
				Point p = new Point(event.chunkX, event.chunkZ);
				biomeOverride = GenerateWorld.getBiomeProperties(event.world.provider.dimensionId).getBiome(p);
			}
			
			//Override biomes based on finite world configurations
			if (biomeOverride != null) {
				for(int i=0; i<event.biomeArray.length; i++)
					event.biomeArray[i] = biomeOverride;
			}
			
			//Low areas of map should use netherrack instead of stone.
			for(int i=0;i<16;i++) {
				for(int j=0;j<16;j++) {
					for(int k=0;k<16;k++) {
						int val = i << 12 | j << 8 | k;
						if (event.blockArray[val] == Blocks.stone && Math.random() <= (17f-k)/8f)
							event.blockArray[val] = Blocks.netherrack;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	//Scatter some dead bushes around the wasteland, for effect.
	public void doDecorations(DecorateBiomeEvent.Pre event) {
		float absChunkX = event.chunkX/16;
		float absChunkZ = event.chunkZ/16;
		
		if (Util.isWasteland(event.world, absChunkX, absChunkZ) > 0) {
			int numDeadBushes = (int)(Math.random()*20D)+15;
			for (int i = 0; i < numDeadBushes; i++) {
				int x = event.chunkX + event.rand.nextInt(16) + 8;
				int z = event.chunkZ + event.rand.nextInt(16) + 8;
				int y = event.rand.nextInt(Math.max(1, event.world.getHeightValue(x, z) * 2));
				(new WorldGenDecoration(WastelandBush.instance)).generate(event.world, event.rand, x, y, z);
			}
		}
	}
	
	@SubscribeEvent
	//We'll handle sending out chat messages ourselves, so that:
	//  -In the games: Chat messages only send to your own team members.
	//  -Elsewhere: Chat messages only send to people in the same dimension as you.
	public void handleChat(ServerChatEvent event) {
		if (event.player == null)
			return;
		
		int dimID = event.player.dimension;
		if (MiniGameUtil.isPvP(dimID) || MiniGameUtil.isTreasure(dimID)) {
			//Team Chat Mode
			PlayerTeamIndividual pdata = PlayerTeamIndividual.get(event.player);

			if (pdata.teamID != null) {
				PlayerTeam team = PlayerTeam.get(pdata.teamID, dimID);						
				team.sendMessageToTeam(team.getFormatCode()+"<"+event.username+"> "+event.message);
				String world = Util.getDimensionString(dimID);
				DataLogger.insertGuildChat(Util.getServerID(), 
						event.username, 
						pdata.teamID, 
						world, 
						event.message, 
						Util.getDate());
			} else
				Util.displayError(event.player, "Could not send chat message -- you aren't in a team!");
		} else {
			//Public Dimension Chat Mode
			for(Object p : event.player.worldObj.playerEntities) {
				if (p instanceof EntityPlayer)
					Util.displayMessage((EntityPlayer)p, "<"+event.username+"> "+event.message);
			}
		}
		
		event.setCanceled(true);
	}
	
	//
	// == Creature Handling ==
	//
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{		
		//Register custom attributes to living entities.
		if (event.entity instanceof EntityLivingBase && !(event.entity instanceof EntityPlayer)) {
			EntityLivingBase mobCast = (EntityLivingBase)event.entity;
			ExtendedLivingBase.register(mobCast);
		}
	}	
	
	@SubscribeEvent
	public void onEntityUpdate(LivingEvent.LivingUpdateEvent event)
	{
		// Link custom spawned monsters (from spawn eggs) to the team of the
		// player that spawned them.
		if (!event.entity.worldObj.isRemote) {
			if (event.entity instanceof EntityLiving && !(event.entity instanceof EntityPlayer)) {
				ExtendedLivingBase props = ExtendedLivingBase.get((EntityLivingBase)event.entity);
				EntityLiving living = (EntityLiving)event.entity;
				if (living.hasCustomNameTag() && props.getTeam() == null) {
					PlayerTeamIndividual ownerData = PlayerTeamIndividual.get(living.getCustomNameTag());
					if (ownerData != null && ownerData.teamID != null) {
						PlayerTeam team = PlayerTeam.get(ownerData.teamID, event.entity.worldObj.provider.dimensionId);
						if (team != null) {
							props.setTeam(ownerData.teamID);
							living.setCustomNameTag(team.getFormatCode()+"§l§n"+team.displayName);
							living.setAlwaysRenderNameTag(true);
						}
					}
				}
				
				if (props.forceFireImmunity)
					event.entity.extinguish();
			}
		}
	}
	
	//
	// === File Handling ===
	//
	@SubscribeEvent
	public void init(WorldEvent.Load event) {
		if (Util.isOverworld(event.world.provider.dimensionId) && !event.world.isRemote)
			Load.loadWorldData(event.world, event.world.provider.dimensionId);
		
		//Default Game Rules
		// We handle what happens to a player's inventory on death manually.
		event.world.getGameRules().setOrCreateGameRule("keepInventory", "true");
		
		// TODO: We turn this off because these death messages are visible across all
		// dimensions... but we may want to replicate this system ourselves to show
		// death messages locally within a dimension.
		event.world.getGameRules().setOrCreateGameRule("showDeathMessages", "false");
	}
	
	@SubscribeEvent
	public void close(WorldEvent.Unload event) {
		if (Util.isOverworld(event.world.provider.dimensionId) && !event.world.isRemote)
			Save.saveWorldData(event.world.provider.dimensionId);
	}
	
	@SubscribeEvent
	public void close(WorldEvent.Save event) {
		if (Util.isOverworld(event.world.provider.dimensionId) && !event.world.isRemote)
			Save.saveWorldData(event.world.provider.dimensionId);
	}
}
