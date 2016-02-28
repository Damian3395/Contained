package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.IconButton;

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
	}
	
	public List getButtonList(){
		this.buttonList.add(bone = new IconButton(-1, this.gui.width/2-15, 30, 20, 20, "perkIcons.png", 0, 48));
		
		this.buttonList.add(book = new IconButton(47, this.gui.width/2-75, 60, 20, 20, "perkIcons.png", 16, 48));
		this.buttonList.add(bottle = new IconButton(48, this.gui.width/2+45, 60, 20, 20, "perkIcons.png", 32, 48));
		
		this.buttonList.add(gun = new IconButton(49, this.gui.width/2-105, 90, 20, 20, "perkIcons.png", 48, 48));
		this.buttonList.add(cauldron = new IconButton(50, this.gui.width/2-45, 90, 20, 20, "perkIcons.png", 64, 48));
		
		this.buttonList.add(brick = new IconButton(51, this.gui.width/2+15, 90, 20, 20, "perkIcons.png", 80, 48));
		this.buttonList.add(stand = new IconButton(52, this.gui.width/2+75, 90, 20, 20, "perkIcons.png", 96, 48));

		this.buttonList.add(magma = new IconButton(53, this.gui.width/2-120, 120, 20, 20, "perkIcons.png", 112, 48));
		this.buttonList.add(crystal = new IconButton(54, this.gui.width/2-90, 120, 20, 20, "perkIcons.png", 128, 48));
		this.buttonList.add(ghast = new IconButton(55, this.gui.width/2-60, 120, 20, 20, "perkIcons.png", 144, 48));
		this.buttonList.add(spider = new IconButton(56, this.gui.width/2-30, 120, 20, 20, "perkIcons.png", 160, 48));
		
		this.buttonList.add(slime = new IconButton(57, this.gui.width/2, 120, 20, 20, "perkIcons.png", 176, 48));
		this.buttonList.add(fire = new IconButton(58, this.gui.width/2+30, 120, 20, 20, "perkIcons.png", 192, 48));
		this.buttonList.add(enderEye = new IconButton(59, this.gui.width/2+60, 120, 20, 20, "perkIcons.png", 208, 48));
		this.buttonList.add(enderPearl = new IconButton(60, this.gui.width/2+90, 120, 20, 20, "perkIcons.png", 224, 48));
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Wizard", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Wizard")/2),
				20, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				155, Color.WHITE.hashCode());
		drawXPBar(this.gui.width/2-55, 150, wizardXP, Color.BLUE);
	}
	
	private void drawXPBar(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + this.nextLevel, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + this.nextLevel) + 160, y+5, Color.WHITE.hashCode());
		this.gui.mc.currentScreen.drawRect(x, y, x + ((int)(100.0 * ((double)xp/(double)this.nextLevel))), y + 20, color.hashCode());
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
