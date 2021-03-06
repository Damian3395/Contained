package com.contained.game.network;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.ObjectGenerator;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

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
		Util.travelToDimension(dimID, player, true);
		//TODO delete below code after enabling ExtendedPlayer carrying info through all dimensions
		
		/*
		 * the code below is to set admin rights when entering another dimension
		 * the ExtendedPlayer somehow didn't carry the admin info into all dimensions
		 * so we use this temporary solution to set admin rights when ever the admin change dimension
		 */
		/*
		player.setInvisible(true);
		player.capabilities.allowFlying = true;
		player.capabilities.disableDamage = true;
		ExtendedPlayer.get(player).setAdminRights(true);
		PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
		
		Contained.channel.sendTo(adminPacket.toPacket(),player);
		*/
	}
	
	/*
	 * write dimID,playerCount,playerNames,dimTime to client
	 */
	public void viewWorldInfo(EntityPlayerMP player, int dimID){
		List<String> playerNames = new ArrayList<String>();
		PacketCustom worldInfoPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADMIN_WORLD_INFO);
		worldInfoPacket.writeInt(dimID);
		if (dimID == -100) {
			// selected to view whole server players
			for (EntityPlayer ep : Util.getPlayerListInMinecraftServer()) {
				if (!ep.getDisplayName().equals(player.getDisplayName())) {
					playerNames.add(ep.getDisplayName().toString());
				} else {
					playerNames.add(ep.getDisplayName().toString()+" (YOU)");
				}
			}
			worldInfoPacket.writeInt(playerNames.size());
			for(int i=0; i<playerNames.size();i++){
				worldInfoPacket.writeString(playerNames.get(i));
			}
			Contained.channel.sendTo(worldInfoPacket.toPacket(),player);
		} else { // selected to view players in a certain dimension
				if(DimensionManager.getWorld(dimID) != null){
					for (EntityPlayer ep : Util.getPlayerListInDimension(dimID)) {
						if (!ep.getDisplayName().equals(player.getDisplayName())) {
							playerNames.add(ep.getDisplayName().toString());
						} else {
							playerNames.add(ep.getDisplayName().toString()+" (YOU)");
						}
					}
					worldInfoPacket.writeInt(playerNames.size());
					for(int i=0; i<playerNames.size();i++){
						worldInfoPacket.writeString(playerNames.get(i));
					}
					worldInfoPacket.writeLong(DimensionManager.getWorld(dimID).getWorldTime());
					Contained.channel.sendTo(worldInfoPacket.toPacket(),player);
				} else {
					Util.displayMessage(player, "Dimension"+dimID+" is empty!");
				}
				
		}
		
	}
	
	public void spect(EntityPlayerMP player, String targetPlayer){
		if(player.dimension != Util.getOnlinePlayer(targetPlayer).dimension){
			Util.travelToDimension(Util.getOnlinePlayer(targetPlayer).dimension, player, false);
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
	
	/*
	 * the kick takes 3 steps:
	 * 1.log the player's data
	 * 2.check if he's the team leader, if yes, reassign a leader. Then kick the player out of team
	 * 3.end the mini game if there's only one team left
	 * 4.kick the player back to lobby
	 */
	public void kick(EntityPlayerMP player, String targetPlayer){
		
		EntityPlayer playerToKick = Util.getOnlinePlayer(targetPlayer);
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(playerToKick);	
		ExtendedPlayer properties = ExtendedPlayer.get(playerToKick);

		/*
		 * if it's a treasure hunt game, simply end the game if playerToKick is the only one in his team
		 * be careful not to log his info twice
		 * 
		 * if it's a PvP game, log the player's data and kick him
		 * the game will automatically end if there's no one else in his team
		 */
		
		if(playerToKick != null){
			if(MiniGameUtil.isTreasure(playerToKick.dimension) && pdata.teamID != null) {
				if(PlayerTeam.get(pdata.teamID).numMembers() == 1){
					PlayerMiniGame.get(playerToKick.dimension).endGame("Kicked", "Kicked");
					Util.displayMessage(player, "You kicked "+targetPlayer+" back to Overworld");
					Util.displayMessage(playerToKick, "You've been kicked back to Overworld by Admin");
					return;
				} else {
					DataLogger.insertTreasureScore(Util.getServerID(), 
							PlayerMiniGame.get(playerToKick.dimension).getGameID(), 
							player.getDisplayName(), pdata.teamID, 
							properties.curTreasuresOpened, properties.curAltersActivated, Util.getDate());
				}
			} else if(MiniGameUtil.isPvP(playerToKick.dimension) && pdata.teamID != null)
				DataLogger.insertPVPScore(Util.getServerID(), 
						PlayerMiniGame.get(playerToKick.dimension).getGameID(), 
						player.getDisplayName(), pdata.teamID, 
						properties.curKills, properties.curDeaths, 
						properties.curAntiTerritory, Util.getDate());
			
			PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
			miniGamePacket.writeInt(playerToKick.dimension);
			Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP)playerToKick);
			pdata.leaveTeam();
			Util.travelToDimension(Resources.OVERWORLD, playerToKick, false);	
			Util.displayMessage(player, "You kicked "+targetPlayer+" back to Overworld");
			Util.displayMessage(playerToKick, "You've been kicked back to Overworld by Admin");
		} else {
			Util.displayMessage(player, "Cannot find player <"+targetPlayer+"> !");
		}
	}
	
	public void becomeRegularPlayer(EntityPlayerMP player){
		ExtendedPlayer.get(player).setAdminRights(false);
		player.setInvisible(false);
		player.capabilities.allowFlying = false;
		player.capabilities.disableDamage = false;
		PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_NORMAL);
		Contained.channel.sendTo(adminPacket.toPacket(),player);
	}
	
	public void setDimTime(int dim, long timeToSet){
//		DimensionManager.getWorld(dim).setWorldTime(timeToSet);
		MinecraftServer.getServer().worldServerForDimension(dim).setWorldTime(timeToSet);
		System.out.println("Setting Dim"+dim+"'s time to"+timeToSet);
	}
}
