package com.contained.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import com.contained.game.commands.*;
import com.contained.game.data.DataLogger;
import com.contained.game.handler.DataEvents;
import com.contained.game.handler.FMLDataEvents;
import com.contained.game.handler.FMLEvents;
import com.contained.game.handler.PlayerEvents;
import com.contained.game.handler.ProtectionEvents;
import com.contained.game.handler.RenderEvents;
import com.contained.game.handler.WorldEvents;
import com.contained.game.handler.games.PVPEvents;
import com.contained.game.handler.games.TreasureEvents;
import com.contained.game.handler.perks.BuilderEvents;
import com.contained.game.handler.perks.CollectorEvents;
import com.contained.game.handler.perks.CookEvents;
import com.contained.game.handler.perks.WarriorEvents;
import com.contained.game.handler.perks.WizardEvents;
import com.contained.game.network.CommonProxy;
import com.contained.game.ui.GuiHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.user.PlayerTrade;
import com.contained.game.util.Resources;
import com.contained.game.world.GenerateWorld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Resources.MOD_ID, name=Resources.NAME, version=Resources.VERSION)
public class Contained{
	@SidedProxy(clientSide="com.contained.game.network.ClientProxy", serverSide="com.contained.game.network.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(Resources.MOD_ID)
	public static Contained instance;
	
	public static FMLEventChannel channel; //For client -> server packets.
	
	public static Settings configs;
	public static GenerateWorld world = new GenerateWorld();
	ContainedRegistry registry = new ContainedRegistry();
	
	// Locations of all blocks that are owned by a team.
	// <Dimension ID, <Block Coordinate, Team ID>>
	// [SERVER] Stores territory data for all dimensions.
	// [CLIENT] Stores territory data for client's current dimension.
	//			(always uses ID 0 for the dimension)
	public static HashMap<Integer, HashMap<Point, String>> territoryData; 
	
	// All created player teams on the server. 
	// <Dimension ID, List of Teams>
	// [SERVER] Full team data for all teams in all dimensions.
	// [CLIENT] Partial team data (name, color, ID) for teams in client's dimension.
	//			(always uses ID 0 for the dimension)
	public static HashMap<Integer, ArrayList<PlayerTeam>>  teamData;      
	
	// Custom mod-relevant data about players on the server.
	// [SERVER] All tracked players, online and offline, even those not in teams.
	// [CLIENT] Only the data on the local player, as well as display names 
	//          and team IDs of others.
	public static ArrayList<PlayerTeamIndividual> teamMemberData;

	// Player trades currently listed in the marketplace.
	// <Dimension ID, List of Trades>
	// [SERVER] All trades in all dimensions.
	// [CLIENT] ???
	public static HashMap<Integer, ArrayList<PlayerTrade>> trades;
	
	// Pending invitations for players requesting to join teams. (FULL_TEAM_MODE only)
	// [SERVER] All pending team invitations.
	// [CLIENT] Only invitations pertaining to the client player.
	public static ArrayList<PlayerTeamInvitation> teamInvitations; 
				
	// Time left for a mini-game in a dimension.
	// [SERVER] Time remaining for all active mini-game dimensions.
	// [CLIENT] Time remaining for client player's current mini-game.
	//			(always uses ID 0 for the dimension)
	public static int[] timeLeft;
	
	// Is a mini-game running in the given dimension? Running means the
	// game has started and the timer is counting down.
	// [SERVER] Activity status of a game in all mini-game dimensions.
	// [CLIENT] Activity status of the client player's current mini-game.
	//	        (always uses ID 0 for the dimension)
	public static boolean[] gameActive;
	
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandTeamChat());
		event.registerServerCommand(new CommandBecomeAdmin());
		event.registerServerCommand(new CommandCreate());
		event.registerServerCommand(new CommandChangeStatus());
		event.registerServerCommand(new CommandTutorialBook());
		event.registerServerCommand(new CommandSurveyBook());
		event.registerServerCommand(new CommandTPX());
		event.registerServerCommand(new CommandTPXD());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		territoryData = new HashMap<Integer, HashMap<Point, String>>();
		teamData = new HashMap<Integer, ArrayList<PlayerTeam>>();
		teamMemberData = new ArrayList<PlayerTeamIndividual>();
		teamInvitations = new ArrayList<PlayerTeamInvitation>();
		trades = new HashMap<Integer, ArrayList<PlayerTrade>>();
		timeLeft = new int[Math.max(Resources.MAX_PVP_DIMID, Resources.MAX_TREASURE_DIMID)+1];
		gameActive = new boolean[Math.max(Resources.MAX_PVP_DIMID, Resources.MAX_TREASURE_DIMID)+1];
		
		MinecraftForge.EVENT_BUS.register(new WorldEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerEvents());
		MinecraftForge.EVENT_BUS.register(new DataEvents());
		MinecraftForge.EVENT_BUS.register(new BuilderEvents());
		MinecraftForge.EVENT_BUS.register(new CollectorEvents());
		MinecraftForge.EVENT_BUS.register(new CookEvents());
		MinecraftForge.EVENT_BUS.register(new WizardEvents());
		MinecraftForge.EVENT_BUS.register(new WarriorEvents());
		MinecraftForge.EVENT_BUS.register(new ProtectionEvents());
		MinecraftForge.EVENT_BUS.register(new PVPEvents());
		MinecraftForge.EVENT_BUS.register(new TreasureEvents());
		
		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new RenderEvents());
		}
		
		FMLCommonHandler.instance().bus().register(new FMLDataEvents());
		FMLCommonHandler.instance().bus().register(new FMLEvents());
		world.init();
		registry.init(event);
		
		Contained.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Resources.MOD_ID);
		proxy.init(event);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		new DataLogger();
		registry.preInit(event);		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		configs = new Settings(event);
		proxy.registerRenderers(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		
	}
	
	public static ArrayList<PlayerTeam> getTeamList(int dimID) {
		if (!teamData.containsKey(dimID))
			teamData.put(dimID, new ArrayList<PlayerTeam>());
		return teamData.get(dimID);
	}
	
	public static HashMap<Point, String> getTerritoryMap(int dimID) {
		if (!territoryData.containsKey(dimID))
			territoryData.put(dimID, new HashMap<Point, String>());
		return territoryData.get(dimID);
	}
	
	public static ArrayList<PlayerTrade> getTradeList(int dimID) {
		if (!trades.containsKey(dimID))
			trades.put(dimID, new ArrayList<PlayerTrade>());
		return trades.get(dimID);
	}
	
	public static void tickTimeLeft(int dimID) {
		if (gameActive[dimID]) {
			timeLeft[dimID] -= 1;
			if (timeLeft[dimID] < 0)
				timeLeft[dimID] = 0;
		}
	}
}
