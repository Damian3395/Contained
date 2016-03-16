package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.IconButton;

import net.minecraft.client.gui.GuiButton;

public class CookClass {
	private ClassPerks gui;
	private int cookXP;
	private int level;
	private int nextLevel;
	private int levelOne = 2500;
	private int levelTwo = 5000;
	private int levelThree = 10000;
	protected List buttonList = new ArrayList();
	
	private IconButton woodHoe, stoneHoe, seeds, ironHoe, fishingRod, apple, carrot;
	private IconButton goldHoe, diamondHoe, fish, salmon, cake, goldenApple, cookie, goldenCarrot;

	public CookClass(ClassPerks gui, int cookXP, int level){
		this.gui = gui;
		this.cookXP = cookXP;
		this.level = level;
		
		if(cookXP < levelOne){
			nextLevel = levelOne;
		}else if(cookXP < levelTwo){
			nextLevel = levelTwo;
		}else if(cookXP < levelThree){
			nextLevel = levelThree;
		}
	}
	
	public List getButtonList(){
		this.buttonList.add(woodHoe = new IconButton(-1, this.gui.width/2-15, 30, 20, 20, "perkIcons.png", 0, 32));
		
		this.buttonList.add(stoneHoe = new IconButton(33, this.gui.width/2-75, 60, 20, 20, "perkIcons.png", 16, 32));
		this.buttonList.add(seeds = new IconButton(34, this.gui.width/2+45, 60, 20, 20, "perkIcons.png", 32, 32));
		
		this.buttonList.add(ironHoe = new IconButton(35, this.gui.width/2-105, 90, 20, 20, "perkIcons.png", 48, 32));
		this.buttonList.add(fishingRod = new IconButton(36, this.gui.width/2-45, 90, 20, 20, "perkIcons.png", 64, 32));
		
		this.buttonList.add(apple = new IconButton(37, this.gui.width/2+15, 90, 20, 20, "perkIcons.png", 80, 32));
		this.buttonList.add(carrot = new IconButton(38, this.gui.width/2+75, 90, 20, 20, "perkIcons.png", 96, 32));

		this.buttonList.add(goldHoe = new IconButton(39, this.gui.width/2-120, 120, 20, 20, "perkIcons.png", 112, 32));
		this.buttonList.add(diamondHoe = new IconButton(40, this.gui.width/2-90, 120, 20, 20, "perkIcons.png", 128, 32));
		this.buttonList.add(fish = new IconButton(41, this.gui.width/2-60, 120, 20, 20, "perkIcons.png", 144, 32));
		this.buttonList.add(salmon = new IconButton(42, this.gui.width/2-30, 120, 20, 20, "perkIcons.png", 160, 32));
		
		this.buttonList.add(cake = new IconButton(43, this.gui.width/2, 120, 20, 20, "perkIcons.png", 176, 32));
		this.buttonList.add(goldenApple = new IconButton(44, this.gui.width/2+30, 120, 20, 20, "perkIcons.png", 192, 32));
		this.buttonList.add(cookie = new IconButton(45, this.gui.width/2+60, 120, 20, 20, "perkIcons.png", 208, 32));
		this.buttonList.add(goldenCarrot = new IconButton(46, this.gui.width/2+90, 120, 20, 20, "perkIcons.png", 224, 32));
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Cook", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Cook")/2),
				20, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				155, Color.WHITE.hashCode());
		drawXPBar(this.gui.width/2-55, 150, cookXP, Color.BLUE);
	}
	
	private void drawXPBar(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + this.nextLevel, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + this.nextLevel) + 160, y+5, Color.WHITE.hashCode());
		this.gui.mc.currentScreen.drawRect(x, y, x + ((int)(100.0 * ((double)xp/(double)this.nextLevel))), y + 20, color.hashCode());
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
