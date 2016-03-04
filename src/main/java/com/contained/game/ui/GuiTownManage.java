package com.contained.game.ui;

import java.awt.Rectangle;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.contained.game.data.Data;
import com.contained.game.item.ItemTerritory;
import com.contained.game.item.TerritoryFlag;
import com.contained.game.util.Resources;
import com.contained.game.world.block.AntiTerritoryMachine;
import com.contained.game.world.block.ContainerTownHall;
import com.contained.game.world.block.TerritoryMachine;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * GUI that is displayed after interacting with the Town Hall block.
 */
public class GuiTownManage extends GuiContainer {

	private RenderBlocks renderBlock = new RenderBlocks();
	private TextureManager texMan;
	private static final ResourceLocation bg = new ResourceLocation(Resources.MOD_ID, "textures/gui/townhall.png");
	private int numTabs = 2;
	private int selectedTab = 0;
	private String teamID;
	
	private int guiX;
	private int guiY;
	private int listX;
	private int listY;
	
	private String[] tabTitles = {"Purchase Territory", "NPC Villagers"};
	private ItemStack[] tabItems = {new ItemStack(TerritoryFlag.instance, 1)
								  , new ItemStack(Items.emerald, 1)};
	private int[] listCounts = {20, 0};
	private ItemStack[][] listItems;
	
	private float currentScroll = 0f;
	private boolean isScrolling = false;
	private boolean wasClicking = false;
	
	//UV coords on the texturepage
	private Rectangle bBg          = new Rectangle(0, 0, 175, 213);
	private Rectangle bUnselRect   = new Rectangle(0, 214, 158, 19);
	private Rectangle bSelRect     = new Rectangle(0, 233, 158, 19);
	private Rectangle bUnselTab    = new Rectangle(208, 0, 31, 27);
	private Rectangle bSelTabFirst = new Rectangle(176, 0, 31, 27);
	private Rectangle bSelTabMid   = new Rectangle(176, 28, 31, 27);
	private Rectangle bSelTabLast  = new Rectangle(176, 56, 31, 27);
	private Rectangle bXPOrb       = new Rectangle(176, 192, 16, 16);
	private Rectangle bScrollSel   = new Rectangle(176, 176, 11, 14);
	private Rectangle bScrollUnsel = new Rectangle(188, 176, 11, 14);
	private Rectangle bScrollBg	   = new Rectangle(242, 0, 13, 96);
	
	public GuiTownManage(InventoryPlayer inv, String teamID) {
		super(new ContainerTownHall(inv));
		
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
		guiY = (int)((float)(this.height-bBg.height)*0.3f);
		listX = guiX+9;
		listY = guiY+29;
		
		int maxLen = 0;
		for(int i : listCounts) {
			if (i > maxLen)
				maxLen = i;
		}
		listItems = new ItemStack[numTabs][maxLen];
		listItems[0][0] = new ItemStack(ItemTerritory.addTerritory, 1);
		listItems[0][1] = new ItemStack(ItemTerritory.addTerritory, 10);
		listItems[0][2] = new ItemStack(ItemTerritory.removeTerritory, 1);
		NBTTagCompound itemData = Data.getTagCompound(listItems[0][2]);
		itemData.setString("teamOwner", teamID);
		listItems[0][2].setTagCompound(itemData);
		listItems[0][3] = new ItemStack(TerritoryMachine.instance, 1);
		listItems[0][4] = new ItemStack(AntiTerritoryMachine.instance, 1);
		
		//scrollbar test
		for(int i=5;i<20;i++)
			listItems[0][i] = new ItemStack(ItemTerritory.removeTerritory, i);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int w, int h) {
		FontRenderer fr = this.mc.fontRenderer;
		
		// Background
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		texMan.bindTexture(bg);
		this.drawTexturedModalRect(guiX, guiY, bBg.x, bBg.y, bBg.width, bBg.height);
		
		// Tabs
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		for(int i=0; i<numTabs; i++) {
			int iconOff = 11;
			Rectangle bound;
			if (i == selectedTab) {
				if (i == 0)
					bound = bSelTabFirst;
				else if (i == numTabs-1)
					bound = bSelTabLast;
				else
					bound = bSelTabMid;
				iconOff = 9;
			}
			else
				bound = bUnselTab;
			
			texMan.bindTexture(bg);
			this.drawTexturedModalRect(guiX-bound.width, guiY+bound.height*i, bound.x, bound.y, bound.width, bound.height);
			texMan.bindTexture(TextureMap.locationItemsTexture);
			GuiScreen.itemRender.renderIcon(guiX-bound.width+iconOff, guiY+bound.height*i+6, tabItems[i].getIconIndex(), 16, 16);
		}
		
		// Scrollbar
		texMan.bindTexture(bg);
		if (needsScrollBars()) {
			int scrollX = guiX+155;
			int scrollY1 = listY+1;
			int scrollY2 = listY+bScrollBg.height-2;
			this.drawTexturedModalRect(scrollX, listY-1, bScrollBg.x, bScrollBg.y, bScrollBg.width, bScrollBg.height);
			this.drawTexturedModalRect(scrollX+1, listY+(int)((float)(scrollY2-scrollY1-bScrollSel.height)*this.currentScroll), bScrollSel.x, bScrollSel.y, bScrollSel.width, bScrollSel.height);
		}
		
		// Title Caption
		int titleXOff = 0;
		if (selectedTab <= 1) {
			titleXOff = -16;	
			this.drawTexturedModalRect(guiX+bBg.width-22, guiY+6, bXPOrb.x, bXPOrb.y, bXPOrb.width, bXPOrb.height);
			String xpStr = ""+mc.thePlayer.experienceLevel;
			fr.drawString(xpStr, guiX+bBg.width-22-fr.getStringWidth(xpStr), guiY+10, 0x000000);
		}
		fr.drawString(tabTitles[selectedTab], guiX+bBg.width/2-fr.getStringWidth(tabTitles[selectedTab])/2+titleXOff, guiY+10, 0x000000);
		
		//Page Contents
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (selectedTab == 0)
			territoryList();
		else if (selectedTab == 1)
			npcList();
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private void territoryList() {	
		int page = 0;
        int offset = 0;
        if (needsScrollBars())
        	offset = (int)(this.currentScroll*(float)((listCounts[page])-5));
    
		for(int i=offset; i<offset+Math.min(5, listCounts[page]); i++) {
			int x = listX+4;
			int y = listY+2+bSelRect.height*(i-offset);
			ItemStack stack = listItems[page][i];
			if (!ForgeHooksClient.renderInventoryItem(renderBlock, texMan, stack, false, 200, x, y)) {
				texMan.bindTexture(TextureMap.locationItemsTexture);
				GuiScreen.itemRender.renderItemAndEffectIntoGUI(this.mc.fontRenderer, texMan, stack, x, y);
			}
			GuiScreen.itemRender.renderItemOverlayIntoGUI(this.mc.fontRenderer, texMan, stack, x, y);
		}
		
        endList(page);
	}
	
	private void npcList() {
		int page = 1;
		endList(page);
	}
	
	private void endList(int page) {
		if (needsScrollBars())
			return;
		
		texMan.bindTexture(bg);
		for(int i=listCounts[page]; i<5; i++) {
			int y = listY+bUnselRect.height*i;
			this.drawTexturedModalRect(listX, y, bUnselRect.x, bUnselRect.y, bUnselRect.width, bUnselRect.height);
		}
	}
	
	private boolean needsScrollBars() {
		return listCounts[selectedTab] > 5;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int par3) {
		super.mouseClicked(x, y, par3);
		
		// Clicking tabs
		for(int i=0; i<numTabs; i++) {
			Rectangle bound = bSelTabFirst;
			if (x >= guiX-bound.width && x < guiX
					&& y >= guiY+bound.height*i && y < guiY+bound.height*(i+1)) {
				this.selectedTab = i;
				this.currentScroll = 0f;
				break;
			}
		}
	}
	
	@Override 
	public void drawScreen(int mouseX, int mouseY, float ticks) {
		super.drawScreen(mouseX, mouseY, ticks);
		
		// Hover tips (tabs)
		for(int i=0; i<numTabs; i++) {
			Rectangle bound = bSelTabFirst;
			if (mouseX >= guiX-bound.width && mouseX < guiX
					&& mouseY >= guiY+bound.height*i && mouseY < guiY+bound.height*(i+1)) {
				this.drawCreativeTabHoveringText(tabTitles[i], mouseX, mouseY);
				break;
			}
		}
		
		// Hover tips (items)
		int offset = 0;
        if (needsScrollBars())
        	offset = (int)(this.currentScroll*(float)((listCounts[selectedTab])-5));
		
		for(int i=offset; i<offset+Math.min(5,listCounts[selectedTab]); i++) {
			int lx = listX+4;
			int ly = listY+2+bSelRect.height*(i-offset);
			if (mouseX >= lx && mouseX < lx+16 && mouseY >= ly && mouseY < ly+16) {
				this.renderToolTip(listItems[selectedTab][i], mouseX, mouseY);
				break;
			}
		}
		
		//Scroll control
		int sx1 = guiX+155;
		int sx2 = sx1+bScrollBg.width;
		int sy1 = listY+1;
		int sy2 = listY+bScrollBg.height-2;
		boolean down = Mouse.isButtonDown(0);
		if (!down)
			this.isScrolling = false;
		else if (this.wasClicking && mouseX >= sx1 && mouseX < sx2 && mouseY >= sy1 && mouseY < sy2)
			this.isScrolling = needsScrollBars();
		
		this.wasClicking = down;
		
		if (this.isScrolling)
			this.currentScroll = Math.max(0, Math.min(1, ((float)(mouseY-sy1)-7.5F)/((float)(sy2-sy1)-15.0F)));
	}
	
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		
		//Scrolling bar with scroll wheel
		int amt = Mouse.getEventDWheel();
		if (amt != 0 && needsScrollBars()) {
			int point = listCounts[selectedTab]-5;
			if (amt > 0)
				amt = 1;
			if (amt < 0)
				amt = -1;
			this.currentScroll = Math.max(0,Math.min(1,(float)((double)this.currentScroll-(double)amt/(double)point)));
		}
	}
	
}
