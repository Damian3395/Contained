package com.contained.game;

import com.contained.game.item.*;
import com.contained.game.world.block.*;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ContainedRegistry {

	public static WastelandBlock wasteland;
	public static WastelandBush wastelandBush;
	public static TownManageBlock townHall;
	public static TerritoryMachine claimMachine;
	public static AntiTerritoryMachine antiMachine;
	
	public static ItemTerritory territoryItems;
	public static TerritoryFlag claimFlag;
	public static TutorialBook book;
	
	public void preInit(FMLPreInitializationEvent event) {
		wasteland = new WastelandBlock(); 		  wasteland.preInit(event);
		wastelandBush = new WastelandBush(); 	  wastelandBush.preInit(event);
		townHall = new TownManageBlock(); 		  townHall.preInit(event);
		claimMachine = new TerritoryMachine();    claimMachine.preInit(event);
		antiMachine = new AntiTerritoryMachine(); antiMachine.preInit(event);
		
		territoryItems = new ItemTerritory();
		claimFlag = new TerritoryFlag();
		book = new TutorialBook();
		GameRegistry.registerItem(ContainedRegistry.book, "tutorialBook");
	}
	
	public void init(FMLInitializationEvent event) {
		TerritoryFlag.defineRecipe();
		TownManageBlock.defineRecipe();
		GameRegistry.registerTileEntity(TerritoryMachineTE.class, "TerritoryMachineTE");
	}
	
}
