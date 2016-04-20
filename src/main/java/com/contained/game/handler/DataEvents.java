package com.contained.game.handler;

import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class DataEvents {
	@SubscribeEvent
	public void onAchievement(AchievementEvent event){
		if(event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote){
			ExtendedPlayer player = ExtendedPlayer.get(event.entityPlayer);
			if(!player.achievements.contains(event.achievement.statId)){
				DataLogger.insertAchievement(Util.getServerID(), 
						event.entityPlayer.getDisplayName(), 
						Util.getDimensionString(event.entityPlayer.dimension), 
						event.achievement.statId, 
						Util.getDate());
				player.achievements.add(event.achievement.statId);
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBuild(PlaceEvent event){
		if(!event.world.isRemote){
			DataLogger.insertBuild(Util.getServerID(), 
					event.player.getDisplayName(), 
					Util.getDimensionString(event.player.dimension),
					Util.getGameID(event.player.dimension),
					event.block.getLocalizedName(), 
					event.x, 
					event.y, 
					event.z, 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event){
		if(!event.world.isRemote){
			DataLogger.insertMine(Util.getServerID(), 
					event.getPlayer().getDisplayName(), 
					Util.getDimensionString(event.getPlayer().dimension),
					Util.getGameID(event.getPlayer().dimension),
					event.block.getLocalizedName(), 
					event.x, 
					event.y, 
					event.z, 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onJoinServer(EntityJoinWorldEvent event){
		if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)event.entity;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			properties.world = Util.getDimensionString(player.dimension);
			DataLogger.insertLogin(Util.getServerID(), 
					player.getDisplayName(), 
					Util.getDimensionString(player.dimension), 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event){
		if(!event.entity.worldObj.isRemote && 
				(event.entity instanceof EntityPlayer ||
						event.source.getSourceOfDamage() instanceof EntityPlayer)){
			
			EntityPlayer player;
			String killer = "";
			String victim = "";
			if(event.entity instanceof EntityPlayer){
				player = (EntityPlayer) event.entity;
				victim = player.getDisplayName();
			} else if (event.entity instanceof EntityLivingBase) {
				victim = LanguageRegistry.instance().getStringLocalization("entity." + EntityList.getEntityString((EntityLivingBase) event.entityLiving)+".name", "en_US");
			} else
				victim = event.entity.getClass().getSimpleName();
			
			if(event.source.getSourceOfDamage() instanceof EntityPlayer){
				player = (EntityPlayer) event.source.getSourceOfDamage();
				killer = player.getDisplayName();
			} else if (event.source.getSourceOfDamage() instanceof EntityLivingBase) {
				killer = LanguageRegistry.instance().getStringLocalization("entity." + EntityList.getEntityString((EntityLivingBase) event.source.getSourceOfDamage())+".name", "en_US");
			} else
				killer = event.source.getDamageType();
			DataLogger.insertKill(Util.getServerID(), 
					Util.getDimensionString(event.entity.dimension),
					Util.getGameID(Util.getGameID(event.entity.dimension)),
					killer, 
					victim, 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onAnvil(AnvilRepairEvent event){
		if(!event.entityPlayer.worldObj.isRemote && event.entity instanceof EntityPlayer){
			DataLogger.insertAnvil(Util.getServerID(), 
					event.entityPlayer.getDisplayName(),
					Util.getDimensionString(event.entityPlayer.dimension),
					Util.getGameID(event.entityPlayer.dimension),
					event.left == null ? "" : event.left.getDisplayName(), 
					event.left == null ? 0 : event.left.stackSize,
					event.right == null ? "" : event.right.getDisplayName(), 
					event.right == null ? 0 : event.right.stackSize,
					event.output.getDisplayName(), event.output.stackSize,
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onUsedItem(PlayerDestroyItemEvent event){
		if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer){
			DataLogger.insertUsed(Util.getServerID(), 
					event.entityPlayer.getDisplayName(),
					Util.getDimensionString(event.entityPlayer.dimension),
					Util.getGameID(event.entityPlayer.dimension),
					event.original.getDisplayName(), 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event){
		if(!event.player.worldObj.isRemote){
			DataLogger.insertChat(Util.getServerID(), 
					event.player.getDisplayName(),
					Util.getDimensionString(event.player.dimension),
					event.message, 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onConsume(PlayerUseItemEvent.Finish event){
		if(!event.entity.worldObj.isRemote  && event.entity instanceof EntityPlayer){
			DataLogger.insertConsume(Util.getServerID(), 
					event.entityPlayer.getDisplayName(), 
					Util.getDimensionString(event.entityPlayer.dimension),
					Util.getGameID(event.entityPlayer.dimension),
					event.item.getDisplayName(), 
					Util.getDate());
		}
	}
	
	@SubscribeEvent
	public void onPortal(LivingUpdateEvent event){
		if(!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entity;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			if(properties.world.compareTo(Util.getDimensionString(event.entityLiving.dimension)) != 0){
				DataLogger.insertPortal(Util.getServerID(), 
						player.getDisplayName(), 
						properties.world, 
						Util.getDimensionString(event.entityLiving.dimension),
						Util.getDate());
				properties.world = Util.getDimensionString(event.entityLiving.dimension);
			}
		}
	}
	
	@SubscribeEvent
	public void onMove(LivingUpdateEvent event){
		if(!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entity;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			int x = (int) event.entityLiving.posX;
			int y = (int) event.entityLiving.posY;
			int z = (int) event.entityLiving.posZ;
			if(properties.posX != x || properties.posY != y || properties.posZ != z){
				DataLogger.insertMove(Util.getServerID(), 
						player.getDisplayName(),
						Util.getDimensionString(event.entityLiving.dimension),
						Util.getGameID(event.entity.dimension),
						x, y, z, 
						Util.getDate());
				properties.posX = x;
				properties.posY = y;
				properties.posZ = z;
			}
		}
	}
}
