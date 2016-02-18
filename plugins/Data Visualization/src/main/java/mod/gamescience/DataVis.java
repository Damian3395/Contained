package mod.gamescience;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.network.*;

import java.util.*;

/**
 * Main class for the mod.
 */
@Mod(modid = DataVis.MODID, name="Data VisualizerGUI", version = DataVis.VERSION)
public class DataVis implements IFuelHandler, IWorldGenerator{

	public static final String MODID = "datavis";
	public static final String VERSION = "1.0";	

	@SidedProxy(clientSide="mod.gamescience.DataVisClientProxy", serverSide="mod.gamescience.DataVisCommonProxy")
	public static DataVisCommonProxy proxy;

	@Instance(MODID)
	public static DataVis instance;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.registerFuelHandler(this);
		GameRegistry.registerWorldGenerator(this, 1);
		MinecraftForge.EVENT_BUS.register(new DataVisGlobalEvents());
		FMLCommonHandler.instance().bus().register(new DataVisFMLEvents());
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){					
		proxy.registerRenderers(this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {		
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
