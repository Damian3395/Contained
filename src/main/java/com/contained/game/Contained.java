package com.contained.game;

import com.contained.game.handler.FMLEvents;
import com.contained.game.handler.PlayerEvents;
import com.contained.game.handler.TerritoryEvents;
import com.contained.game.handler.WorldEvents;
import com.contained.game.network.CommonProxy;
import com.contained.game.util.Debug;
import com.contained.game.util.Resources;
import com.contained.game.world.GenerateWorld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Resources.MOD_ID, name=Resources.NAME, version=Resources.VERSION)
public class Contained{
	GenerateWorld world = new GenerateWorld();
	
	@SidedProxy(clientSide="com.contained.game.network.ClientProxy", serverSide="com.contained.game.network.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(Resources.MOD_ID)
	public static Contained instance;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new Debug());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new WorldEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerEvents());
		MinecraftForge.EVENT_BUS.register(new TerritoryEvents());
		FMLCommonHandler.instance().bus().register(new FMLEvents());
		world.init();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		world.preInit(event);
		proxy.registerRenderers(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		
	}
}
