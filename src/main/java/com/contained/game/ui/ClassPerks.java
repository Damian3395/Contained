package com.contained.game.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.perks.BaseClass;
import com.contained.game.ui.perks.BuilderClass;
import com.contained.game.ui.perks.CollectorClass;
import com.contained.game.ui.perks.CookClass;
import com.contained.game.ui.perks.WarriorClass;
import com.contained.game.ui.perks.WizardClass;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class ClassPerks extends GuiScreen{
	public static final int NONE = -1;
	public static final int COLLECTOR = 0;
	public static final int BUILDER = 1;
	public static final int COOK = 2;
	public static final int WIZARD = 3;
	public static final int WARRIOR = 4;
	
	private BaseClass base;
	private CollectorClass collector;
	private BuilderClass builder;
	private CookClass cook;
	private WizardClass wizard;
	private WarriorClass warrior;
	
	private int collectorXP = 0;
	private int cookXP = 0;
	private int builderXP = 0;
	private int wizardXP = 0;
	private int warriorXP = 0;
	private int[] classXP;
	private int selectedClass;
	private int level;
	
	private boolean update = false;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui(){
		selectedClass = ExtendedPlayer.get(mc.thePlayer).getOccupationClass();
		classXP = ExtendedPlayer.get(mc.thePlayer).getOccupationValues();
		level = ExtendedPlayer.get(mc.thePlayer).occupationLevel;
		
		collectorXP = classXP[Data.MINING] + classXP[Data.LUMBER];
		cookXP = classXP[Data.FARMING] + classXP[Data.FISHING] + classXP[Data.COOKING];
		builderXP = classXP[Data.BUILDING] + classXP[Data.MACHINE] + classXP[Data.TRANSPORT];
		wizardXP = classXP[Data.POTION];
		warriorXP = classXP[Data.FIGHTER];
		
		switch(selectedClass){
		case NONE:
			base = new BaseClass(this, collectorXP, cookXP, builderXP, wizardXP, warriorXP);
			this.buttonList = base.getButtonList();
			break;
		case COLLECTOR:
			collector = new CollectorClass(this, collectorXP, level);
			this.buttonList = collector.getButtonList();
			break;
		case BUILDER:
			builder = new BuilderClass(this, builderXP, level);
			this.buttonList = builder.getButtonList();
			break;
		case COOK:
			cook = new CookClass(this, cookXP, level);
			this.buttonList = cook.getButtonList();
			break;
		case WIZARD:
			wizard = new WizardClass(this, wizardXP, level);
			this.buttonList = wizard.getButtonList();
			break;
		case WARRIOR:
			warrior = new WarriorClass(this, warriorXP, level);
			this.buttonList = warrior.getButtonlist();
			break;
		}
	}
	
	@Override
	public void updateScreen(){
		if(this.update){
			switch(selectedClass){
			case NONE:
				base = new BaseClass(this, collectorXP, cookXP, builderXP, wizardXP, warriorXP);
				this.buttonList = base.getButtonList();
				break;
			case COLLECTOR:
				collector = new CollectorClass(this, collectorXP, level);
				this.buttonList = collector.getButtonList();
				break;
			case BUILDER:
				builder = new BuilderClass(this, builderXP, level);
				this.buttonList = builder.getButtonList();
				break;
			case COOK:
				cook = new CookClass(this, cookXP, level);
				this.buttonList = cook.getButtonList();
				break;
			case WIZARD:
				wizard = new WizardClass(this, wizardXP, level);
				this.buttonList = wizard.getButtonList();
				break;
			case WARRIOR:
				warrior = new WarriorClass(this, warriorXP, level);
				this.buttonList = warrior.getButtonlist();
				break;
			}
			this.update = false;
		}
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){
		this.drawDefaultBackground();
		
		this.mc.getTextureManager().bindTexture(new ResourceLocation("minecraft", "textures/gui/background.png"));
		this.drawTexturedModalRect((this.width-256)/2, ((this.height-256)/2) + 20, 0, 0, 256, 256);
		
		switch(selectedClass){
		case NONE:
			base.render();
			break;
		case COLLECTOR:
			collector.render();
			break;
		case BUILDER:
			builder.render();
			break;
		case COOK:
			cook.render();
			break;
		case WIZARD:
			wizard.render();
			break;
		case WARRIOR:
			warrior.render();
			break;
		}
		
		super.drawScreen(w, h, ticks);
	}
	
	@Override
	protected void mouseClickMove(int x, int y, int button, long ticks){
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(!this.update){
			//Debug For Now
			if(button.id >= -1 && button.id <= 4){
				ExtendedPlayer.get(mc.thePlayer).setOccupationClass(button.id);
				
				/*
				PacketCustom classPacket = new PacketCustom(Resources.MOD_ID, 1);
				classPacket.writeInt(ExtendedPlayer.get(mc.thePlayer).getOccupationClass());
				classPacket.sendToServer();
				*/
				
				this.selectedClass = button.id;
				this.update = true;
			}
			
			/*
			switch(selectedClass){
			case NONE:
				base.actionPerformed(button);
				break;
			case COLLECTOR:
				collector.actionPerformed(button);
				break;
			case BUILDER:
				builder.actionPerformed(button);
				break;
			case COOK:
				cook.actionPerformed(button);
				break;
			case WIZARD:
				wizard.actionPerformed(button);
				break;
			case WARRIOR:
				warrior.actionPerformed(button);
				break;
			}
			*/
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
