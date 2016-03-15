package com.contained.game.ui.components;

import java.awt.Point;

import com.contained.game.util.Resources;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

// TODO: Add Highlighted Description To Tab
public class GuiTab extends Gui{
	public static final int STAR = 0;
	public static final int SHIELD = 1;
	public static final int SETTINGS = 2;
	
	private Point[] icons = {
			new Point(64, 176), //STAR
			new Point(48, 176), //SHIELD
			new Point(32, 176) //Settings
	};
	
	private GuiScreen gui;
	
	public int selectedTab = 0;
	private int x, y;
	private String tabOne = "";
	private String tabTwo = "";
	private String tabThree = "";
	private int iconOne = -1;
	private int iconTwo = -2;
	private int iconThree = -3;
	
	private boolean highLightTabOne = false;
	private boolean highLightTabTwo = false;
	private boolean highLightTabThree = false;
	
	private ResourceLocation img;
	
	public GuiTab(GuiScreen gui){
		this.gui = gui;
		x = (gui.width/2);
		y = (gui.height/2);	
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/GuiTabbedPane.png");
	}
	
	public GuiTab(GuiScreen gui, String tabOne, String tabTwo, String tabThree){
		this.gui = gui;
		this.tabOne = tabOne;
		this.tabTwo = tabTwo;
		this.tabThree = tabThree;
		x = (gui.width/2);
		y = (gui.height/2);
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/GuiTabbedPane.png");
	}
	
	public GuiTab(GuiScreen gui, int iconOne, int iconTwo, int iconThree){
		this.gui = gui;
		this.iconOne = iconOne;
		this.iconTwo = iconTwo;
		this.iconThree = iconThree;
		x = (gui.width/2);
		y = (gui.height/2);
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/GuiTabbedPane.png");
	}
	
	public void setTabIcon(int tab, int icon){
		switch(tab){
		case 0:
			iconOne = icon;
			break;
		case 1:
			iconTwo = icon;
			break;
		case 2:
			iconThree = icon;
			break;
		}
	}
	
	public void mouseMovedOrUp(int x, int y, int button){
		if((x > this.x-128 && x < this.x-100) && (y > this.y-116 && y < this.y-84)){ //Check Tab One
			selectedTab = 0;
		}else if((x > (this.x-100) && x < this.x-72) && (y > this.y-116 && y < this.y-84)){ //Check Tab Two
			selectedTab = 1;
		}else if((x > (this.x-72) && x < this.x-44) && (y > this.y-116 && y < this.y-84)){ //Check Tab Three
			selectedTab = 2;
		}
		
		highLightTabOne = false;
		highLightTabTwo = false;
		highLightTabThree = false;
	}
	
	public void render(){
		gui.mc.getTextureManager().bindTexture(this.img);
        this.drawTexturedModalRect(this.x-128, this.y-88, 0, 0, 256, 176);
        this.drawTexturedModalRect(this.x-128, this.y-116, 0, 224, 84, 32);
        this.drawTexturedModalRect(this.x-128 + (selectedTab * 28), this.y-116, 0, 190, 32, 32);
        renderIcons();
        renderDescription();
	}
	
	private void renderIcons(){
		if(iconOne != -1){
			Point icon = icons[iconOne];
			this.drawTexturedModalRect(this.x-123, this.y-106, icon.x, icon.y, 16, 16);
		}
		
		if(iconTwo != -1){
			Point icon = icons[iconTwo];
			this.drawTexturedModalRect(this.x-95, this.y-106, icon.x, icon.y, 16, 16);
		}
		
		if(iconThree != -1){
			Point icon = icons[iconThree];
			this.drawTexturedModalRect(this.x-67, this.y-106, icon.x, icon.y, 16, 16);
		}
	}
	
	private void renderDescription(){
		if(highLightTabOne && !tabOne.isEmpty()){
			
		}
		
		if(highLightTabTwo && !tabTwo.isEmpty()){
			
		}
		
		if(highLightTabThree && !tabThree.isEmpty()){
			
		}
	}
}
