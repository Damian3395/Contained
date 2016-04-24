package com.contained.game.world;

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
		ChunkCoordinates c = this.worldServer.provider.getRandomizedSpawnPoint();
		player.setLocationAndAngles(c.posX, c.posY, c.posZ, rot, 0);
		player.motionX = player.motionY = player.motionZ = 0;
	}
	
}
