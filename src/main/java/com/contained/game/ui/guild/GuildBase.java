package com.contained.game.ui.guild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.network.ServerPacketHandler;
import com.contained.game.ui.GuiGuild;
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

// TODO: Log Data Events For Creating or Joining Guild, Fix Create Team

public class GuildBase {
	private final int JOIN = 0;
	private final int DECLINE = 1;
	private final int NEXT = 2;
	private final int PREV = 3;
	private final int CREATE = 4;
	private final int TEAM_COLOR = 5;
	
	private ArrayList<String> invites;
	private int currentInvite = 0;
	
	private GuiGuild gui;
	
	private GuiButton join, decline, next, prev;
	private GuiButton create;
	private IconButton teamColor;
	
	private GuiTextField teamName;
	
	private String statusInfo = "";
	private Color statusColor = Color.WHITE;
	
	private String teamStatus = "";
	private Color teamColorStatus = Color.WHITE;
	
	private PlayerTeam newTeam;
	private int currentCol = 0;
	
	protected List buttonList = new ArrayList();
	
	public GuildBase(GuiGuild gui){
		this.gui = gui;
		getInvites();
		
		int x = (this.gui.width/2);
		int y = (this.gui.height/2);
		
		teamName = new GuiTextField(this.gui.mc.fontRenderer, x - 120, y-10, 100, 20);
		teamName.setTextColor(Color.WHITE.hashCode());
        teamName.setEnableBackgroundDrawing(true);
        teamName.setMaxStringLength(25);
        teamName.setText("Team Name");
        teamName.setFocused(false);
        
        newTeam = new PlayerTeam("", 0);
        newTeam.randomColor();
	}
	
	public List getButtonList(){
		int x = (this.gui.width/2);
		int y = this.gui.height/2;
		
		this.buttonList.add(this.join = new GuiButton(JOIN, x + 80, y-50, 30, 20, "Join"));
		this.buttonList.add(this.decline = new GuiButton(DECLINE, x+30, y-50, 40, 20, "Decline"));
		this.buttonList.add(this.next = new GuiButton(NEXT, x+90, y-75, 20, 20, "->"));
		this.buttonList.add(this.prev = new GuiButton(PREV, x-120, y-75, 20, 20 ,"<-"));
		
		this.buttonList.add(this.create = new GuiButton(CREATE, x+70, y+30, 40, 20, "Create"));
		this.buttonList.add(this.teamColor = new IconButton(TEAM_COLOR, x-10, y-10, 20, 20, PlayerTeam.rgbColors[currentCol]));
		
		if(this.invites.isEmpty()){
			this.join.enabled = false;
			this.decline.enabled = false;
			this.next.enabled = false;
			this.prev.enabled = false;
		}
		
		return buttonList;
	}
	
	public void update(){
		getInvites();
		
		if(this.invites.isEmpty()){
			this.join.enabled = false;
			this.decline.enabled = false;
			this.next.enabled = false;
			this.prev.enabled = false;
		}
	}
	
	public void mouseMovedOrUp(int x, int y, int button){
		
	}
	
	public void mouseClicked(int i , int j, int k){
		teamName.mouseClicked(i, j, k);
	}
	
	public void keyTyped(char c, int i){
		if(teamName.isFocused()){
			teamName.textboxKeyTyped(c, i);
		}
	}
	
	public void render(){
		int x = this.gui.width/2;
		int y = this.gui.height/2;
		String invite = (invites.isEmpty()) ? "Zero Invites" : invites.get(currentInvite);
		
		renderFont(0, -100, "Guild", Color.WHITE);
		renderFont(-105, -90, "Join", Color.WHITE);
		renderFont(-65, -90, "(" + currentInvite + "/" + invites.size() + ")", Color.YELLOW);
		renderFont(-100, -30, "Create", Color.WHITE);
		renderFont(0, -70, invite, Color.WHITE);
		renderFont(-50, -45, statusInfo, statusColor);
		renderFont(-50, 20, teamStatus, teamColorStatus);
		
		teamName.drawTextBox();
	}
	
	public void actionPerformed(GuiButton button){
		switch(button.id){
		case JOIN:
			if(!invites.isEmpty()){
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(this.gui.mc.thePlayer);
				ArrayList<PlayerTeamInvitation> myInvites
								= PlayerTeamInvitation.getInvitations(pdata);
				
				PlayerTeam matchedTeam = null;
				PlayerTeamIndividual matchedPlayer = null;
				
				String inviteName = invites.get(currentInvite);
				for(PlayerTeamInvitation inv : myInvites) {
					PlayerTeam teamData = PlayerTeam.get(inv.teamID);
					if (teamData != null && ((pdata.isLeader && inv.playerName.toLowerCase().equals(inviteName))
						|| (!pdata.isLeader && teamData.displayName.toLowerCase().equals(inviteName)))) 
					{
						matchedTeam = teamData;
						matchedPlayer = PlayerTeamIndividual.get(inv.playerName);
						break;
					}
				}
				
				//Try to join the player to the team.
				ErrorCase.Error result = matchedPlayer.joinTeam(matchedTeam.id);
				if (result == ErrorCase.Error.NOT_EXISTS){
					statusInfo = "Team No Longer Exists";
					statusColor = Color.RED;
				}else if (result == ErrorCase.Error.TEAM_FULL){
					statusInfo = "Team Is Already Full";
					statusColor = Color.RED;
				} else if (result == ErrorCase.Error.NONE) {
					matchedTeam.sendMessageToTeam(matchedTeam.getFormatCode()
							+ "[NOTICE] §l"+ matchedPlayer.playerName + "§r"
							+ matchedTeam.getFormatCode() + " has joined the team!");
						
					//Accept successful, remove invitation.
					ArrayList<PlayerTeamInvitation> toRemove = new ArrayList<PlayerTeamInvitation>();
					toRemove.add(new PlayerTeamInvitation(matchedPlayer.playerName, matchedTeam.id, PlayerTeamInvitation.NEITHER));
					Contained.teamInvitations.removeAll(toRemove);
					
					statusInfo = "Joined Team";
					statusColor = Color.GREEN;
					
					ExtendedPlayer properties = ExtendedPlayer.get(this.gui.mc.thePlayer);
					properties.guild = GuiGuild.TEAM_PLAYER;
					
					this.gui.guildStatus = GuiGuild.TEAM_PLAYER;
					this.gui.update = true;
					
					PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.UPDATE_GUILD_STATUS);
					packet.writeInt(GuiGuild.TEAM_PLAYER);
					ServerPacketHandler.sendToServer(packet.toPacket());
					
					DataLogger.insertJoinTeam("debugmode", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), matchedTeam.displayName, Util.getDate());
				}
			}
			break;
		case DECLINE:
			if(!invites.isEmpty()){
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(this.gui.mc.thePlayer);
				ArrayList<PlayerTeamInvitation> myInvites
								= PlayerTeamInvitation.getInvitations(pdata);
				
				String inviteName = invites.get(currentInvite);
				PlayerTeamInvitation probe = null;
				if (pdata.isLeader)
					probe = new PlayerTeamInvitation(inviteName, "", PlayerTeamInvitation.TO);
				else {
					for (PlayerTeam t : Contained.teamData) {
						if (t.displayName.toLowerCase().equals(inviteName)) {
							probe = new PlayerTeamInvitation("", t.id, PlayerTeamInvitation.FROM);
							break;
						}
					}
				}
				
				//Try to remove the invitation(s) from the invitations list.
				ArrayList<PlayerTeamInvitation> toRemove = new ArrayList<PlayerTeamInvitation>();
				toRemove.add(probe);
				int beforeSize = Contained.teamInvitations.size();
				Contained.teamInvitations.removeAll(toRemove);
				int afterSize = Contained.teamInvitations.size();
				
				if (beforeSize != afterSize){
					statusInfo = "Invitation has been removed.";
					statusColor = Color.GREEN;
					if(currentInvite < invites.size()){
						currentInvite++;
					}else{
						currentInvite = 0;
					}
				}
			}
			break;
		case NEXT:
			if(currentInvite < invites.size())
				currentInvite++;
			else
				currentInvite = 0;
			break;
		case PREV:
			if(currentInvite > 0)
				currentInvite--;
			else
				currentInvite = invites.size();
			break;
		case CREATE:
			String name = teamName.getText();
			if(!name.isEmpty() && 
					name.compareTo("Team Name") != 0){
				EntityPlayer player = (EntityPlayer) this.gui.mc.thePlayer;
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
				
				if (pdata.teamID == null) {
					boolean allowedName = true;
					for(PlayerTeam t : Contained.teamData) {
						if (t.displayName.toLowerCase().equals(name.toLowerCase())) {
							allowedName = false;
							break;
						}
					}
					
					if (allowedName) {
						newTeam.displayName = name;
						newTeam.colorID = currentCol;
						Contained.teamData.add(newTeam);
						System.out.println(pdata.joinTeam(newTeam.id).toString());
						
						pdata.isLeader = true;
						teamStatus = "Team Successfully Created";
						teamColorStatus = Color.GREEN;
						ClientPacketHandler.packetSyncTeams(Contained.teamData).sendToClients();
						
						ExtendedPlayer properties = ExtendedPlayer.get(player);
						properties.guild = GuiGuild.LEADER;
						gui.update = true;
						gui.guildStatus = GuiGuild.LEADER;
						
						PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.UPDATE_GUILD_STATUS);
						packet.writeInt(GuiGuild.LEADER);
						ServerPacketHandler.sendToServer(packet.toPacket());
						
						DataLogger.insertCreateTeam("debugMode", pdata.playerName, this.gui.mc.theWorld.provider.getDimensionName(), newTeam.displayName, Util.getDate());
					} else {
						teamStatus = "Team Name Already In-Use";
						teamColorStatus = Color.RED;
					}
				}
			}else{
				teamStatus = "Enter Team Name";
				teamColorStatus = Color.RED;
			}
			break;
		case TEAM_COLOR:
			if(currentCol < PlayerTeam.rgbColors.length-1)
				currentCol++;
			else
				currentCol = 0;
			
			teamColor.color = PlayerTeam.rgbColors[currentCol];
			break;
		}
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(text, 
				(this.gui.width - this.gui.mc.fontRenderer.getStringWidth(text))/2 + x,
				(this.gui.height/2) + y, color.hashCode());
	}
	
	private void getInvites(){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(this.gui.mc.thePlayer);
		ArrayList<PlayerTeamInvitation> myInvites
						= PlayerTeamInvitation.getInvitations(pdata);
		
		invites = new ArrayList(myInvites.size());
		for(int i=0; i<myInvites.size(); i++) {
			PlayerTeam teamData = PlayerTeam.get(myInvites.get(i).teamID);
				if (teamData != null)
					invites.add(teamData.displayName);
		}
	}
	
	public void reset(){
		this.gui.setButtonList(this.getButtonList());
		
		getInvites();
		
		currentCol = 0;
		teamColor.color = PlayerTeam.rgbColors[currentCol];
		
		teamName.setText("Team Name");
		newTeam = new PlayerTeam("", 0);
        newTeam.randomColor();
        
        statusInfo = "";
    	statusColor = Color.WHITE;
    	
    	teamStatus = "";
    	teamColorStatus = Color.WHITE;
	}
}
