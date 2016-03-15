package com.contained.game.handler;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.GuiGuild;
import com.contained.game.ui.TerritoryRender;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

public class KeyInputHandler {
	private Minecraft mc = FMLClientHandler.instance().getClient();
	private DataVisualization gui_data;
	private TerritoryRender territory;
	
	public KeyInputHandler(DataVisualization guiReference,
							   TerritoryRender terrReference) {
		this.gui_data = guiReference;
		this.territory = terrReference;
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeyBindings.toggleVisualizerGUI.isPressed())
			gui_data.guiRender = !gui_data.guiRender;
		if (KeyBindings.toggleClassPerks.isPressed())
			mc.displayGuiScreen(new ClassPerks());
		if(KeyBindings.toggleGuild.isPressed())
			mc.displayGuiScreen(new GuiGuild());
		if (KeyBindings.toggleTerritoryRender.isPressed())
			territory.doRender = !territory.doRender;
	}
}
