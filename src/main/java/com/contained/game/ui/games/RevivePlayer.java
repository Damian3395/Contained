package com.contained.game.ui.games;

import com.contained.game.ui.components.Container;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class RevivePlayer extends GuiScreen {
	private final int REVIVE = 0;
	private final int CANCEL = 1;
	
	private int x,y, selected;
	private Container background;
	private GuiButton revive;
	private GuiButton cancel;
	
	public RevivePlayer(){}
	
	@Override
	public void initGui(){
		x = this.width/2;
		y = this.height/2;
		selected = -1;
		
		background = new Container(x-128, y-88, 256, 176, "ui.png", this);
		
		this.buttonList.add(revive = new GuiButton(REVIVE, x+64, y+128, 60, 20, "Revive Player"));
		this.buttonList.add(cancel = new GuiButton(CANCEL, x+100, y+128, 60, 20, "Cancel"));
		
		revive.enabled = false;
	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){	
		background.render();
		super.drawScreen(w, h, ticks);
	}
	

	@Override
	protected void keyTyped(char c, int i){
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case REVIVE:
			if(selected != -1){
				
			}
		break;
		case CANCEL:
			this.mc.displayGuiScreen(null);
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
}
