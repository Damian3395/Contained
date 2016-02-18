package mod.gamescience;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;

/**
 * Process client-sided packets.
 */
public class DataVisClientPacketHandler implements IClientPacketHandler {

	private VisualizerGUI gui;
	
	public DataVisClientPacketHandler(VisualizerGUI gui) {
		this.gui = gui;
	}
	
	@Override
	public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient net) {
		switch(packet.getType()) {
			case 1:
				//Occupational Data
				for(int i=0; i<DataVisUtil.occupationNames.length; i++)
					ExtendedPlayer.get(mc.thePlayer).setOccupation(i, packet.readInt());
				break;
				
			case 2:
				//Item Usage Data
				ExtendedPlayer.get(mc.thePlayer).usedOwnItems = packet.readInt();
				ExtendedPlayer.get(mc.thePlayer).usedOthersItems = packet.readInt();
				ExtendedPlayer.get(mc.thePlayer).usedByOthers = packet.readInt();
				break;
		}
	}
	
}
