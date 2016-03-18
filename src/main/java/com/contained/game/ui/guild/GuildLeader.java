package com.contained.game.ui.guild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.network.ServerPacketHandler;
import com.contained.game.ui.GuiGuild;
import com.contained.game.ui.components.GuiScrollPane;
import com.contained.game.ui.components.GuiTab;
import com.contained.game.ui.components.IconButton;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.util.ErrorCase;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class GuildLeader {
	private final int SAVE = 0;
	private final int RESET = 1;
	private final int TEAM_COLOR = 2;
	private final int DISBAND = 3;
	private final int INVITE = 4;
	
	private final int DEMOTE = 5;
	private final int PROMOTE = 6;
	private final int KICK = 7;
	
	private int x, y;
	private int selectedColor;
	
	private String teamUpdateStatus = "";
	private Color teamUpdateColor = Color.WHITE;
	
	private GuiGuild gui;
	private GuiTab tabPane;
	private GuiScrollPane teamPlayers, findPlayers;
	
	private GuiButton save, reset, disband, invite;
	private IconButton teamColor;
	private GuiButton promote, demote, kick;
	
	private GuiTextField teamName;
	
	private PlayerTeamIndividual pdata;
	private PlayerTeam team;
	
	protected List buttonList = new ArrayList();
	
	public GuildLeader(GuiGuild gui){
		this.gui = gui;
		
		x = this.gui.width/2;
		y = this.gui.height/2;
		
		EntityPlayer player = (EntityPlayer) this.gui.mc.thePlayer;
		pdata = PlayerTeamIndividual.get(player);
		
		team = PlayerTeam.get(pdata.teamID);
		selectedColor = team.getColorID();
		
		tabPane = new GuiTab(gui, GuiTab.STAR, GuiTab.SHIELD, GuiTab.SETTINGS);
		teamPlayers = new GuiScrollPane(gui, x-80, y-70, team.getTeamPlayers(this.gui.mc.thePlayer.getDisplayName()));
		findPlayers = new GuiScrollPane(gui,x-80, y-50, team.getLonerList(this.gui.mc.thePlayer.getDisplayName()));
		
		teamName = new GuiTextField(this.gui.mc.fontRenderer, x - 120, y-80, 100, 20);
		teamName.setTextColor(Color.WHITE.hashCode());
        teamName.setEnableBackgroundDrawing(true);
        teamName.setMaxStringLength(50);
        teamName.setText(team.displayName);
        teamName.setFocused(false);
	}
	
	public List getButtonList(){
		//Settings Buttons
		buttonList.add(save = new GuiButton(SAVE, x+80, y+50, 30, 20, "Save"));
		buttonList.add(reset = new GuiButton(RESET, x+30, y+50, 40, 20, "Reset"));
		buttonList.add(disband = new GuiButton(DISBAND, x-120, y+50, 50, 20, "Disband"));
		buttonList.add(teamColor = new IconButton(TEAM_COLOR, x-10, y-80, 20, 20, PlayerTeam.rgbColors[selectedColor]));
		buttonList.add(invite = new GuiButton(INVITE, x+70, y-50, 40, 20, "Invite"));
		save.visible = false;
		reset.visible = false;
		disband.visible = false;
		teamColor.visible = false;
		invite.visible = false;
		
		//Team Buttons
		buttonList.add(demote = new GuiButton(DEMOTE, x+70, y+50, 40, 20, "Demote"));
		buttonList.add(promote = new GuiButton(PROMOTE, x+10, y+50, 50, 20, "Promote"));
		buttonList.add(kick = new GuiButton(KICK, x-120, y+50, 40, 20, "Kick"));
		demote.visible = false;
		promote.visible = false;
		kick.visible = false;
		
		//Action Buttons
		
		return buttonList;
	}
	
	public void mouseMovedOrUp(int x, int y, int button){
		tabPane.mouseMovedOrUp(x, y, button);
	}
	
	public void mouseClickMove(int x, int y, int button, long ticks){
		switch(tabPane.selectedTab){
		case 0: // Actions
			break;
		case 1: // Team Members
			teamPlayers.mouseClickMove(x, y, button, ticks);
			break;
		case 2: // Settings
			findPlayers.mouseClickMove(x, y, button, ticks);
			break;
		}
	}
	
	public void mouseClicked(int i , int j, int k){
		teamName.mouseClicked(i, j, k);
		switch(tabPane.selectedTab){
		case 0: // Actions
			break;
		case 1: // Team Members
			teamPlayers.mouseClicked(i, j, k);
			break;
		case 2: // Settings
			findPlayers.mouseClicked(i, j, k);
			break;
		}
	}
	
	public void keyTyped(char c, int i){
		if(teamName.isFocused()){
			teamName.textboxKeyTyped(c, i);
		}
	}
	
	public void update(){
		
	}
	
	public void actionPerformed(GuiButton button){
		String teammate;
		switch(button.id){
		case SAVE:
			String newName = teamName.getText();
			boolean update = true;
			if((!newName.isEmpty() && (newName.compareTo(team.displayName) != 0) 
					|| (selectedColor != team.colorID && !newName.isEmpty()))){
				for(PlayerTeam t : Contained.teamData) {
					if (t.displayName.toLowerCase().equals(newName.toLowerCase())) {
						update = false;
						break;
					}
				}
				
				if(update){
					team.displayName = teamName.getText();
					team.colorID = selectedColor;
					teamUpdateStatus = "Changes Saved";
					teamUpdateColor = Color.GREEN;
					team.sendMessageToTeam(team.getFormatCode() + "[NOTICE] Team Update: " + team.getFormatCode() + "§l" + team.displayName + team.getFormatCode() +".");
					Contained.channel.sendToAll(ClientPacketHandler.packetSyncTeams(Contained.teamData).toPacket());
				}else{
					teamUpdateStatus = "Team Name Taken";
					teamUpdateColor = Color.RED;
				}
			}
			
			break;
		case RESET:
			teamName.setText(team.displayName);
			teamColor.color = PlayerTeam.rgbColors[team.colorID];
			break;
		case TEAM_COLOR:
			if(selectedColor < PlayerTeam.rgbColors.length -1)
				selectedColor++;
			else
				selectedColor = 0;
			teamColor.color = PlayerTeam.rgbColors[selectedColor];
			break;
		case DISBAND:
			team.disbandTeam();
			EntityPlayer player = (EntityPlayer) this.gui.mc.thePlayer;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			properties.guild = GuiGuild.LONER;
			this.gui.guildStatus = GuiGuild.LONER;
			this.gui.update = true;
			
			PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.UPDATE_GUILD_STATUS);
			packet.writeInt(GuiGuild.LONER);
			ServerPacketHandler.sendToServer(packet.toPacket());
			
			DataLogger.insertDisbandTeam("debugmod", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), team.displayName, Util.getDate());
			break;
		case INVITE:
			String username = findPlayers.getText();
			if(username.isEmpty())
				return;
		
			PlayerTeamIndividual recipient = PlayerTeamIndividual.get(username);
			PlayerTeamInvitation newInvite = new PlayerTeamInvitation(recipient.playerName, team.id, PlayerTeamInvitation.FROM);
			
			if (Contained.teamInvitations.indexOf(newInvite) == -1) {
				Contained.teamInvitations.add(newInvite);

				//Send message to invited player to let them know they got an invitation.
				@SuppressWarnings("rawtypes")
				List onlinePlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
				for (Object o : onlinePlayers) {
					if (o instanceof EntityPlayer) {
						EntityPlayer onlinePlayer = (EntityPlayer)o;
						PlayerTeamIndividual onlineData = PlayerTeamIndividual.get(onlinePlayer);
						if (onlinePlayer.getDisplayName().toLowerCase().equals(newInvite.playerName.toLowerCase())) {
							PlayerTeam teamData = PlayerTeam.get(newInvite.teamID);
							onlinePlayer.addChatComponentMessage(new ChatComponentText("[*] "+teamData.getFormatCode()+"§l"+teamData.displayName+"§r would like you to join their group."));
						}
					}
				}		
			}
			
			DataLogger.insertInvitePlayer("debugmode", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), username, Util.getDate());
			break;
		case KICK:
			teammate = teamPlayers.getText();
			if(teammate.isEmpty())
				return;
			
			PlayerTeamIndividual toKick = PlayerTeamIndividual.get(teammate);
			team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+toKick.playerName+" has been kicked from the team.");
			toKick.leaveTeam();
			
			DataLogger.insertKickPlayer("debugmode", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), toKick.playerName, Util.getDate());
			break;
		case PROMOTE:
			teammate = teamPlayers.getText();
			if(teammate.isEmpty())
				return;
			
			PlayerTeamIndividual toPromote = PlayerTeamIndividual.get(teammate);
			ErrorCase.Error resultP = toPromote.promote();
			
			DataLogger.insertPromoteTeamPlayer("debugmode", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), team.displayName, teammate, Util.getDate());
			
			break;
		case DEMOTE:
			ErrorCase.Error result = pdata.demote();
			if(result == ErrorCase.Error.CANNOT_DEMOTE)
				System.out.println("You're the only member of this group... you must stay the leader."); //Render This
			else{
				team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+pdata.playerName+" is no longer a team leader.");
				ExtendedPlayer leader = ExtendedPlayer.get(this.gui.mc.thePlayer);
				
				leader.guild = GuiGuild.TEAM_PLAYER;
				this.gui.guildStatus = GuiGuild.TEAM_PLAYER;
				this.gui.update = true;
				
				DataLogger.insertDemoteTeamPlayer("debugmode", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), team.displayName, pdata.playerName, Util.getDate());
			}
			break;
		}
	}
	
	public void render(){
		this.gui.drawDefaultBackground();
		tabPane.render();
		
		//Settings Buttons
		save.visible = false;
		reset.visible = false;
		disband.visible = false;
		teamColor.visible = false;
		invite.visible = false;
		//Team Buttons
		demote.visible = false;
		promote.visible = false;
		kick.visible = false;
		
		switch(tabPane.selectedTab){
		case 0: // Actions
			renderActions();
			break;
		case 1: // Team Members
			renderTeam();
			break;
		case 2: // Settings
			renderSettings();
			break;
		}
	}
	
	private void renderActions(){
		renderFont(0, -100, "Guild: Actions", Color.WHITE);
	}
	
	private void renderTeam(){
		renderFont(0, -100, "Guild: Team", Color.WHITE);
		
		teamPlayers.render();
		
		demote.visible = true;
		promote.visible = true;
		kick.visible = true;
	}
	
	private void renderSettings(){
		renderFont(0, -100, "Guild: Settings", Color.WHITE);
		
		findPlayers.render();
		
		save.visible = true;
		reset.visible = true;
		disband.visible = true;
		teamColor.visible = true;
		invite.visible = true;
		if(team.numMembers() == Contained.configs.maxTeamSize){
			invite.enabled = false;
			renderFont(0, -80, "Max Players Reached", Color.YELLOW);
		}
		teamName.drawTextBox();
		renderFont(-35, 60, teamUpdateStatus, teamUpdateColor);
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(text, 
				(this.gui.width - this.gui.mc.fontRenderer.getStringWidth(text))/2 + x,
				(this.gui.height/2) + y, color.hashCode());
	}
}
