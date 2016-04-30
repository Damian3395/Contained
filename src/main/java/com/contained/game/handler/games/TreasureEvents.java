package com.contained.game.handler.games;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.MiniGameUtil;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class TreasureEvents {
	public static void initializeTreasureGame(int dimID) {
		WorldServer w = DimensionManager.getWorld(dimID);
		Contained.getActiveTreasures(dimID).clear();
		if (w != null)
			MiniGameUtil.generateChest(w, 15, ContainedRegistry.CUSTOM_CHEST_LOOT);
	}
	
	@SubscribeEvent
	public void onTreasureChestOpen(PlayerInteractEvent event){
		if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer != null)
			handleTreasureChest(event.world, event.entityPlayer, event.x, event.y, event.z);
	}
	
	@SubscribeEvent
	public void onTreasureChestBreak(BlockEvent.BreakEvent event){
		if(event.getPlayer() != null)
			handleTreasureChest(event.world, event.getPlayer(), event.x, event.y, event.z);		
	}
	
	public void handleTreasureChest(World w, EntityPlayer p, int x, int y, int z) {
		if(!w.isRemote && (w.getBlock(x, y, z) instanceof BlockChest)) {	
			BlockCoord eventLocation = new BlockCoord(x, y, z);
			int dimID = w.provider.dimensionId;
			if (Contained.getActiveTreasures(dimID).contains(eventLocation)) {
				ExtendedPlayer properties = ExtendedPlayer.get(p);
				properties.treasuresOpened++;
				properties.curTreasuresOpened++;
				
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
				PlayerMiniGame miniGame = PlayerMiniGame.get(p.dimension);
				int teamID = miniGame.getTeamID(pdata);
				if (teamID != -1) {
					Contained.gameScores[p.dimension][teamID]++;
					ClientPacketHandlerUtil.syncMiniGameScore(p.dimension, teamID, Contained.gameScores[p.dimension][teamID]);
				}
				
				ClientPacketHandlerUtil.removeTreasureAndSync(dimID, eventLocation);
				MiniGameUtil.generateChest(w, 1, ContainedRegistry.CUSTOM_CHEST_LOOT);
			}
		}
	}
}
