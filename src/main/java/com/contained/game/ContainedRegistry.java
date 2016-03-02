package com.contained.game;

import com.contained.game.item.ItemTerritory;
import com.contained.game.item.TerritoryFlag;
import com.contained.game.item.TutorialBook;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ContainedRegistry {

	public static ItemTerritory territoryItems;
	public static TerritoryFlag claimFlag;
	public static TutorialBook book;
	
	public void preInit(FMLPreInitializationEvent event) {
		territoryItems = new ItemTerritory();
		claimFlag = new TerritoryFlag();
		book = new TutorialBook();
		GameRegistry.registerItem(ContainedRegistry.book, "tutorialBook");
	}
	
	public void init(FMLInitializationEvent event) {
		TerritoryFlag.defineRecipe();
	}
	
}
