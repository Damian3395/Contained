package com.contained.game.ui.games;

import java.awt.Color;
import java.util.ArrayList;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ServerPacketHandlerUtil;
import com.contained.game.ui.components.Container;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class RevivePlayer extends GuiScreen {
	private final int CANCEL = 0;
	
	private int x,y;
	private Container background;
	
	private String miniGameTeam;
	private ArrayList<String> teamPlayers;
	
	private GuiButton[] revive;
	private GuiButton cancel;
	
	public RevivePlayer(){}
	
	@Override
	public void initGui(){
		ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
		if(properties.inGame() && properties.gameMode != Resources.OVERWORLD){
			mc.displayGuiScreen(null);
			return;
		}
		
		x = this.width/2;
		y = this.height/2;
		
		background = new Container(x-128, y-88, 256, 176, "ui.png", this);
		
		teamPlayers = new ArrayList<String>();
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(mc.thePlayer);
		miniGameTeam = pdata.teamID;
		if (miniGameTeam == null) {
			mc.displayGuiScreen(null);
			return;			
		}
		
		int index = 1;
		int yOffset = 0;
		for(PlayerTeamIndividual player : Contained.teamMemberData)
			if(player.teamID.equals(miniGameTeam)){
				teamPlayers.add(player.playerName);
				this.buttonList.add(new GuiButton(index, x-50, y+yOffset-5, 100, 20, player.playerName));
				yOffset+=25;
				index++;
			}
		
		this.buttonList.add(cancel = new GuiButton(CANCEL, x+100, y+128, 60, 20, "Cancel"));
	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){	
		background.render();
		super.drawScreen(w, h, ticks);
	}
	

	@Override
	protected void keyTyped(char c, int i){
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(button.id == CANCEL){
			this.mc.displayGuiScreen(null);
		}else{
			PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.REVIVE_PLAYER);
			packet.writeString(teamPlayers.get(button.id-1));
			ServerPacketHandlerUtil.sendToServer(packet.toPacket());
			
			mc.displayGuiScreen(null);
		}
	}
	
	private void renderCenterFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, 
				(x - this.mc.fontRenderer.getStringWidth(text)/2),
				y, color.hashCode());
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, x, y, color.hashCode());
	}
	
	@Override
	public void onGuiClosed(){

	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
}
