package com.contained.game.ui;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.handler.KeyBindings;
import com.contained.game.network.ServerPacketHandler;
import com.contained.game.ui.components.Container;
import com.contained.game.ui.perks.BaseClass;
import com.contained.game.ui.perks.BuilderClass;
import com.contained.game.ui.perks.CollectorClass;
import com.contained.game.ui.perks.CookClass;
import com.contained.game.ui.perks.WarriorClass;
import com.contained.game.ui.perks.WizardClass;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ClassPerks extends GuiScreen{
	public static final int NONE = -1;
	public static final int COLLECTOR = 0;
	public static final int BUILDER = 1;
	public static final int COOK = 2;
	public static final int WIZARD = 3;
	public static final int WARRIOR = 4;
	
	private Container background;
	
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
	public int selectedClass;
	private int level;
	
	public boolean update = false;
	
	@Override
	public void initGui(){
		selectedClass = ExtendedPlayer.get(mc.thePlayer).getOccupationClass();
		classXP = ExtendedPlayer.get(mc.thePlayer).getOccupationValues();
		level = ExtendedPlayer.get(mc.thePlayer).occupationLevel;
		
		background = new Container((this.width-256)/2, ((this.height-256)/2) + 20, 256, 176, "ui.png", this);
		
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
		background.render();
		
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
	protected void keyTyped(char c, int i){		
		if(i == 1 || i == KeyBindings.toggleClassPerks.getKeyCode())
			this.mc.thePlayer.closeScreen();
		
		super.keyTyped(c, i);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(!this.update){
			this.update = true;
			
			if(selectedClass == NONE){
				PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.SELECT_CLASS);
				packet.writeInt(button.id);
				ServerPacketHandler.sendToServer(packet.toPacket());
			}
			
			PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.LEVEL_UP);
			packet.writeInt(button.id);
			packet.writeInt((ExtendedPlayer.get(this.mc.thePlayer).occupationLevel) + 1);
			ServerPacketHandler.sendToServer(packet.toPacket());
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
