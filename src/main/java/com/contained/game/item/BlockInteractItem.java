package com.contained.game.item;

import net.minecraft.entity.player.EntityPlayer;

/**
 * An item which should perform an action when right-clicked on a instance.
 */
public interface BlockInteractItem {

	public void onBlockInteract(EntityPlayer p, int x, int y, int z);
	
}
