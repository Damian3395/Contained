package com.contained.game.handler;


import org.lwjgl.input.Keyboard;

import com.contained.game.util.Resources;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings {
	public static KeyBinding toggleVisualizerGUI;
	
	public static void init() {
		toggleVisualizerGUI = new KeyBinding("key.dataVisGUI"
				, Keyboard.KEY_0
				, "key.categories."+Resources.MOD_ID);
		
		ClientRegistry.registerKeyBinding(toggleVisualizerGUI);
	}
}
