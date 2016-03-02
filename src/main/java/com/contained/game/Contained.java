package com.contained.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import com.contained.game.commands.CommandChangeColor;
import com.contained.game.commands.CommandChangeName;
import com.contained.game.commands.CommandCreateTeam;
import com.contained.game.commands.CommandDemote;
import com.contained.game.commands.CommandInvitationAccept;
import com.contained.game.commands.CommandInvitationReject;
import com.contained.game.commands.CommandInvitationSend;
import com.contained.game.commands.CommandInvitationsView;
import com.contained.game.commands.CommandKickPlayer;
import com.contained.game.commands.CommandLeaveTeam;
import com.contained.game.commands.CommandPromote;
import com.contained.game.commands.CommandTeamChat;
import com.contained.game.commands.CommandViewColors;
import com.contained.game.commands.CommandViewTeamInfo;
import com.contained.game.commands.CommandViewTeams;
import com.contained.game.commands.CommandDebugOreGen;
import com.contained.game.data.DataLogger;
import com.contained.game.handler.DataEvents;
import com.contained.game.handler.FMLDataEvents;
import com.contained.game.handler.PerkEvents;
import com.contained.game.handler.PlayerEvents;
import com.contained.game.handler.ProtectionEvents;
import com.contained.game.handler.WorldEvents;
import com.contained.game.network.CommonProxy;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Resources.MOD_ID, name=Resources.NAME, version=Resources.VERSION)
public class Contained{
	@SidedProxy(clientSide="com.contained.game.network.ClientProxy", serverSide="com.contained.game.network.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(Resources.MOD_ID)
	public static Contained instance;
	
	public static Settings configs;
	GenerateWorld world = new GenerateWorld();
	ContainedRegistry registry = new ContainedRegistry();
	
	public static HashMap<Point, String> territoryData; //coordinates, teamID
	public static ArrayList<PlayerTeam>  teamData;
	public static ArrayList<PlayerTeamIndividual> teamMemberData;
	public static ArrayList<PlayerTeamInvitation> teamInvitations;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandDebugOreGen());
		event.registerServerCommand(new CommandChangeColor());
		event.registerServerCommand(new CommandChangeName());
		event.registerServerCommand(new CommandCreateTeam());
		event.registerServerCommand(new CommandInvitationAccept());
		event.registerServerCommand(new CommandInvitationReject());
		event.registerServerCommand(new CommandInvitationSend());
		event.registerServerCommand(new CommandInvitationsView());
		event.registerServerCommand(new CommandTeamChat());
		event.registerServerCommand(new CommandViewColors());
		event.registerServerCommand(new CommandViewTeamInfo());
		event.registerServerCommand(new CommandViewTeams());
		event.registerServerCommand(new CommandLeaveTeam());
		event.registerServerCommand(new CommandDemote());
		event.registerServerCommand(new CommandPromote());
		event.registerServerCommand(new CommandKickPlayer());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		territoryData = new HashMap<Point, String>();
		teamData = new ArrayList<PlayerTeam>();
		teamMemberData = new ArrayList<PlayerTeamIndividual>();
		teamInvitations = new ArrayList<PlayerTeamInvitation>();
		
		MinecraftForge.EVENT_BUS.register(new WorldEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerEvents());
		MinecraftForge.EVENT_BUS.register(new DataEvents());
		MinecraftForge.EVENT_BUS.register(new PerkEvents());
		MinecraftForge.EVENT_BUS.register(new ProtectionEvents());
		
		FMLCommonHandler.instance().bus().register(new FMLDataEvents());
		world.init();
		registry.init(event);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		new DataLogger();
		registry.preInit(event);
		configs = new Settings(event);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Contained.instance, proxy);
		
		world.preInit(event);
		proxy.registerRenderers(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		
	}
}
