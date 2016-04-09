package com.contained.game.util;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandlerUtil;

public class MiniGameUtil {
	public static boolean isPvP(int dimID) {
		if (dimID >= Resources.MIN_PVP_DIMID && dimID <= Resources.MAX_PVP_DIMID)
			return true;
		return false;
	}
	
	public static boolean isTreasure(int dimID) {
		if (dimID >= Resources.MIN_TREASURE_DIMID && dimID <= Resources.MAX_TREASURE_DIMID)
			return true;
		return false;
	}
	
	public static void startGame(int dimID) {
		if (isPvP(dimID))
			Contained.timeLeft[dimID] = Contained.configs.pvpDuration*20;
		else if (isTreasure(dimID))
			Contained.timeLeft[dimID] = Contained.configs.treasureDuration*20;
		else
			return;
		Contained.gameActive[dimID] = true;
		ClientPacketHandlerUtil.syncMinigameTime(dimID);
	}
	
	public static void stopGame(int dimID) {
		Contained.gameActive[dimID] = false;
		Contained.timeLeft[dimID] = 0;
		ClientPacketHandlerUtil.syncMinigameTime(dimID);
	}
	
	public static void resetGame(int dimID) {
		// TODO: Kick any remaining people in this dimension (including offline
		// players) back to the lobby, clear any stale data regarding this game,
		// and regenerate the dimension.
		stopGame(dimID);
		WorldServer w = DimensionManager.getWorld(dimID);
		if (w != null) {
			ArrayList<EntityPlayer> toTeleport = new ArrayList<EntityPlayer>();
			for(Object p : w.playerEntities) {
				if (p instanceof EntityPlayer)
					toTeleport.add((EntityPlayer)p);
			}
			for(EntityPlayer p : toTeleport)
				Util.travelToDimension(0, p);
		}
	}
}
