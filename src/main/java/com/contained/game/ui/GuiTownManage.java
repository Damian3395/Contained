package com.contained.game.ui;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.contained.game.data.Data;
import com.contained.game.item.ItemTerritory;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * GUI that is displayed after interacting with the Town Hall block.
 */
public class GuiTownManage extends GuiScreen {

	private RenderItem renderItem = new RenderItem();
	private RenderBlocks renderBlock = new RenderBlocks();
	private TextureManager texMan;
	private static final ResourceLocation bg = new ResourceLocation(Resources.MOD_ID, "textures/gui/townhall.png");
	private static final ResourceLocation tabs = new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tabs.png");
	private int numTabs = 2;
	private int selectedTab = 0;
	private String teamID;
	
	private Rectangle bBg          = new Rectangle(0, 0, 175, 138);
	private Rectangle bUnselRect   = new Rectangle(0, 139, 158, 19);
	private Rectangle bSelRect     = new Rectangle(0, 158, 158, 19);
	private Rectangle bUnselTab    = new Rectangle(0, 0, 27, 32);
	private Rectangle bSelTabFirst = new Rectangle(0, 32, 27, 32);
	private Rectangle bSelTabMid   = new Rectangle(28, 32, 27, 32);
	private Rectangle bSelTabLast  = new Rectangle(140, 32, 27, 32);
	
	private int guiX;
	private int guiY;
	private int listX;
	private int listY;
	
	private String[] tabTitles = {"Purchase Territory", "NPC Villagers"};
	private int[] listCounts = {2, 0};
	private int maxListCount = 5;
	private ItemStack[][] listItems;
	
	public GuiTownManage(String teamID) {
		if (teamID == null)
			this.teamID = "";
		else
			this.teamID = teamID;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		texMan = this.mc.getTextureManager();
		guiX = (this.width-bBg.width)/2;
		guiY = (this.height-bBg.height)/2;
		listX = guiX+9;
		listY = guiY+29;
		
		listItems = new ItemStack[numTabs][5];
		listItems[0][0] = new ItemStack(ItemTerritory.addTerritory, 1);
		listItems[0][1] = new ItemStack(ItemTerritory.removeTerritory, 1);
		NBTTagCompound itemData = Data.getTagCompound(listItems[0][1]);
		itemData.setString("teamOwner", teamID);
		listItems[0][1].setTagCompound(itemData);
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks) {
		FontRenderer fr = this.mc.fontRenderer;
		
		// Background
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		texMan.bindTexture(bg);
		this.drawTexturedModalRect(guiX, guiY, bBg.x, bBg.y, bBg.width, bBg.height);
		
		// Tabs
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		texMan.bindTexture(tabs);
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
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		// Title Caption
		fr.drawString(tabTitles[selectedTab], guiX+bBg.width/2-fr.getStringWidth(tabTitles[selectedTab])/2, guiY+10, 0x000000);
		
		//Page Contents
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (selectedTab == 0)
			territoryList();
		else if (selectedTab == 1)
			npcList();
	}
	
	private void territoryList() {	
		int page = 0;
		GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        
		for(int i=0; i<listCounts[page]; i++) {
			int x = listX+4;
			int y = listY+2+bSelRect.height*i;
			ItemStack stack = listItems[page][i];
			if (!ForgeHooksClient.renderInventoryItem(renderBlock, texMan, stack, false, 200, x, y)) {
				texMan.bindTexture(TextureMap.locationItemsTexture);
				renderItem.renderIcon(x, y, stack.getIconIndex(), 16, 16);
			}
		}
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        endList(page);
	}
	
	private void npcList() {
		int page = 1;
		endList(page);
	}
	
	private void endList(int page) {
		texMan.bindTexture(bg);
		for(int i=listCounts[page]; i<maxListCount; i++) {
			int y = listY+bUnselRect.height*i;
			this.drawTexturedModalRect(listX, y, bUnselRect.x, bUnselRect.y, bUnselRect.width, bUnselRect.height);
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
