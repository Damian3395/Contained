package mod.gamescience;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.*;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;

/**
 * Main class for the mod.
 */
@Mod(modid = FiniteWorldGen.MODID, name="Finite World Generator", version = FiniteWorldGen.VERSION)
public class FiniteWorldGen{

	public static final String MODID = "fwgMod";
	public static final String VERSION = "1.0";	
	public static int worldRadius;
	public static OreClusterProperties[] oreSpawnProperties;

	@SidedProxy(clientSide="mod.gamescience.FWGClientProxy", serverSide="mod.gamescience.FWGCommonProxy")
	public static FWGCommonProxy proxy;
	
	@Instance(MODID)
	public static FiniteWorldGen instance;
	
	WastelandBlock wasteland = new WastelandBlock();
	WastelandBush wastelandBush = new WastelandBush();
	WastelandBiome wastelandBiome = new WastelandBiome();
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new FWGGlobalEvents());
		MinecraftForge.TERRAIN_GEN_BUS.register(new FWGTerrainGen());
		MinecraftForge.ORE_GEN_BUS.register(new FWGOreGen());
		wastelandBiome.load();
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new DebugCommand());
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){	
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		worldRadius = config.getInt("worldSize", Configuration.CATEGORY_GENERAL, 25, 0, 500, "Radius of the finite world in chunks (16x16 blocks), centered around spawn.");
		OreClusterProperties.writeConfigComment(config);
		oreSpawnProperties = new OreClusterProperties[FWGUtil.NUM_MINERALS];
		oreSpawnProperties[FWGUtil.DIAMOND] = OreClusterProperties.generateFromConfig(config, 
				Blocks.diamond_ore, 2, 8, 6, 12, 6, 12, 2, 3, 2, 16);
		oreSpawnProperties[FWGUtil.EMERALD] = OreClusterProperties.generateFromConfig(config, 
				Blocks.emerald_ore, 2, 8, 6, 12, 6, 12, 2, 3, 2, 16);
		oreSpawnProperties[FWGUtil.GOLD] = OreClusterProperties.generateFromConfig(config, 
				Blocks.gold_ore, 8, 32, 2, 5, 5, 10, 3, 5, 2, 32);
		oreSpawnProperties[FWGUtil.REDSTONE] = OreClusterProperties.generateFromConfig(config, 
				Blocks.redstone_ore, 8, 32, 3, 6, 8, 15, 4, 6, 2, 24);
		oreSpawnProperties[FWGUtil.LAPIS] = OreClusterProperties.generateFromConfig(config, 
				Blocks.lapis_ore, 8, 32, 3, 6, 8, 15, 4, 6, 2, 24);
		oreSpawnProperties[FWGUtil.IRON] = OreClusterProperties.generateFromConfig(config, 
				Blocks.iron_ore, 12, 48, 3, 6, 5, 10, 4, 6, 24, 64);
		oreSpawnProperties[FWGUtil.COAL] = OreClusterProperties.generateFromConfig(config, 
				Blocks.coal_ore, 12, 48, 3, 6, 5, 10, 4, 6, 24, 64);
		config.save();
		
		wasteland.preInit(event);
		wastelandBush.preInit(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {		
	}
	
	//
	// === File Handling ===
	//
	public static void loadCustomData(World w) {
		for(int i=0; i<oreSpawnProperties.length; i++) {
			oreSpawnProperties[i].loadFromFile();
			oreSpawnProperties[i].determineAllChunks(w, worldRadius);	
		}
	}
	
	public static void saveCustomData() {
		for(int i=0; i<oreSpawnProperties.length; i++)
			oreSpawnProperties[i].saveToFile();
	}
}
