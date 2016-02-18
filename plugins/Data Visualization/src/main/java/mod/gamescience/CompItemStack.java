package mod.gamescience;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Minecraft's ItemStack class does not contain an equals method, so
 * they are not comparable -- this is a class to work around that.
 */
public class CompItemStack {
	public final int id;
	public final int meta;
	
	public CompItemStack(ItemStack stack) { 
		id = Item.getIdFromItem(stack.getItem());
		meta = stack.getItemDamage();
	}
	
	public CompItemStack(Item i) {
		id = Item.getIdFromItem(i);
		meta = -1;
	}
	
	public CompItemStack(Item i, int count, int meta) {
		id = Item.getIdFromItem(i);
		this.meta = meta;
	}
	
	public CompItemStack(Block i) {
		id = Item.getIdFromItem(Item.getItemFromBlock(i));
		meta = -1;
	}
	
	public CompItemStack(Block i, int count, int meta) {
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
		} else if (o instanceof CompItemStack) {
			CompItemStack cstack = (CompItemStack)o;
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
