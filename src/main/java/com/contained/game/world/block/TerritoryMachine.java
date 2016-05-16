package com.contained.game.world.block;

import com.contained.game.util.Resources;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TerritoryMachine {
	public static String blockName = "ClaimTerritoryMachine";
	public static String textureName = Resources.MOD_ID+":territory_machine";
	public static String textureTop = Resources.MOD_ID+":territory_machine_top";
	
	public static BlockClaimTerritory instance = new BlockClaimTerritory();
	
	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(instance,  blockName);
	}
	
	public static class BlockClaimTerritory extends BlockContainer {
		IIcon gor = null, dol = null, st1 = null, st2 = null, st3 = null, st4 = null;
		
		protected BlockClaimTerritory(){
			super(Material.rock);
			setHardness(22.5f);
			setResistance(6000.0f);
			setHarvestLevel("pickaxe", 0);
			setStepSound(Block.soundTypeStone);
			setCreativeTab(CreativeTabs.tabBlock);
			setBlockName(blockName);
			setBlockTextureName(textureName);
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
			this.gor = reg.registerIcon(TerritoryMachine.textureTop);
			this.dol = reg.registerIcon(TerritoryMachine.textureTop);
			this.st1 = reg.registerIcon(TerritoryMachine.textureName);
			this.st2 = reg.registerIcon(TerritoryMachine.textureName);
			this.st3 = reg.registerIcon(TerritoryMachine.textureName);
			this.st4 = reg.registerIcon(TerritoryMachine.textureName);
		}
		
		@Override
		public TileEntity createNewTileEntity(World w, int par1) {
			return new TerritoryMachineTE(true, w.provider.dimensionId);
		}
		
		@Override
		public boolean isOpaqueCube() {
			return false;
		}
		
		@Override
		public boolean hasTileEntity() {
			return true;
		}
	}	
}
