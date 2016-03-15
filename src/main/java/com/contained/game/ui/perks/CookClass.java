package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.components.IconButton;
import com.contained.game.ui.components.ProgressBar;

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
	
	private ProgressBar cook;

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
		
		cook = new ProgressBar(this.gui.width/2-50, this.gui.height/2+30, ProgressBar.GREEN, cookXP, nextLevel, this.gui.mc);
	}
	
	public List getButtonList(){
		int x = this.gui.width/2;
		int y = this.gui.height/2;
		this.buttonList.add(woodHoe = new IconButton(-1, x-15, y-90, 20, 20, "perkIcons.png", 0, 32, "Test"));
		
		this.buttonList.add(stoneHoe = new IconButton(33, x-75, y-60, 20, 20, "perkIcons.png", 16, 32, "Test"));
		this.buttonList.add(seeds = new IconButton(34, x+45,y-60, 20, 20, "perkIcons.png", 32, 32, "Test"));
		
		this.buttonList.add(ironHoe = new IconButton(35, x-105, y-30, 20, 20, "perkIcons.png", 48, 32, "Test"));
		this.buttonList.add(fishingRod = new IconButton(36, x-45, y-30, 20, 20, "perkIcons.png", 64, 32, "Test"));
		
		this.buttonList.add(apple = new IconButton(37, x+15, y-30, 20, 20, "perkIcons.png", 80, 32, "Test"));
		this.buttonList.add(carrot = new IconButton(38, x+75, y-30, 20, 20, "perkIcons.png", 96, 32, "Test"));

		
		this.buttonList.add(goldenCarrot = new IconButton(46, x+90, y, 20, 20, "perkIcons.png", 224, 32, "Test"));
		this.buttonList.add(cookie = new IconButton(45, x+60, y, 20, 20, "perkIcons.png", 208, 32, "Test"));
		this.buttonList.add(goldenApple = new IconButton(44, x+30, y, 20, 20, "perkIcons.png", 192, 32, "Test"));
		this.buttonList.add(cake = new IconButton(43, x, y, 20, 20, "perkIcons.png", 176, 32, "Test"));
		
		this.buttonList.add(salmon = new IconButton(42, x-30, y, 20, 20, "perkIcons.png", 160, 32, "Test"));
		this.buttonList.add(fish = new IconButton(41, x-60, y, 20, 20, "perkIcons.png", 144, 32, "Test"));
		this.buttonList.add(diamondHoe = new IconButton(40, x-90, y, 20, 20, "perkIcons.png", 128, 32, "Test"));
		this.buttonList.add(goldHoe = new IconButton(39, x-120, y, 20, 20, "perkIcons.png", 112, 32, "Test"));
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Cook", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Cook")/2),
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				gui.height/2+35, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow(cookXP + "/" + this.nextLevel,
				((this.gui.width)/2) - this.gui.mc.fontRenderer.getStringWidth(cookXP + "/" + this.nextLevel)/2 + 80,
				gui.height/2+35, Color.WHITE.hashCode());
		cook.render();
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
