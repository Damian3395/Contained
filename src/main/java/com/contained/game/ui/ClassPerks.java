package com.contained.game.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import com.contained.game.data.ExtendedPlayer;
import com.contained.game.util.Data;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class ClassPerks extends GuiScreen{
	private int level = 1000;
	private GuiButton mining;
	private GuiButton cooking;
	private GuiButton farming;
	private GuiButton fishing;
	private GuiButton lumber;
	private GuiButton figher;
	private GuiButton potion;
	private GuiButton building;
	private GuiButton machine;
	private GuiButton transport;
	
	private int[] classXP;
	private int selectedClass;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui(){
		selectedClass = ExtendedPlayer.get(mc.thePlayer).getOccupationClass();
		if(selectedClass == -1){
			int x = this.width-60;
			this.buttonList.add(this.mining = new GuiButton(0, x, 15, 30, 20, "Perk"));
			this.buttonList.add(this.cooking = new GuiButton(1, x, 35, 30, 20, "Perk"));
			this.buttonList.add(this.farming = new GuiButton(2, x, 55, 30, 20, "Perk"));
			this.buttonList.add(this.fishing = new GuiButton(3, x, 75, 30, 20, "Perk"));
			this.buttonList.add(this.lumber = new GuiButton(4, x, 95, 30, 20, "Perk"));
			this.buttonList.add(this.figher = new GuiButton(5, x, 115, 30, 20, "Perk"));
			this.buttonList.add(this.potion = new GuiButton(6, x, 135, 30, 20, "Perk"));
			this.buttonList.add(this.building = new GuiButton(7, x, 155, 30, 20, "Perk"));
			this.buttonList.add(this.machine = new GuiButton(8, x, 175, 30, 20, "Perk"));
			this.buttonList.add(this.transport = new GuiButton(9, x, 195, 30, 20, "Perk"));
			
			classXP = ExtendedPlayer.get(mc.thePlayer).getOccupationValues();
			
			this.mining.enabled = (classXP[Data.MINING] >= level) ? true : false;
			this.cooking.enabled = (classXP[Data.COOKING] >= level) ? true : false;
			this.farming.enabled = (classXP[Data.FARMING] >= level) ? true : false;
			this.fishing.enabled = (classXP[Data.FISHING] >= level) ? true : false;
			this.lumber.enabled = (classXP[Data.LUMBER] >= level) ? true : false;
			this.figher.enabled = (classXP[Data.FIGHTER] >= level) ? true : false;
			this.potion.enabled = (classXP[Data.POTION] >= level) ? true : false;
			this.building.enabled = (classXP[Data.BUILDING] >= level) ? true : false;
			this.machine.enabled = (classXP[Data.MACHINE] >= level) ? true : false;
			this.transport.enabled = (classXP[Data.TRANSPORT] >= level) ? true : false;
		}else{
			/*
			 * Create A Complex Level System For Each Class
			 */
		}
	}
	
	@Override
	public void updateScreen(){
		
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){
		this.drawDefaultBackground();
		
		if(selectedClass == -1){
			int textColor = Color.white.hashCode();
			this.mc.fontRenderer.drawString("Class Perks", this.width/2 - this.mc.fontRenderer.getStringWidth("Class Perks")/2, 5, textColor);
			
			this.mc.fontRenderer.drawString("Mining: ", 10, 20, textColor);
			drawXPBar(100, 20, classXP[Data.MINING], level);
			
			this.mc.fontRenderer.drawString("Cooking: ", 10, 40, textColor);
			drawXPBar(100, 40, classXP[Data.COOKING], level);
			
			this.mc.fontRenderer.drawString("Farming: ", 10, 60, textColor);
			drawXPBar(100, 60, classXP[Data.FARMING], level);
			
			this.mc.fontRenderer.drawString("Fishing: ", 10, 80, textColor);
			drawXPBar(100,  80, classXP[Data.FISHING], level);
			
			this.mc.fontRenderer.drawString("Lumber: ", 10, 100, textColor);
			drawXPBar(100,  100, classXP[Data.LUMBER], level);
			
			this.mc.fontRenderer.drawString("Fighting: ", 10, 120, textColor);
			drawXPBar(100,  120, classXP[Data.FIGHTER], level);
			
			this.mc.fontRenderer.drawString("Brewing: ", 10, 140, textColor);
			drawXPBar(100,  140, classXP[Data.POTION], level);
			
			this.mc.fontRenderer.drawString("Building: ", 10, 160, textColor);
			drawXPBar(100,  160, classXP[Data.BUILDING], level);
			
			this.mc.fontRenderer.drawString("Engineering: ", 10, 180, textColor);
			drawXPBar(100,  180, classXP[Data.MACHINE], level);
			
			this.mc.fontRenderer.drawString("Transporting: ", 10, 200, textColor);
			drawXPBar(100,  200, classXP[Data.TRANSPORT], level);
		}
		
		super.drawScreen(w, h, ticks);
	}
	
	private void drawXPBar(int x, int y, int xp, int level){
		drawRect(x, y, x + ((int)(200.0 * ((double)xp/(double)level))), y + 15, Color.GREEN.hashCode());
		this.mc.fontRenderer.drawString(xp + "/" + level, this.width - 80 - this.mc.fontRenderer.getStringWidth(xp + "/" + level), y, Color.WHITE.hashCode());
	}
	
	@Override
	protected void mouseClickMove(int x, int y, int button, long ticks){
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(button.id <= 9 && button.id >= 0){
			ExtendedPlayer.get(mc.thePlayer).setOccupationClass(button.id);
			this.selectedClass = button.id;
		}
	}
	
	@Override
	public void onGuiClosed(){
		
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
}
