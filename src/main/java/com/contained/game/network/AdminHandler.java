package com.contained.game.network;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.ObjectGenerator;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class AdminHandler {
	public void becomeAdmin(EntityPlayerMP player){
		player.setInvisible(true);
		player.capabilities.allowFlying = true;
		player.capabilities.disableDamage = true;
		ExtendedPlayer.get(player).setAdminRights(true);
		PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
		Contained.channel.sendTo(adminPacket.toPacket(),player);
	}
	public void create(EntityPlayerMP player, String object, String name){
		int x,y,z;
		try{
			x = Util.getOnlinePlayer(name).getPlayerCoordinates().posX;
			y = Util.getOnlinePlayer(name).getPlayerCoordinates().posY;
			z = Util.getOnlinePlayer(name).getPlayerCoordinates().posZ;
			if(!ObjectGenerator.generate(object, Util.getOnlinePlayer(name).getEntityWorld(), x, y, z)){
				Util.displayMessage(player, "Cannot generate <"+object+"> ");
			}else{
				Util.displayMessage(player, "Generate <"+object+"> succedded");
			}
		}catch (NullPointerException e){
			Util.displayMessage(player, "The Player:"+name+" does not exist or is off-line.");
		}
	}
	public void change(EntityPlayerMP player, int healthPercentage, int hungerPercentage, String playerName){
		try{
			EntityPlayer target = Util.getOnlinePlayer(playerName); 
			if(healthPercentage!=-1){
				
				if(healthPercentage<=100 && healthPercentage>0){
					target.setHealth(target.getMaxHealth()*healthPercentage/100);
				}
			}
			if(hungerPercentage!=-1){
				
				if(hungerPercentage<=100 && hungerPercentage>0){
					target.getFoodStats().setFoodLevel(20*hungerPercentage/100);//20 is the default max food level
				}
			}
		}catch (NullPointerException e){
			Util.displayMessage(player, "The Player:"+playerName+" does not exist or is off-line.");
		}
	}
	public void join(EntityPlayerMP player, int dimID){
		Util.travelToDimension(dimID, player);
		//TODO delete below code after enabling ExtendedPlayer carrying info through all dimensions
		
		/*
		 * the code below is to set admin rights when entering another dimension
		 * the ExtendedPlayer somehow didn't carry the admin info into all dimensions
		 * so we use this temporary solution to set admin rights when ever the admin change dimension
		 */
		player.setInvisible(true);
		player.capabilities.allowFlying = true;
		player.capabilities.disableDamage = true;
		ExtendedPlayer.get(player).setAdminRights(true);
		PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
		Contained.channel.sendTo(adminPacket.toPacket(),player);
		
	}
	public void spect(EntityPlayerMP player, int dimID , String targetPlayer){
		if(player.dimension != dimID){
			Util.travelToDimension(dimID, player);
		}
		int x,y,z;
		x = 2+Util.getOnlinePlayer(targetPlayer).getPlayerCoordinates().posX;
		z = 2+Util.getOnlinePlayer(targetPlayer).getPlayerCoordinates().posZ;
		y = Util.getOnlinePlayer(targetPlayer).getEntityWorld().getTopSolidOrLiquidBlock(x, z);
		player.setPositionAndUpdate(x,y,z);	//teleport to near the target player's position
		
		//TODO delete below code after enabling ExtendedPlayer carrying info through all dimensions
		
		/*
		* the code below is to set admin rights when entering another dimension
		* the ExtendedPlayer somehow didn't carry the admin info into all dimensions
		 * so we use this temporary solution to set admin rights when ever the admin change dimension
		 */
		player.setInvisible(true);
		player.capabilities.allowFlying = true;
		player.capabilities.disableDamage = true;
		ExtendedPlayer.get(player).setAdminRights(true);
		PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
		Contained.channel.sendTo(adminPacket.toPacket(),player);
		
	}
	public void kick(EntityPlayerMP player, int dimID, String targetPlayer){
		if(Util.getOnlinePlayer(targetPlayer).dimension == dimID){
			PlayerTeamIndividual.get(targetPlayer).leaveTeam();
		}
		Util.travelToDimension(0, Util.getOnlinePlayer(targetPlayer));
		Util.displayMessage(player, "You kicked "+targetPlayer+" back to Overworld");
		Util.displayMessage(Util.getOnlinePlayer(targetPlayer), "You've been kicked back to Overworld by Admin");
	}
}
