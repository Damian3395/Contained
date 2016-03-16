package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.IconButton;

import net.minecraft.client.gui.GuiButton;

public class CollectorClass {
	private ClassPerks gui;
	private int collectorXP;
	private int level;
	private int nextLevel;
	private int levelOne = 2500;
	private int levelTwo = 5000;
	private int levelThree = 10000;
	protected List buttonList = new ArrayList();
	
	private IconButton woodenShovel, stonePickAxe, stoneAxe, ironPickAxe, ironAxe, goldPickAxe, goldAxe;
	private IconButton diamondPickAxe, diamondAxe, diamondShovel, goldShovel;
	private IconButton bucket, ladder, lamp, tree;
	
	public CollectorClass(ClassPerks gui, int collectorXP, int level) {
		this.gui = gui;
		this.collectorXP = collectorXP;
		this.level = level;
		
		if(collectorXP < levelOne){
			nextLevel = levelOne;
		}else if(collectorXP < levelTwo){
			nextLevel = levelTwo;
		}else if(collectorXP < levelThree){
			nextLevel = levelThree;
		}
	}

	public List getButtonList(){
		this.buttonList.add(woodenShovel = new IconButton(-1, this.gui.width/2-15, 30, 20, 20, "perkIcons.png", 0, 0));
		
		this.buttonList.add(stonePickAxe = new IconButton(5, this.gui.width/2-75, 60, 20, 20, "perkIcons.png", 16, 0));
		this.buttonList.add(stoneAxe = new IconButton(6, this.gui.width/2+45, 60, 20, 20, "perkIcons.png", 32, 0));
		
		this.buttonList.add(ironPickAxe = new IconButton(7, this.gui.width/2-105, 90, 20, 20, "perkIcons.png", 48, 0));
		this.buttonList.add(ironAxe = new IconButton(8, this.gui.width/2-45, 90, 20, 20, "perkIcons.png", 64, 0));
		
		this.buttonList.add(goldPickAxe = new IconButton(9, this.gui.width/2+15, 90, 20, 20, "perkIcons.png", 80, 0));
		this.buttonList.add(goldAxe = new IconButton(10, this.gui.width/2+75, 90, 20, 20, "perkIcons.png", 96, 0));
		
		this.buttonList.add(diamondPickAxe = new IconButton(11, this.gui.width/2-120, 120, 20, 20, "perkIcons.png", 112, 0));
		this.buttonList.add(diamondShovel = new IconButton(12, this.gui.width/2-90, 120, 20, 20, "perkIcons.png", 144, 0));
		this.buttonList.add(bucket = new IconButton(13, this.gui.width/2-60, 120, 20, 20, "perkIcons.png", 176, 0));
		this.buttonList.add(ladder = new IconButton(14, this.gui.width/2-30, 120, 20, 20, "perkIcons.png", 192, 0));
		
		this.buttonList.add(diamondAxe = new IconButton(15, this.gui.width/2+90, 120, 20, 20, "perkIcons.png", 128, 0));
		this.buttonList.add(goldShovel = new IconButton(16, this.gui.width/2+60, 120, 20, 20, "perkIcons.png", 160, 0));
		this.buttonList.add(lamp = new IconButton(17, this.gui.width/2+30, 120, 20, 20, "perkIcons.png", 208, 0));
		this.buttonList.add(tree = new IconButton(18, this.gui.width/2, 120, 20, 20, "perkIcons.png", 224, 0));
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Collector", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Collector")/2),
				20, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				155, Color.WHITE.hashCode());
		drawXPBar(this.gui.width/2-55, 150, collectorXP, Color.BLUE);
	}
	
	private void drawXPBar(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + this.nextLevel, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + this.nextLevel) + 160, y+5, Color.WHITE.hashCode());
		this.gui.mc.currentScreen.drawRect(x, y, x + ((int)(100.0 * ((double)xp/(double)this.nextLevel))), y + 20, color.hashCode());
	}
	
	public void actionPerformed(GuiButton buton){
		
	}
}
