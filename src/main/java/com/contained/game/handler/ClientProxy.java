package com.contained.game.handler;

import com.contained.game.Contained;
import com.contained.game.ui.DataVisualization;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy{
	@Override
	public void registerRenderers(Contained ins) {
		DataVisualization gui = new DataVisualization(Minecraft.getMinecraft());
		PacketCustom.assignHandler(Resources.MOD_ID, new ClientPacketHandler(gui));
		MinecraftForge.EVENT_BUS.register(gui);
		FMLCommonHandler.instance().bus().register(new KeyInputHandler(gui));
		KeyBindings.init();
	}
}
