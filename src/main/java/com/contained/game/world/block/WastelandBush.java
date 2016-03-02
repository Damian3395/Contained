package com.contained.game.world.block;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WastelandBush extends BlockDeadBush {
	public static String blockName = "wastelandBush";
	public static BlockWastelandBush instance = new BlockWastelandBush();

	public void preInit(FMLPreInitializationEvent event){
		GameRegistry.registerBlock(instance, blockName);
	}
	
	static class BlockWastelandBush extends BlockDeadBush {
		public BlockWastelandBush() {
			super();
			setHardness(0.0F);
			setStepSound(soundTypeGrass);
			setBlockName(blockName);
			setBlockTextureName("deadbush");
		}
		
		@Override
		public boolean canPlaceBlockAt(World w, int x, int y, int z) {
			return true;
		}
		
		@Override
		public boolean canPlaceBlockOn(Block b) {
			return b == Blocks.grass || b == Blocks.dirt || b == Blocks.farmland
			  || b == Blocks.sand || b == Blocks.soul_sand || b == WastelandBlock.instance;
		}
	}
}
