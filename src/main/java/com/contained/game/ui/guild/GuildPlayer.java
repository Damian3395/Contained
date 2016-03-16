package com.contained.game.ui.guild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.ui.GuiGuild;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

public class GuildPlayer {
	private final int LEAVE = 0;
	
	private int x, y;
	
	private GuiGuild gui;
	
	private GuiButton leave;
	
	private PlayerTeamIndividual pdata;
	private PlayerTeam team;
	
	protected List buttonList = new ArrayList();
	
	public GuildPlayer(GuiGuild gui){
		this.gui = gui;
		
		x = this.gui.width/2;
		y = this.gui.height/2;
		
		EntityPlayer player = (EntityPlayer) this.gui.mc.thePlayer;
		pdata = PlayerTeamIndividual.get(player);
		
		team = PlayerTeam.get(pdata.teamID);
	}
	
	public List getButtonList(){
		this.buttonList.add(leave = new GuiButton(LEAVE, x+70, y+30, 40, 20, "Leave"));
		
		return buttonList;
	}
	
	public void update(){
		
	}
	
	public void render(){
		renderFont(0, -100, "Guild: " + team.displayName, Color.WHITE);
	}
	
	public void actionPerformed(GuiButton button){
		switch(button.id){
		case LEAVE:
			pdata.leaveTeam();
			team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+pdata.playerName+" has left the team.");
			break;
		}
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(text, 
				(this.gui.width - this.gui.mc.fontRenderer.getStringWidth(text))/2 + x,
				(this.gui.height/2) + y, color.hashCode());
	}
}