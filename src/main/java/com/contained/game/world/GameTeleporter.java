package com.contained.game.world;

import com.contained.game.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * A teleportation between dimensions that doesn't require a portal.
 */
public class GameTeleporter extends Teleporter {
	private final WorldServer worldServer;
	
	public GameTeleporter(WorldServer server) {
		super(server);
		this.worldServer = server;
	}
	
	@Override
	public boolean makePortal(Entity ent) {
		return true;
	}
	
	@Override
	public void placeInPortal(Entity player, double x, double y, double z, float rot) {
		int posX = (int)Util.getRandomLocation(this.worldServer).getX();
		int posZ = (int)Util.getRandomLocation(this.worldServer).getY();
		player.setLocationAndAngles(posX, this.worldServer.getTopSolidOrLiquidBlock(posX, posZ), posZ, rot, 0);
		player.motionX = player.motionY = player.motionZ = 0;
	}
	
}
