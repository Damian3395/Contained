package com.contained.game.ui;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.Data;
import com.contained.game.item.ItemTerritory;
import com.contained.game.item.TerritoryFlag;
import com.contained.game.network.ServerPacketHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamPermission;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.AntiTerritoryMachine;
import com.contained.game.world.block.ContainerTownHall;
import com.contained.game.world.block.TerritoryMachine;
import com.contained.game.world.block.TownManageTE;

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
	private int numTabs = 4;
	private int selectedTab = 0;
	private String blockTeamID;  //The ID of the territory this block is occupying.
	private String playerTeamID; //The team of the player interacting with this block.
	private int blockTeamInd;
	
	// We'll keep a local "offline" copy of the team list for this GUI, because if
	// any team data changes (especially if a team is removed) on the "online" side,
	// it can break things badly here.
	private ArrayList<PlayerTeam> localTeams;
	private int permTeamInd = -1; //Team to view the permissions on in the permissions tab.
	private boolean permissionsModified = false;
		
	private int guiX;
	private int guiY;
	private int listX;
	private int listY;
	
	private String[] tabTitles;
	private ItemStack[] tabItems;
	private int[] listCounts = {0, 0, 0, 0};
	private ItemStack[][] listItems; //Items to be sold in the given tab.
	private int[][] xpCosts;         //XP cost of items in the given tab.
	private ItemStack[][] itemCosts; //Trade cost of the items in the given tab.
	private ArrayList<String> availableAntiTeams;
	
	private float currentScroll = 0f;
	private boolean isScrolling = false;
	private boolean wasClicking = false;
	private int itemHovering = -1;
	
	//UV coords on the texturepage
	private Rectangle bBg          = new Rectangle(0, 0, 175, 213);
	private Rectangle bUnselRect   = new Rectangle(0, 233, 158, 19);
	private Rectangle bSelRect     = new Rectangle(0, 214, 158, 19);
	private Rectangle bUnselTab    = new Rectangle(208, 0, 31, 27);
	private Rectangle bSelTabFirst = new Rectangle(176, 0, 31, 27);
	private Rectangle bSelTabMid   = new Rectangle(176, 28, 31, 27);
	private Rectangle bSelTabLast  = new Rectangle(176, 56, 31, 27);
	private Rectangle bXPOrb       = new Rectangle(176, 192, 16, 16);
	private Rectangle bScrollSel   = new Rectangle(176, 176, 11, 14);
	private Rectangle bScrollBg	   = new Rectangle(242, 0, 13, 96);
	private Rectangle bDivider     = new Rectangle(200, 176, 2, 12);
	
	private int tabTerritory = 0;
	private int tabNPC = 1;
	private int tabPermission = 2;
	private int tabMarket = 3;
	
	public GuiTownManage(InventoryPlayer inv, TownManageTE te, String blockTeamID, String playerTeamID) {
		super(new ContainerTownHall(inv, te));
		
		this.blockTeamID = blockTeamID;
		this.playerTeamID = playerTeamID;
		this.localTeams = new ArrayList<PlayerTeam>(Contained.teamData);
		
		if (this.blockTeamID != null) {
			for(int i=0; i<localTeams.size(); i++) {
				if (localTeams.get(i).id.equals(this.blockTeamID)) {
					this.blockTeamInd = i;
					break;
				}
			}
		}
		
		availableAntiTeams = new ArrayList<String>();
		for(ItemStack item : inv.mainInventory) {
			if (item == null) continue;
			if (!(item.getItem() instanceof ItemTerritory.AntiTerritoryGem)) continue;
			NBTTagCompound itemData = Data.getTagCompound(item);	
			String team = itemData.getString("teamOwner");
			if (team != null && this.blockTeamID != null && !team.equals("") && !availableAntiTeams.contains(team) && !team.equals(this.blockTeamID))
				availableAntiTeams.add(team);
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		permissionsModified = false;
		texMan = this.mc.getTextureManager();
		guiX = (this.width-bBg.width)/2;
		guiY = (int)((float)(this.height-bBg.height)*0.3f);
		listX = guiX+9;
		listY = guiY+29;
		this.xSize = bBg.width;
		this.ySize = bBg.height;
		
		if (blockTeamID == null || playerTeamID == null || 
				!blockTeamID.equals(playerTeamID)) 
		{
			// This player is not a member of the team this town hall block
			// belongs to. Just show permissions instead.
			tabPermission = 0;
			tabTerritory = 2;
			if (playerTeamID == null)
				this.permTeamInd = -2; //Default permissions
			else {
				for(int i=0; i<localTeams.size(); i++) {
					if (localTeams.get(i).id.equals(this.playerTeamID)) {
						this.permTeamInd = i;
						break;
					}
				}
			}
		} else {
			// This player is a member of the team this town hall block
			// belongs to. Show everything.
			int maxLen = 0;		
			listCounts[tabTerritory] = 4+availableAntiTeams.size();
			for(int i : listCounts) {
				if (i > maxLen)
					maxLen = i;
			}
			listItems = new ItemStack[numTabs][maxLen];
			xpCosts = new int[numTabs][maxLen];
			itemCosts = new ItemStack[numTabs][maxLen];
			listCounts[tabPermission] = localTeams.size()-1;
		
			//Territory Purchase Declarations
			listItems[tabTerritory][0] = new ItemStack(ItemTerritory.addTerritory, 1);
			listItems[tabTerritory][1] = new ItemStack(ItemTerritory.addTerritory, 10);
			listItems[tabTerritory][2] = new ItemStack(ItemTerritory.removeTerritory, 1);
			NBTTagCompound itemData = Data.getTagCompound(listItems[tabTerritory][2]);
			if (this.blockTeamID == null)
				itemData.setString("teamOwner", "");
			else
				itemData.setString("teamOwner", this.blockTeamID);
			listItems[tabTerritory][2].setTagCompound(itemData);
			listItems[tabTerritory][3] = new ItemStack(TerritoryMachine.instance, 1);
			
			xpCosts[tabTerritory][0] = 1;
			xpCosts[tabTerritory][1] = 8;
			xpCosts[tabTerritory][2] = -1;
			xpCosts[tabTerritory][3] = 30;
			
			itemCosts[tabTerritory][0] = null;
			itemCosts[tabTerritory][1] = null;
			itemCosts[tabTerritory][2] = new ItemStack(Items.dye, 4, 4);
			itemCosts[tabTerritory][3] = null;
			
			for(int i=0; i<availableAntiTeams.size(); i++) {
				listItems[tabTerritory][4+i] = new ItemStack(AntiTerritoryMachine.instance, 1);
				itemData = Data.getTagCompound(listItems[tabTerritory][4+i]);
				itemData.setString("teamOwner", availableAntiTeams.get(i));
				listItems[tabTerritory][4+i].setTagCompound(itemData);
				xpCosts[tabTerritory][4+i] = 30;
				itemCosts[tabTerritory][4+i] = new ItemStack(ItemTerritory.removeTerritory, 4);
				itemData = Data.getTagCompound(itemCosts[tabTerritory][4+i]);
				itemData.setString("teamOwner", availableAntiTeams.get(i));
				itemCosts[tabTerritory][4+i].setTagCompound(itemData);
			}
		}
		
		tabTitles = new String[numTabs];
		tabTitles[tabTerritory] = "Purchase Territory";
		tabTitles[tabNPC] = "NPC Villagers";
		tabTitles[tabPermission] = "Permissions";
		tabTitles[tabMarket] = "Marketplace";
		
		tabItems = new ItemStack[numTabs];
		tabItems[tabTerritory] = new ItemStack(TerritoryFlag.instance, 1);
		tabItems[tabNPC] = new ItemStack(Items.emerald, 1);
		tabItems[tabPermission] = new ItemStack(Items.diamond_pickaxe, 1);
		tabItems[tabMarket] = new ItemStack(Items.gold_ingot, 1);
		
		if (blockTeamID == null || playerTeamID == null || 
				!blockTeamID.equals(playerTeamID)) 
		{
			numTabs = 1;
		}
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (permissionsModified) {
			PacketCustom packet = ServerPacketHandler.packetUpdatePermissions(localTeams.get(blockTeamInd));
			ServerPacketHandler.sendToServer(packet.toPacket());
		}
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
		
		// Title Caption
		int titleXOff = 0;
		if (selectedTab == tabTerritory || selectedTab == tabNPC) {
			titleXOff = -16;	
			texMan.bindTexture(bg);
			this.drawTexturedModalRect(guiX+bBg.width-22, guiY+6, bXPOrb.x, bXPOrb.y, bXPOrb.width, bXPOrb.height);
			String xpStr = ""+mc.thePlayer.experienceLevel;
			fr.drawString(xpStr, guiX+bBg.width-22-fr.getStringWidth(xpStr), guiY+10, 0x000000);
		}
		String title = tabTitles[selectedTab];
		if (permTeamInd != -1) {
			if (permTeamInd >= 0) {
				PlayerTeam permTeamTitle = localTeams.get(permTeamInd);
				title += " ("+permTeamTitle.getFormatCode()+permTeamTitle.displayName+"§r)";
			}
			else
				title += " (Defaults)";
		}
		fr.drawString(title, guiX+bBg.width/2-fr.getStringWidth(title)/2+titleXOff, guiY+10, 0x000000);
		
		//Page Contents
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (selectedTab != tabPermission)
			shopList();
		else {
			if (permTeamInd == -1)
				teamList();
			else
				permList();
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
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	/**
	 * Display a list of trades.
	 */
	private void shopList() {	
		FontRenderer fr = this.mc.fontRenderer;
        int offset = scrollInd();
        int priceX = calcPriceX();
    
		for(int i=offset; i<offset+Math.min(5, listCounts[selectedTab]); i++) {
			//Hover Selection Highlight
			int x = listX+4;
			int y = listY+2+bSelRect.height*(i-offset);
			texMan.bindTexture(bg);
			if (!canAfford(i))
				this.drawTexturedModalRect(listX, y-2, bUnselRect.x, bUnselRect.y, bUnselRect.width, bUnselRect.height);
			else if (itemHovering == i-offset)
				this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);
			
			//Item Icon
			ItemStack stack = listItems[selectedTab][i];
			if (!ForgeHooksClient.renderInventoryItem(renderBlock, texMan, stack, false, 200, x, y)) {
				texMan.bindTexture(TextureMap.locationItemsTexture);
				GuiScreen.itemRender.renderItemAndEffectIntoGUI(fr, texMan, stack, x, y);
			}
			GuiScreen.itemRender.renderItemOverlayIntoGUI(fr, texMan, stack, x, y);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			
			texMan.bindTexture(bg);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(priceX-24, y+1, bDivider.x, bDivider.y, bDivider.width, bDivider.height);
			
			int itemOff = -16;
			if (xpCosts[selectedTab][i] == -1)
				itemOff = 0;
			else {
				//XP cost
				texMan.bindTexture(bg);
				this.drawTexturedModalRect(priceX, y, bXPOrb.x, bXPOrb.y, bXPOrb.width, bXPOrb.height);
				fr.drawStringWithShadow(""+xpCosts[selectedTab][i], priceX+8, y+8, 0xFFFFFF);
			}
			
			if (itemCosts[selectedTab][i] != null) {
				//Item Cost
				stack = itemCosts[selectedTab][i];
				if (!ForgeHooksClient.renderInventoryItem(renderBlock, texMan, stack, false, 200, priceX+itemOff, y)) {
					texMan.bindTexture(TextureMap.locationItemsTexture);
					GuiScreen.itemRender.renderItemAndEffectIntoGUI(fr, texMan, stack, priceX+itemOff, y);
				}
				GuiScreen.itemRender.renderItemOverlayIntoGUI(fr, texMan, stack, priceX+itemOff, y);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}
			
		}
        endList();
	}
	
	private void shopHover(int mouseX, int mouseY) {
		int offset = scrollInd();		
		for(int i=offset; i<offset+Math.min(5,listCounts[selectedTab]); i++) {
			int lx = listX+4;
			int ly = listY+2+bSelRect.height*(i-offset);
			if (mouseX >= lx && mouseX < lx+16 && mouseY >= ly && mouseY < ly+16) {
				this.renderToolTip(listItems[selectedTab][i], mouseX, mouseY);
				break;
			}
			
			int px = calcPriceX();
			if (mouseX >= px && mouseX < px+16 && mouseY >= ly && mouseY < ly+16) {
				if (xpCosts[selectedTab][i] != -1)
					this.drawCreativeTabHoveringText("Experience Level", mouseX, mouseY);
				else if (itemCosts[selectedTab][i] != null)
					this.renderToolTip(itemCosts[selectedTab][i], mouseX, mouseY);
			}
			if (mouseX >= px-16 && mouseX < px && mouseY >= ly && mouseY < ly+16) {
				if (itemCosts[selectedTab][i] != null && xpCosts[selectedTab][i] != -1)
					this.renderToolTip(itemCosts[selectedTab][i], mouseX, mouseY);
			}
		}
	}
	
	private void shopClick(int mouseX, int mouseY) {
		attemptPurchase(scrollInd()+itemHovering);
	}
	
	private void attemptPurchase(int id) {
		if (canAfford(id)) {
			if (xpCosts[selectedTab][id] != -1) {
				mc.thePlayer.experienceLevel -= xpCosts[selectedTab][id];
				PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.OFFSET_XPLEVEL);
				packet.writeInt(-xpCosts[selectedTab][id]);
				ServerPacketHandler.sendToServer(packet.toPacket());
			}
			if (itemCosts[selectedTab][id] != null) {
				Util.removeItem(itemCosts[selectedTab][id], mc.thePlayer);
				PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.INVENTORY_REMOVE);
				packet.writeItemStack(itemCosts[selectedTab][id]);
				ServerPacketHandler.sendToServer(packet.toPacket());
			}
			PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.INVENTORY_ADD);
			packet.writeItemStack(listItems[selectedTab][id]);
			ServerPacketHandler.sendToServer(packet.toPacket());
		}
	}
	
	/**
	 * Display a list of all teams (excluding your own)
	 */
	private void teamList() {
		listCounts[tabPermission] = localTeams.size();
		FontRenderer fr = this.mc.fontRenderer;
        int offset = scrollInd();
        
        for(int i=offset; i<offset+Math.min(5, listCounts[selectedTab]); i++) {
			int x = listX+4;
			int y = listY+2+bSelRect.height*(i-offset);
        	int ind = i-1;
        	if (ind >= this.blockTeamInd)
        		ind++;
        	
			//Hover Selection Highlight
        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			texMan.bindTexture(bg);
			if (itemHovering == i-offset)
				this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);
        
			//Team Name
			if (ind == -1) {
				fr.drawString("Defaults", x, y+4, 0x000000);
			} else {
				PlayerTeam team = localTeams.get(ind);
				fr.drawString(team.getFormatCode()+team.displayName, x, y+4, 0xFFFFFF);
			}
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        endList();
	}
	
	private void teamClick(int mouseX, int mouseY) {
		int ind = scrollInd()+itemHovering-1;
		if (ind == -1) {
			this.permTeamInd = -2; //Default permissions
			return;
		} else {
			if (ind >= this.blockTeamInd)
				ind++;
			
			this.permTeamInd = ind;
		}
	}
	
	/**
	 * Display a list of permissions for the given team
	 */
	private void permList() {
		listCounts[tabPermission] = 11;
		PlayerTeam blockTeam = localTeams.get(blockTeamInd);
		PlayerTeamPermission perm = null;
		if (permTeamInd >= 0)
			perm = blockTeam.permissions.get(localTeams.get(permTeamInd).displayName);
		if (perm == null)
			perm = blockTeam.getDefaultPermissions();
		
		int indOff = 0;
		if (playerTeamID == null || !playerTeamID.equals(blockTeamID)) {
			//Foreign Permission read-only mode: You don't belong to the same team as this
			//town hall block, so you can only use it to view your permissions within the territory.
			indOff = -1;
			listCounts[tabPermission]--;
		}
			
		//0 = back button (if not in foreign permission read-only mode)
		if (indOff == 0)
			displayString(0+indOff, "[Back]");		
		displayPermItem(1+indOff, perm.breakDisable, "Break Blocks");
		displayPermItem(2+indOff, perm.buildDisable, "Place Blocks");
		displayPermItem(3+indOff, perm.chestDisable, "Open Chests");
		displayPermItem(4+indOff, perm.containerDisable, "Interact Containers");
		displayPermItem(5+indOff, perm.harvestDisable, "Harvest Crops");
		displayPermItem(6+indOff, perm.bucketDisable, "Scoop Fluids");
		displayPermItem(7+indOff, perm.itemDisable, "Loot Item Drops");
		displayPermItem(8+indOff, perm.animalDisable, "Damage Passives");
		displayPermItem(9+indOff, perm.mobDisable, "Damage Hostiles");
		displayPermItem(10+indOff, perm.interactDisable, "Interact Entities");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		endList();
	}
	
	private void displayPermItem(int ind, boolean value, String caption) {
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;

		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);

		//Hover Selection Highlight
		if (playerTeamID != null && playerTeamID.equals(blockTeamID) && Contained.isLeader) {
			texMan.bindTexture(bg);
			if (itemHovering == ind-offset)
				this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);
		}

		fr.drawString(caption, x, y+4, 0x000000);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		String valueCapt = "§2§lALLOW";
		if (value)
			valueCapt = "§4§lDENY";
		fr.drawString(valueCapt, x+bSelRect.width-bScrollBg.width-fr.getStringWidth(valueCapt)-4, y+4, 0xFFFFFF);
	}
	
	private void displayString(int ind, String caption) {
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;

		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);

		//Hover Selection Highlight
		texMan.bindTexture(bg);
		if (itemHovering == ind-offset)
			this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);

		fr.drawString(caption, x, y+4, 0x000000);	
	}
	
	private void permClick(int mouseX, int mouseY) {
		int ind = scrollInd()+itemHovering;
		PlayerTeam blockTeam = localTeams.get(blockTeamInd);
		PlayerTeamPermission perm = null;
		if (permTeamInd >= 0) 
			perm = blockTeam.permissions.get(localTeams.get(permTeamInd).displayName);
		if (perm == null)
			perm = blockTeam.getDefaultPermissions();
		
		perm = new PlayerTeamPermission(perm);
		
		int indOff = 0;
		if (playerTeamID == null || !playerTeamID.equals(blockTeamID)) {
			//Foreign Permission read-only mode: You don't belong to the same team as this
			//town hall block, so you can only use it to view your permissions within the territory.
			indOff = 1;
		}
		if (!Contained.isLeader) {
			if (ind+indOff > 0)
				return;
		}
		
		switch(ind+indOff) {
			case 0: permTeamInd = -1; return;
			case 1: perm.breakDisable = !perm.breakDisable; break;
			case 2: perm.buildDisable = !perm.buildDisable; break;
			case 3: perm.chestDisable = !perm.chestDisable; break;
			case 4: perm.containerDisable = !perm.containerDisable; break;
			case 5: perm.harvestDisable = !perm.harvestDisable; break;
			case 6: perm.bucketDisable = !perm.bucketDisable; break;
			case 7: perm.itemDisable = !perm.itemDisable; break;
			case 8: perm.animalDisable = !perm.animalDisable; break;
			case 9: perm.mobDisable = !perm.mobDisable; break;
			case 10: perm.interactDisable = !perm.interactDisable; break;
		}
		
		permissionsModified = true;
		
		String teamName = null;
		if (permTeamInd >= 0) 
			teamName = localTeams.get(permTeamInd).displayName;
		else
			teamName = PlayerTeam.getDefaultPermissionsKey();
		if (!perm.equals(blockTeam.getDefaultPermissions()))
			blockTeam.permissions.put(teamName, perm);
		else if (blockTeam.permissions.containsKey(teamName))
			blockTeam.permissions.remove(teamName);
	}
	
	private int scrollInd() {
        int offset = 0;
        if (needsScrollBars())
        	offset = (int)(this.currentScroll*(float)((listCounts[selectedTab])-5));
        return offset;
	}
	
	private int calcPriceX() {
		int priceX = listX+137;
        if (needsScrollBars()) 
        	priceX -= bScrollBg.width;
        return priceX;
	}
	
	private boolean canAfford(int id) {
		if (id >= listCounts[selectedTab])
			return false;
		if (xpCosts[selectedTab][id] != -1 && mc.thePlayer.experienceLevel < xpCosts[selectedTab][id])
			return false;
		if (itemCosts[selectedTab][id] != null) {
			int amount = 0;
			for(ItemStack item : mc.thePlayer.inventory.mainInventory) {
				if (item == null) continue;
				if (Util.itemsEqual(item, itemCosts[selectedTab][id])) {
					amount += item.stackSize;
					if (amount >= itemCosts[selectedTab][id].stackSize)
						break;
				}
			}
			
			if (amount < itemCosts[selectedTab][id].stackSize)
				return false;
		}
		return true;
	}
	
	private void endList() {
		if (needsScrollBars())
			return;
		
		texMan.bindTexture(bg);
		for(int i=listCounts[selectedTab]; i<5; i++) {
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
		
		//Purchase item
		if (itemHovering != -1) {
			if (selectedTab != tabPermission)
				shopClick(x, y);
			else {
				if (permTeamInd == -1)
					teamClick(x, y);
				else 
					permClick(x, y);
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
		
		// Hover tips (list contents)
		if (selectedTab != tabPermission)
			shopHover(mouseX, mouseY);
		
		//Update hover status
		int hoverOffset = 0;
		if (needsScrollBars())
			hoverOffset = -bScrollBg.width;
		itemHovering = -1;
		for(int i=0; i<5; i++) {
		if (mouseX >= listX && mouseX < listX+bSelRect.width+hoverOffset
				&& mouseY >= listY+i*bSelRect.height
				&& mouseY < listY+(i+1)*bSelRect.height) 
			{
				itemHovering = i;
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
