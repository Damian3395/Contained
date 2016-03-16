package com.contained.game.ui.perks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ServerPacketHandler;
import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.components.ProgressBar;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
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
	
	private ProgressBar collectorBar, builderBar, cookBar, wizardBar, warriorBar;
	
	public BaseClass(ClassPerks gui, int collectorXP, int cookXP, int builderXP, int wizardXP, int warriorXP){
		this.gui = gui;
		this.collectorXP = collectorXP;
		this.builderXP = builderXP;
		this.cookXP = cookXP;
		this.wizardXP = wizardXP;
		this.warriorXP = wizardXP;
		
		int x = gui.width/2-60;
		int y = gui.height/2;
		collectorBar = new ProgressBar(x, y-90, ProgressBar.RED, collectorXP, level, this.gui.mc);
		builderBar = new ProgressBar(x, y-60, ProgressBar.BLUE, builderXP, level, this.gui.mc);
		cookBar = new ProgressBar(x, y-30, ProgressBar.GREEN, cookXP, level, this.gui.mc);
		wizardBar = new ProgressBar(x, y, ProgressBar.PINK, wizardXP, level, this.gui.mc);
		warriorBar = new ProgressBar(x, y+30, ProgressBar.YELLOW, warriorXP, level, this.gui.mc);
	}
	
	public List getButtonList(){
		int x = (this.gui.width/2)+50;
		int y = this.gui.height/2;
		this.buttonList.add(this.collector = new GuiButton(ClassPerks.COLLECTOR, x, y-90, 60, 20, "Collector"));
		this.buttonList.add(this.builder = new GuiButton(ClassPerks.BUILDER, x, y-60, 60, 20, "Builder"));
		this.buttonList.add(this.cook = new GuiButton(ClassPerks.COOK, x, y-30, 60, 20, "Cook"));
		this.buttonList.add(this.wizard = new GuiButton(ClassPerks.WIZARD, x, y, 60, 20, "Wizard"));
		this.buttonList.add(this.warrior = new GuiButton(ClassPerks.WARRIOR, x, y+30, 60, 20, "Warrior"));
		
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
				(this.gui.height/2)-100, Color.WHITE.hashCode());
		
		int x = gui.width/2-60;
		int y = gui.height/2;
		
		drawXP(x, y-90, collectorXP, Color.WHITE);
		collectorBar.render();
		
		drawXP(x, y-60, builderXP, Color.WHITE);
		builderBar.render();
		
		drawXP(x, y-30, cookXP, Color.WHITE);
		cookBar.render();
		
		drawXP(x, y, wizardXP, Color.WHITE);
		wizardBar.render();
		
		drawXP(x, y+30, warriorXP, Color.WHITE);
		warriorBar.render();
	}
	
	private void drawXP(int x, int y, int xp, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(xp + "/" + level, x - this.gui.mc.fontRenderer.getStringWidth(xp + "/" + level) - 10, y+5, Color.WHITE.hashCode());
	}
	
	public void actionPerformed(GuiButton button){
		ExtendedPlayer.get(this.gui.mc.thePlayer).setOccupationClass(button.id);
		
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.UPDATE_CLASS);
		packet.writeInt(button.id);
		ServerPacketHandler.sendToServer(packet.toPacket());
			
		this.gui.selectedClass = button.id;
		this.gui.update = true;
	}
}
