package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class BaseClass {
	private ClassPerks gui;
	private int level = 1000;
	private GuiButton collector;
	private GuiButton cook;
	private GuiButton builder;
	private GuiButton wizard;
	private GuiButton warrior;
	private int collectorXP, cookXP, builderXP, wizardXP, warriorXP;
	protected List buttonList = new ArrayList();
	
	public BaseClass(ClassPerks gui, int collectorXP, int cookXP, int builderXP, int wizardXP, int warriorXP){
		this.gui = gui;
		this.collectorXP = collectorXP;
		this.builderXP = builderXP;
		this.cookXP = cookXP;
		this.wizardXP = wizardXP;
		this.warriorXP = wizardXP;
	}
	
	public List getButtonList(){
		int x = (this.gui.width/2)+50;
		this.buttonList.add(this.collector = new GuiButton(ClassPerks.COLLECTOR, x, 30, 60, 20, "Collector"));
		this.buttonList.add(this.builder = new GuiButton(ClassPerks.BUILDER, x, 60, 60, 20, "Builder"));
		this.buttonList.add(this.cook = new GuiButton(ClassPerks.COOK, x, 90, 60, 20, "Cook"));
		this.buttonList.add(this.wizard = new GuiButton(ClassPerks.WIZARD, x,120, 60, 20, "Wizard"));
		this.buttonList.add(this.warrior = new GuiButton(ClassPerks.WARRIOR, x, 150, 60, 20, "Warrior"));
		
		this.collector.enabled = (collectorXP >= level) ? true : false;
		this.builder.enabled = (builderXP >= level) ? true : false;
		this.cook.enabled = (cookXP >= level) ? true : false;
		this.wizard.enabled = (wizardXP >= level) ? true : false;
		this.warrior.enabled = (warriorXP >= level) ? true : false;
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("Class Perks", 
				(this.gui.width - this.gui.mc.fontRenderer.getStringWidth("Class Perks"))/2,
				20, Color.WHITE.hashCode());
		int x = 150;
		drawXPBar(x, 30, collectorXP, Color.RED);
		drawXPBar(x, 60, builderXP, Color.BLUE);
		drawXPBar(x, 90, cookXP, Color.GREEN);
		drawXPBar(x, 120, wizardXP, Color.YELLOW);
		drawXPBar(x, 150, warriorXP, Color.CYAN);
	}
	
	private void drawXPBar(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + level, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + level) - 10, y+5, Color.WHITE.hashCode());
		this.gui.mc.currentScreen.drawRect(x, y, x + ((int)(100.0 * ((double)xp/(double)level))), y + 20, color.hashCode());
	}
	
	public void actionPerformed(GuiButton button){
		
	}
}
