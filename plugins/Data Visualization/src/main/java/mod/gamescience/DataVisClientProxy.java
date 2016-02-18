package mod.gamescience;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

/**
 * Set up handlers for client-sided actions
 */
public class DataVisClientProxy extends DataVisCommonProxy {

	@Override
	public void registerRenderers(DataVis ins) {
		VisualizerGUI gui = new VisualizerGUI(Minecraft.getMinecraft());
		PacketCustom.assignHandler(DataVis.MODID, new DataVisClientPacketHandler(gui));
		MinecraftForge.EVENT_BUS.register(gui);
		FMLCommonHandler.instance().bus().register(new KeyInputHandler(gui));
		KeyBindings.init();
	}
}
