package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.IconButton;

import net.minecraft.client.gui.GuiButton;

public class WarriorClass {
	private ClassPerks gui;
	private int warriorXP;
	private int level;
	private int nextLevel;
	private int levelOne = 2500;
	private int levelTwo = 5000;
	private int levelThree = 10000;
	protected List buttonList = new ArrayList();
	
	private IconButton woodSword, chestPlate, stoneSword, arrow, ironHorse, goldHorse, diamondHorse;
	private IconButton saddle, bow, ironPlate, ironSword, goldPlate, goldSword, diamondPlate, diamondSword;

	public WarriorClass(ClassPerks gui, int warriorXP, int level){
		this.gui = gui;
		this.warriorXP = warriorXP;
		this.level = level;
		
		if(warriorXP < levelOne){
			nextLevel = levelOne;
		}else if(warriorXP < levelTwo){
			nextLevel = levelTwo;
		}else if(warriorXP < levelThree){
			nextLevel = levelThree;
		}
	}
	
	public List getButtonlist(){
		this.buttonList.add(woodSword = new IconButton(-1, this.gui.width/2-15, 30, 20, 20, "perkIcons.png", 0, 64));
		
		this.buttonList.add(chestPlate = new IconButton(61, this.gui.width/2-75, 60, 20, 20, "perkIcons.png", 16, 64));
		this.buttonList.add(stoneSword = new IconButton(62, this.gui.width/2+45, 60, 20, 20, "perkIcons.png", 32, 64));
		
		this.buttonList.add(arrow = new IconButton(63, this.gui.width/2-105, 90, 20, 20, "perkIcons.png", 48, 64));
		this.buttonList.add(ironHorse = new IconButton(64, this.gui.width/2-45, 90, 20, 20, "perkIcons.png", 64, 64));
		
		this.buttonList.add(goldHorse = new IconButton(65, this.gui.width/2+15, 90, 20, 20, "perkIcons.png", 80, 64));
		this.buttonList.add(diamondHorse = new IconButton(66, this.gui.width/2+75, 90, 20, 20, "perkIcons.png", 96, 64));

		this.buttonList.add(saddle = new IconButton(67, this.gui.width/2-120, 120, 20, 20, "perkIcons.png", 112, 64));
		this.buttonList.add(bow = new IconButton(68, this.gui.width/2-90, 120, 20, 20, "perkIcons.png", 128, 64));
		this.buttonList.add(ironPlate = new IconButton(69, this.gui.width/2-60, 120, 20, 20, "perkIcons.png", 144, 64));
		this.buttonList.add(ironSword = new IconButton(70, this.gui.width/2-30, 120, 20, 20, "perkIcons.png", 160, 64));
		
		this.buttonList.add(goldPlate = new IconButton(71, this.gui.width/2, 120, 20, 20, "perkIcons.png", 176, 64));
		this.buttonList.add(goldSword = new IconButton(72, this.gui.width/2+30, 120, 20, 20, "perkIcons.png", 192, 64));
		this.buttonList.add(diamondPlate = new IconButton(73, this.gui.width/2+60, 120, 20, 20, "perkIcons.png", 208, 64));
		this.buttonList.add(diamondSword = new IconButton(74, this.gui.width/2+90, 120, 20, 20, "perkIcons.png", 224, 64));
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Warrior", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Warrior")/2),
				20, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				155, Color.WHITE.hashCode());
		drawXPBar(this.gui.width/2-55, 150, warriorXP, Color.BLUE);
	}
	
	private void drawXPBar(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + this.nextLevel, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + this.nextLevel) + 160, y+5, Color.WHITE.hashCode());
		this.gui.mc.currentScreen.drawRect(x, y, x + ((int)(100.0 * ((double)xp/(double)this.nextLevel))), y + 20, color.hashCode());
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
