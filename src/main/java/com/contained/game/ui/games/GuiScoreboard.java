package com.contained.game.ui.games;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.network.ServerPacketHandlerUtil;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiScoreboard extends GuiScreen {
	public static boolean updated;
	public static HashMap<String, Integer> kills; 
	public static HashMap<String, Integer> deaths;
	public static HashMap<String, Integer> treasures;
	
	private int x,y;
	private final int CLOSE = 0;
	
	private GuiButton close;
	
	private ResourceLocation img;
	
	@Override
	public void initGui(){
		this.updated = false;
		
		kills = new HashMap<String, Integer>();
		deaths = new HashMap<String, Integer>();
		treasures = new HashMap<String, Integer>();
		
		if(MiniGameUtil.isPvP(mc.thePlayer.dimension)) {
			PacketCustom pvpPacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.LEADERBOARD_PVP);
			ServerPacketHandlerUtil.sendToServer(pvpPacket.toPacket());
		}else if(MiniGameUtil.isTreasure(mc.thePlayer.dimension)){
			PacketCustom treasurePacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.LEADERBOARD_TREASURE);
			ServerPacketHandlerUtil.sendToServer(treasurePacket.toPacket());
		}
		
		x = this.width/2;
		y = this.height/2;
		this.buttonList.add(close = new GuiButton(CLOSE, x+110, y+95, 60, 20, "Close"));
		
		this.img = new ResourceLocation(Resources.MOD_ID, "textures/gui/leader.png");
	}
	
	@Override
	public void updateScreen(){
		
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){
		this.drawDefaultBackground();
		
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);   
		
		this.mc.getTextureManager().bindTexture(img);
		this.drawTexturedModalRect(x-115, y-120, 0, 0, 256, 32);
	
		GL11.glPopMatrix();
		
		if(this.updated){
			int yOffset = 20;
			if(MiniGameUtil.isPvP(mc.thePlayer.dimension)){
				Iterator<HashMap.Entry<String, Integer>> iterator = kills.entrySet().iterator();
				int index = 0;
				while(iterator.hasNext()){
					HashMap.Entry<String, Integer> entry = iterator.next();
					String user = entry.getKey();
					int userKills = entry.getValue();
					int userDeaths = (int) deaths.get(user);
					double ratio = 0.0;
					if(userDeaths != 0)
						ratio = new BigDecimal(((double) userKills) / ((double) userDeaths)).setScale(2, RoundingMode.HALF_UP).doubleValue();
					else if(userKills != 0)
						ratio = 1.0;
					this.renderCenterFont(x, y-80, (index+1) + ". " + user + ": Kills= " + userKills + " Deaths= " + userDeaths + " Ratio= " + ratio, Color.WHITE);
					index++;
					//+(index * yOffset)
				}
			}else if(MiniGameUtil.isTreasure(mc.thePlayer.dimension)){
				Iterator<HashMap.Entry<String, Integer>> iterator = treasures.entrySet().iterator();
				int index = 0;
				while(iterator.hasNext()){
					HashMap.Entry<String, Integer> entry = iterator.next();
					String user = entry.getKey();
					int userTreasures = entry.getValue();
					this.renderCenterFont(x,  y-80, (index+1) + ". " + user + ": Treasures=" + userTreasures, Color.WHITE);
					index++;
					//+(index * yOffset)
				}
			}
		}
		
		super.drawScreen(w, h, ticks);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case CLOSE:
			this.mc.thePlayer.closeScreen();
		break;
		}
	}
	
	@Override
	public void onGuiClosed(){
		
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	private void renderCenterFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, 
				(x - this.mc.fontRenderer.getStringWidth(text)/2),
				y, color.hashCode());
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawString(text, x, y, color.hashCode());
	}
}
