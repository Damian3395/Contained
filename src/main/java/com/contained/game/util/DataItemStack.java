package com.contained.game.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DataItemStack {
	public final int id;
	public final int meta;
	
	public DataItemStack(ItemStack stack) { 
		id = Item.getIdFromItem(stack.getItem());
		meta = stack.getItemDamage();
	}
	
	public DataItemStack(Item i) {
		id = Item.getIdFromItem(i);
		meta = -1;
	}
	
	public DataItemStack(Item i, int count, int meta) {
		id = Item.getIdFromItem(i);
		this.meta = meta;
	}
	
	public DataItemStack(Block i) {
		id = Item.getIdFromItem(Item.getItemFromBlock(i));
		meta = -1;
	}
	
	public DataItemStack(Block i, int count, int meta) {
		id = Item.getIdFromItem(Item.getItemFromBlock(i));
		this.meta = meta;
	}
	
	@Override
	public boolean equals(Object o) {		
		if (o instanceof ItemStack) {
			ItemStack cstack = (ItemStack)o;
			if (this.meta == -1)
				return this.id == Item.getIdFromItem(cstack.getItem());
			else
				return this.id == Item.getIdFromItem(cstack.getItem())
					&& this.meta == cstack.getItemDamage();		
		} else if (o instanceof DataItemStack) {
			DataItemStack cstack = (DataItemStack)o;
			if (cstack.meta == -1 || this.meta == -1)
				return cstack.id == this.id;
			else
				return cstack.id == this.id && cstack.meta == this.meta;
		}
		return false;
	}
	
	@Override 
	public int hashCode() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "[id: "+this.id+", meta: "+this.meta+"]";
	}
}
