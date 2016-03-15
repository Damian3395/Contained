package com.contained.game.handler;


import org.lwjgl.input.Keyboard;

import com.contained.game.util.Resources;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings {
	public static KeyBinding toggleVisualizerGUI;
	public static KeyBinding toggleTerritoryRender;
	public static KeyBinding toggleClassPerks;
	public static KeyBinding toggleGuild;
	
	public static void init() {
		// Data Visualization GUI Container Keyboard Event Handler
		toggleVisualizerGUI = new KeyBinding("key.dataVisGUI"
				, Keyboard.KEY_0
				, "key.categories."+Resources.MOD_ID);
		ClientRegistry.registerKeyBinding(toggleVisualizerGUI);
		
		// Territory Rendering GUI Container Keyboard Event Handler
		toggleTerritoryRender = new KeyBinding("key.territoryRender"
				, Keyboard.KEY_O
				, "key.categories."+Resources.MOD_ID);
		ClientRegistry.registerKeyBinding(toggleTerritoryRender);
		
		// Class Perks GUI Container Keyboard Event Handler
		toggleClassPerks = new KeyBinding("key.classPerkGUI"
				, Keyboard.KEY_P
				, "key.categories."+Resources.MOD_ID);
		ClientRegistry.registerKeyBinding(toggleClassPerks);
		
		// Guild GUI Container Keyboard Event Handler
		toggleGuild = new KeyBinding("key.guildGUI"
				, Keyboard.KEY_G
				,"key.categories."+Resources.MOD_ID);
		ClientRegistry.registerKeyBinding(toggleGuild);
	}
}
