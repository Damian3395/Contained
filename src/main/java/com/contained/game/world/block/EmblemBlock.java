package com.contained.game.world.block;

import com.contained.game.data.DataLogger;
import com.contained.game.item.ItemTerritory;
import com.contained.game.item.TreasureGem;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Emblem Blocks used in the Treasure Mini-game. Must right click these with emblems
 * constructed from items found in chests, in order to activate them.
 */
public class EmblemBlock {	
	public static BlockEmblemReciever fireEmblemInact = new BlockEmblemReciever(TreasureGem.RED, false, "fireEmblemBlock");
	public static BlockEmblemReciever earthEmblemInact = new BlockEmblemReciever(TreasureGem.GREEN, false, "earthEmblemBlock");
	public static BlockEmblemReciever waterEmblemInact = new BlockEmblemReciever(TreasureGem.BLUE, false, "waterEmblemBlock");
	public static BlockEmblemReciever windEmblemInact = new BlockEmblemReciever(TreasureGem.WHITE, false, "windEmblemBlock");
	public static BlockEmblemReciever fireEmblemAct = new BlockEmblemReciever(TreasureGem.RED, true, "fireEmblemBlockActive");
	public static BlockEmblemReciever earthEmblemAct = new BlockEmblemReciever(TreasureGem.GREEN, true, "earthEmblemBlockActive");
	public static BlockEmblemReciever waterEmblemAct = new BlockEmblemReciever(TreasureGem.BLUE, true, "waterEmblemBlockActive");
	public static BlockEmblemReciever windEmblemAct = new BlockEmblemReciever(TreasureGem.WHITE, true, "windEmblemBlockActive");

	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(fireEmblemInact,  fireEmblemInact.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(earthEmblemInact,  earthEmblemInact.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(waterEmblemInact,  waterEmblemInact.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(windEmblemInact,  windEmblemInact.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(fireEmblemAct,  fireEmblemAct.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(earthEmblemAct,  earthEmblemAct.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(waterEmblemAct,  waterEmblemAct.getUnlocalizedName().replace("tile.", ""));
		GameRegistry.registerBlock(windEmblemAct,  windEmblemAct.getUnlocalizedName().replace("tile.", ""));
	}

	static class BlockEmblemReciever extends BlockContainer {
		IIcon gor = null, dol = null, st1 = null, st2 = null, st3 = null, st4 = null;
		public int myColor;
		public boolean active;

		protected BlockEmblemReciever(int color, boolean isActive, String name){
			super(Material.rock);
			myColor = color;
			this.active = isActive;
			setHardness(-1);
			setResistance(6000.0f);
			setStepSound(Block.soundTypeStone);
			setCreativeTab(CreativeTabs.tabBlock);
			setBlockName(name);
			if (isActive) {
				setBlockTextureName(Resources.MOD_ID+":activeEmblem"+color);
				setLightLevel(1F);
			}
			else
				setBlockTextureName(Resources.MOD_ID+":inactiveEmblem"+color);
		}

		@Override
		public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int par6, float par7, float par8, float par9) {
			EmblemBlockTE te = getEmblemTE(w, x, y, z);
			if (te == null || p.isSneaking() || w.isRemote)
				return false;

			PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
			if (pdata.teamID != null && !te.isActive && te.teamID != null && pdata.teamID.equals(te.teamID)) {
				ItemStack holding = p.getHeldItem();
				Item compareItem = (Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+TreasureGem.getUnlocalizedName(myColor,TreasureGem.FULL));
				if (holding != null && holding.getItem().equals(compareItem)) 
				{
					if (myColor == TreasureGem.RED)
						w.setBlock(x, y, z, fireEmblemAct);
					else if (myColor == TreasureGem.GREEN)
						w.setBlock(x, y, z, earthEmblemAct);
					else if (myColor == TreasureGem.BLUE)
						w.setBlock(x, y, z, waterEmblemAct);
					else
						w.setBlock(x, y, z, windEmblemAct);

					p.inventory.consumeInventoryItem(compareItem);
					
					EmblemBlockTE newTE = getEmblemTE(w, x, y, z);
					if (newTE != null && newTE.isActive) {
						// Count nearby activated emblem altars, to determine
						// if player's team has won or not.
						newTE.teamID = pdata.teamID;						
						int searchRadius = 9;
						int count = 0;
						for(int i=-searchRadius; i<=searchRadius; i++) {
							for(int j=-searchRadius; j<=searchRadius; j++) {
								EmblemBlockTE checkTE = getEmblemTE(w, x+i, y, z+j);
								if (checkTE != null && checkTE.isActive)
									count++;
							}
						}
						
						PlayerTeam team = PlayerTeam.get(pdata.teamID);
						for(Object player : w.playerEntities) {
							if (player instanceof EntityPlayer)
								Util.displayMessage((EntityPlayer)player, "[*] "+team.formattedName()+"§f has activated "+count+" of 3 altars!");
						}
						
						DataLogger.insertAlter(Util.getServerID(), Util.getGameID(p.dimension), pdata.teamID, p.getDisplayName(), Util.getDate());
						
						if (count >= 3)
							MiniGameUtil.teamWins(pdata.teamID, p.dimension, "EMBLEMS");
					}
					return true;
				}
				else
					Util.displayError(p, "You must be holding a completed emblem of the same color.");
			}
			else if (!te.isActive)
				Util.displayError(p, "This Emblem Altar does not belong to your team!");
			return false;
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
			String texname = "inactiveEmblem";
			if (this.active)
				texname = "activeEmblem";
			this.gor = reg.registerIcon(Resources.MOD_ID+":"+texname+myColor);
			this.dol = reg.registerIcon(Resources.MOD_ID+":"+texname+myColor);
			this.st1 = reg.registerIcon(Resources.MOD_ID+":"+texname+myColor);
			this.st2 = reg.registerIcon(Resources.MOD_ID+":"+texname+myColor);
			this.st3 = reg.registerIcon(Resources.MOD_ID+":"+texname+myColor);
			this.st4 = reg.registerIcon(Resources.MOD_ID+":"+texname+myColor);
		}

		@Override
		public TileEntity createNewTileEntity(World arg0, int arg1) {
			return new EmblemBlockTE(this.myColor, this.active);
		}
	}	
	
	public static EmblemBlockTE getEmblemTE(World w, int x, int y, int z) {
		TileEntity te = w.getTileEntity(x, y, z);
		if (te != null && te instanceof EmblemBlockTE)
			return (EmblemBlockTE)te;
		else
			return null;
	}
}
