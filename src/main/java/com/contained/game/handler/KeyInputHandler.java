package com.contained.game.handler;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.GuiGuild;
import com.contained.game.ui.TerritoryRender;
import com.contained.game.ui.games.GuiMiniGames;

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
		if (KeyBindings.toggleClassPerks.isPressed()) {
			if (mc.currentScreen == null)
				mc.displayGuiScreen(new ClassPerks());
			else
				mc.thePlayer.closeScreen();
		}
		if(KeyBindings.toggleGuild.isPressed()) {
			if (mc.currentScreen == null)
				mc.displayGuiScreen(new GuiGuild());
			else
				mc.thePlayer.closeScreen();
		}
		
		if (KeyBindings.toggleMiniGames.isPressed())
			mc.displayGuiScreen(new GuiMiniGames());
		
		if (KeyBindings.toggleTerritoryRender.isPressed())
			territory.doRender = !territory.doRender;
	}
}
