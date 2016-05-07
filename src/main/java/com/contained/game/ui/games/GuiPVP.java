package com.contained.game.ui.games;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.RenderUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GuiPVP extends Gui {
	private Minecraft mc;
	private ResourceLocation img;
	
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
	private Rectangle pvp = new Rectangle(0,64,192,32);
	private Rectangle left_life = new Rectangle(0,128,16,32);
	private Rectangle right_life = new Rectangle(16,128,16,32);
	
	public GuiPVP(Minecraft mc){
		super();
		this.mc = mc;
		this.img = new ResourceLocation(Resources.MOD_ID, "textures/gui/games.png");
	}
		
	@SubscribeEvent
	public void renderPVPHUD(RenderGameOverlayEvent.Pre event){
		if(event.type.equals(ElementType.ALL)){
			ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
			if(!MiniGameUtil.isPvP(mc.thePlayer.dimension)){
				//System.out.println("Not MiniGame Dimension");
				return;
			}
			
			PlayerMiniGame game = PlayerMiniGame.get(mc.thePlayer.dimension);

			if(game == null){
				//System.out.println("MiniGame Not Found");
				return;
			}
			
			int teamID = game.getTeamID(PlayerTeamIndividual.get(mc.thePlayer));
			if(teamID == -1){
				//System.out.println("MiniGame Team Not Found");
				return;
			}
			
			renderFancy(event, Contained.gameScores[game.getGameDimension()][teamID], properties.lives);
			//renderSimple(event, Contained.gameScores[game.getGameDimension()][teamID], properties.lives);		
		}
	}
	
	private void renderFancy(RenderGameOverlayEvent.Pre event, int theScore, int theLives) {
		int mar = 5;
		int sw = mc.displayWidth;
		int sh = mc.displayHeight;
		RenderUtil.setupNonScaledOverlayRendering();
		
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);     
		this.mc.renderEngine.bindTexture(img);
		
		//GameMode
		this.drawTexturedModalRect(sw/2-(pvp.width/2), mar, pvp.x, pvp.y, pvp.width, pvp.height);
		
		//Score
		this.drawTexturedModalRect(mar, mar, score.x, score.y, score.width, score.height);
		String scoreVal = Integer.toString(theScore);
		int offset = 0;
		for(int i = scoreVal.length()-1; i >= 0; i--)
			offset += renderNumber(Character.toString(scoreVal.charAt(i)), mar+score.width+offset, mar, true);
		
		//Timer
		String time = Util.getTimestamp(Contained.timeLeft[0]);
		offset = 0;
		for(int i = time.length()-1; i >= 0; i--)
			offset -= renderNumber(Character.toString(time.charAt(i)), sw-mar+offset, mar, false);
		
		//Lives
		for(int i = 0; i < theLives; i++){
			if(i % 2 ==0){
				this.drawTexturedModalRect(
						(left_life.width/2)+((i*left_life.width)/2)+(i*2)
						, sh-mar-left_life.height
						, left_life.x, left_life.y, left_life.width, left_life.height);
			} else {
				this.drawTexturedModalRect(
						(right_life.width/2)+((i*right_life.width)/2)+(i*2)
						, sh-mar-right_life.height
						, right_life.x, right_life.y, right_life.width, right_life.height);
			}
		}
		GL11.glPopMatrix();	
	}
	
	private void renderSimple(RenderGameOverlayEvent.Pre event, int theScore, int theLives) {
		String mode = "§cPlayer vs Player";
		String timeLeft = "Time Left: "+Util.getTimestamp(Contained.timeLeft[0]);
		String teamScore = Integer.toString(theScore);
		
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		mc.entityRenderer.setupOverlayRendering();
		int mar = 5;
		int sw = event.resolution.getScaledWidth()-mar;
		int sh = event.resolution.getScaledHeight()-mar;
		
		mc.fontRenderer.drawStringWithShadow(mode, sw-fr.getStringWidth(mode), mar, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow(timeLeft, sw-fr.getStringWidth(timeLeft), mar+12, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow("Lives: "+Integer.toString(theLives), mar, sh-12, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow("Score: "+teamScore, mar, sh-24, 0xFFFFFF);
	}
	
	private int renderNumber(String number, int x, int y, boolean leftAlign){
		int num;
		if(Character.isDigit(number.charAt(0)))
			num = Integer.parseInt(number);
		else
			num = (int) number.charAt(0);
		
		int align = 1;
		if (leftAlign)
			align = -1;
		
		switch(num){
		case 58:
			this.drawTexturedModalRect(x-(colon.width*align), y, colon.x, colon.y, colon.width, colon.height);
			return colon.width;
		case 0:
			this.drawTexturedModalRect(x-(zero.width*align), y, zero.x, zero.y, zero.width, zero.height);
			return zero.width;
		case 1:
			this.drawTexturedModalRect(x-(one.width*align), y, one.x, one.y, one.width, one.height);
			return one.width;
		case 2:
			this.drawTexturedModalRect(x-(two.width*align), y, two.x, two.y, two.width, two.height);
			return two.width;
		case 3:
			this.drawTexturedModalRect(x-(three.width*align), y, three.x, three.y, three.width, three.height);
			return three.width;
		case 4:
			this.drawTexturedModalRect(x-(four.width*align), y, four.x, four.y, four.width, four.height);
			return four.width;
		case 5:
			this.drawTexturedModalRect(x-(five.width*align), y, five.x, five.y, five.width, five.height);
			return five.width;
		case 6:
			this.drawTexturedModalRect(x-(six.width*align), y, six.x, six.y, six.width, six.height);
			return six.width;
		case 7:
			this.drawTexturedModalRect(x-(seven.width*align), y, seven.x, seven.y, seven.width, seven.height);
			return seven.width;
		case 8:
			this.drawTexturedModalRect(x-(eight.width*align), y, eight.x, eight.y, eight.width, eight.height);
			return eight.width;
		case 9:
			this.drawTexturedModalRect(x-(nine.width*align), y, nine.x, nine.y, nine.width, nine.height);
			return nine.width;
		}
		
		return 0;
	}
}
