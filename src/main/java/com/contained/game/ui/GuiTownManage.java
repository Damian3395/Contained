package com.contained.game.ui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

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
import com.contained.game.user.PlayerTrade;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.AntiTerritoryMachine;
import com.contained.game.world.block.ContainerTownHall;
import com.contained.game.world.block.TerritoryMachine;
import com.contained.game.world.block.TownManageTE;

import net.minecraft.potion.Potion;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
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
	
	// marketInd ID # For Rendering Pages
	private final int MARKET = 0;
	private final int MY_TRADES = 1;
	private final int CREATE_TRADE = 2;
	private final int SELECTION = 3;
	private final int MATERIALS = 4;
	private final int FOODS = 5;
	private final int TOOLS = 6;
	private final int WEAPONS = 7;
	private final int ARMOR = 8;
	private final int TRANSPORTATION = 9;
	private final int POTIONS = 10;
	private final int MUSIC_DISCS = 11;
	private final int NPC = 12;
	private final int DECORATIONAL = 13;
	private final int BLOCKS = 14;
	
	// We'll keep a local "offline" copy of the team list for this GUI, because if
	// any team data changes (especially if a team is removed) on the "online" side,
	// it can break things badly here.
	private ArrayList<PlayerTeam> localTeams;
	private int permTeamInd = -1; //Team to view the permissions on in the permissions tab.
	private int marketInd = -1; //Team to view the market options in the markets tab
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
	private ArrayList<PlayerTrade> myTrades;
	private ArrayList<PlayerTrade> marketTrades;
	private ItemStack makeOffer, makeRequest;
	
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
		
		myTrades = Contained.trades;
		marketTrades = myTrades;
		for(PlayerTrade trade : myTrades){
			if(trade.displayName.equals(this.mc.thePlayer.getDisplayName()))
				myTrades.add(trade);
			else
				marketTrades.add(trade);
		}
		
		makeOffer = null;
		makeRequest = null;
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
		if (permTeamInd != -1 && selectedTab == tabPermission) {
			if (permTeamInd >= 0 && permTeamInd < this.localTeams.size()) {
				PlayerTeam permTeamTitle = localTeams.get(permTeamInd);
				title += " ("+permTeamTitle.getFormatCode()+permTeamTitle.displayName+"§r)";
			}
			else
				title += " (Defaults)";
		}else if (marketInd != -1 && selectedTab == tabMarket){
			switch(marketInd){
			case MARKET: title += " (Market Offers)"; break;
			case MY_TRADES: title += " (My Offers)"; break;
			case CREATE_TRADE: title += " (Create Offer)"; break;
			case SELECTION: title += " (Selections)"; break;
			case MATERIALS: title += " (Materials)"; break;
			case FOODS: title += " (Foods)"; break;
			case TOOLS: title += " (Tools)"; break;
			case WEAPONS: title += " (Weapons)"; break;
			case ARMOR: title += " (Armor)"; break;
			case TRANSPORTATION: title += " (Transportation)"; break;
			case POTIONS: title += " (Potions)"; break;
			case MUSIC_DISCS: title += " (Music Discs)"; break;
			case NPC: title += " (NPC)"; break;
			case DECORATIONAL: title += " (Decorational)"; break;
			case BLOCKS: title += " (Blocks)"; break;
			}
		}
		fr.drawString(title, guiX+bBg.width/2-fr.getStringWidth(title)/2+titleXOff, guiY+10, 0x000000);
		
		//Page Contents
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (selectedTab == this.tabTerritory)
			shopList();
		else if (selectedTab == tabPermission){
			if (permTeamInd == -1)
				teamList();
			else
				permList();
		}else if (selectedTab == this.tabNPC){
			npcList();
		}else{
			if (this.marketInd == -1)
				marketList();
			else
				marketOption();
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
	
	private void npcList(){
		
	}
	
	private void npcClick(int mouseX, int mouseY){
		
	}
	
	private void marketList(){
		listCounts[tabMarket] = 3;
		
		int indOff = 0;
		displayMarketOption(0+indOff, "Find Trade");
		displayMarketOption(1+indOff, "My Trade Offers");
		displayMarketOption(2+indOff, "Create New Offer");
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		endList();
	}
	
	private void displayMarketOption(int ind, String caption){
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;
		
		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);
		
		texMan.bindTexture(bg);
		if (itemHovering == ind-offset)
			this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);
		fr.drawString(caption, x, y+4, 0x000000);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private void marketOption(){
		switch(marketInd){
		case MARKET: renderMarketTrades(); break;
		case MY_TRADES: renderMyTrades();break;
		case CREATE_TRADE: renderCreateTrade(); break;
		case SELECTION: renderSelectOption(); break;
		case MATERIALS: renderMaterials(); break;
		case FOODS: renderFoods(); break;
		case TOOLS: renderTools(); break;
		case WEAPONS: renderWeapons(); break;
		case ARMOR: renderArmor(); break;
		case TRANSPORTATION: renderTransportation(); break;
		case POTIONS: renderPotions(); break;
		case MUSIC_DISCS: renderMusicDiscs(); break;
		case NPC: renderNPC(); break;
		case DECORATIONAL: renderDecorational(); break;
		case BLOCKS: renderBlocks(); break;
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		endList();
	}
	
	private void renderSelectOption(){
		listCounts[tabMarket] = 12;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		displayMarketOption(1+indOff, "Materials");
		displayMarketOption(2+indOff, "Foods");
		displayMarketOption(3+indOff, "Tools");
		displayMarketOption(4+indOff, "Weapons");
		displayMarketOption(5+indOff, "Armor");
		displayMarketOption(6+indOff, "Transportation");
		displayMarketOption(7+indOff, "Potions");
		displayMarketOption(8+indOff, "Music Discs");
		displayMarketOption(9+indOff, "NPC");
		displayMarketOption(10+indOff, "Decorations");
		displayMarketOption(11+indOff, "Blocks");
	}
	
	private void renderMaterials(){
		listCounts[tabMarket] = 33;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.stick, null, null);
		renderItem(2+indOff, Items.flint, null, null);
		renderItem(3+indOff, Items.string, null, null);
		renderItem(4+indOff, Items.dye, null, null);
		renderItem(5+indOff, Items.paper, null, null);
		renderItem(6+indOff, Items.feather, null, null);
		renderItem(7+indOff, Items.leather, null, null);
		renderItem(8+indOff, Items.clay_ball, null, null);
		renderItem(9+indOff, Items.brick, null, null);
		renderItem(10+indOff, Items.snowball, null, null);
		renderItem(11+indOff, Items.slime_ball, null, null);
		renderItem(12+indOff, Items.glowstone_dust, null, null);
		renderItem(13+indOff, Items.redstone, null, null);
		renderItem(14+indOff, Items.gunpowder, null, null);
		renderItem(15+indOff, Items.gold_nugget, null, null);
		renderItem(16+indOff, Items.bone, null, null);
		renderItem(17+indOff, Items.skull, null, null);
		renderItem(18+indOff, Items.glass_bottle, null, null);
		renderItem(19+indOff, Items.fireworks, null, null);
		renderItem(20+indOff, Items.lead, null, null);
		renderItem(21+indOff, Items.coal, null, null);
		renderItem(22+indOff, Items.iron_ingot, null, null);
		renderItem(23+indOff, Items.gold_ingot, null, null);
		renderItem(24+indOff, Items.emerald, null, null);
		renderItem(25+indOff, Items.quartz, null, null);
		renderItem(26+indOff, Items.diamond, null, null);
		renderItem(27+indOff, Items.ender_pearl, null, null);
		renderItem(28+indOff, Items.ghast_tear, null, null);
		renderItem(29+indOff, Items.magma_cream, null, null);
		renderItem(30+indOff, Items.netherbrick, null, null);
		renderItem(31+indOff, Items.nether_wart, null, null);
		renderItem(32+indOff, Items.nether_star, null, null);
	}
	
	private void renderFoods(){
		listCounts[tabMarket] = 32;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.bread, null, null);
		renderItem(2+indOff, Items.cake, null, null);
		renderItem(3+indOff, Items.cookie, null, null);
		renderItem(4+indOff, Items.pumpkin_pie, null, null);
		renderItem(5+indOff, Items.apple, null, null);
		renderItem(6+indOff, Items.melon, null, null);
		renderItem(7+indOff, Items.potato, null, null);
		renderItem(8+indOff, Items.carrot, null, null);
		renderItem(9+indOff, Items.baked_potato, null, null);
		renderItem(10+indOff, Items.fish, null, null);
		renderItem(11+indOff, Items.beef, null, null);
		renderItem(12+indOff, Items.porkchop, null, null);
		renderItem(13+indOff, Items.chicken, null, null);
		renderItem(14+indOff, Items.cooked_fished, null, null);
		renderItem(15+indOff, Items.cooked_beef, null, null);
		renderItem(16+indOff, Items.cooked_porkchop, null, null);
		renderItem(17+indOff, Items.cooked_chicken, null, null);
		renderItem(18+indOff, Items.mushroom_stew, null, null);
		renderItem(19+indOff, Items.spider_eye, null, null);
		renderItem(20+indOff, Items.golden_carrot, null, null);
		renderItem(21+indOff, Items.golden_apple, null, null);
		renderItem(22+indOff, Items.wheat_seeds, null, null);
		renderItem(23+indOff, Items.pumpkin_seeds, null, null);
		renderItem(24+indOff, Items.melon_seeds, null, null);
		renderItem(25+indOff, Items.sugar, null, null);
		renderItem(26+indOff, Items.egg, null, null);
		renderItem(27+indOff, Items.milk_bucket, null, null);
		renderItem(28+indOff, Items.speckled_melon, null, null);
		renderItem(29+indOff, Items.wheat, null, null);
		renderItem(30+indOff, Items.reeds, null, null);
		renderItem(31+indOff, Items.bowl, null, null);
	}
	
	private void renderTools(){
		listCounts[tabMarket] = 38;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.bucket, null, null);
		renderItem(2+indOff, Items.fishing_rod, null, null);
		renderItem(3+indOff, Items.shears, null, null);
		renderItem(4+indOff, Items.flint_and_steel, null, null);
		renderItem(5+indOff, Items.book, null, null);
		renderItem(6+indOff, Items.clock, null, null);
		renderItem(7+indOff, Items.compass, null, null);
		renderItem(8+indOff, Items.map, null, null);
		renderItem(9+indOff, Items.brewing_stand, null, null);
		renderItem(10+indOff, Items.cauldron, null, null);
		renderItem(11+indOff, Items.flower_pot, null, null);
		renderItem(12+indOff, Items.item_frame, null, null);
		renderItem(13+indOff, Items.saddle, null, null);
		renderItem(14+indOff, Items.sign, null, null);
		renderItem(15+indOff, Items.wooden_shovel, null, null);
		renderItem(16+indOff, Items.wooden_hoe, null, null);
		renderItem(17+indOff, Items.wooden_axe, null, null);
		renderItem(18+indOff, Items.wooden_pickaxe, null, null);
		renderItem(19+indOff, Items.stone_shovel, null, null);
		renderItem(20+indOff, Items.stone_hoe, null, null);
		renderItem(21+indOff, Items.stone_axe, null, null);
		renderItem(22+indOff, Items.stone_pickaxe, null, null);
		renderItem(23+indOff, Items.iron_shovel, null, null);
		renderItem(24+indOff, Items.iron_hoe, null, null);
		renderItem(25+indOff, Items.iron_axe, null, null);
		renderItem(26+indOff, Items.iron_pickaxe, null, null);
		renderItem(27+indOff, Items.golden_shovel, null, null);
		renderItem(28+indOff, Items.golden_hoe, null, null);
		renderItem(29+indOff, Items.golden_axe, null, null);
		renderItem(30+indOff, Items.golden_pickaxe, null, null);
		renderItem(31+indOff, Items.diamond_shovel, null, null);
		renderItem(32+indOff, Items.diamond_hoe, null, null);
		renderItem(33+indOff, Items.diamond_axe, null, null);
		renderItem(34+indOff, Items.diamond_pickaxe, null, null);
		renderItem(35+indOff, Items.blaze_rod, null, null);
		renderItem(36+indOff, Items.repeater, null, null);
		renderItem(37+indOff, Items.comparator, null, null);
	}
	
	private void renderWeapons(){
		listCounts[tabMarket] = 8;
		int indOff = 0;
		
		//renderItem(+indOff, Items.);
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.wooden_sword, null, null);
		renderItem(2+indOff, Items.stone_sword, null, null);
		renderItem(3+indOff, Items.iron_sword, null, null);
		renderItem(4+indOff, Items.golden_sword, null, null);
		renderItem(5+indOff, Items.diamond_sword, null, null);
		renderItem(6+indOff, Items.bow, null, null);
		renderItem(7+indOff, Items.arrow, null, null);
	}
	
	private void renderArmor(){
		listCounts[tabMarket] = 20;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.leather_boots, null, null);
		renderItem(2+indOff, Items.leather_leggings, null, null);
		renderItem(3+indOff, Items.leather_chestplate, null, null);
		renderItem(4+indOff, Items.leather_helmet, null, null);
		renderItem(5+indOff, Items.iron_boots, null, null);
		renderItem(6+indOff, Items.iron_leggings, null, null);
		renderItem(7+indOff, Items.iron_chestplate, null, null);
		renderItem(8+indOff, Items.iron_helmet, null, null);
		renderItem(9+indOff, Items.golden_boots, null, null);
		renderItem(10+indOff, Items.golden_leggings, null, null);
		renderItem(11+indOff, Items.golden_chestplate, null, null);
		renderItem(12+indOff, Items.golden_helmet, null, null);
		renderItem(13+indOff, Items.diamond_boots, null, null);
		renderItem(14+indOff, Items.diamond_leggings, null, null);
		renderItem(15+indOff, Items.diamond_chestplate, null, null);
		renderItem(16+indOff, Items.diamond_helmet, null, null);
		renderItem(17+indOff, Items.iron_horse_armor, null, null);
		renderItem(18+indOff, Items.golden_horse_armor, null, null);
		renderItem(19+indOff, Items.diamond_horse_armor, null, null);
	}
	
	private void renderTransportation(){
		listCounts[tabMarket] = 8;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		//renderItem(1+indOff, null, Blocks.rail, null);
		//renderItem(2+indOff, null, Blocks.detector_rail, null);
		//renderItem(3+indOff, null, Blocks.activator_rail, null);
		//renderItem(4+indOff, null, Blocks.golden_rail, null);
		renderItem(1+indOff, Items.minecart, null, null);
		renderItem(2+indOff, Items.chest_minecart, null, null);
		renderItem(3+indOff, Items.furnace_minecart, null, null);
		renderItem(4+indOff, Items.tnt_minecart, null, null);
		renderItem(5+indOff, Items.hopper_minecart, null, null);
		renderItem(6+indOff, Items.boat, null, null);
		renderItem(7+indOff, Items.carrot_on_a_stick, null, null);
	}
	
	private void renderPotions(){
		listCounts[tabMarket] = 1;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
	}
	
	private void renderMusicDiscs(){
		listCounts[tabMarket] = 13;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.record_blocks, null, null);
		renderItem(2+indOff, Items.record_11, null, null);
		renderItem(3+indOff, Items.record_13, null, null);
		renderItem(4+indOff, Items.record_cat, null, null);
		renderItem(5+indOff, Items.record_chirp, null, null);
		renderItem(6+indOff, Items.record_far, null, null);
		renderItem(7+indOff, Items.record_mall, null, null);
		renderItem(8+indOff, Items.record_mellohi, null, null);
		renderItem(9+indOff, Items.record_stal, null, null);
		renderItem(10+indOff, Items.record_strad, null, null);
		renderItem(11+indOff, Items.record_wait, null, null);
		renderItem(12+indOff, Items.record_ward, null, null);
	}
	
	private void renderNPC(){
		listCounts[tabMarket] = 1;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
	}
	
	private void renderDecorational(){
		listCounts[tabMarket] = 5;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
		renderItem(1+indOff, Items.bed, null, null);
		renderItem(2+indOff, Items.wooden_door, null, null);
		renderItem(3+indOff, Items.sign, null, null);
		renderItem(4+indOff, Items.painting, null, null);
	}
	
	private void renderBlocks(){
		listCounts[tabMarket] = 1;
		int indOff = 0;
		
		displayString(0+indOff, "[Back]");
	}

	private void renderItem(int ind, Item item, Block block, Potion potion){
		ItemStack itemStack = null;
		if(item != null)
			itemStack = new ItemStack(item);
		if(block != null)
			itemStack = new ItemStack(block);
		if(potion != null);
		if(itemStack == null)
			return;
		
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;
		
		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);
		
		texMan.bindTexture(bg);
		if (itemHovering == ind-offset)
			this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);

		GL11.glPushMatrix();
		IIcon iicon1 = itemStack.getIconIndex();
		this.mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
		this.drawTexturedModelRectFromIcon(x, y, iicon1, 16, 16);
		GL11.glPopMatrix();
		
		fr.drawString(itemStack.getDisplayName(), x+20, y+4, 0x000000);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private void renderCreateTrade(){
		int indOff = 0;
		listCounts[tabMarket] = 4;
		if (indOff == 0)
			displayString(0+indOff, "[Back]");
		renderTradeItem(1+indOff);
		selectRequest(2+indOff);
		displayMarketOption(3+indOff, "Create Offer");
	}
	
	private void selectRequest(int ind){
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;
		
		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);
		
		texMan.bindTexture(bg);
		if (itemHovering == ind-offset)
			this.drawTexturedModalRect(listX, y-2, bSelRect.x, bSelRect.y, bSelRect.width, bSelRect.height);
		if(makeRequest == null)
			fr.drawString("Select Request", x, y+4, 0x000000);
		else{
			GL11.glPushMatrix();
			IIcon iicon1 = makeRequest.getIconIndex();
			this.mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			this.drawTexturedModelRectFromIcon(x+100, y, iicon1, 16, 16);
			GL11.glPopMatrix();
			fr.drawString("Selected Request: ", x, y+4, 0x000000);
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private void renderTradeItem(int ind){
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;
		
		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);
		
		if(makeOffer == null)
			fr.drawString("Drag Item Here", x, y+4, 0x000000);
		else{
			GL11.glPushMatrix();
			IIcon iicon1 = makeOffer.getIconIndex();
			this.mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
			this.drawTexturedModelRectFromIcon(x+100, y, iicon1, 16, 16);
			GL11.glPopMatrix();
			fr.drawString("Selected Item: ", x, y+4, 0x000000);
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private void renderMyTrades(){
		int indOff = 0;
		int i = 1;
		listCounts[tabMarket] = myTrades.size()+1;
		
		displayString(0+indOff, "[Back]");
		for(PlayerTrade trade : myTrades){
			displayTradeOffer(indOff+i, trade.offer, trade.request);
			i++;
		}
	}
	
	private void renderMarketTrades(){
		int indOff = 0;
		int i = 1;
		listCounts[tabMarket] = marketTrades.size()+1;
		if (indOff == 0)
			displayString(0+indOff, "[Back]");
		for(PlayerTrade trade : marketTrades){
			displayTradeOffer(indOff+i, trade.offer, trade.request);
			i++;
		}
	}
	
	private void displayTradeOffer(int ind, ItemStack offer, ItemStack request){
		FontRenderer fr = this.mc.fontRenderer;
		int offset = scrollInd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (ind < offset || ind >= offset+Math.min(5, listCounts[selectedTab]))
			return;
		
		int x = listX+4;
		int y = listY+2+bSelRect.height*(ind-offset);
		
		GL11.glPushMatrix();
		IIcon iicon1 = makeOffer.getIconIndex();
		IIcon iicon2 = makeRequest.getIconIndex();
		this.mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
		this.drawTexturedModelRectFromIcon(x+50, y, iicon1, 16, 16);
		this.drawTexturedModelRectFromIcon(x+150, y, iicon2, 16, 16);
		GL11.glPopMatrix();
		fr.drawString("Offer: ", x, y+4, 0x000000);
		fr.drawString("Request: ", x+100, y+4, 0x000000);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private void marketClick(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		if(marketInd == -1){
			switch(ind+indOff){
			case 0:
				marketInd = 0;
				break;
			case 1:
				marketInd = 1;
				break;
			case 2:
				marketInd = 2;
				break;
			}
		}else{
			switch(marketInd){
			case MARKET: marketTradeClick(mouseX, mouseY); break;
			case MY_TRADES: myTradeClick(mouseX, mouseY); break;
			case CREATE_TRADE: createTradeClick(mouseX, mouseY); break;
			case SELECTION: selectTradeTypeClick(mouseX, mouseY); break;
			case MATERIALS: selectMaterial(mouseX, mouseY); break;
			case FOODS: selectFoods(mouseX, mouseY); break;
			case TOOLS: selectTools(mouseX, mouseY); break;
			case WEAPONS: selectWeapons(mouseX, mouseY); break;
			case ARMOR: selectArmor(mouseX, mouseY); break;
			case TRANSPORTATION: selectTransportation(mouseX, mouseY); break;
			case POTIONS: selectPotions(mouseX, mouseY); break;
			case MUSIC_DISCS: selectMusicDiscs(mouseX, mouseY); break;
			case NPC: selectNPC(mouseX, mouseY); break;
			case DECORATIONAL: selectDecorational(mouseX, mouseY); break;
			case BLOCKS: selectBlocks(mouseX, mouseY); break;
			}
		}
	}
	
	private void marketTradeClick(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		if(indOff+ind == 0)
			marketInd = -1;
		else{
			PacketCustom packet = new PacketCustom(Resources.MOD_ID, ServerPacketHandler.PLAYER_TRADE);
			packet.writeString(marketTrades.get((indOff+ind)-1).id);
			ServerPacketHandler.sendToServer(packet.toPacket());
		}
	}
	
	private void myTradeClick(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		if(indOff+ind == 0)
			marketInd = -1;
	}
	
	private void createTradeClick(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = -1;	break; //Back
		case 1: 
			System.out.println("Trade Offer"); //Offer Item
		break;
		case 2: marketInd = 3; break; //Select Item
		case 3:
			if(makeOffer != null && makeRequest != null){ //Create New Trade
				System.out.println("Trade");
			}
		break;
		}
	}
	
	private void selectTradeTypeClick(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 2; break; //Back
		case 1: marketInd = 4; break; //Materials
		case 2: marketInd = 5; break; //Foods
		case 3: marketInd = 6; break; //Tools
		case 4: marketInd = 7; break; //Weapons
		case 5: marketInd = 8; break; //Armor
		case 6: marketInd = 9; break; //Transportation
		case 7: marketInd = 10; break; //Potions
		case 8: marketInd = 11; break; //Music Discs
		case 9: marketInd = 12; break; //NPC
		case 10: marketInd = 13; break; //Decorational Items
		case 11: marketInd = 14; break; //Blocks
		}
	}
	
	private void selectMaterial(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		Item item = null;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		case 1: item = Items.stick; break;
		case 2: item = Items.flint; break;
		case 3: item = Items.string; break;
		case 4: item = Items.dye; break;
		case 5: item = Items.paper; break;
		case 6: item = Items.feather; break;
		case 7: item = Items.leather; break;
		case 8: item = Items.clay_ball; break;
		case 9: item = Items.brick; break;
		case 10: item = Items.snowball; break;
		case 11: item = Items.slime_ball; break;
		case 12: item = Items.glowstone_dust; break;
		case 13: item = Items.redstone; break;
		case 14: item = Items.gunpowder; break;
		case 15: item = Items.gold_nugget; break;
		case 16: item = Items.bone; break;
		case 17: item = Items.skull; break;
		case 18: item = Items.glass_bottle; break;
		case 19: item = Items.fireworks; break;
		case 20: item = Items.lead; break;
		case 21: item = Items.coal; break;
		case 22: item = Items.iron_ingot; break;
		case 23: item = Items.gold_ingot; break;
		case 24: item = Items.emerald; break;
		case 25: item = Items.quartz; break;
		case 26: item = Items.diamond; break;
		case 27: item = Items.ender_pearl; break;
		case 28: item = Items.ghast_tear; break;
		case 29: item = Items.magma_cream; break;
		case 30: item = Items.netherbrick; break;
		case 31: item = Items.nether_wart; break;
		case 32: item = Items.nether_star; break;
		}
		if(item != null){
			makeRequest = new ItemStack(item);
			marketInd = 2;
		}
	}
	
	private void selectFoods(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		Item item = null;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		case 1: item = Items.bread; break;
		case 2: item = Items.cake; break;
		case 3: item = Items.cookie; break;
		case 4: item = Items.pumpkin_pie; break;
		case 5: item = Items.apple; break;
		case 6: item = Items.melon; break;
		case 7: item = Items.potato; break;
		case 8: item = Items.carrot; break;
		case 9: item = Items.baked_potato; break;
		case 10: item = Items.fish; break;
		case 11: item = Items.beef; break;
		case 12: item = Items.porkchop; break;
		case 13: item = Items.chicken; break;
		case 14: item = Items.cooked_fished; break;
		case 15: item = Items.cooked_beef; break;
		case 16: item = Items.cooked_porkchop; break;
		case 17: item = Items.cooked_chicken; break;
		case 18: item = Items.mushroom_stew; break;
		case 19: item = Items.spider_eye; break;
		case 20: item = Items.golden_carrot; break;
		case 21: item = Items.golden_apple; break;
		case 22: item = Items.wheat_seeds; break;
		case 23: item = Items.pumpkin_seeds; break;
		case 24: item = Items.melon_seeds; break;
		case 25: item = Items.sugar; break;
		case 26: item = Items.egg; break;
		case 27: item = Items.milk_bucket; break;
		case 28: item = Items.speckled_melon; break;
		case 29: item = Items.wheat; break;
		case 30: item = Items.reeds; break;
		case 31: item = Items.bowl; break;
		}
		if(item != null){
			makeRequest = new ItemStack(item);
			marketInd = 2;
		}
	}
	
	private void selectTools(int mouseX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		
		}
	}
	
	private void selectWeapons(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectArmor(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectTransportation(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectPotions(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectMusicDiscs(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectNPC(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectDecorational(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
	}
	
	private void selectBlocks(int mousX, int mouseY){
		int ind = scrollInd()+itemHovering;
		int indOff = 0;
		switch(indOff+ind){
		case 0: marketInd = 3; break; //Back
		}
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
			if (ind >= this.blockTeamInd && ind < localTeams.size())
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
		if (permTeamInd >= 0 && permTeamInd < localTeams.size())
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
		if (permTeamInd >= 0 && permTeamInd < localTeams.size()) 
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
		if (permTeamInd >= 0 && permTeamInd < localTeams.size()) 
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
			if (selectedTab == tabTerritory)
				shopClick(x, y);
			else if (selectedTab == tabPermission)
				if (permTeamInd == -1)
					teamClick(x, y);
				else 
					permClick(x, y);
			else if (selectedTab == tabNPC)
				npcClick(x, y);
			else
				marketClick(x, y);
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
		if (selectedTab == tabTerritory)
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
