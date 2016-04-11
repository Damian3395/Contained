package com.contained.game.ui.games;

import java.awt.Color;

import com.contained.game.ui.components.Container;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMiniGames extends GuiScreen {
	private final int JOIN_GAME = 0;
	private int x,y;
	
	private GuiButton joinGame;
	private Container background;
	
	@Override
	public void initGui(){
		x = this.width/2;
		y = this.height/2;
		this.buttonList.add(this.joinGame = new GuiButton(JOIN_GAME, x+30, y+30, 80, 20, "Join Game"));
	
		background = new Container((this.width-256)/2, ((this.height-256)/2) + 20, 256, 176, "ui.png", this);
	}
	
	@Override
	public void updateScreen(){
		
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){
		background.render();
		
		renderFont(x, y-100, "Mini Games", Color.BLACK);
		
		super.drawScreen(w, h, ticks);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case JOIN_GAME:
			
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
	
	private void renderFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, 
				(x - this.mc.fontRenderer.getStringWidth(text)/2),
				y, color.hashCode());
	}
}
