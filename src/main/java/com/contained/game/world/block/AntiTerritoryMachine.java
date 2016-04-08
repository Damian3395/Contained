package com.contained.game.world.block;

import java.util.List;
import java.util.Random;

import com.contained.game.data.Data;
import com.contained.game.user.PlayerTeam;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class AntiTerritoryMachine {
	public static String blockName = "RemoveTerritoryMachine";
	public static String textureName = Resources.MOD_ID+":antiterritory_machine";
	public static String textureTop = Resources.MOD_ID+":antiterritory_machine_top";
	
	public static BlockAntiTerritory instance = new BlockAntiTerritory();
	
	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(instance, AntiTerritoryMachine.IBlockAntiTerritory.class, blockName);
	}
	
	public static class BlockAntiTerritory extends BlockContainer {
		IIcon gor = null, dol = null, st1 = null, st2 = null, st3 = null, st4 = null;
		
		protected BlockAntiTerritory(){
			super(Material.rock);
			setHardness(22.5f);
			setResistance(6000.0f);
			setHarvestLevel("pickaxe", 0);
			setStepSound(Block.soundTypeStone);
			setCreativeTab(CreativeTabs.tabBlock);
			setBlockName(blockName);
			setBlockTextureName(textureName);
		}
		
		@Override
		public void breakBlock(World w, int x, int y, int z, Block b, int meta) {
			if (!w.isRemote) {
				String teamID = null;
				TileEntity te = w.getTileEntity(x, y, z);
				if (te != null && te instanceof TerritoryMachineTE) {
					TerritoryMachineTE machine = (TerritoryMachineTE)te;
					teamID = machine.teamID;
				}
				
				ItemStack toDrop = new ItemStack(instance, 1);
				if (teamID != null) {
					NBTTagCompound itemData = Data.getTagCompound(toDrop);
					itemData.setString("teamOwner", teamID);
					toDrop.setTagCompound(itemData);
				}
				w.spawnEntityInWorld(new EntityItem(w, x, y+1, z, toDrop));
			}
			
			super.breakBlock(w, x, y, z, b, meta);
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
			this.gor = reg.registerIcon(AntiTerritoryMachine.textureTop);
			this.dol = reg.registerIcon(AntiTerritoryMachine.textureTop);
			this.st1 = reg.registerIcon(AntiTerritoryMachine.textureName);
			this.st2 = reg.registerIcon(AntiTerritoryMachine.textureName);
			this.st3 = reg.registerIcon(AntiTerritoryMachine.textureName);
			this.st4 = reg.registerIcon(AntiTerritoryMachine.textureName);
		}
		
		@Override
		public TileEntity createNewTileEntity(World w, int par1) {
			return new TerritoryMachineTE(false);
		}
		
		@Override
		public int quantityDropped(Random r) {
			// We'll handle the item drop ourself, so the team data can be
			// transferred to the dropped itemstack.
			return 0; 
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
	
	public static class IBlockAntiTerritory extends ItemBlock {
		public IBlockAntiTerritory(Block b) {
			super(b);
		}
		
		@Override
	    // Display the linked team in the hover tooltip.
		public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean bool)
		{
			super.addInformation(itemStack, player, list, bool);
			
			String teamName = "[Disbanded Team]";
			String formatCode = "";
			
			NBTTagCompound itemData = Data.getTagCompound(itemStack);	
			String teamToRemove = itemData.getString("teamOwner");
			if (teamToRemove != null && !teamToRemove.equals("")) {
				PlayerTeam team = PlayerTeam.get(teamToRemove, player.dimension);
				if (team != null) {
					teamName = team.displayName;
					formatCode = team.getFormatCode();
				}
			}
			
			list.add("Linked Team: "+formatCode+"§l"+teamName);
		}
	}
}
