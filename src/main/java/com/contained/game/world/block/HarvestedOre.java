package com.contained.game.world.block;

import java.util.Random;

import com.contained.game.util.Resources;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * The remnants of an ore block that has already been harvested. Over time,
 * will regenerate the original ore.
 */
public class HarvestedOre {
	public static String blockName = "harvestedOre";
	public static String textureName = Resources.MOD_ID+":harvestedOre";
	
	public static BlockHarvestedOre instance = new BlockHarvestedOre();
	
	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(instance,  blockName);
	}
	
	static class BlockHarvestedOre extends Block implements ITileEntityProvider {
		IIcon gor = null, dol = null, st1 = null, st2 = null, st3 = null, st4 = null;
		
		protected BlockHarvestedOre(){
			super(Material.rock);
			setHardness(50.0f);
			setResistance(6000.0f);
			setHarvestLevel("pickaxe", 0);
			setStepSound(Block.soundTypeStone);
			setCreativeTab(CreativeTabs.tabBlock);
			setBlockName(blockName);
			setBlockTextureName(textureName);
		}
		
		@Override
	    public Item getItemDropped(int par1, Random par2, int par3) {
	        return Item.getItemFromBlock(Blocks.cobblestone);
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
			this.gor = reg.registerIcon(HarvestedOre.textureName);
			this.dol = reg.registerIcon(HarvestedOre.textureName);
			this.st1 = reg.registerIcon(HarvestedOre.textureName);
			this.st2 = reg.registerIcon(HarvestedOre.textureName);
			this.st3 = reg.registerIcon(HarvestedOre.textureName);
			this.st4 = reg.registerIcon(HarvestedOre.textureName);
		}

		@Override
		public TileEntity createNewTileEntity(World arg0, int arg1) {
			return new HarvestedOreTE();
		}
	}	
}
