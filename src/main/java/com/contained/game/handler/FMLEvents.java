package com.contained.game.handler;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class FMLEvents {

	/**
	 * Handle ticking of mini-game timer on client and server.
	 */
	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (event.phase == Phase.START) {
			for(int i=Resources.MIN_PVP_DIMID; i<=Resources.MAX_PVP_DIMID; i++)
				processGameTick(i);
			for(int i=Resources.MIN_TREASURE_DIMID; i<=Resources.MAX_TREASURE_DIMID; i++)
				processGameTick(i);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.START) { 
			if (Contained.gameActive[0])
				Contained.tickTimeLeft(0);
		}
	}
	
	public void processGameTick(int dimID) {
		if (Contained.gameActive[dimID]) {
			Contained.tickTimeLeft(dimID);
			int timeRemaining = Contained.timeLeft[dimID];
			if (timeRemaining % 300 == 0 || timeRemaining == 0)
				ClientPacketHandlerUtil.syncMinigameTime(dimID);
			if (timeRemaining == 0)
				MiniGameUtil.stopGame(dimID);
		}
	}
	
}
