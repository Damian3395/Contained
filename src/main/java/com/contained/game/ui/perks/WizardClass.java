package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.components.IconButton;
import com.contained.game.ui.components.ProgressBar;

import net.minecraft.client.gui.GuiButton;

public class WizardClass {
	private ClassPerks gui;
	private int wizardXP;
	private int level;
	private int nextLevel;
	private int levelOne = 2500;
	private int levelTwo = 5000;
	private int levelThree = 10000;
	protected List buttonList = new ArrayList();
	
	private IconButton bone, book, bottle, gun, cauldron, brick, stand;
	private IconButton magma, crystal, ghast, spider, slime, fire, enderEye, enderPearl;
	
	private ProgressBar wizard;

	public WizardClass(ClassPerks gui, int wizardXP, int level){
		this.gui = gui;
		this.wizardXP = wizardXP;
		this.level = level;
		
		if(wizardXP < levelOne){
			nextLevel = levelOne;
		}else if(wizardXP < levelTwo){
			nextLevel = levelTwo;
		}else if(wizardXP < levelThree){
			nextLevel = levelThree;
		}
		
		wizard = new ProgressBar(this.gui.width/2-50, this.gui.height/2+30, ProgressBar.PINK, wizardXP, nextLevel, this.gui.mc);
	}
	
	public List getButtonList(){
		int x = this.gui.width/2;
		int y = this.gui.height/2;
		this.buttonList.add(bone = new IconButton(-1, x-15, y-90, 20, 20, "perkIcons.png", 0, 48, "Test"));
		
		this.buttonList.add(book = new IconButton(47, x-75, y-60, 20, 20, "perkIcons.png", 16, 48, "Test"));
		this.buttonList.add(bottle = new IconButton(48, x+45, y-60, 20, 20, "perkIcons.png", 32, 48, "Test"));
		
		this.buttonList.add(gun = new IconButton(49, x-105, y-30, 20, 20, "perkIcons.png", 48, 48, "Test"));
		this.buttonList.add(cauldron = new IconButton(50, x-45, y-30, 20, 20, "perkIcons.png", 64, 48, "Test"));
		
		this.buttonList.add(brick = new IconButton(51, x+15, y-30, 20, 20, "perkIcons.png", 80, 48, "Test"));
		this.buttonList.add(stand = new IconButton(52, x+75, y-30, 20, 20, "perkIcons.png", 96, 48, "Test"));

		this.buttonList.add(enderPearl = new IconButton(60, x+90, y, 20, 20, "perkIcons.png", 224, 48, "Test"));
		this.buttonList.add(enderEye = new IconButton(59, x+60, y, 20, 20, "perkIcons.png", 208, 48, "Test"));
		this.buttonList.add(fire = new IconButton(58, x+30, y, 20, 20, "perkIcons.png", 192, 48, "Test"));
		this.buttonList.add(slime = new IconButton(57, x, y, 20, 20, "perkIcons.png", 176, 48, "Test"));
		
		this.buttonList.add(spider = new IconButton(56, x-30, y, 20, 20, "perkIcons.png", 160, 48, "Test"));
		this.buttonList.add(ghast = new IconButton(55, x-60, y, 20, 20, "perkIcons.png", 144, 48, "Test"));
		this.buttonList.add(crystal = new IconButton(54, x-90, y, 20, 20, "perkIcons.png", 128, 48, "Test"));
		this.buttonList.add(magma = new IconButton(53, x-120, y, 20, 20, "perkIcons.png", 112, 48, "Test"));
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Wizard", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Wizard")/2),
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				gui.height/2+35, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow(wizardXP + "/" + this.nextLevel, 
				((this.gui.width)/2) - this.gui.mc.fontRenderer.getStringWidth(wizardXP + "/" + this.nextLevel)/2 + 80,
				gui.height/2+35, Color.WHITE.hashCode());
		wizard.render();
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
