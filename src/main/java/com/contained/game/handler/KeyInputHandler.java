package com.contained.game.handler;

import com.contained.game.Contained;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.GuiAdmin;
import com.contained.game.ui.games.GuiMiniGames;
import com.contained.game.ui.games.GuiScoreboard;
import com.contained.game.ui.guild.GuiGuild;
import com.contained.game.ui.perks.ClassPerks;
import com.contained.game.ui.territory.TerritoryRender;
import com.contained.game.util.MiniGameUtil;

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
			if(!MiniGameUtil.isPvP(mc.thePlayer.dimension) 
					&& !MiniGameUtil.isTreasure(mc.thePlayer.dimension))
				mc.displayGuiScreen(new GuiMiniGames());
			else
				mc.displayGuiScreen(new GuiScoreboard());
		
		if (KeyBindings.toggleTerritoryRender.isPressed())
			territory.doRender = !territory.doRender;
		
		if(KeyBindings.toggleAdmin.isPressed())
			if(mc.currentScreen == null){
				mc.displayGuiScreen(new GuiAdmin());	
			}else{
				mc.thePlayer.closeScreen();
			}
		
		if (KeyBindings.toggleGUIStyle.isPressed()) {
			if (Contained.guiStyle == 1)
				Contained.guiStyle = 0;
			else
				Contained.guiStyle = 1;
		}
			
	}
}
