package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.components.IconButton;
import com.contained.game.ui.components.ProgressBar;

import net.minecraft.client.gui.GuiButton;

public class WarriorClass {
	private ClassPerks gui;
	private int warriorXP;
	private int level;
	private int nextLevel;
	private int levelOne = 2500;
	private int levelTwo = 5000;
	private int levelThree = 10000;
	protected List<GuiButton> buttonList = new ArrayList<GuiButton>();
	
	private IconButton woodSword, chestPlate, stoneSword, arrow, ironHorse, goldHorse, diamondHorse;
	private IconButton saddle, bow, ironPlate, ironSword, goldPlate, goldSword, diamondPlate, diamondSword;

	private ProgressBar warrior;
	
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
		
		warrior = new ProgressBar(this.gui.width/2-50, this.gui.height/2+30, ProgressBar.YELLOW, warriorXP, nextLevel, this.gui.mc);
	}
	
	public List<GuiButton> getButtonlist(){
		int x = this.gui.width/2;
		int y = this.gui.height/2;
		
		this.buttonList.add(woodSword = new IconButton(-1, x-15, y-90, 20, 20, "perkIcons.png", 0, 64, "+10% Attack"));
		
		this.buttonList.add(chestPlate = new IconButton(61, x-75, y-60, 20, 20, "perkIcons.png", 16, 64, "-10% Damage"));
		this.buttonList.add(stoneSword = new IconButton(62, x+45, y-60, 20, 20, "perkIcons.png", 32, 64, "+10% Attack"));
		
		this.buttonList.add(ironHorse = new IconButton(64, x-45, y-30, 20, 20, "perkIcons.png", 64, 64, "-20% Damage"));
		this.buttonList.add(arrow = new IconButton(63, x-105, y-30, 20, 20, "perkIcons.png", 48, 64, "+5% Damage"));
		
		this.buttonList.add(diamondHorse = new IconButton(66, x+75, y-30, 20, 20, "perkIcons.png", 96, 64, "-20% Damage"));
		this.buttonList.add(goldHorse = new IconButton(65, x+15, y-30, 20, 20, "perkIcons.png", 80, 64, "-20% Damage"));

		this.buttonList.add(diamondSword = new IconButton(74, x+90, y, 20, 20, "perkIcons.png", 224, 64, "+10% Attack"));
		this.buttonList.add(diamondPlate = new IconButton(73, x+60, y, 20, 20, "perkIcons.png", 208, 64, "-10% Damage"));
		this.buttonList.add(goldSword = new IconButton(72, x+30, y, 20, 20, "perkIcons.png", 192, 64, "+10% Attack"));
		this.buttonList.add(goldPlate = new IconButton(71, x, y, 20, 20, "perkIcons.png", 176, 64, "-10% Damage"));

		this.buttonList.add(ironSword = new IconButton(70, x-30, y, 20, 20, "perkIcons.png", 160, 64, "+10% Attack"));
		this.buttonList.add(ironPlate = new IconButton(69, x-60, y, 20, 20, "perkIcons.png", 144, 64, "-10% Damage"));
		this.buttonList.add(bow = new IconButton(68, x-90, y, 20, 20, "perkIcons.png", 128, 64, "+10% Range"));
		this.buttonList.add(saddle = new IconButton(67, x-120, y, 20, 20, "perkIcons.png", 112, 64, "+5% Speed"));

		renderTree();
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Warrior", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Warrior")/2),
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				gui.height/2+35, Color.WHITE.hashCode());
		
		String xp = (this.warriorXP < levelThree) ? this.warriorXP + "/" + this.nextLevel : "MAX";
		this.gui.mc.fontRenderer.drawStringWithShadow(xp,
				((this.gui.width)/2) - this.gui.mc.fontRenderer.getStringWidth(xp)/2 + 80,
				gui.height/2+35, Color.WHITE.hashCode());
		warrior.render();
	}
	
	private void renderTree(){
		ArrayList<Integer> perks = ExtendedPlayer.get(this.gui.mc.thePlayer).perks;
		woodSword.enabled = false;
		if(perks.size() >= 2 || warriorXP < levelOne){
			chestPlate.enabled = false;
			stoneSword.enabled = false;
		}
		if(perks.size() >= 3 || warriorXP < levelTwo){
			arrow.enabled = false;
			ironHorse.enabled = false;
			goldHorse.enabled = false;
			diamondHorse.enabled = false;
		}else{
			if(!perks.contains(chestPlate.id)){
				arrow.enabled = false;
				ironHorse.enabled = false;
			}
			if(!perks.contains(stoneSword.id)){
				goldHorse.enabled = false;
				diamondHorse.enabled = false;
			}
		}
		if(perks.size() >= 4 || warriorXP < levelThree){
			saddle.enabled = false;
			bow.enabled = false;
			ironPlate.enabled = false;
			ironSword.enabled = false;
			goldPlate.enabled = false;
			goldSword.enabled = false;
			diamondPlate.enabled = false;
			diamondSword.enabled = false;
		}else{
			if(!perks.contains(arrow.id)){
				saddle.enabled = false;
				bow.enabled = false;
			}
			
			if(!perks.contains(ironHorse.id)){
				ironPlate.enabled = false;
				ironSword.enabled = false;
			}
			
			if(!perks.contains(goldHorse.id)){
				goldPlate.enabled = false;
				goldSword.enabled = false;
			}
			
			if(!perks.contains(diamondHorse.id)){
				diamondPlate.enabled = false;
				diamondSword.enabled = false;
			}
		}
	}
}
