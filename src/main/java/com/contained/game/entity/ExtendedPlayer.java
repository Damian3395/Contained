package com.contained.game.entity;

import java.util.ArrayList;

import com.contained.game.Contained;
import com.contained.game.data.Data;
import com.contained.game.ui.perks.ClassPerks;
import com.contained.game.util.Resources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

public class ExtendedPlayer implements IExtendedEntityProperties {
	private final static String EXT_PROP_NAME = "ExtendedPlayer";
	private final EntityPlayer entity;
	public int lives = Contained.configs.pvpMaxLives;
	private int[] occupationValues = null;
	public int occupationClass = ClassPerks.NONE;
	public int occupationLevel = 0;
	public ArrayList<Integer> perks = new ArrayList<Integer>();
	public ArrayList<String> achievements = new ArrayList<String>();
	public int usedOwnItems = 0;	//# of times player used an item they owned themselves.
	public int usedOthersItems = 0; //# of times player used an item owned by someone else.
	public int usedByOthers = 0;    //# of times another player used an item owned by this player.
	private boolean isAdmin = false;
	public boolean isSpectator = false;
	private boolean inGame = false;
	private boolean joiningGame = false;
	public int gameMode = Resources.OVERWORLD;
	public int gameID = -1;
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public int pvpWon = 0;
	public int pvpLost = 0;
	public int treasureWon = 0;
	public int treasureLost = 0;
	public int kills = 0;
	public int deaths = 0;
	public int antiTerritory = 0;
	public int treasuresOpened = 0;
	public int altersActivated = 0;
	public int curKills = 0;
	public int curDeaths = 0;
	public int curAntiTerritory = 0;
	public int curTreasuresOpened = 0;
	public int curAltersActivated = 0;
	public String world = "";
	public double spawnX = 0.0;
	public double spawnY = 0.0;
	public double spawnZ = 0.0;
	public double spawnMiniGameX = 0.0;
	public double spawnMiniGameY = 0.0;
	public double spawnMiniGameZ = 0.0;
	
	public ExtendedPlayer(EntityPlayer entity) {
		this.entity = entity;
	}
	
	public static final void register(EntityPlayer entity) {
		entity.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(entity));
	}
	
	public static final ExtendedPlayer get(EntityPlayer entity) {
		return (ExtendedPlayer)entity.getExtendedProperties(EXT_PROP_NAME);
	}
	
	public void increaseOccupation(int occID, int amount) {
		getOccupationValues()[occID] += amount;
	}
	
	public void setOccupation(int occID, int val) {
		getOccupationValues()[occID] = val;
	}
	
	public int getOccupationValue(int occID) {
		return getOccupationValues()[occID];
	}
	
	public int[] getOccupationValues() {
		if (this.occupationValues == null) {
			this.occupationValues = new int[Data.occupationNames.length];
			Data.zero(this.occupationValues);
		}
		return this.occupationValues;
	}
	
	public int getOccupationClass(){
		return this.occupationClass;
	}
	
	public void setOccupationClass(int occupation){
		this.occupationClass = occupation;
	}
	
	public ArrayList<String> getAchievement(){
		return this.achievements;
	}
	
	public void setAchievement(int index, String val){
		this.achievements.set(index, val);
	}
	
	public void addPerk(int perkID){
		if(!perks.contains(perkID))
			perks.add(perkID);
	}
	
	public boolean isAdmin(){
		return this.isAdmin;
	}
	
	public void setAdminRights(boolean rights){
		this.isAdmin = rights;
	}
	
	public boolean isSpectator(){
		return this.isSpectator;
	}
	
	public void setSpectator(boolean spectator){
		this.isSpectator = spectator;
	}
	
	public boolean inGame(){
		return this.inGame;
	}
	
	public void setGame(boolean game){
		this.inGame = game;
	}
	
	public void removeLife(){
		if(this.lives > 0)
			this.lives--;
	}
	
	public void addLife(){
		if(this.lives < Contained.configs.pvpMaxLives)
			this.lives++;
	}
	
	public void resurrect(){
		if(this.lives == 0)
			this.lives = Contained.configs.pvpResurrectLives;
	}
	
	public void setLives(int lives){
		this.lives = lives;
	}
	
	public void setGameMode(int gameMode){
		this.gameMode = gameMode;
	}
	
	public void setJoiningGame(boolean joining){
		this.joiningGame = joining;
	}
	
	public boolean isWaitingForMiniGame(){
		return joiningGame;
	}

	@Override
	public void loadNBTData(NBTTagCompound load) {
		this.lives = load.getInteger("lives_pt");
		this.inGame = load.getBoolean("inGame");
		this.joiningGame = load.getBoolean("joiningGame");
		this.gameMode = load.getInteger("gameMode");
		this.gameID = load.getInteger("gameID");
		this.curKills = load.getInteger("curKills");
		this.curDeaths = load.getInteger("curDeaths");
		this.curAntiTerritory = load.getInteger("curAntiTerritory");
		this.curTreasuresOpened = load.getInteger("curTreasuresOpened");
		this.curAltersActivated = load.getInteger("curAltersActivated");
		this.world = load.getString("world");
		
		this.occupationValues = load.getIntArray("occupationValues");
		this.occupationClass = load.getInteger("occupationClass");
		this.occupationLevel = load.getInteger("occupationLevel");
		
		this.usedOwnItems = load.getInteger("usedOwnItems");
		this.usedOthersItems = load.getInteger("usedOthersItems");
		this.usedByOthers = load.getInteger("usedByOthers");
		
		this.achievements.clear();
		NBTTagList list = load.getTagList("achievements", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			this.achievements.add(nbt.getString(Integer.toString(i)));
		}
		
		int[] temp = load.getIntArray("perks");
		this.perks.clear();
		for(int i = 0; i < temp.length; i++)
			perks.add(temp[i]);
		
		this.isAdmin = load.getBoolean("isAdmin");
		this.isSpectator = load.getBoolean("isSpectator");
		
		this.posX = load.getInteger("posX");
		this.posY = load.getInteger("posY");
		this.posZ = load.getInteger("posZ");
		
		this.pvpWon = load.getInteger("pvpWon");
		this.pvpLost = load.getInteger("pvpLost");
		this.treasureWon = load.getInteger("treasureWon");
		this.treasureLost = load.getInteger("treasureLost");
		this.kills = load.getInteger("kills");
		this.deaths = load.getInteger("deaths");
		this.antiTerritory = load.getInteger("antiTerritory");
		this.treasuresOpened = load.getInteger("treasuresOpened");
		this.altersActivated = load.getInteger("altersActivated");
		
		this.spawnX = load.getDouble("spawnX");
		this.spawnY = load.getDouble("spawnY");
		this.spawnZ = load.getDouble("spawnz");
		
		this.spawnMiniGameX = load.getDouble("spawnMiniGameX");
		this.spawnMiniGameY = load.getDouble("spawnMiniGameY");
		this.spawnMiniGameZ = load.getDouble("spawnMiniGameZ");
	}

	@Override
	public void saveNBTData(NBTTagCompound save) {
		save.setInteger("lives_pt", this.lives);
		save.setBoolean("inGame", this.inGame);
		save.setBoolean("joiningGame", this.joiningGame);
		save.setInteger("gameMode", this.gameMode);
		save.setInteger("gameID", this.gameID);
		save.setInteger("curKills", this.curKills);
		save.setInteger("curDeaths", this.curDeaths);
		save.setInteger("curAntiTerritory", this.curAntiTerritory);
		save.setInteger("curTreasuresOpened", this.curTreasuresOpened);
		save.setInteger("curAltersActivated", this.curAltersActivated);
		save.setString("world", this.world);
		
		save.setIntArray("occupationValues", getOccupationValues());
		save.setInteger("occupationClass", getOccupationClass());
		save.setInteger("occupationLevel", this.occupationLevel);
		
		save.setInteger("usedOwnItems", this.usedOwnItems);
		save.setInteger("usedOthersItems", this.usedOthersItems);
		save.setInteger("usedByOthers", this.usedByOthers);
		
		NBTTagList list = new NBTTagList();
		for(int i = 0;i < this.achievements.size(); i++){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString(Integer.toString(i), this.achievements.get(i));
			list.appendTag(nbt);
		}
		save.setTag("achievements", list);
		
		int[] temp = new int[perks.size()];
		for(int i = 0; i < perks.size(); i++)
			temp[i] = perks.get(i);
		save.setIntArray("perks", temp);
		
		save.setBoolean("isAdmin", this.isAdmin);
		save.setBoolean("isSpectator", this.isSpectator);
		
		save.setInteger("posX", this.posX);
		save.setInteger("posY", this.posY);
		save.setInteger("posZ", this.posZ);
		
		save.setInteger("pvpWon", this.pvpWon);
		save.setInteger("pvpLost", this.pvpLost);
		save.setInteger("treasureWon", this.treasureWon);
		save.setInteger("treasureLost", this.treasureLost);
		save.setInteger("kills", this.kills);
		save.setInteger("deaths", this.deaths);
		save.setInteger("antiTerritory", this.antiTerritory);
		save.setInteger("treasuresOpened", this.treasuresOpened);
		save.setInteger("altersActivated", this.altersActivated);
		
		save.setDouble("spawnX", this.spawnX);
		save.setDouble("spawnY", this.spawnY);
		save.setDouble("spawnZ", this.spawnZ);
		
		save.setDouble("spawnMiniGameX", this.spawnMiniGameX);
		save.setDouble("spawnMiniGameY", this.spawnMiniGameY);
		save.setDouble("spawnMiniGameZ", this.spawnMiniGameZ);
	}

	@Override
	public void init(Entity entity, World world) {}
}
