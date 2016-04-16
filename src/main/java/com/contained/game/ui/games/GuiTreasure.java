package com.contained.game.ui.games;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GuiTreasure extends Gui {
	private Minecraft mc;
	private ResourceLocation img;
	
	private int x,y;
	
	private Rectangle zero = new Rectangle(0,0,32,32);
	private Rectangle one = new Rectangle(32,0,32,32);
	private Rectangle two = new Rectangle(64,0,32,32);
	private Rectangle three = new Rectangle(96,0,32,32);
	private Rectangle four = new Rectangle(128,0,32,32);
	private Rectangle five = new Rectangle(160,0,32,32);
	private Rectangle six = new Rectangle(192,0,32,32);
	private Rectangle seven = new Rectangle(224,0,32,32);
	private Rectangle eight = new Rectangle(0,32,32,32);
	private Rectangle nine = new Rectangle(32,32,32,32);
	private Rectangle colon = new Rectangle(64,32,32,32);
	private Rectangle score = new Rectangle(96,32,128,32);
	private Rectangle treasures = new Rectangle(0,96,240,32);
	
	public GuiTreasure(Minecraft mc){
		super();
		this.mc = mc;
		this.img = new ResourceLocation(Resources.MOD_ID, "textures/gui/games");
	}
	
	@SubscribeEvent
	public void renderAllHUD(RenderGameOverlayEvent.Pre event){
		if(event.type.equals(ElementType.ALL)){
			ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
			if(properties.gameMode != Resources.TREASURE_MODE)
				return;
			
			PlayerMiniGame game = PlayerMiniGame.get(mc.thePlayer.dimension);
			if(game == null)
				return;
			
			int teamID = game.getTeamID(mc.thePlayer.getDisplayName());
			if(teamID == -1)
				return;
			
			mc.entityRenderer.setupOverlayRendering();
			
			x = event.resolution.getScaledWidth()/2;
			y = event.resolution.getScaledHeight()/2;
			
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);;
			GL11.glDisable(GL11.GL_LIGHTING);     
			this.mc.renderEngine.bindTexture(img);
			//GameMode
			this.drawTexturedModalRect(x-(treasures.width/2), y-170, treasures.x, treasures.y, treasures.width, treasures.height);
			
			//Score
			this.drawTexturedModalRect(x-(score.width/2)-250, y-170, score.x, score.y, score.width, score.height);
			String scoreVal = Integer.toString(Contained.gameScores[game.getGameDimension()][teamID]);
			int offset = 0;
			for(int i = 0; i < scoreVal.length(); i++)
				offset += renderNumber(Character.toString(scoreVal.charAt(i)), x-160+offset, y-170);
			
			//Timer
			String time = Util.getTimestamp(Contained.timeLeft[0]);
			offset = 0;
			for(int i = 0; i < time.length(); i++)
				offset += renderNumber(Character.toString(time.charAt(i)), x+180+offset, y-170);
			
			GL11.glPopMatrix();
		}
	}
	
	private int renderNumber(String number, int x, int y){
		int num;
		if(Character.isDigit(number.charAt(0)))
			num = Integer.parseInt(number);
		else
			num = (int) number.charAt(0);
		
		switch(num){
		case 58:
			this.drawTexturedModalRect(x-(colon.width/2), y, colon.x, colon.y, colon.width, colon.height);
			return colon.width/4;
		case 0:
			this.drawTexturedModalRect(x-(zero.width/2), y, zero.x, zero.y, zero.width, zero.height);
			return zero.width/2;
		case 1:
			this.drawTexturedModalRect(x-(one.width/2), y, one.x, one.y, one.width, one.height);
			return one.width/2;
		case 2:
			this.drawTexturedModalRect(x-(two.width/2), y, two.x, two.y, two.width, two.height);
			return two.width/2;
		case 3:
			this.drawTexturedModalRect(x-(three.width/2), y, three.x, three.y, three.width, three.height);
			return three.width/2;
		case 4:
			this.drawTexturedModalRect(x-(four.width/2), y, four.x, four.y, four.width, four.height);
			return four.width/2;
		case 5:
			this.drawTexturedModalRect(x-(five.width/2), y, five.x, five.y, five.width, five.height);
			return five.width/2;
		case 6:
			this.drawTexturedModalRect(x-(six.width/2), y, six.x, six.y, six.width, six.height);
			return six.width/2;
		case 7:
			this.drawTexturedModalRect(x-(seven.width/2), y, seven.x, seven.y, seven.width, seven.height);
			return seven.width/2;
		case 8:
			this.drawTexturedModalRect(x-(eight.width/2), y, eight.x, eight.y, eight.width, eight.height);
			return eight.width/2;
		case 9:
			this.drawTexturedModalRect(x-(nine.width/2), y, nine.x, nine.y, nine.width, nine.height);
			return nine.width/2;
		}
		
		return 0;
	}
}