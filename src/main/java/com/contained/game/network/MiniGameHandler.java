package com.contained.game.network;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.entity.player.EntityPlayerMP;

//TODO: Finish This
public class MiniGameHandler {
	public MiniGameHandler(){}
	
	public void joinMiniGame(EntityPlayerMP player){
		ExtendedPlayer properties = ExtendedPlayer.get(player);
		properties.setJoiningGame(true);
		
		PlayerMiniGame game = findGame();
		if(game != null){ //Join Game
			game.addPlayer(player);
			
			if(game.isGameReady())
				game.launchGame();
		}else{ //Create New Game
			PlayerMiniGame newGame = new PlayerMiniGame();
			if(newGame.getGameDimension() == -1){
				properties.setJoiningGame(false);
				Util.debugMessage(player, "Sorry, All Games Are Filled, Please Try Again Later.");
				return;
			}
			
			newGame.addPlayer(player);
			Contained.miniGames.add(newGame);
		}
		
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.JOIN_MINI_GAME);
		Contained.channel.sendTo(packet.toPacket(), player);
	}
	
	public void cancelMiniGame(EntityPlayerMP player){
		ExtendedPlayer properties = ExtendedPlayer.get(player);
		properties.setJoiningGame(false);
		
		for(PlayerMiniGame miniGame : Contained.miniGames){
			if(miniGame.hasPlayer(player.getDisplayName())){
				miniGame.removePlayer(player);
				break;
			}
		}
		
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.CANCEL_JOIN_MINI_GAME);
		Contained.channel.sendTo(packet.toPacket(), player);
	}
	
	private PlayerMiniGame findGame(){
		for(PlayerMiniGame game : Contained.miniGames)
			if(!game.isGameReady())
				return game;
		
		return null;
	}
}
