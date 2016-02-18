package mod.gamescience;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;

public class WastelandBlock {
	public static String blockName = "wasteland";
	public static String textureName = "wasteland";

	public static BlockWasteland block = new BlockWasteland();

	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(block, blockName);
	}

	static class BlockWasteland extends Block {
		IIcon gor = null, dol = null, st1 = null, st2 = null, st3 = null, st4 = null;

		protected BlockWasteland() {
			super(Material.ground);
			setHardness(2.0f);
			setResistance(2.0F);
			setHarvestLevel("shovel", 0);
			setStepSound(Block.soundTypeSand);
			setCreativeTab(CreativeTabs.tabBlock);
			setBlockName(blockName);
			setBlockTextureName(textureName);
		}

		//Modify bounding box so it can detect collisions with entities
		public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
		{
			float f = 0.125F;
			return AxisAlignedBB.getBoundingBox((double)p_149668_2_, (double)p_149668_3_, (double)p_149668_4_, (double)(p_149668_2_ + 1), (double)((float)(p_149668_3_ + 1) - f), (double)(p_149668_4_ + 1));
		}

		//Players move very slowly when walking on wasteland blocks, and are given a
		//heavy hunger effect. However, monsters are not given these effects.
		public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity ent)
		{
			if (!(ent instanceof EntityMob)) {
				ent.motionX *= 0.25D;
				ent.motionZ *= 0.25D;
				if (!w.isRemote && ent instanceof EntityLivingBase)
					((EntityLivingBase)ent).addPotionEffect(
							new PotionEffect(Potion.hunger.id, 20, 3));
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public IIcon getIcon(int i, int par2){
			switch(i) {
			case 0: return gor;
			case 1: return dol;
			case 2: return st1;
			case 3: return st2;
			case 4: return st4;
			case 5: return st3;
			default: return gor;
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void registerBlockIcons(IIconRegister reg){
			this.gor = reg.registerIcon(WastelandBlock.textureName);
			this.dol = reg.registerIcon(WastelandBlock.textureName);
			this.st1 = reg.registerIcon(WastelandBlock.textureName);
			this.st2 = reg.registerIcon(WastelandBlock.textureName);
			this.st3 = reg.registerIcon(WastelandBlock.textureName);
			this.st4 = reg.registerIcon(WastelandBlock.textureName);
		}
	}
}
