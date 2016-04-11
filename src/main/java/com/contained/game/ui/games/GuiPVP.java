package com.contained.game.ui.games;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class GuiPVP extends Gui {
	private Minecraft mc;
	private ResourceLocation img;
	
	private int x,y;
	
	private Rectangle zero = new Rectangle(0,0,32,32);
	private Rectangle one = new Rectangle(32,0,32,32);
	private Rectangle two = new Rectangle(64,0,32,32);
	private Rectangle three = new Rectangle(96,0,32,32);
	private Rectangle four = new Rectangle(128,0,32,32);
	private Rectangle five = new Rectangle(160,0,32,32);
	private Rectangle six = new Rectangle(182,0,32,32);
	private Rectangle seven = new Rectangle(224,0,32,32);
	private Rectangle eight = new Rectangle(0,32,32,32);
	private Rectangle nine = new Rectangle(32,32,32,32);
	private Rectangle colon = new Rectangle(64,32,32,32);
	private Rectangle score = new Rectangle(96,32,128,32);
	private Rectangle pvp = new Rectangle(0,64,192,32);
	//private Rectangle treasures = new Rectangle(0,96,240,32);
	//private Rectangle chests_left = new Rectangle(0,160,240,32);
	private Rectangle left_life = new Rectangle(0,128,16,32);
	private Rectangle right_life = new Rectangle(16,128,16,32);
	
	public GuiPVP(Minecraft mc){
		super();
		this.mc = mc;
		this.img = new ResourceLocation(Resources.MOD_ID, "textures/gui/games");
	}
	
	@SubscribeEvent
	public void renderPVPOverlay(RenderGameOverlayEvent event){
		ExtendedPlayer properties = ExtendedPlayer.get(mc.thePlayer);
		if(properties.gameMode != Resources.PVP_MODE)
			return;
		
		x = event.resolution.getScaledWidth()/2;
		y = event.resolution.getScaledHeight()/2;
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.mc.renderEngine.bindTexture(img);
		this.drawTexturedModalRect(x-(pvp.width/2), y-50, pvp.x, pvp.y, pvp.width, pvp.height);
	}
}
