package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.components.IconButton;
import com.contained.game.ui.components.ProgressBar;

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
	
	private ProgressBar collector;
	
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
		
		collector = new ProgressBar(this.gui.width/2-50, this.gui.height/2+30, ProgressBar.RED, collectorXP, nextLevel, this.gui.mc);
	}

	public List getButtonList(){
		int x = this.gui.width/2;
		int y = this.gui.height/2;
		this.buttonList.add(woodenShovel = new IconButton(-1, x-15, y-90, 20, 20, "perkIcons.png", 0, 0, "Test"));
		
		this.buttonList.add(stonePickAxe = new IconButton(5, x-75, y-60, 20, 20, "perkIcons.png", 16, 0, "Test"));
		this.buttonList.add(stoneAxe = new IconButton(6, x+45, y-60, 20, 20, "perkIcons.png", 32, 0, "Test"));
		
		this.buttonList.add(ironPickAxe = new IconButton(7, x-105, y-30, 20, 20, "perkIcons.png", 48, 0, "Test"));
		this.buttonList.add(ironAxe = new IconButton(8, x-45, y-30, 20, 20, "perkIcons.png", 64, 0, "Test"));
		
		this.buttonList.add(goldPickAxe = new IconButton(9, x+15, y-30, 20, 20, "perkIcons.png", 80, 0, "Test"));
		this.buttonList.add(goldAxe = new IconButton(10, x+75, y-30, 20, 20, "perkIcons.png", 96, 0, "Test"));
		
		this.buttonList.add(diamondAxe = new IconButton(15, x+90, y, 20, 20, "perkIcons.png", 128, 0, "Test"));
		this.buttonList.add(goldShovel = new IconButton(16, x+60, y, 20, 20, "perkIcons.png", 160, 0, "Test"));
		this.buttonList.add(lamp = new IconButton(17, x+30, y, 20, 20, "perkIcons.png", 208, 0, "Test"));
		this.buttonList.add(tree = new IconButton(18, x, y, 20, 20, "perkIcons.png", 224, 0, "Test"));
		
		this.buttonList.add(ladder = new IconButton(14, x-30, y, 20, 20, "perkIcons.png", 192, 0, "Test"));
		this.buttonList.add(bucket = new IconButton(13, x-60, y, 20, 20, "perkIcons.png", 176, 0, "Test"));
		this.buttonList.add(diamondShovel = new IconButton(12, x-90, y, 20, 20, "perkIcons.png", 144, 0, "Test"));
		this.buttonList.add(diamondPickAxe = new IconButton(11, x-120, y, 20, 20, "perkIcons.png", 112, 0, "Test"));
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Collector", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Collector")/2),
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				gui.height/2+35, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow(this.collectorXP + "/" + this.nextLevel, 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth(this.collectorXP + "/" + this.nextLevel)/2) + 80, 
				gui.height/2+35, Color.WHITE.hashCode());
		collector.render();
	}
	
	public void actionPerformed(GuiButton buton){
		
	}
}
