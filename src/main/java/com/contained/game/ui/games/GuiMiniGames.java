package com.contained.game.ui.games;

import java.awt.Color;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ServerPacketHandlerUtil;
import com.contained.game.ui.components.Container;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMiniGames extends GuiScreen {
	private final int JOIN_GAME = 0;
	private final int CANCEL_GAME = 1;
	private int x,y;
	private int pvpWon, pvpLost, treasureWon, treasureLost, kills, deaths, treasuresOpened;
	
	boolean inQueue = false;
	
	private GuiButton joinGame, cancelGame;
	private Container background;
	
	@Override
	public void initGui(){
		ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
		if(properties.inGame() || properties.gameMode != Resources.OVERWORLD ||
				MiniGameUtil.isPvP(mc.thePlayer.dimension) || MiniGameUtil.isTreasure(mc.thePlayer.dimension)){
			mc.displayGuiScreen(null);
			return;
		}
		
		pvpWon = properties.pvpWon;
		pvpLost = properties.pvpLost;
		treasureWon = properties.treasureWon;
		treasureLost = properties.treasureLost;
		kills = properties.kills;
		deaths = properties.deaths;
		treasuresOpened = properties.treasuresOpened;
		inQueue = properties.isWaitingForMiniGame();
		
		x = this.width/2;
		y = this.height/2;
		this.buttonList.add(this.joinGame = new GuiButton(JOIN_GAME, x+30, y+30, 80, 20, "Join Game"));
		this.buttonList.add(this.cancelGame = new GuiButton(CANCEL_GAME, x-100, y+30, 80, 20, "Cancel Game"));
	
		if(inQueue)
			this.joinGame.enabled = false;
		else
			this.cancelGame.enabled = false;
		
		background = new Container((this.width-256)/2, ((this.height-256)/2) + 20, 256, 176, "ui.png", this);
	}
	
	@Override
	public void updateScreen(){
		
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){
		background.render();
		
		renderCenterFont(x, y-100, "Mini Games", Color.BLACK);
		
		if(inQueue){
			renderCenterFont(x-5,y-85, "You Are Currently Waiting For A Game To Start!", Color.BLUE);
		}else
			renderCenterFont(x, y-85, "Please Click Join Game To Find A Mini Game!", Color.BLUE);
		
		renderFont(x-120, y-65, "Player Stats:", Color.BLACK);
		renderFont(x-120, y-55, "Games Won/Games Lost: " , Color.BLACK);
		renderFont(x-120+mc.fontRenderer.getStringWidth("Games Won/Games Lost: ")
				, y-55, "("+(this.pvpWon+this.treasureWon)+"/"+(this.pvpLost+this.treasureLost)+")"
				, Color.RED);
		renderFont(x-120, y-45, "Pvp Won/Pvp Lost: " , Color.BLACK);
		renderFont(x-120+mc.fontRenderer.getStringWidth("Pvp Won/Pvp Lost: ")
				, y-45, "("+this.pvpWon+"/"+this.pvpLost+")"
				, Color.RED);
		renderFont(x-120, y-35, "TreasureHunt Won/TreasureHunt Lost: ", Color.BLACK);
		renderFont(x-120+mc.fontRenderer.getStringWidth("TreasureHunt Won/TreasureHunt Lost: ")
				, y-35, "("+this.treasureWon+"/"+this.treasureLost+")"
				, Color.RED);
		renderFont(x-120, y-25, "Kills/Deaths: ", Color.BLACK);
		renderFont(x-120+mc.fontRenderer.getStringWidth("Kills/Deaths: ")
				, y-25, "("+this.kills+"/"+this.deaths+")"
				, Color.RED);
		renderFont(x-120, y-15, "Treasures Opened: ", Color.BLACK);
		renderFont(x-120+mc.fontRenderer.getStringWidth("Treasures Opened: ")
				, y-15, "("+this.treasuresOpened+")"
				, Color.RED);
		super.drawScreen(w, h, ticks);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case JOIN_GAME:
			PacketCustom joinGamePacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.JOIN_MINI_GAME);
			ServerPacketHandlerUtil.sendToServer(joinGamePacket.toPacket());
		break;
		case CANCEL_GAME:
			PacketCustom cancelGamePacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.CANCEL_JOIN_MINI_GAME);
			ServerPacketHandlerUtil.sendToServer(cancelGamePacket.toPacket());
		break;
		}
	}
	
	@Override
	public void onGuiClosed(){
		
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	private void renderCenterFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, 
				(x - this.mc.fontRenderer.getStringWidth(text)/2),
				y, color.hashCode());
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, x, y, color.hashCode());
	}
}
