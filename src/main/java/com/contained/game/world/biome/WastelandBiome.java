package com.contained.game.world.biome;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.*;

public class WastelandBiome {
	public static WastelandBiomeDef biome = new WastelandBiomeDef();
		public WastelandBiome(){}
	
	public void load(){
		BiomeDictionary.registerBiomeType(biome, BiomeDictionary.Type.DEAD);
	}
	
	static class WastelandBiomeDef extends BiomeGenBase
	{
		public WastelandBiomeDef()
		{
			super(0);
			setBiomeName("Wasteland");
			topBlock = Blocks.air;
			fillerBlock = Blocks.water;
			theBiomeDecorator.generateLakes = false;
			theBiomeDecorator.treesPerChunk = 0;
			theBiomeDecorator.flowersPerChunk = 0;
			theBiomeDecorator.grassPerChunk = 0;
			theBiomeDecorator.deadBushPerChunk = 0;
			theBiomeDecorator.mushroomsPerChunk = 0;
			theBiomeDecorator.reedsPerChunk = 0;
			theBiomeDecorator.cactiPerChunk = 0;
			theBiomeDecorator.sandPerChunk = 0;
			rainfall = 0F;
			setHeight(new BiomeGenBase.Height(0.1F, 0.3F));

			this.setDisableRain();
			this.spawnableCreatureList.clear();
			this.spawnableWaterCreatureList.clear();
			this.setColor(Color.getHSBColor(0.05f, 0.4f, 0.3f).hashCode());
		}
		
		@Override
		public int getWaterColorMultiplier() {
			return Color.getHSBColor(0.0f, 1.0f, 0.6f).hashCode();
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public int getSkyColorByTemp(float temp) {
			return Color.getHSBColor(0.0f, 0.6f, 0.4f).hashCode();
		}
	}
}
