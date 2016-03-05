package com.contained.game.network;

import com.contained.game.Contained;
import com.contained.game.handler.KeyBindings;
import com.contained.game.handler.KeyInputHandler;
import com.contained.game.item.AntiTerritoryRender;
import com.contained.game.item.ItemTerritory;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.TerritoryRender;
import com.contained.game.util.Resources;
import com.contained.game.world.block.AntiTerritoryMachine;
import com.contained.game.world.block.TerritoryMachineRender;
import com.contained.game.world.block.TerritoryMachineTE;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

/*
 * Client Side Handlers
 */
public class ClientProxy extends CommonProxy{
	@Override
	public void registerRenderers(Contained ins) {
		DataVisualization gui = new DataVisualization(Minecraft.getMinecraft());
		TerritoryRender territory = new TerritoryRender();
		
		PacketCustom.assignHandler(Resources.MOD_ID, new ClientPacketHandler(gui, territory));
		
		MinecraftForge.EVENT_BUS.register(gui);
		MinecraftForge.EVENT_BUS.register(territory);
		FMLCommonHandler.instance().bus().register(new KeyInputHandler(gui, territory));
		KeyBindings.init();
		
		MinecraftForgeClient.registerItemRenderer(ItemTerritory.removeTerritory, new AntiTerritoryRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TerritoryMachineTE.class, new TerritoryMachineRender());	
	}
}
