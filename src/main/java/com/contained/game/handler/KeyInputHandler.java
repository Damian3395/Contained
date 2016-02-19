package com.contained.game.handler;

import com.contained.game.ui.DataVisualization;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class KeyInputHandler {
	private DataVisualization gui;
	
	public KeyInputHandler(DataVisualization guiReference) {
		this.gui = guiReference;
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeyBindings.toggleVisualizerGUI.isPressed()) {
			gui.guiRender = !gui.guiRender;
		}
	}
}
