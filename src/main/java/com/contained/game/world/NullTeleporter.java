package com.contained.game.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * A teleportation between dimensions that doesn't require a portal.
 */
public class NullTeleporter extends Teleporter {

	public NullTeleporter(WorldServer server) {
		super(server);
	}
	
	@Override
	public boolean makePortal(Entity ent) {
		return true;
	}
	
}
