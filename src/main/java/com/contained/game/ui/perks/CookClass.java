package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.components.IconButton;
import com.contained.game.ui.components.ProgressBar;

import net.minecraft.client.gui.GuiButton;

public class CookClass {
	private ClassPerks gui;
	private int cookXP;
	public int level;
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
		this.buttonList.add(woodHoe = new IconButton(-1, x-15, y-90, 20, 20, "perkIcons.png", 0, 32, "-10% Damage When Held"));
		
		this.buttonList.add(stoneHoe = new IconButton(33, x-75, y-60, 20, 20, "perkIcons.png", 16, 32, "-15% Damage When Held"));
		this.buttonList.add(seeds = new IconButton(34, x+45,y-60, 20, 20, "perkIcons.png", 32, 32, "2x Harvest"));
		
		this.buttonList.add(ironHoe = new IconButton(35, x-105, y-30, 20, 20, "perkIcons.png", 48, 32, "+15% Damage"));
		this.buttonList.add(fishingRod = new IconButton(36, x-45, y-30, 20, 20, "perkIcons.png", 64, 32, "-15% Damage When Held"));
		
		this.buttonList.add(apple = new IconButton(37, x+15, y-30, 20, 20, "perkIcons.png", 80, 32, "-10% Damage To Mounted Horse"));
		this.buttonList.add(carrot = new IconButton(38, x+75, y-30, 20, 20, "perkIcons.png", 96, 32, "+10% Damage To All Animals"));

		this.buttonList.add(goldenCarrot = new IconButton(46, x+90, y, 20, 20, "perkIcons.png", 224, 32, "+10% Damage To Players"));
		this.buttonList.add(cookie = new IconButton(45, x+60, y, 20, 20, "perkIcons.png", 208, 32, "Restore Health"));
		this.buttonList.add(goldenApple = new IconButton(44, x+30, y, 20, 20, "perkIcons.png", 192, 32, "+10% Damate To Horses"));
		this.buttonList.add(cake = new IconButton(43, x, y, 20, 20, "perkIcons.png", 176, 32, "Restore Health"));
		
		this.buttonList.add(salmon = new IconButton(42, x-30, y, 20, 20, "perkIcons.png", 160, 32, "Potion of Strength"));
		this.buttonList.add(fish = new IconButton(41, x-60, y, 20, 20, "perkIcons.png", 144, 32, "Potion of Water Breathing"));
		this.buttonList.add(diamondHoe = new IconButton(40, x-90, y, 20, 20, "perkIcons.png", 128, 32, "+25% Damage"));
		this.buttonList.add(goldHoe = new IconButton(39, x-120, y, 20, 20, "perkIcons.png", 112, 32, "-25% Damage When Held"));
		
		renderTree();
		
		return buttonList;
	}
	
	public void render(){
		this.gui.mc.fontRenderer.drawStringWithShadow("The Cook", 
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("The Cook")/2),
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		this.gui.mc.fontRenderer.drawStringWithShadow("LeveL: " + this.level,
				((this.gui.width)/2) - (this.gui.mc.fontRenderer.getStringWidth("LeveL: " + this.level)/2) - 100,
				gui.height/2+35, Color.WHITE.hashCode());
		
		String xp = (this.cookXP < levelThree) ? this.cookXP + "/" + this.nextLevel : "MAX";
		this.gui.mc.fontRenderer.drawStringWithShadow(xp,
				((this.gui.width)/2) - this.gui.mc.fontRenderer.getStringWidth(xp)/2 + 80,
				gui.height/2+35, Color.WHITE.hashCode());
		cook.render();
	}
	
	private void renderTree(){
		ArrayList<Integer> perks = ExtendedPlayer.get(this.gui.mc.thePlayer).perks;
		woodHoe.enabled = false;
		if(perks.size() >= 2 || cookXP < levelOne){
			stoneHoe.enabled = false;
			seeds.enabled = false;
		}
		if(perks.size() >= 3 || cookXP < levelTwo){
			ironHoe.enabled = false;
			fishingRod.enabled = false;
			apple.enabled = false;
			carrot.enabled = false;
		}else{
			if(!perks.contains(stoneHoe.id)){
				ironHoe.enabled = false;
				fishingRod.enabled = false;
			}
			if(!perks.contains(seeds.id)){
				apple.enabled = false;
				carrot.enabled = false;
			}
		}
		if(perks.size() >= 4 || cookXP < levelThree){
			goldHoe.enabled = false;
			diamondHoe.enabled = false;
			fish.enabled = false;
			salmon.enabled = false;
			cake.enabled = false;
			goldenApple.enabled = false;
			cookie.enabled = false;
			goldenCarrot.enabled = false;
		}else{
			if(!perks.contains(ironHoe.id)){
				goldHoe.enabled = false;
				diamondHoe.enabled = false;
			}
			
			if(!perks.contains(fishingRod.id)){
				fish.enabled = false;
				salmon.enabled = false;
			}
			
			if(!perks.contains(apple.id)){
				cake.enabled = false;
				goldenApple.enabled = false;
			}
			
			if(!perks.contains(carrot.id)){
				cookie.enabled = false;
				goldenCarrot.enabled = false;
			}
		}
	}
}
