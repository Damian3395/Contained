package com.contained.game.ui.games;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.BlockCoord;

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
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GuiTreasure extends Gui {
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
			if((properties.gameMode != Resources.TREASURE || !properties.inGame())
					&& !MiniGameUtil.isTreasure(mc.thePlayer.dimension))
				
			if(properties.gameMode != Resources.TREASURE)
				return;
			
			PlayerMiniGame game = PlayerMiniGame.get(mc.thePlayer.dimension);
			if(game == null)
				return;
			
			int teamID = game.getTeamID(PlayerTeamIndividual.get(mc.thePlayer));
			if(teamID == -1)
				return;
					
			//renderFancy(event, Contained.gameScores[game.getGameDimension()][teamID]);
			renderSimple(event, Contained.gameScores[game.getGameDimension()][teamID]);
		}
	}
	
	private void renderFancy(RenderGameOverlayEvent.Pre event, int theScore) {
		int mar = 5;
		int sw = mc.displayWidth;
		RenderUtil.setupNonScaledOverlayRendering();
		
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);     
		this.mc.renderEngine.bindTexture(img);
		
		//Game Mode
		this.drawTexturedModalRect(sw/2-(treasures.width/2), mar, treasures.x, treasures.y, treasures.width, treasures.height);
		
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
		
		GL11.glPopMatrix();		
	}
	
	private void renderSimple(RenderGameOverlayEvent.Pre event, int theScore) {
		String mode = "§cTreasure Hunt";
		String timeLeft = "Time Left: "+Util.getTimestamp(Contained.timeLeft[0]);
		String teamScore = Integer.toString(theScore);
		
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		mc.entityRenderer.setupOverlayRendering();
		int mar = 5;
		int sw = event.resolution.getScaledWidth()-mar;
		
		mc.fontRenderer.drawStringWithShadow(mode, sw-fr.getStringWidth(mode), mar, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow(timeLeft, sw-fr.getStringWidth(timeLeft), mar+12, 0xFFFFFF);
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
	
	@SubscribeEvent
	public void onRenderScreen(RenderWorldLastEvent ev) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.theWorld != null) {
			renderChests(RenderUtil.getOriginX(mc, ev.partialTicks),
						 RenderUtil.getOriginY(mc, ev.partialTicks),
						 RenderUtil.getOriginZ(mc, ev.partialTicks), ev.partialTicks);
		}
	}
	
	public void renderChests(float ox, float oy, float oz, float dt) {
		Minecraft mc = Minecraft.getMinecraft();
		
		ArrayList<BlockCoord> chestPositions = Contained.getActiveTreasures(0);
	
		for (BlockCoord point : chestPositions) {	
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_BLEND);
			//GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
			RenderUtil.drawPillar(point.x, point.z, 0.4f, Color.yellow.hashCode(),
									RenderUtil.getOriginX(mc, dt),
									RenderUtil.getOriginY(mc, dt),
									RenderUtil.getOriginZ(mc, dt));
			RenderUtil.drawPillar(point.x, point.z, 0.15f, Color.white.hashCode(),
									RenderUtil.getOriginX(mc, dt),
									RenderUtil.getOriginY(mc, dt),
									RenderUtil.getOriginZ(mc, dt));
			int dist = (int)Util.euclidDist((float)mc.thePlayer.posX, (float)mc.thePlayer.posY, (float)mc.thePlayer.posZ, 
											(float)point.x, (float)point.y, (float)point.z);
			RenderUtil.drawClampedWorldLabel("["+dist+"m]", point.x, point.y+1, point.z, 50,
					RenderUtil.getOriginX(mc, dt),
					RenderUtil.getOriginY(mc, dt),
					RenderUtil.getOriginZ(mc, dt));
		}
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);	
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}