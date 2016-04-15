package com.contained.game.handler.games;

import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.minigames.TreasureChestGenerator;
import com.contained.game.network.ClientPacketHandlerUtil;

import net.minecraft.block.BlockChest;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TreasureEvents {
	@SubscribeEvent
	public void onTreasureChestOpen(PlayerInteractEvent event){
		if(!event.world.isRemote
				&& (event.world.getBlock(event.x, event.y, event.z) instanceof BlockChest) 
				&& event.action == Action.RIGHT_CLICK_BLOCK){	
			BlockCoord eventLocation = new BlockCoord(event.x, event.y, event.z);
			int dimID = event.world.provider.dimensionId;
			if (Contained.getActiveTreasures(dimID).contains(eventLocation)) {
				ClientPacketHandlerUtil.removeTreasureAndSync(dimID, eventLocation);
				//TODO: Increase team score by 1.
				TreasureChestGenerator.generateChest(event.world, 1, ContainedRegistry.CUSTOM_CHEST_LOOT);
			}
		}
		
	}
}
