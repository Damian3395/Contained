package com.contained.game;

import com.contained.game.handler.CommonProxy;
import com.contained.game.handler.FMLEvents;
import com.contained.game.handler.GlobalEvents;
import com.contained.game.util.Debug;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.GeneratePostWorld;
import com.contained.game.world.post.ResourceCluster;
import com.contained.game.world.post.ResourceGen;
import com.contained.game.world.post.TerrainGen;
import com.contained.game.world.post.biomes.WastelandBiome;
import com.contained.game.world.post.block.WastelandBlock;
import com.contained.game.world.post.block.WastelandBush;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = Resources.MOD_ID, name=Resources.NAME, version=Resources.VERSION)
public class Contained implements IFuelHandler{
	GeneratePostWorld postWorld = new GeneratePostWorld();
	
	@SidedProxy(clientSide="com.contained.game.handler.ClientProxy", serverSide="com.contained.game.handler.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(Resources.MOD_ID)
	public static Contained instance;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new Debug());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new GlobalEvents());
		postWorld.init();
		FMLCommonHandler.instance().bus().register(new FMLEvents());
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		postWorld.preInit(event);
		proxy.registerRenderers(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		
	}
	
	/*
	 * File Handling
	 */
	public static void loadCustomData(World w){
		for(int i=0; i<GeneratePostWorld.oreSpawnProperties.length; i++) {
			GeneratePostWorld.oreSpawnProperties[i].loadFromFile();
			GeneratePostWorld.oreSpawnProperties[i].determineAllChunks(w, Resources.worldRadius);	
		}
	}
	
	public static void saveCustomData() {
		for(int i=0; i<GeneratePostWorld.oreSpawnProperties.length; i++)
			GeneratePostWorld.oreSpawnProperties[i].saveToFile();
	}
	
	public static MovingObjectPosition getLookBlock(World world, EntityPlayer entity, float range) {
		Vec3 posVec = Vec3.createVectorHelper(entity.posX, entity.posY /*+ entity.getEyeHeight()*/, entity.posZ);
		Vec3 lookVec = entity.getLookVec();
		MovingObjectPosition mop = world.rayTraceBlocks(posVec, lookVec);

		if (mop == null || (float)mop.hitVec.distanceTo(posVec) > range) {
			return null;
		} else 
			return mop;
	}
	
	public static float vec3Dist(int x1, int y1, int z1, int x2, int y2, int z2) {
		return (float)Math.sqrt(Math.pow((double)(x2-x1), 2.0)+Math.pow((double)(y2-y1), 2.0)+Math.pow((double)(z2-z1), 2.0));
	}
	
	public static class GuiHandler implements IGuiHandler {
		@Override public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			return null;}
		@Override public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			return null;}
	}

	@Override
	public int getBurnTime(ItemStack arg0) {
		return 0;
	}
}
