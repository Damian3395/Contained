package com.contained.game.world.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTownHall extends Container {

	protected TownManageTE tileEntity;
	
	public ContainerTownHall(InventoryPlayer inv, TownManageTE te) {
		int guiX = 9;
		int guiY = 103;
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, guiX + j * 18, guiY + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(inv, i, guiX + i * 18, guiY+58));
		this.tileEntity = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer arg0) {
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot.
	 */
	public ItemStack transferStackInSlot(EntityPlayer p, int slotID)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack)null);
			else
				slot.onSlotChanged();

			if (itemstack1.stackSize == itemstack.stackSize)
				return null;

			slot.onPickupFromSlot(p, itemstack1);
		}

		return itemstack;
	}

}
