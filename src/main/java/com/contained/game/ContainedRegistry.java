package com.contained.game;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import mantle.client.MProxyClient;
import mantle.lib.client.MantleClientRegistry;

import com.contained.game.entity.DeepBlaze;
import com.contained.game.entity.DeepLavaSlime;
import com.contained.game.entity.DeepWitherSkeleton;
import com.contained.game.item.*;
import com.contained.game.ui.ToolPage;
import com.contained.game.util.Resources;
import com.contained.game.world.block.*;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ContainedRegistry {

	public static WastelandBlock wasteland;
	public static WastelandBush wastelandBush;
	public static HarvestedOre harvestOre;
	public static TownManageBlock townHall;
	public static TerritoryMachine claimMachine;
	public static AntiTerritoryMachine antiMachine;
	
	public static ItemLife life;
	public static ItemTerritory territoryItems;
	public static DowsingRod oreDetector;
	public static TerritoryFlag claimFlag;
	public static SurveyClipboard surveyItem;
	public static TutorialBook book;
	
	public static DeepBlaze mobBlaze;
	public static DeepLavaSlime mobMagma;
	public static DeepWitherSkeleton mobWitherSkel;
	
	public void preInit(FMLPreInitializationEvent event) {
		wasteland = new WastelandBlock(); 		  wasteland.preInit(event);
		wastelandBush = new WastelandBush(); 	  wastelandBush.preInit(event);
		harvestOre = new HarvestedOre();		  harvestOre.preInit(event);
		townHall = new TownManageBlock(); 		  townHall.preInit(event);
		claimMachine = new TerritoryMachine();    claimMachine.preInit(event);
		antiMachine = new AntiTerritoryMachine(); antiMachine.preInit(event);
		
		mobBlaze = new DeepBlaze(); 
		mobBlaze.instance = Contained.instance;
		mobBlaze.preInit(event);
		
		mobMagma = new DeepLavaSlime(); 
		mobMagma.instance = Contained.instance;
		mobMagma.preInit(event);
		
		mobWitherSkel = new DeepWitherSkeleton(); 
		mobWitherSkel.instance = Contained.instance;
		mobWitherSkel.preInit(event);
		
		life = new ItemLife();
		territoryItems = new ItemTerritory();
		oreDetector = new DowsingRod();
		claimFlag = new TerritoryFlag();
		surveyItem = new SurveyClipboard();
		book = new TutorialBook();
		GameRegistry.registerItem(ContainedRegistry.book, "tutorialBook");
	}
	
	public void init(FMLInitializationEvent event) {
		ItemLife.defineRecipe();
		TerritoryFlag.defineRecipe();
		TownManageBlock.defineRecipe();
		MantleClientRegistry.registerManualIcon(ItemTerritory.addTerritoryName, new ItemStack(ItemTerritory.addTerritory, 1));
		MantleClientRegistry.registerManualIcon(ItemTerritory.removeTerritoryName, new ItemStack(ItemTerritory.removeTerritory, 1));
		MantleClientRegistry.registerManualIcon(AntiTerritoryMachine.blockName, new ItemStack(AntiTerritoryMachine.instance, 1));
		MantleClientRegistry.registerManualIcon(TerritoryMachine.blockName, new ItemStack(TerritoryMachine.instance, 1));
		MProxyClient.registerManualPage("toolpage", ToolPage.class);
		
		GameRegistry.registerTileEntity(TerritoryMachineTE.class, "TerritoryMachineTE");
		GameRegistry.registerTileEntity(TownManageTE.class, "TownManageTE");
		GameRegistry.registerTileEntity(HarvestedOreTE.class, "harvestedOreTE");
		
		for(int i=Resources.MIN_PVP_DIMID; i<=Resources.MAX_PVP_DIMID; i++)
			DimensionManager.registerDimension(i, 0);
		for(int i=Resources.MIN_TREASURE_DIMID; i<=Resources.MAX_TREASURE_DIMID; i++)
			DimensionManager.registerDimension(i, 0);
	}
	
}
