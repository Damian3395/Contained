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
	GenerateWorld world = new GenerateWorld();
	ContainedRegistry registry = new ContainedRegistry();
	
	public static HashMap<Point, String> territoryData; // [SERVER & CLIENT SIDE] coordinates, teamID. Locations of all blocks that are owned by a team.
	public static ArrayList<PlayerTeam>  teamData;      // [SERVER & (partial) CLIENT SIDE] all created player teams on the server.
	public static ArrayList<PlayerTeamIndividual> teamMemberData;  // [SERVER SIDE] all tracked players, online and offline, even those not in teams.
																   // [CLIENT SIDE] only the data on the local player, as well as display names and team IDs of others.
	public static ArrayList<PlayerTeamInvitation> teamInvitations; // [SERVER SIDE] all pending team invitations.
																   // [CLIENT SIDE] all invitations pertaining to the local player.
	public static ArrayList<PlayerTrade> trades;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		//event.registerServerCommand(new CommandDebugOreGen());
		event.registerServerCommand(new CommandTeamChat());
		event.registerServerCommand(new CommandBecomeAdmin());
		event.registerServerCommand(new CommandCreate());
		event.registerServerCommand(new CommandChangeStatus());
		event.registerServerCommand(new CommandTutorialBook());
		event.registerServerCommand(new CommandSurveyBook());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		territoryData = new HashMap<Point, String>();
		teamData = new ArrayList<PlayerTeam>();
		teamMemberData = new ArrayList<PlayerTeamIndividual>();
		teamInvitations = new ArrayList<PlayerTeamInvitation>();
		trades = new ArrayList<PlayerTrade>();
		
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
		configs = new Settings(event);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		world.preInit(event);
		proxy.registerRenderers(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		
	}
}
