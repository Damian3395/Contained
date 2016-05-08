package com.contained.game.ui.games;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

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
		
		if(MiniGameUtil.isPvP(mc.thePlayer.dimension)) {
			
		}else if(MiniGameUtil.isTreasure(mc.thePlayer.dimension)){
			
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
		this.drawTexturedModalRect(x-110, y-120, 0, 0, 256, 32);
	
		GL11.glPopMatrix();
		
		if(this.updated){
			int yOffset = 20;
			if(MiniGameUtil.isPvP(mc.thePlayer.dimension)){
				Iterator<Entry<String, Integer>> iterator = kills.entrySet().iterator();
				int index = 0;
				while(iterator.hasNext()){
					Entry<String, Integer> entry = iterator.next();
					String user = entry.getKey();
					int userKills = entry.getValue();
					int userDeaths = (int) deaths.get(user);
					double ratio = ((double) userKills) / ((double) userDeaths);
					DecimalFormat f = new DecimalFormat("##.00");
					this.renderFont(x-60, y-60+(index * yOffset), user + ": Kills=" + userKills + " Deaths=" + userDeaths + " Ratio=" + f.format(ratio), Color.WHITE);
					index++;
				}
			}else if(MiniGameUtil.isTreasure(mc.thePlayer.dimension)){
				Iterator<Entry<String, Integer>> iterator = deaths.entrySet().iterator();
				int index = 0;
				while(iterator.hasNext()){
					Entry<String, Integer> entry = iterator.next();
					String user = entry.getKey();
					int userTreasures = entry.getValue();
					this.renderFont(x-60,  y-60+(index * yOffset), user + ": Treasures=" + userTreasures, Color.WHITE);
					index++;
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
