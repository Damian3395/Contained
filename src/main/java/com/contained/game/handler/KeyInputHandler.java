package com.contained.game.handler;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.DataVisualization;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

public class KeyInputHandler {
	private Minecraft mc = FMLClientHandler.instance().getClient();
	private DataVisualization gui_data;
	private ClassPerks gui_perks;
	
	public KeyInputHandler(DataVisualization guiReference) {
		this.gui_data = guiReference;
	}
	
	public KeyInputHandler(ClassPerks guiReference){
		this.gui_perks = guiReference;
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeyBindings.toggleVisualizerGUI.isPressed())
			gui_data.guiRender = !gui_data.guiRender;
		if(KeyBindings.toggleClassPerks.isPressed())
			mc.displayGuiScreen(new ClassPerks());
	}
}
