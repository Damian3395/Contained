package com.contained.game.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.handler.KeyBindings;
import com.contained.game.network.ServerPacketHandlerUtil;
import com.contained.game.ui.components.Container;
import com.contained.game.ui.components.GuiScrollPane;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class GuiAdmin extends GuiScreen {

	private Container adminWindow;
	private GuiScrollPane playerInfo, dimensionInfo;
	private int x, y;
	private EntityPlayer player;

	List<String> dimensions = new ArrayList<String>();
	List<EntityPlayer> diamensionPlayerList;
	List<String> onlinePlayerNames = new ArrayList<String>();
	
	private HashMap<String,Integer> dimID = new HashMap<String, Integer>(); 

	private boolean isObjectDefaultText = false;
	private boolean isPlayerDefaultText = false;
	private boolean isEditingDimTime = false;
	

	private int selectedDimID = -100;	// use -100 to represent the whole server
	private String selectedPlayer = null;
	private Long currentDimTime = -1L;

	private int pageID = 0; // default page is Login page

	public final int LOGIN_PAGE = 0;
	public final int WORLD_PAGE = 1;
	public final int PLAYER_PAGE = 2;
	public final int CONFIRM_PAGE = 3;

	// TextFields
	private GuiTextField tf_password;
	private GuiTextField tf_objectName;
	private GuiTextField tf_healthPercentage;
	private GuiTextField tf_hungerPercentage;
	private GuiTextField tf_targetPlayer;
	private GuiTextField tf_dimTime;

	// Buttons
	private GuiButton btn_login;
	private GuiButton btn_loginCancel;
	private GuiButton btn_create;
	private GuiButton btn_change;
	private GuiButton btn_getMostVulnerable;
	private GuiButton btn_getHungeriest;
	private GuiButton btn_getClosest;
	private GuiButton btn_getFarthest;
	private GuiButton btn_enter;
	private GuiButton btn_worldInfo;
	private GuiButton btn_kick;
	private GuiButton btn_spect;
	private GuiButton btn_back;
	private GuiButton btn_confirm;
	private GuiButton btn_cancel;
	private GuiButton btn_regular;
	private GuiButton btn_setTime;

	// Button ID
	private final int BTN_LOGIN = 0;
	private final int BTN_LOGIN_CANCEL = 1;
	private final int BTN_CREATE = 2;
	private final int BTN_CHANGE = 3;
	private final int BTN_VUL = 4;
	private final int BTN_HUN = 5;
	private final int BTN_CLS = 6;
	private final int BTN_FAR = 7;
	private final int BTN_ENTER = 8;
	private final int BTN_WORLD_INFO = 9;
	private final int BTN_KICK = 10;
	private final int BTN_SPECT = 11;
	private final int BTN_BACK = 12;
	private final int BTN_CONFIRM = 13;
	private final int BTN_CANCEL = 14;
	private final int BTN_REGULAR = 15;
	private final int BTN_SET_TIME = 16;
	

	public GuiAdmin() {
		this.player = FMLClientHandler.instance().getClient().thePlayer;
	}

	@Override
	public void initGui() {
		this.x = this.width / 2;
		this.y = this.height / 2;

		this.adminWindow = new Container((this.width - 256) / 2, ((this.height - 200) / 2), 256, 200, "adminWindow.png",
				this);
		this.dimensions.add("Whole Server");
		this.dimID.put("Whole Server", -100);
		this.dimensions.add("Overworld (Dim:0)");
		this.dimID.put("Overworld (Dim:0)", 0);
		for (int id = Resources.MIN_PVP_DIMID; id <= Resources.MAX_PVP_DIMID; id++) {
			this.dimensions.add("PvP (Dim:" + id + ")");
			this.dimID.put("PvP (Dim:"+id+")", id);
		}
		for (int id = Resources.MIN_TREASURE_DIMID; id <= Resources.MAX_TREASURE_DIMID; id++) {
			this.dimensions.add("Treasure Hunt (Dim:" + id + ")");
			this.dimID.put("Treasure Hunt (Dim:"+id+")", id);
		}
		
		this.playerInfo = new GuiScrollPane(this, x - 120, y - 5, this.onlinePlayerNames);
		this.dimensionInfo = new GuiScrollPane(this, x - 120, y - 25, this.dimensions);

		this.tf_password = new GuiTextField(this.mc.fontRenderer, x - 50, y - 40, 100, 20);
		this.tf_password.setTextColor(Color.WHITE.hashCode());

		this.tf_healthPercentage = new GuiTextField(this.mc.fontRenderer, x - 120, y - 70, 30, 20);
		this.tf_healthPercentage.setTextColor(Color.WHITE.hashCode());

		this.tf_hungerPercentage = new GuiTextField(this.mc.fontRenderer, x - 85, y - 70, 30, 20);
		this.tf_hungerPercentage.setTextColor(Color.WHITE.hashCode());

		this.tf_objectName = new GuiTextField(this.mc.fontRenderer, x - 50, y - 70, 60, 20);
		this.tf_objectName.setTextColor(Color.WHITE.hashCode());

		this.tf_targetPlayer = new GuiTextField(this.mc.fontRenderer, x + 30, y - 70, 90, 20);
		this.tf_targetPlayer.setTextColor(Color.WHITE.hashCode());
		
		this.tf_dimTime = new GuiTextField(this.mc.fontRenderer, x + 35, y - 30, 80, 20);
		this.tf_dimTime.setTextColor(Color.WHITE.hashCode());

		this.btn_login = new GuiButton(BTN_LOGIN, x - 65, y, 50, 20, "Login");
		this.btn_loginCancel = new GuiButton(BTN_LOGIN_CANCEL, x + 15, y, 50, 20, "Cancel");
		this.btn_create = new GuiButton(BTN_CREATE, x + 35, y + 55, 40, 20, "Create");
		this.btn_change = new GuiButton(BTN_CHANGE, x + 75, y + 55, 40, 20, "Change");
		this.btn_getMostVulnerable = new GuiButton(BTN_VUL, x + 35, y + 55, 80, 20, "Most Vulnerable");
		this.btn_getHungeriest = new GuiButton(BTN_HUN, x + 35, y + 35, 80, 20, "Hungriest");
		this.btn_getClosest = new GuiButton(BTN_CLS, x + 35, y + 15, 80, 20, "Closest");
		this.btn_getFarthest = new GuiButton(BTN_FAR, x + 35, y - 5, 80, 20, "Farthest");
		this.btn_enter = new GuiButton(BTN_ENTER, x + 35, y , 80, 20, "Enter World");
		this.btn_worldInfo = new GuiButton(BTN_WORLD_INFO, x + 35, y + 50, 80, 20, "View Info");
		this.btn_kick = new GuiButton(BTN_KICK, x + 35, y + 15, 80, 20, "Kick");
		this.btn_spect = new GuiButton(BTN_SPECT, x + 35, y + 35, 80, 20, "Spect");
		this.btn_back = new GuiButton(BTN_BACK, x + 35, y + 75, 80, 20, "Back");
		this.btn_confirm = new GuiButton(BTN_CONFIRM, x - 65, y, 50, 20, "Confirm");
		this.btn_cancel = new GuiButton(BTN_CANCEL, x + 15, y, 50, 20, "Cancel");
		this.btn_regular = new GuiButton(BTN_REGULAR, x-110, y-90, 60, 20, "Regular");
		this.btn_setTime = new GuiButton(BTN_SET_TIME, x + 35, y -5 , 80, 20, "Set Time");
		
		this.updateButtons();
	}

	@SuppressWarnings("unchecked")
	private void updateButtons() {
		this.buttonList.clear();
		switch (this.pageID) {
		case LOGIN_PAGE:
			if (!ExtendedPlayer.get(this.player).isAdmin()) {
				// Login Page
				this.buttonList.add(this.btn_login);
				this.buttonList.add(this.btn_loginCancel);
			} else { // no need to login again
				setPage(WORLD_PAGE);
			}
			break;
		case WORLD_PAGE:
			// World Selection Page
			this.buttonList.add(this.btn_enter);
			this.buttonList.add(this.btn_worldInfo);
			this.buttonList.add(this.btn_regular);
			break;
		case PLAYER_PAGE:
			// Player Selection Page
			this.buttonList.add(this.btn_setTime);
			this.buttonList.add(this.btn_kick);
			this.buttonList.add(this.btn_spect);
			this.buttonList.add(this.btn_create);
			this.buttonList.add(this.btn_change);
			this.buttonList.add(this.btn_back);
			
			break;
		case CONFIRM_PAGE:
			this.buttonList.add(this.btn_confirm);
			this.buttonList.add(this.btn_cancel);
			// Admin Control Page

			break;
		}
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		// handle mouseClicked events on textFields
		this.tf_password.mouseClicked(i, j, k);
		this.tf_healthPercentage.mouseClicked(i, j, k);
		this.tf_hungerPercentage.mouseClicked(i, j, k);
		this.tf_objectName.mouseClicked(i, j, k);
		this.tf_targetPlayer.mouseClicked(i, j, k);
		this.tf_dimTime.mouseClicked(i, j, k);
		this.playerInfo.mouseClicked(i, j, k);
		this.dimensionInfo.mouseClicked(i, j, k);
		if (this.playerInfo.isElementSelected) {
			this.tf_targetPlayer.setText(this.playerInfo.getText());
		}
		if (this.tf_dimTime.isFocused()) {
			this.isEditingDimTime = true;
		}
		// handle other mouseClicked events
		super.mouseClicked(i, j, k);
	}

	@Override
	public void keyTyped(char c, int i) {

		// handle keyTyped events on tf_password
		if (this.tf_password.isFocused()) {

			this.tf_password.textboxKeyTyped(c, i);

		} else if (this.tf_healthPercentage.isFocused()) {
			// handle keyTyped events on tf_health

			this.tf_healthPercentage.textboxKeyTyped(c, i);

		} else if (this.tf_hungerPercentage.isFocused()) {
			// handle keyTyped events on tf_hunger

			this.tf_hungerPercentage.textboxKeyTyped(c, i);

		} else if (this.tf_objectName.isFocused()) {
			// handle keyTyped events on tf_object
			if (this.isObjectDefaultText) {
				this.tf_objectName.setText("" + c);
				this.isObjectDefaultText = false;
			} else {
				this.tf_objectName.textboxKeyTyped(c, i);
			}
		} else if (this.tf_targetPlayer.isFocused()) {
			// handle keyTyped events on tf_player
			if (this.isPlayerDefaultText) {
				this.tf_targetPlayer.setText("" + c);
				this.isPlayerDefaultText = false;
			} else {
				this.tf_targetPlayer.textboxKeyTyped(c, i);
			}
		} else if(i == KeyBindings.toggleAdmin.getKeyCode()){
			this.mc.thePlayer.closeScreen();
		} else if(this.tf_dimTime.isFocused()){
			this.tf_dimTime.textboxKeyTyped(c, i);
		}	else {
		
		
			// handle other keyTyped events
			super.keyTyped(c, i);
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) { // handle button click events
		if (b.enabled) {
			switch (b.id) {
			case BTN_LOGIN:
				if (this.tf_password.getText().equals("GameScience2016")) {
					this.player.setInvisible(true);
					this.player.capabilities.disableDamage = true;
					this.player.capabilities.allowFlying = true;
					ExtendedPlayer.get(this.player).setAdminRights(true);
					PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.BECOME_ADMIN);
					Contained.channel.sendToServer(adminPacket.toPacket());
					Util.displayMessage(mc.thePlayer, Util.successCode + "You Are Now An Admin!");
				} else {
					Util.displayMessage(mc.thePlayer, Util.errorCode + "Wrong Password!");
				}
				break;

			case BTN_LOGIN_CANCEL:
				// TODO:close GUI
				break;

			case BTN_CREATE:
				if (!this.tf_objectName.getText().isEmpty() && !this.tf_targetPlayer.getText().isEmpty()) {
					try {
						PacketCustom adminCreatePacket = new PacketCustom(Resources.MOD_ID,
								ServerPacketHandlerUtil.ADMIN_CREATE);
						adminCreatePacket.writeString(this.tf_objectName.getText());
						adminCreatePacket.writeString(this.tf_targetPlayer.getText());
						Contained.channel.sendToServer(adminCreatePacket.toPacket());

					} catch (NullPointerException e) {
						this.tf_objectName.setText("Check Object Name");
						this.tf_targetPlayer.setText("Check Player Name");
						this.isObjectDefaultText = true;
						this.isPlayerDefaultText = true;
					} catch (Exception e) {
						this.tf_objectName.setText("Check Object Name");
						this.tf_targetPlayer.setText("Check Player Name");
						this.isObjectDefaultText = true;
						this.isPlayerDefaultText = true;
					}
				}
				break;

			case BTN_CHANGE:
				int healthPercentage, hungerPercentage;
				PacketCustom adminChangePacket = new PacketCustom(Resources.MOD_ID,
						ServerPacketHandlerUtil.ADMIN_CHANGE);
				if (!this.tf_targetPlayer.getText().isEmpty() && !this.isPlayerDefaultText) {
					if (this.tf_healthPercentage.getText().isEmpty()) {
						healthPercentage = -1;
					} else {
						healthPercentage = Integer.parseInt(this.tf_healthPercentage.getText());
					}
					if (this.tf_hungerPercentage.getText().isEmpty()) {
						hungerPercentage = -1;
					} else {
						hungerPercentage = Integer.parseInt(this.tf_hungerPercentage.getText());
					}
					adminChangePacket.writeInt(healthPercentage);
					adminChangePacket.writeInt(hungerPercentage);
					adminChangePacket.writeString(this.tf_targetPlayer.getText());
					Contained.channel.sendToServer(adminChangePacket.toPacket());
				}
				break;

			case BTN_VUL: // look for the most vulnerable player

				try {
					if (Util.getPlayerListInDimension(this.selectedDimID).size() > 1) {
						// only if there are more players online
						EntityPlayer target = Util.getPlayerListInDimension(this.selectedDimID).get(0);

						if (Util.getPlayerListInDimension(this.selectedDimID).get(0).equals(this.player)) {
							target = Util.getPlayerListInDimension(this.selectedDimID).get(1);
						}
						float minHealth = target.getHealth();
						for (int i = 0; i < Util.getPlayerListInDimension(this.selectedDimID).size(); i++) {
							if (Util.getPlayerListInDimension(this.selectedDimID).get(i).getHealth() < minHealth
									&& !Util.getPlayerListInDimension(this.selectedDimID).get(i).equals(this.player)) {
								minHealth = Util.getPlayerListInDimension(this.selectedDimID).get(i).getHealth();
								target = Util.getPlayerListInDimension(this.selectedDimID).get(i);
							}
						}

						this.tf_targetPlayer.setText(target.getDisplayName());

					} else {
						this.tf_targetPlayer.setText("No Player Online.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case BTN_HUN: // look for the hungriest player
				try {
					if (Util.getPlayerListInDimension(this.selectedDimID).size() > 1) {
						// only if there are more players online
						EntityPlayer target = Util.getPlayerListInDimension(this.selectedDimID).get(0);

						if (Util.getPlayerListInDimension(this.selectedDimID).get(0).equals(this.player)) {
							target = Util.getPlayerListInDimension(this.selectedDimID).get(1);
						}
						float minHunger = target.getFoodStats().getFoodLevel();
						for (int i = 0; i < Util.getPlayerListInDimension(this.selectedDimID).size(); i++) {
							if (Util.getPlayerListInDimension(this.selectedDimID).get(i).getFoodStats().getFoodLevel() < minHunger
									&& !Util.getPlayerListInDimension(this.selectedDimID).get(i).equals(this.player)) {
								minHunger = Util.getPlayerListInDimension(this.selectedDimID).get(i).getFoodStats().getFoodLevel();
								target = Util.getPlayerListInDimension(this.selectedDimID).get(i);
							}
						}

						this.tf_targetPlayer.setText(target.getDisplayName());

					} else {
						this.tf_targetPlayer.setText("No Player Online.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case BTN_CLS: // look for the closest player
				try {
					if (Util.getPlayerListInDimension(this.selectedDimID).size() > 1) {
						// only if there are more players online
						EntityPlayer target = Util.getPlayerListInDimension(this.selectedDimID).get(0);

						if (Util.getPlayerListInDimension(this.selectedDimID).get(0).equals(this.player)) {
							target = Util.getPlayerListInDimension(this.selectedDimID).get(1);
						}
						float minDistance = target.getDistanceToEntity(this.player);
						for (int i = 0; i < Util.getPlayerListInDimension(this.selectedDimID).size(); i++) {
							if (Util.getPlayerListInDimension(this.selectedDimID).get(i).getDistanceToEntity(this.player) < minDistance
									&& !Util.getPlayerListInDimension(this.selectedDimID).get(i).equals(this.player)) {
								minDistance = Util.getPlayerListInDimension(this.selectedDimID).get(i).getDistanceToEntity(this.player);
								target = Util.getPlayerListInDimension(this.selectedDimID).get(i);
							}
						}

						this.tf_targetPlayer.setText(target.getDisplayName());

					} else {
						this.tf_targetPlayer.setText("No Player Online.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case BTN_FAR: // look for the farthest player
				try {
					if (Util.getPlayerListInDimension(this.selectedDimID).size() > 1) {
						// only if there are more players online
						EntityPlayer target = Util.getPlayerListInDimension(this.selectedDimID).get(0);

						if (Util.getPlayerListInDimension(this.selectedDimID).get(0).equals(this.player)) {
							target = Util.getPlayerListInDimension(this.selectedDimID).get(1);
						}
						float maxDistance = 0.0f;
						for (int i = 0; i < Util.getPlayerListInDimension(this.selectedDimID).size(); i++) {
							if (Util.getPlayerListInDimension(this.selectedDimID).get(i).getDistanceToEntity(this.player) > maxDistance
									&& !Util.getPlayerListInDimension(this.selectedDimID).get(i).equals(this.player)) {
								maxDistance = Util.getPlayerListInDimension(this.selectedDimID).get(i).getDistanceToEntity(this.player);
								target = Util.getPlayerListInDimension(this.selectedDimID).get(i);
							}
						}

						this.tf_targetPlayer.setText(target.getDisplayName());

					} else {
						this.tf_targetPlayer.setText("No Player Online.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case BTN_ENTER:
				if (this.dimensionInfo.isElementSelected && this.dimID.get(this.dimensionInfo.getText()) >= 0) {
					this.selectedDimID = this.dimID.get(this.dimensionInfo.getText());
					if (this.player.dimension != this.selectedDimID) {
						PacketCustom adminJoinPacket = new PacketCustom(Resources.MOD_ID,
						ServerPacketHandlerUtil.ADMIN_JOIN);
						adminJoinPacket.writeInt(this.selectedDimID);
						Contained.channel.sendToServer(adminJoinPacket.toPacket());
					} else {
						Util.displayMessage(this.player, "You are already in Dimension:" + this.selectedDimID);
					}	
				}
			

				break;
			case BTN_WORLD_INFO:
				
				/*
				 * send the packet to server
				 * the server retrive the player list in requested dimension
				 * and then send the list back to player(client side)
				 * the player displays the player info GUI
				 */
				
				this.onlinePlayerNames.clear();
				if (this.dimensionInfo.isElementSelected) {
					PacketCustom worldInfoPacket = new PacketCustom(Resources.MOD_ID,ServerPacketHandlerUtil.ADMIN_WORLD_INFO);
					if (this.dimensionInfo.getText().equals("Whole Server")) {
						// selected to view whole server players
						this.selectedDimID = -100;
						worldInfoPacket.writeInt(this.selectedDimID);
						Contained.channel.sendToServer(worldInfoPacket.toPacket());	
					} else { // selected to view players in a certain dimension
						this.selectedDimID = this.dimID.get(this.dimensionInfo.getText());
						worldInfoPacket.writeInt(this.selectedDimID);
						Contained.channel.sendToServer(worldInfoPacket.toPacket());
					}
				}
				
				break;

			case BTN_KICK:
				if(this.playerInfo.isElementSelected){
					if(this.playerInfo.getText().contains(" (YOU)")){
						Util.displayMessage(this.player, "You cannot kick yourself!");
					} else {
						this.pageID++;
						this.selectedPlayer = this.playerInfo.getText();	
					}
				}
				break;
			
			case BTN_SPECT:
				if (this.playerInfo.isElementSelected) {
					if(this.playerInfo.getText().contains(" (YOU)")){
						Util.displayMessage(this.player, "You cannot spect yourself!");
					} else {
						PacketCustom adminSpectPacket = new PacketCustom(Resources.MOD_ID,
								ServerPacketHandlerUtil.ADMIN_SPECT);
						adminSpectPacket.writeString(this.playerInfo.getText());
						Contained.channel.sendToServer(adminSpectPacket.toPacket());	
					}
				} else {
					Util.displayMessage(this.player, "Please select a player to spect");
				}
				break;
			
			case BTN_BACK:
				this.pageID--;
				break;
			
			case BTN_CONFIRM:
				if(this.playerInfo.isElementSelected){
					PacketCustom adminKickPacket = new PacketCustom(Resources.MOD_ID,ServerPacketHandlerUtil.ADMIN_KICK);
					adminKickPacket.writeString(this.selectedPlayer);
					Contained.channel.sendToServer(adminKickPacket.toPacket());
				}
				this.pageID--;
			break;
			
			case BTN_CANCEL:
				this.mc.thePlayer.closeScreen();
				this.pageID--;
			break;
			
			case BTN_REGULAR:
				this.player.setInvisible(false);
				this.player.capabilities.disableDamage = false;
				this.player.capabilities.allowFlying = false;
				ExtendedPlayer.get(this.player).setAdminRights(false);
				
				PacketCustom spectatorPacket = new PacketCustom(Resources.MOD_ID,ServerPacketHandlerUtil.ADMIN_REGULAR_PLAYER);
				Contained.channel.sendToServer(spectatorPacket.toPacket());
			break;
			
			case BTN_SET_TIME:
				try{
					PacketCustom setTimePacket = new PacketCustom(Resources.MOD_ID,ServerPacketHandlerUtil.ADMIN_SET_TIME);
					setTimePacket.writeInt(this.selectedDimID);
					setTimePacket.writeLong(Long.parseLong(this.tf_dimTime.getText()));
					Contained.channel.sendToServer(setTimePacket.toPacket());
				} catch (NumberFormatException e) {
					System.out.println("ERROR in parsing time string.");
				}
				
			break;
			}
		}
	}

	@Override
	public void drawScreen(int w, int h, float ticks) {

		this.adminWindow.render();
		this.updateButtons();
		switch (this.pageID) {
		case LOGIN_PAGE:
			this.renderLoginPage();
			break;
		case WORLD_PAGE:
			this.renderWorldSelectionPage();
			break;
		case PLAYER_PAGE:
			this.renderPlayerSlectionPage();
			break;
		case CONFIRM_PAGE:
			this.renderConfirmDialog();
			break;
		}
		super.drawScreen(w, h, ticks);
	}

	private void renderLoginPage() { // render Admin Login UI
		this.tf_password.drawTextBox();
		this.fontRendererObj.drawString("Please Enter Admin Password:", x - 65, y - 50, 0);
	}

	private void renderWorldSelectionPage() {
		this.fontRendererObj.drawString("Please choose a dimension:", x - 120, y - 55, Color.WHITE.hashCode());
		this.dimensionInfo.render();
	}

	private void renderPlayerSlectionPage() {
		
		this.tf_healthPercentage.drawTextBox();
		this.tf_hungerPercentage.drawTextBox();
		this.tf_objectName.drawTextBox();
		this.tf_targetPlayer.drawTextBox();
		this.tf_dimTime.drawTextBox();
		// show current dim time as a reference
		if(!this.isEditingDimTime)
		this.tf_dimTime.setText(""+this.currentDimTime);
		
		
		
		this.fontRendererObj.drawString("HP(%)  FP(%)   Object", x - 120, y - 85, Color.WHITE.hashCode());
		this.fontRendererObj.drawString("Target Player", x + 40, y - 85, Color.WHITE.hashCode());
		this.fontRendererObj.drawString("Dim Time:", x + 40, y - 45, Color.WHITE.hashCode());
		if(this.selectedDimID == -100){
			this.fontRendererObj.drawString("Players in whole server:", x - 120, y - 25, Color.WHITE.hashCode());
		} else {
			this.fontRendererObj.drawString("Players in Dimension "+this.selectedDimID+":", x - 120, y - 25, Color.WHITE.hashCode());	
		}
		
		this.playerInfo.render();
	}

	private void renderConfirmDialog() {
		this.fontRendererObj.drawString("Are You Sure To Kick "+this.selectedPlayer, x - 85, y - 50, 0);
		this.fontRendererObj.drawString("back to Overworld?", x - 85, y - 30, 0);
	}
	
	public void setPlayerInfoPanel(List<String> playerNames){
		this.playerInfo = new GuiScrollPane(this, x - 120, y - 5, playerNames);
	}
	
	public void setPage(int pageID){
		this.pageID = pageID;
	}
	
	public void setSelectedDimID(int dimID){
		this.selectedDimID = dimID;
	}
	
	public void setDimTimeInfo(long currentDimTime){
		this.currentDimTime = currentDimTime;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {

	}
}
