package com.contained.game.handler;

import com.contained.game.Contained;
import com.contained.game.item.ItemTerritory;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TeamTerritoryEvents {

	@SubscribeEvent
	//When a player joins the server, send their client the territory & team data.
	public void onJoin(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayer && !event.world.isRemote) {
			EntityPlayer joined = (EntityPlayer)event.entity;
			
			//Future TODO:
			//  -Remind the player on join if they have pending invitations.
			//  -Notify the player on join if one of their invitations got accepted
			//   since the last time they were online.
			
			if (PlayerTeamIndividual.get(joined) == null) {
				// Server has no info about this player, this must be their first
				// time joining. Initialize their custom data.
				Contained.teamMemberData.add(new PlayerTeamIndividual(joined.getDisplayName()));
			}
			
			ClientPacketHandler.packetSyncTeams(Contained.teamData).sendToPlayer(joined);
			ClientPacketHandler.packetSyncTerritories(Contained.territoryData).sendToPlayer(joined);			
		}
	}
	
	@SubscribeEvent
	//Handle the usage of the territory claim/removal items
	public void onItemUse(PlayerInteractEvent event) {
		if (!event.world.isRemote) {
			ItemStack usedItem = event.entityPlayer.getHeldItem();
			if (usedItem == null)
				return;
			if (usedItem.getItem() instanceof ItemTerritory.TerritoryRod) {
				ItemTerritory.TerritoryRod rod = (ItemTerritory.TerritoryRod)usedItem.getItem();
				rod.claimTerritory(event.entityPlayer, event.x, event.z);
			}
			else if (usedItem.getItem() instanceof ItemTerritory.AntiTerritoryRod) {
				ItemTerritory.AntiTerritoryRod rod = (ItemTerritory.AntiTerritoryRod)usedItem.getItem();
				rod.removeTerritory(event.entityPlayer, event.x, event.z);
			}
		}
	}
	
}
