package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.components.IconButton;
import com.contained.game.ui.components.ProgressBar;

import net.minecraft.client.gui.GuiButton;

public class BuilderClass {
	private ClassPerks gui;
	private int builderXP;
	private int level;
	private int nextLevel;
	private int levelOne = 2500;
	private int levelTwo = 5000;
	private int levelThree = 10000;
	protected List buttonList = new ArrayList();
	
	private IconButton grass, sand, dirt, sandstone, standstoneCurved, cobble, stone;
	private IconButton iron, brick, stoneBrick, obsidian, furnace, gold, glass, diamond;
	
	private ProgressBar builder;
	
	public BuilderClass(ClassPerks gui, int builderXP, int level){
		this.gui = gui;
		this.builderXP = builderXP;
		this.level = level;
		
		if(builderXP < levelOne){
			nextLevel = levelOne;
		}else if(builderXP < levelTwo){
			nextLevel = levelTwo;
		}else if(builderXP < levelThree){
			nextLevel = levelThree;
		}
		
		builder = new ProgressBar(this.gui.width/2-50, this.gui.height/2+30, ProgressBar.BLUE, builderXP, nextLevel, this.gui.mc);
	}
	
	public List getButtonList(){
		int x = this.gui.width/2;
		int y = this.gui.height/2;
		this.buttonList.add(grass = new IconButton(-1, x-15, y-90, 20, 20, "perkIcons.png", 0, 16, "Test"));
		
		this.buttonList.add(sand = new IconButton(19, x-75, y-60, 20, 20, "perkIcons.png", 16, 16, "Test"));
		this.buttonList.add(dirt = new IconButton(20, x+45, y-60, 20, 20, "perkIcons.png", 32, 16, "Test"));
		
		this.buttonList.add(sandstone = new IconButton(21, x-105, y-30, 20, 20, "perkIcons.png", 48, 16, "Test"));
		this.buttonList.add(standstoneCurved = new IconButton(22, x-45, y-30, 20, 20, "perkIcons.png", 64, 16, "Test"));
		
		this.buttonList.add(cobble = new IconButton(23, x+15, y-30, 20, 20, "perkIcons.png", 80, 16, "Test"));
		this.buttonList.add(stone = new IconButton(24, x+75, y-30, 20, 20, "perkIcons.png", 96, 16, "Test"));
		
		this.buttonList.add(furnace = new IconButton(29, x+90, y, 20, 20, "perkIcons.png", 176, 16, "Test"));
		this.buttonList.add(gold = new IconButton(30, x+60, y, 20, 20, "perkIcons.png", 192, 16, "Test"));
		this.buttonList.add(glass = new IconButton(31, x+30, y, 20, 20, "perkIcons.png", 208, 16, "Test"));
		this.buttonList.add(diamond = new IconButton(32, x, y, 20, 20, "perkIcons.png", 224, 16, "Test"));
		
		this.buttonList.add(obsidian = new IconButton(28, x-30, y, 20, 20, "perkIcons.png", 160, 16, "Test"));
		this.buttonList.add(stoneBrick = new IconButton(27, x-60, y, 20, 20, "perkIcons.png", 144, 16, "Test"));
		this.buttonList.add(brick = new IconButton(26, x-90, y, 20, 20, "perkIcons.png", 128, 16, "Test"));
		this.buttonList.add(iron = new IconButton(25, x-120, y, 20, 20, "perkIcons.png", 112, 16, "Test"));
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Builder", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Builder")/2),
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				gui.height/2+35, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow(builderXP + "/" + this.nextLevel, 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth(builderXP + "/" + this.nextLevel)/2) + 80,
				gui.height/2+35, Color.WHITE.hashCode());
		builder.render();
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
