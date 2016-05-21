package com.contained.game.network;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

import com.contained.game.Contained;
import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.GuiAdmin;
import com.contained.game.ui.GuiTownManage;
import com.contained.game.ui.games.GameOverUI;
import com.contained.game.ui.games.GuiMiniGames;
import com.contained.game.ui.games.GuiScoreboard;
import com.contained.game.ui.guild.GuiGuild;
import com.contained.game.ui.guild.GuildBase;
import com.contained.game.ui.guild.GuildLeader;
import com.contained.game.ui.perks.ClassPerks;
import com.contained.game.ui.survey.GuiSurvey;
import com.contained.game.ui.territory.TerritoryRender;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.user.PlayerTrade;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.TerritoryMachineTE;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.relauncher.Side;

/**
 * Handling of packets sent from server to client.
 */
public class ClientPacketHandler extends ServerPacketHandler {
	//private DataVisualization gui;
	private TerritoryRender render;
	
	public ClientPacketHandler(DataVisualization gui, TerritoryRender render) {
		//this.gui = gui;
		this.render = render;
	}
	
	@SubscribeEvent
	public void handlePacket(ClientCustomPacketEvent event) {	
		BlockCoord bc;
		TileEntity te;
		String teamID;
		int num;
		channelName = event.packet.channel();
		Minecraft mc = Minecraft.getMinecraft();
		
		if (channelName.equals(Resources.MOD_ID) && event.packet.getTarget() == Side.CLIENT) {
			PacketCustom packet = new PacketCustom(event.packet.payload());
			
			String status;
			int color;
			switch(packet.getType()) {
				case ClientPacketHandlerUtil.OCCUPATIONAL_DATA:
					for(int i=0; i<Data.occupationNames.length; i++)
						ExtendedPlayer.get(mc.thePlayer).setOccupation(i, packet.readInt());
				break;
					
				case ClientPacketHandlerUtil.ITEM_USAGE_DATA:
					ExtendedPlayer.get(mc.thePlayer).usedOwnItems = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).usedOthersItems = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).usedByOthers = packet.readInt();
				break;
					
				case ClientPacketHandlerUtil.FULL_TERRITORY_SYNC:
					int numBlocks = packet.readInt();
					Contained.getTerritoryMap(0).clear();
					for(int i=0; i<numBlocks; i++) {
						bc = packet.readCoord();
						Contained.getTerritoryMap(0).put(new Point(bc.x, bc.z), packet.readString());
					}
					render.regenerateEdges();
				break;
					
				case ClientPacketHandlerUtil.ADD_TERRITORY_BLOCK:
					bc = packet.readCoord();
					Contained.getTerritoryMap(0).put(new Point(bc.x, bc.z), packet.readString());
					render.regenerateEdges();
				break;
				
				case ClientPacketHandlerUtil.REMOVE_TERRITORY_BLOCK:
					bc = packet.readCoord();
					Contained.getTerritoryMap(0).remove(new Point(bc.x, bc.z));
					render.regenerateEdges();
				break;
					
				case ClientPacketHandlerUtil.SYNC_TEAMS:
					int numTeamsBefore = Contained.getTeamList(0).size();
					Contained.getTeamList(0).clear();
					int numTeams = packet.readInt();
					for(int i=0; i<numTeams; i++) {
						PlayerTeam readTeam = new PlayerTeam(packet.readNBTTagCompound());
						Contained.getTeamList(0).add(readTeam);
					}
						
					if (Contained.getTeamList(0).size() < numTeamsBefore) {
						//Some team got disbanded. Need to remove stale territory blocks.
						ArrayList<Point> terrToRemove = new ArrayList<Point>();
						for(Point p : Contained.getTerritoryMap(0).keySet()) {
							String terrID = Contained.getTerritoryMap(0).get(p);
							if (PlayerTeam.get(terrID,0) == null)
								terrToRemove.add(p);
						}
						for (Point p : terrToRemove)
							Contained.getTerritoryMap(0).remove(p);
						render.regenerateEdges();
					}
				break;
				
				case ClientPacketHandlerUtil.TE_PARTICLE:
					te = mc.theWorld.getTileEntity(packet.readInt(), packet.readInt(), packet.readInt());
					if (te instanceof TerritoryMachineTE) {
						TerritoryMachineTE machine = (TerritoryMachineTE)te;
						machine.displayParticle = packet.readString();
					}
				break;
				
				case ClientPacketHandlerUtil.TMACHINE_STATE:
					te = mc.theWorld.getTileEntity(packet.readInt(), packet.readInt(), packet.readInt());
					if (te instanceof TerritoryMachineTE) {
						TerritoryMachineTE machine = (TerritoryMachineTE)te;
						machine.tickTimer = packet.readInt();
						teamID = packet.readString();
						if (teamID.equals(""))
							teamID = null;
						machine.teamID = teamID;
						machine.shouldClaim = packet.readBoolean();
						machine.refreshColor();
					}
				break;
				
				case ClientPacketHandlerUtil.GUILD_JOIN:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Joined Team")){
						mc.displayGuiScreen(new GuiGuild());
					}else if(mc.currentScreen instanceof GuiGuild){
						GuildBase.statusInfo = status;
						GuildBase.statusColor = new Color(color);
					}
				break;
				
				case ClientPacketHandlerUtil.GUILD_LEAVE:
					if(mc.currentScreen instanceof GuiGuild)
						mc.displayGuiScreen(new GuiGuild());
				break;
				
				case ClientPacketHandlerUtil.GUILD_CREATE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Team Successfully Created")){
						mc.displayGuiScreen(new GuiGuild());
					}else if(mc.currentScreen instanceof GuiGuild){
						GuildBase.statusInfo = status;
						GuildBase.statusColor = new Color(color);
					}
				break;
				
				case ClientPacketHandlerUtil.GUILD_DISBAND:
					if(mc.currentScreen instanceof GuiGuild)
						mc.displayGuiScreen(new GuiGuild());
				break;
				
				case ClientPacketHandlerUtil.GUILD_UPDATE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Changed Saved")){
						if(mc.currentScreen instanceof GuiGuild){
							GuildLeader.teamUpdateStatus = status;
							GuildLeader.teamUpdateColor = new Color(color);
						}
					}
				break;
				
				case ClientPacketHandlerUtil.PLAYER_INVITE:
					
				break;
				
				case ClientPacketHandlerUtil.PLAYER_DECLINE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Invitation has been removed")){
						if(mc.currentScreen instanceof GuiGuild){
							GuildBase.invites.remove(GuildBase.currentCol);
							GuildBase.currentCol = (GuildBase.currentCol < GuildBase.invites.size()) ? GuildBase.currentCol++ : 0;
						}
					}else if(mc.currentScreen instanceof GuiGuild){
						GuildBase.statusInfo = status;
						GuildBase.statusColor = new Color(color);
					}
				break;
				
				case ClientPacketHandlerUtil.PLAYER_KICK:
					if(mc.currentScreen instanceof GuiGuild)
						mc.displayGuiScreen(new GuiGuild());
				break;
				
				case ClientPacketHandlerUtil.PLAYER_PROMOTE:
					
				break;
				
				case ClientPacketHandlerUtil.PLAYER_DEMOTE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Successfully Demoted")){
						if(mc.currentScreen instanceof GuiGuild)
							mc.displayGuiScreen(new GuiGuild());
					}
				break;
					
				case ClientPacketHandlerUtil.LEVEL_UP:
					ExtendedPlayer.get(mc.thePlayer).occupationLevel = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).addPerk(packet.readInt());
					if(mc.currentScreen instanceof ClassPerks)
						mc.displayGuiScreen(new ClassPerks());
				break;
					
				case ClientPacketHandlerUtil.SELECT_CLASS:
					ExtendedPlayer.get(mc.thePlayer).occupationClass = packet.readInt();
					if(mc.currentScreen instanceof ClassPerks)
						mc.displayGuiScreen(new ClassPerks());
				break;
				
				case ClientPacketHandlerUtil.PERK_INFO:
					int perkID;
					for(int i = 0; i < 5; i++)
						if((perkID = packet.readInt()) != -1)
							ExtendedPlayer.get(mc.thePlayer).addPerk(perkID);
					ExtendedPlayer.get(mc.thePlayer).occupationClass = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).occupationLevel = packet.readInt();
				break;
				
				case ClientPacketHandlerUtil.UPDATE_PERMISSIONS:
					PlayerTeam team = new PlayerTeam(packet.readNBTTagCompound());					
					PlayerTeam toModify = PlayerTeam.get(team,0);
					toModify.permissions = team.permissions;
				break;
				
				case ClientPacketHandlerUtil.SYNC_LOCAL_PLAYER:
					NBTTagCompound ntc = packet.readNBTTagCompound();
					PlayerTeamIndividual pdata = new PlayerTeamIndividual(ntc);
					boolean found = false;
					for(PlayerTeamIndividual player : Contained.teamMemberData) {
						if (player.playerName.equals(pdata.playerName)) {
							found = true;
							player.readFromNBT(ntc);
							break;
						}
					}
					if (!found)
						Contained.teamMemberData.add(pdata);
				break;
				
				case ClientPacketHandlerUtil.REMOVE_ITEM:
					int slotId = packet.readInt() + 9;
					if(slotId >= mc.thePlayer.inventory.mainInventory.length)
						slotId -= mc.thePlayer.inventory.mainInventory.length;
					
					if(mc.thePlayer.inventory.getStackInSlot(slotId) != null)
						mc.thePlayer.inventory.setInventorySlotContents(slotId, null);
					
					if(mc.currentScreen instanceof GuiTownManage) {
						GuiTownManage guiTown = (GuiTownManage)mc.currentScreen;
						mc.displayGuiScreen(new GuiTownManage(mc.thePlayer.inventory, guiTown.te, guiTown.blockTeamID, guiTown.playerTeamID));
					}
				break;
				
				case ClientPacketHandlerUtil.ADD_ITEM:
					ItemStack item = packet.readItemStack();
					if(mc.thePlayer.inventory.getFirstEmptyStack() > -1 && item != null)
						mc.thePlayer.inventory.addItemStackToInventory(item);
					else if (item != null) {
						// Player's inventory was full, drop this item on the ground 
						// near them instead.
						mc.theWorld.spawnEntityInWorld(new EntityItem(mc.theWorld, 
								mc.thePlayer.posX, mc.thePlayer.posY+1, mc.thePlayer.posZ, 
								item));
					}
					
					if(mc.currentScreen instanceof GuiTownManage) {
						GuiTownManage guiTown = (GuiTownManage)mc.currentScreen;
						mc.displayGuiScreen(new GuiTownManage(mc.thePlayer.inventory, guiTown.te, guiTown.blockTeamID, guiTown.playerTeamID));
					}
				break;
				
				case ClientPacketHandlerUtil.CREATE_TRADE:
					PlayerTrade addTrade = new PlayerTrade(packet.readNBTTagCompound());
					if(addTrade != null && addTrade.offer != null && addTrade.request != null)
						Contained.getTradeList(0).add(addTrade);
					
					if(mc.currentScreen instanceof GuiTownManage) {
						GuiTownManage guiTown = (GuiTownManage)mc.currentScreen;
						mc.displayGuiScreen(new GuiTownManage(mc.thePlayer.inventory, guiTown.te, guiTown.blockTeamID, guiTown.playerTeamID));
					}
				break;
				
				case ClientPacketHandlerUtil.REMOVE_TRADE:
					String UUID = packet.readString();
					if(UUID.isEmpty())
						return;
					
					for(PlayerTrade remTrade : Contained.getTradeList(0))
						if(remTrade.id.equals(UUID)){
							Contained.trades.remove(remTrade);
							break;
						}
					
					if(mc.currentScreen instanceof GuiTownManage) {
						GuiTownManage guiTown = (GuiTownManage)mc.currentScreen;
						mc.displayGuiScreen(new GuiTownManage(mc.thePlayer.inventory, guiTown.te, guiTown.blockTeamID, guiTown.playerTeamID));
					}
				break;
				
				case ClientPacketHandlerUtil.TRADE_TRANS:
					ItemStack offer = packet.readItemStack();
					ItemStack request = packet.readItemStack();
					
					if(offer == null){
						mc.thePlayer.inventory.addItemStackToInventory(request);
					}else{
						int count = request.stackSize;
						for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++){
							ItemStack itemRemove = mc.thePlayer.inventory.getStackInSlot(i);
							if(itemRemove != null && itemRemove.getItem().equals(request.getItem())){
								if((count-itemRemove.stackSize) > 0){
									mc.thePlayer.inventory.setInventorySlotContents(i, null);
									count -= itemRemove.stackSize;
								}else{
									mc.thePlayer.inventory.decrStackSize(i, itemRemove.stackSize-count);
									count = 0;
									break;
								}
							}
						}
						
						mc.thePlayer.inventory.addItemStackToInventory(offer);
					}
					
					if(mc.currentScreen instanceof GuiTownManage) {
						GuiTownManage guiTown = (GuiTownManage)mc.currentScreen;
						mc.displayGuiScreen(new GuiTownManage(mc.thePlayer.inventory, guiTown.te, guiTown.blockTeamID, guiTown.playerTeamID));
					}
				break;
				
				case ClientPacketHandlerUtil.SYNC_TRADE:
					Contained.trades.clear();
					int numTrades = packet.readInt();
					for(int i=0; i<numTrades; i++) {
						PlayerTrade readTrade = new PlayerTrade(packet.readNBTTagCompound());
						if(readTrade != null && readTrade.offer != null && readTrade.request != null)
							Contained.getTradeList(0).add(readTrade);
					}
				break;
				
				case ClientPacketHandlerUtil.PLAYER_ADMIN:
					mc.thePlayer.setInvisible(true);
					mc.thePlayer.capabilities.allowFlying = true;
					mc.thePlayer.capabilities.disableDamage = true;
					mc.thePlayer.capabilities.isCreativeMode = true;
					ExtendedPlayer.get(mc.thePlayer).setAdminRights(true);
				break;
				
				case ClientPacketHandlerUtil.NEW_PLAYER:
					Contained.teamMemberData.add(new PlayerTeamIndividual(packet.readString()));
				break;
				
				case ClientPacketHandlerUtil.UPDATE_PLAYER:
					String name = packet.readString();
					PlayerTeamIndividual toUpdate = null;
					for(PlayerTeamIndividual player : Contained.teamMemberData) {
						if (player.playerName.equals(name)) {
							toUpdate = player;
							break;
						}
					}
					
					if (toUpdate == null) {
						toUpdate = new PlayerTeamIndividual(name);
						Contained.teamMemberData.add(toUpdate);
					}
					
					teamID = packet.readString();
					if (!teamID.equals(""))
						toUpdate.teamID = teamID;
				break;
				
				case ClientPacketHandlerUtil.PLAYER_LIST:
					PlayerTeamIndividual self = PlayerTeamIndividual.get(mc.thePlayer);
					Contained.teamMemberData.clear();
					if (self != null)
						Contained.teamMemberData.add(self);
					num = packet.readInt();
					for(int i=0; i<num; i++) {
						PlayerTeamIndividual newPlayer = new PlayerTeamIndividual(packet.readString());
						teamID = packet.readString();
						if (newPlayer.playerName.equals(mc.thePlayer.getDisplayName()))
							continue;
						else {
							if (!teamID.equals(""))
								newPlayer.teamID = teamID;
							Contained.teamMemberData.add(newPlayer);
						}
					}
				break;
				
				case ClientPacketHandlerUtil.SYNC_INVITATIONS:
					Contained.teamInvitations.clear();
					num = packet.readInt();
					for(int i=0; i<num; i++)
						Contained.teamInvitations.add(new PlayerTeamInvitation(packet.readNBTTagCompound()));
				break;
				
				case ClientPacketHandlerUtil.END_GAME:
					mc.displayGuiScreen(new GameOverUI());
				break;
				
				case ClientPacketHandlerUtil.RESURRECT:
					ExtendedPlayer resurrectLife = ExtendedPlayer.get(mc.thePlayer);
					resurrectLife.resurrect();
				break;
				
				case ClientPacketHandlerUtil.SYNC_LIVES:
					ExtendedPlayer syncLife = ExtendedPlayer.get(mc.thePlayer);
					syncLife.lives = packet.readInt();
				break;
				
				case ClientPacketHandlerUtil.PLAYER_SPECTATOR:
					mc.thePlayer.setInvisible(true);
					mc.thePlayer.capabilities.allowFlying = true;
					mc.thePlayer.capabilities.disableDamage = true;
					mc.thePlayer.capabilities.allowEdit = false;
					ExtendedPlayer.get(mc.thePlayer).setSpectator(true);
				break;
				
				case ClientPacketHandlerUtil.PLAYER_NORMAL:
					if(mc.currentScreen instanceof GuiAdmin)
						mc.thePlayer.closeScreen();
					
					ExtendedPlayer playerState = ExtendedPlayer.get(mc.thePlayer);
					mc.thePlayer.setInvisible(false);
					mc.thePlayer.capabilities.allowFlying = false;
					mc.thePlayer.capabilities.disableDamage = false;
					mc.thePlayer.capabilities.allowEdit = true;
					mc.thePlayer.capabilities.isCreativeMode = false;
					
					if(playerState.isAdmin())
						playerState.setAdminRights(false);
					if(playerState.isSpectator)
						playerState.setSpectator(false);
				break;
				
				case ClientPacketHandlerUtil.MINIGAME_TIMER_SYNC:
					Contained.timeLeft[0] = packet.readInt();
					if (Contained.timeLeft[0] == 0)
						Contained.gameActive[0] = false;
					else
						Contained.gameActive[0] = true;
				break;
				
				case ClientPacketHandlerUtil.JOIN_MINI_GAME:
					ExtendedPlayer joinMiniGame = ExtendedPlayer.get(mc.thePlayer);
					joinMiniGame.setJoiningGame(true);
					
					if(mc.currentScreen instanceof GuiMiniGames)
						mc.displayGuiScreen(new GuiMiniGames());
				break;
				
				case ClientPacketHandlerUtil.CANCEL_JOIN_MINI_GAME:
					ExtendedPlayer cancelMiniGame = ExtendedPlayer.get(mc.thePlayer);
					cancelMiniGame.setJoiningGame(false);
					
					if(mc.currentScreen instanceof GuiMiniGames)
						mc.displayGuiScreen(new GuiMiniGames());
				break;
				
				case ClientPacketHandlerUtil.MINIGAME_STARTED:					
					ExtendedPlayer startMiniGame = ExtendedPlayer.get(mc.thePlayer);
					startMiniGame.setGameMode(packet.readInt());
					startMiniGame.gameID = packet.readInt();
					startMiniGame.setJoiningGame(false);
					startMiniGame.setGame(true);
					
					PlayerMiniGame newGame = new PlayerMiniGame(packet.readNBTTagCompound());
					Contained.miniGames.add(newGame);
					int dimID = packet.readInt();
					int teams = packet.readInt();
					Contained.getTeamList(dimID).clear();
					for(int i = 0; i < teams; i++){
						PlayerTeam newTeam = new PlayerTeam(packet.readNBTTagCompound());
						Contained.getTeamList(dimID).add(newTeam);
					}
				break;
				
				case ClientPacketHandlerUtil.MINIGAME_ENDED:
					ExtendedPlayer endMiniGame = ExtendedPlayer.get(mc.thePlayer);
					endMiniGame.setGameMode(Resources.OVERWORLD);
					endMiniGame.gameID = -1;
					endMiniGame.setGame(false);
					int removeDim = packet.readInt();
					Contained.getTeamList(removeDim).clear();
					for(PlayerMiniGame game : Contained.miniGames)
						if(game.getGameDimension() == removeDim){
							Contained.miniGames.remove(game);
							break;
						}
					for(int i = 0; i < Contained.gameScores[removeDim].length; i++)
						Contained.gameScores[removeDim][i] = 0;
					
					if(MiniGameUtil.isTreasure(removeDim))
						Contained.getActiveTreasures(0).clear();
				break;
				
				case ClientPacketHandlerUtil.SYNC_PVP_STATS:
					ExtendedPlayer pvpStats = ExtendedPlayer.get(mc.thePlayer);
					pvpStats.pvpWon = packet.readInt();
					pvpStats.pvpLost = packet.readInt();
					pvpStats.kills = packet.readInt();
					pvpStats.deaths = packet.readInt();
					pvpStats.antiTerritory = packet.readInt();
				break;
					
				case ClientPacketHandlerUtil.SYNC_TEASURE_STATS:
					ExtendedPlayer treasureStats = ExtendedPlayer.get(mc.thePlayer);
					treasureStats.treasureWon = packet.readInt();
					treasureStats.treasureLost = packet.readInt();
					treasureStats.treasuresOpened = packet.readInt();
					treasureStats.altersActivated = packet.readInt();
				break;
				
				case ClientPacketHandlerUtil.SYNC_GAME_SCORE:
					int dim = packet.readInt();
					int teamNum = packet.readInt();
					int score = packet.readInt();
					Contained.gameScores[dim][teamNum] = score;
				break;
				
				case ClientPacketHandlerUtil.ADD_TREASURE_POINTS:
					int numToAdd = packet.readInt();
					boolean clearFirst = packet.readBoolean();
					if (clearFirst)
						Contained.getActiveTreasures(0).clear();
					for(int i=0; i<numToAdd; i++)
						Contained.getActiveTreasures(0).add(packet.readCoord());
				break;
				
				case ClientPacketHandlerUtil.REMOVE_TREASURE_POINTS:
					int numToRemove = packet.readInt();
					for(int i=0; i<numToRemove; i++)
						Contained.getActiveTreasures(0).remove(packet.readCoord());
				break;
				
				case ClientPacketHandlerUtil.SAVE_PLAYER:
					PlayerTeamIndividual storePdata = PlayerTeamIndividual.get(mc.thePlayer.getDisplayName());
					storePdata.xp = packet.readInt();
					storePdata.level = packet.readInt();
					storePdata.armor = new ItemStack[4];
					int armorSize = packet.readInt();
					for(int i = 0; i < armorSize; i++){
						int index = packet.readInt();
						ItemStack armor = ItemStack.loadItemStackFromNBT(packet.readNBTTagCompound());
						storePdata.armor[index] = armor;
					}
					
					storePdata.inventory = new ItemStack[36];
					int invSize = packet.readInt();
					for(int i = 0; i < invSize; i++){
						int index = packet.readInt();
						ItemStack itemStore = ItemStack.loadItemStackFromNBT(packet.readNBTTagCompound());
						storePdata.inventory[index] = itemStore;
					}
					
					mc.thePlayer.addExperience(-(mc.thePlayer.experienceTotal));
					MiniGameUtil.clearMainInventory(mc.thePlayer);
					MiniGameUtil.clearArmorInventory(mc.thePlayer);
				break;
				
				case ClientPacketHandlerUtil.RESTORE_PLAYER:
					MiniGameUtil.clearMainInventory(mc.thePlayer);
					MiniGameUtil.clearArmorInventory(mc.thePlayer);
					
					mc.thePlayer.addExperience(packet.readInt());
					mc.thePlayer.addExperienceLevel(packet.readInt());
					int armorSizeRestore = packet.readInt();
					for(int i = 0; i < armorSizeRestore; i++){
						int index = packet.readInt();
						ItemStack armor = ItemStack.loadItemStackFromNBT(packet.readNBTTagCompound());
						mc.thePlayer.inventory.armorInventory[index] = armor;
					}
					
					int invSizeRestore = packet.readInt();
					for(int i = 0; i < invSizeRestore; i++){
						int index = packet.readInt();
						ItemStack itemStore = ItemStack.loadItemStackFromNBT(packet.readNBTTagCompound());
						mc.thePlayer.inventory.mainInventory[index] = itemStore;
					}
				break;
				
				case ClientPacketHandlerUtil.ADMIN_WORLD_INFO:
					int selectedDimID = packet.readInt();
					int playerCount=packet.readInt();
					ArrayList<String> playerNames = new ArrayList<String>();
					for(int i=0; i<playerCount; i++){
						playerNames.add(packet.readString());
					}
					if(mc.currentScreen instanceof GuiAdmin){
						GuiAdmin newGuiAdmin = new GuiAdmin();
						mc.displayGuiScreen(newGuiAdmin);
						newGuiAdmin.setPage(newGuiAdmin.PLAYER_PAGE);
						newGuiAdmin.setSelectedDimID(selectedDimID);
						newGuiAdmin.setPlayerInfoPanel(playerNames);
						newGuiAdmin.setDimTimeInfo(DimensionManager.getWorld(selectedDimID).getWorldTime());
					}
				break;
				
				case ClientPacketHandlerUtil.START_SURVEY:
					if(!mc.thePlayer.isInvisible() || !mc.thePlayer.capabilities.disableDamage){
						mc.thePlayer.setInvisible(true);
						mc.thePlayer.capabilities.disableDamage = true;
					}
					if(!(mc.currentScreen instanceof GuiSurvey))
						mc.displayGuiScreen(new GuiSurvey(PlayerTeamIndividual.get(mc.thePlayer)));
				break;
				
				case ClientPacketHandlerUtil.LEADERBOARD_PVP_UPDATE:
					if(MiniGameUtil.isPvP(mc.thePlayer.dimension) 
							&& mc.currentScreen instanceof GuiScoreboard){
						int size = packet.readInt();
						for(int i = 0; i < size; i++){
							String userPvp = packet.readString();
							GuiScoreboard.kills.put(userPvp, packet.readInt());
							GuiScoreboard.deaths.put(userPvp, packet.readInt());
							GuiScoreboard.territory.put(userPvp, packet.readInt());
						}
						GuiScoreboard.updated = true;
					}
				break;
				
				case ClientPacketHandlerUtil.LEADERBOARD_TREASURE_UPDATE:
					if(MiniGameUtil.isTreasure(mc.thePlayer.dimension)
							&& mc.currentScreen instanceof GuiScoreboard){
						int size = packet.readInt();
						for(int i = 0; i < size; i++){
							String userTreasure = packet.readString();
							GuiScoreboard.treasures.put(userTreasure, packet.readInt());
							GuiScoreboard.alters.put(userTreasure, packet.readInt());
						}
						GuiScoreboard.updated = true;
					}
				break;
				
				case ClientPacketHandlerUtil.SET_CHAT_MODE:
					Contained.chatMode = packet.readInt();
					notifyChatMode(mc.thePlayer);
				break;
				
				case ClientPacketHandlerUtil.SWITCH_CHAT_MODE:
					if (Contained.chatMode == Resources.GLOBAL_CHAT)
						Contained.chatMode = Resources.TEAM_CHAT;
					else
						Contained.chatMode = Resources.GLOBAL_CHAT;
					notifyChatMode(mc.thePlayer);
				break;
			}
		}
	}
	
	public void notifyChatMode(EntityPlayer player) {
		if (Contained.chatMode == Resources.GLOBAL_CHAT)
			Util.displayMessage(player, "[*] You are now chatting publically.");
		else if (Contained.chatMode == Resources.TEAM_CHAT)
			Util.displayMessage(player, "[*] You are now chatting to your team members.");
	}
}
