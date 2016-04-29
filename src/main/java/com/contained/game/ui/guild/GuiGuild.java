package com.contained.game.ui.guild;

import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.handler.KeyBindings;
import com.contained.game.ui.components.Container;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiGuild extends GuiScreen {	
	public GuildBase loner;
	public GuildPlayer teamplayer;
	public GuildLeader leader;
	
	private Container guild;
	
	public int guildStatus;
	public static boolean update = false;
	
	@Override
	public void initGui(){
		ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
		if(properties.inGame() || properties.gameMode != Resources.OVERWORLD ||
				MiniGameUtil.isPvP(mc.thePlayer.dimension) || MiniGameUtil.isTreasure(mc.thePlayer.dimension)){
			mc.displayGuiScreen(null);
			return;
		}
		
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(mc.thePlayer);
		guildStatus = pdata.getStatus();
				
		guild = new Container((this.width-256)/2, ((this.height-256)/2) + 20, 256, 176, "ui.png", this);
		
		switch(guildStatus){
		case PlayerTeamIndividual.LONER:
			loner = new GuildBase(this);
			this.buttonList = loner.getButtonList();
			break;
		case PlayerTeamIndividual.TEAM_PLAYER:
			teamplayer = new GuildPlayer(this);
			this.buttonList = teamplayer.getButtonList();
			break;
		case PlayerTeamIndividual.LEADER:
			leader = new GuildLeader(this);
			this.buttonList = leader.getButtonList();
			break;
		}
	}
	
	@Override
	public void updateScreen(){
		if(update){
			switch(guildStatus){
			case PlayerTeamIndividual.LONER:
				loner = new GuildBase(this);
				this.buttonList = loner.getButtonList();
				break;
			case PlayerTeamIndividual.TEAM_PLAYER:
				teamplayer = new GuildPlayer(this);
				this.buttonList = teamplayer.getButtonList();
				break;
			case PlayerTeamIndividual.LEADER:
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
		case PlayerTeamIndividual.LONER:
			guild.render();
			loner.render();
			break;
		case PlayerTeamIndividual.TEAM_PLAYER:
			guild.render();
			teamplayer.render();
			break;
		case PlayerTeamIndividual.LEADER:
			leader.render();
			break;
		}
		
		super.drawScreen(w, h, ticks);
	}
	
	@Override
	protected void mouseClickMove(int x, int y, int button, long ticks){
		if(!update){
			switch(guildStatus){
			case PlayerTeamIndividual.LONER:
				break;
			case PlayerTeamIndividual.TEAM_PLAYER:
				break;
			case PlayerTeamIndividual.LEADER:
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
			case PlayerTeamIndividual.LONER:
				loner.mouseMovedOrUp(x, y, button);
				break;
			case PlayerTeamIndividual.TEAM_PLAYER:
				break;
			case PlayerTeamIndividual.LEADER:
				leader.mouseMovedOrUp(x, y, button);
				break;
			}
		}
		super.mouseMovedOrUp(x, y, button);
	}
	
	@Override
	protected void mouseClicked(int i , int j, int k){
		switch(guildStatus){
		case PlayerTeamIndividual.LONER:
			loner.mouseClicked(i, j, k);
			break;
		case PlayerTeamIndividual.TEAM_PLAYER:
			break;
		case PlayerTeamIndividual.LEADER:
			leader.mouseClicked(i, j, k);
			break;
		}
		
		super.mouseClicked(i, j, k);
	}
	
	@Override
	protected void keyTyped(char c, int i){
		boolean textFocused = false;
		
		switch(guildStatus){
		case PlayerTeamIndividual.LONER:
			textFocused = loner.keyTyped(c, i);
			break;
		case PlayerTeamIndividual.TEAM_PLAYER:
			break;
		case PlayerTeamIndividual.LEADER:
			textFocused = leader.keyTyped(c, i);
			break;
		}
		
		if(!textFocused && (i == 1 || i == KeyBindings.toggleGuild.getKeyCode()))
			this.mc.thePlayer.closeScreen();
		
		super.keyTyped(c, i);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(!GuiGuild.update){
			switch(guildStatus){
			case PlayerTeamIndividual.LONER:
				loner.actionPerformed(button);
				break;
			case PlayerTeamIndividual.TEAM_PLAYER:
				teamplayer.actionPerformed(button);
				break;
			case PlayerTeamIndividual.LEADER:
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
	
	public void setButtonList(List<GuiButton> buttonList){
		this.buttonList = buttonList;
	}
}
