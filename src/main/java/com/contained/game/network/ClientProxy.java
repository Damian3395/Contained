package com.contained.game.network;

import com.contained.game.Contained;
import com.contained.game.handler.KeyBindings;
import com.contained.game.handler.KeyInputHandler;
import com.contained.game.item.AntiTerritoryRender;
import com.contained.game.item.ItemTerritory;
import com.contained.game.item.TutorialBook;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.TerritoryRender;
import com.contained.game.world.block.TerritoryMachineRender;
import com.contained.game.world.block.TerritoryMachineTE;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

/*
 * Client Side Handlers
 */
public class ClientProxy extends CommonProxy{
	public static DataVisualization gui = new DataVisualization(Minecraft.getMinecraft());
	public static TerritoryRender territory = new TerritoryRender();
	
	@Override
	public void registerRenderers(Contained ins) {		
		MinecraftForge.EVENT_BUS.register(gui);
		MinecraftForge.EVENT_BUS.register(territory);
		FMLCommonHandler.instance().bus().register(new KeyInputHandler(gui, territory));
		KeyBindings.init();
		
		MinecraftForgeClient.registerItemRenderer(ItemTerritory.removeTerritory, new AntiTerritoryRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TerritoryMachineTE.class, new TerritoryMachineRender());	
	}
	
	public void init(FMLInitializationEvent event) {
		Contained.channel.register(new ClientPacketHandler(gui, territory));
	}
}
