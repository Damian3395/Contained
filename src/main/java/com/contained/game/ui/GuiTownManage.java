package com.contained.game.ui;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.contained.game.util.Resources;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiTownManage extends GuiScreen {

	private static final ResourceLocation bg = new ResourceLocation(Resources.MOD_ID, "textures/gui/townhall.png");
	private static final ResourceLocation tabs = new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tabs.png");
	private int numTabs = 2;
	private int selectedTab = 0;
	
	private Rectangle bBg          = new Rectangle(0, 0, 175, 138);
	private Rectangle bUnselRect   = new Rectangle(0, 139, 158, 18);
	private Rectangle bSelRect     = new Rectangle(0, 158, 158, 18);
	private Rectangle bUnselTab    = new Rectangle(0, 0, 27, 32);
	private Rectangle bSelTabFirst = new Rectangle(0, 32, 27, 32);
	private Rectangle bSelTabMid   = new Rectangle(28, 32, 27, 32);
	private Rectangle bSelTabLast  = new Rectangle(140, 32, 27, 32);
	
	private int guiX = 0;
	private int guiY = 0;
	
	private String[] tabTitles = {"Purchase Territory", "NPC Villagers"};
	
	@Override
	public void initGui () {
		super.initGui();
		guiX = (this.width-bBg.width)/2;
		guiY = (this.height-bBg.height)/2;
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks) {
		FontRenderer fr = this.mc.fontRenderer;
		
		// Background
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(bg);
		this.drawTexturedModalRect(guiX, guiY, bBg.x, bBg.y, bBg.width, bBg.height);
		
		// Title Caption
		fr.drawString(tabTitles[selectedTab], guiX+bBg.width/2-fr.getStringWidth(tabTitles[selectedTab])/2, guiY+10, 0x000000);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		// Tabs
		this.mc.getTextureManager().bindTexture(tabs);
		for(int i=0; i<numTabs; i++) {
			Rectangle bound;
			if (i == selectedTab) {
				if (i == 0)
					bound = bSelTabFirst;
				else if (i == numTabs-1)
					bound = bSelTabLast;
				else
					bound = bSelTabMid;
			}
			else
				bound = bUnselTab;
			
			this.drawTexturedModalRect(guiX+bound.width*i, guiY-bound.height, bound.x, bound.y, bound.width, bound.height);
		}
	}
	
	@Override
	protected void mouseClicked(int x, int y, int par3) {
		super.mouseClicked(x, y, par3);
		
		// Clicking tabs
		for(int i=0; i<numTabs; i++) {
			Rectangle bound = bSelTabFirst;
			if (x >= guiX+bound.width*i && x < guiX+bound.width*(i+1)
					&& y >= guiY-bound.height && y <= guiY) {
				selectedTab = i;
				break;
			}
		}
	}
	
}
