package com.contained.game.ui;

import java.util.List;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.ui.components.Container;
import com.contained.game.ui.guild.GuildBase;
import com.contained.game.ui.guild.GuildLeader;
import com.contained.game.ui.guild.GuildPlayer;
import com.contained.game.util.Resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;

public class GuiGuild extends GuiScreen{
	public static final int LONER = 0;
	public static final int TEAM_PLAYER = 1;
	public static final int LEADER = 2;
	
	public GuildBase loner;
	public GuildPlayer teamplayer;
	public GuildLeader leader;
	
	private Container guild;
	
	public int guildStatus;
	public static boolean update = false;;
	
	@Override
	public void initGui(){
		ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
		guildStatus = properties.guild;
				
		guild = new Container((this.width-256)/2, ((this.height-256)/2) + 20, 256, 176, "ui.png", this);
		
		switch(guildStatus){
		case LONER:
			loner = new GuildBase(this);
			this.buttonList = loner.getButtonList();
			break;
		case TEAM_PLAYER:
			teamplayer = new GuildPlayer(this);
			this.buttonList = teamplayer.getButtonList();
			break;
		case LEADER:
			leader = new GuildLeader(this);
			this.buttonList = leader.getButtonList();
			break;
		}
	}
	
	@Override
	public void updateScreen(){
		if(update){
			switch(guildStatus){
			case LONER:
				loner = new GuildBase(this);
				this.buttonList = loner.getButtonList();
				break;
			case TEAM_PLAYER:
				teamplayer = new GuildPlayer(this);
				this.buttonList = teamplayer.getButtonList();
				break;
			case LEADER:
				leader = new GuildLeader(this);
				this.buttonList = leader.getButtonList();
				break;
			}
			update = false;
		}
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){	
		switch(guildStatus){
		case LONER:
			guild.render();
			loner.render();
			break;
		case TEAM_PLAYER:
			guild.render();
			teamplayer.render();
			break;
		case LEADER:
			leader.render();
			break;
		}
		
		super.drawScreen(w, h, ticks);
	}
	
	@Override
	protected void mouseClickMove(int x, int y, int button, long ticks){
		if(!update){
			switch(guildStatus){
			case LONER:
				break;
			case TEAM_PLAYER:
				break;
			case LEADER:
				leader.mouseClickMove(x, y, button, ticks);
				break;
			}
		}
		super.mouseClickMove(x, y, button, ticks);
	}
	
	@Override
	protected void mouseMovedOrUp(int x, int y, int button){
		if(!update){
			switch(guildStatus){
			case LONER:
				loner.mouseMovedOrUp(x, y, button);
				break;
			case TEAM_PLAYER:
				break;
			case LEADER:
				leader.mouseMovedOrUp(x, y, button);
				break;
			}
		}
		super.mouseMovedOrUp(x, y, button);
	}
	
	@Override
	protected void mouseClicked(int i , int j, int k){
		switch(guildStatus){
		case LONER:
			loner.mouseClicked(i, j, k);
			break;
		case TEAM_PLAYER:
			break;
		case LEADER:
			leader.mouseClicked(i, j, k);
			break;
		}
		
		super.mouseClicked(i, j, k);
	}
	
	@Override
	protected void keyTyped(char c, int i){
		switch(guildStatus){
		case LONER:
			loner.keyTyped(c, i);
			break;
		case TEAM_PLAYER:
			break;
		case LEADER:
			leader.keyTyped(c, i);
			break;
		}
		
		if(i == 1)
			this.mc.thePlayer.closeScreen();
		
		super.keyTyped(c, i);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(!this.update){
			switch(guildStatus){
			case LONER:
				loner.actionPerformed(button);
				break;
			case TEAM_PLAYER:
				teamplayer.actionPerformed(button);
				break;
			case LEADER:
				leader.actionPerformed(button);
				break;
			}
		}
	}
	
	@Override
	public void onGuiClosed(){

	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	public void clearButtonList(){
		this.buttonList.clear();
	}
	
	public void setButtonList(List buttonlist){
		this.buttonList = buttonList;
	}
}
