package com.contained.game.world.block;

import com.contained.game.item.TerritoryFlag;
import com.contained.game.ui.GuiTownManage;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TownManageBlock {
	public static String blockName = "townHall";
	public static String textureTop = Resources.MOD_ID+":townBlock_top";
	public static String textureSide = Resources.MOD_ID+":townBlock_side";
	public static String textureBottom = Resources.MOD_ID+":townBlock_bottom";
	
	public static BlockTownHall instance = new BlockTownHall();
	
	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(instance,  blockName);
	}
	
	public static void defineRecipe(){
		ItemStack output = new ItemStack(instance, 1);
		ItemStack inputWorkbench = new ItemStack(Blocks.crafting_table, 1);
		ItemStack inputFlag = new ItemStack(TerritoryFlag.instance, 1);
		
		GameRegistry.addShapelessRecipe(output, inputWorkbench, inputFlag);
		
		// Data for use in the Mantle books.
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MantleClientRegistry.registerManualSmallRecipe(blockName, output,
					inputFlag, inputWorkbench,
					null, null);
			MantleClientRegistry.registerManualIcon(blockName, output);
		}
	}
	
	static class BlockTownHall extends Block {
		IIcon gor = null, dol = null, st1 = null, st2 = null, st3 = null, st4 = null;
		
		protected BlockTownHall(){
			super(Material.rock);
			setHardness(5.0f);
			setResistance(6000.0f);
			setHarvestLevel("pickaxe", 0);
			setStepSound(Block.soundTypeStone);
			setCreativeTab(CreativeTabs.tabBlock);
			setBlockName(blockName);
			setBlockTextureName(textureTop);
		}
		
		@Override
		public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int par6, float par7, float par8, float par9) {
			if (p.worldObj.isRemote) {
				Minecraft mc = Minecraft.getMinecraft();
				PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
				mc.displayGuiScreen(new GuiTownManage(playerData.teamID));
			}
			return true;
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
			this.gor = reg.registerIcon(TownManageBlock.textureBottom);
			this.dol = reg.registerIcon(TownManageBlock.textureTop);
			this.st1 = reg.registerIcon(TownManageBlock.textureSide);
			this.st2 = reg.registerIcon(TownManageBlock.textureSide);
			this.st3 = reg.registerIcon(TownManageBlock.textureSide);
			this.st4 = reg.registerIcon(TownManageBlock.textureSide);
		}
	}	
}
