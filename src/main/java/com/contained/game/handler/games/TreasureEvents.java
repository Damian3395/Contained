package com.contained.game.handler.games;

import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.minigames.TreasureChestGenerator;
import com.contained.game.network.ClientPacketHandlerUtil;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TreasureEvents {
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
				ClientPacketHandlerUtil.removeTreasureAndSync(dimID, eventLocation);
				//TODO: Increase team score by 1.
				TreasureChestGenerator.generateChest(w, 1, ContainedRegistry.CUSTOM_CHEST_LOOT);
			}
		}
	}
}
