package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.IconButton;

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
	}
	
	public List getButtonList(){
		this.buttonList.add(grass = new IconButton(-1, this.gui.width/2-15, 30, 20, 20, "perkIcons.png", 0, 16));
		
		this.buttonList.add(sand = new IconButton(19, this.gui.width/2-75, 60, 20, 20, "perkIcons.png", 16, 16));
		this.buttonList.add(dirt = new IconButton(20, this.gui.width/2+45, 60, 20, 20, "perkIcons.png", 32, 16));
		
		this.buttonList.add(sandstone = new IconButton(21, this.gui.width/2-105, 90, 20, 20, "perkIcons.png", 48, 16));
		this.buttonList.add(standstoneCurved = new IconButton(22, this.gui.width/2-45, 90, 20, 20, "perkIcons.png", 64, 16));
		
		this.buttonList.add(cobble = new IconButton(23, this.gui.width/2+15, 90, 20, 20, "perkIcons.png", 80, 16));
		this.buttonList.add(stone = new IconButton(24, this.gui.width/2+75, 90, 20, 20, "perkIcons.png", 96, 16));

		this.buttonList.add(iron = new IconButton(25, this.gui.width/2-120, 120, 20, 20, "perkIcons.png", 112, 16));
		this.buttonList.add(brick = new IconButton(26, this.gui.width/2-90, 120, 20, 20, "perkIcons.png", 128, 16));
		this.buttonList.add(stoneBrick = new IconButton(27, this.gui.width/2-60, 120, 20, 20, "perkIcons.png", 144, 16));
		this.buttonList.add(obsidian = new IconButton(28, this.gui.width/2-30, 120, 20, 20, "perkIcons.png", 160, 16));
		
		this.buttonList.add(furnace = new IconButton(29, this.gui.width/2+90, 120, 20, 20, "perkIcons.png", 176, 16));
		this.buttonList.add(gold = new IconButton(30, this.gui.width/2+60, 120, 20, 20, "perkIcons.png", 192, 16));
		this.buttonList.add(glass = new IconButton(31, this.gui.width/2+30, 120, 20, 20, "perkIcons.png", 208, 16));
		this.buttonList.add(diamond = new IconButton(32, this.gui.width/2, 120, 20, 20, "perkIcons.png", 224, 16));
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Builder", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Builder")/2),
				20, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				155, Color.WHITE.hashCode());
		drawXPBar(this.gui.width/2-55, 150, builderXP, Color.BLUE);
	}
	
	private void drawXPBar(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + this.nextLevel, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + this.nextLevel) + 160, y+5, Color.WHITE.hashCode());
		this.gui.mc.currentScreen.drawRect(x, y, x + ((int)(100.0 * ((double)xp/(double)this.nextLevel))), y + 20, color.hashCode());
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
