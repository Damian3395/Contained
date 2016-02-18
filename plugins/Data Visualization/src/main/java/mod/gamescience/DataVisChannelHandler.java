package mod.gamescience;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * Send packets for handling to the appropriate (server/client) side.
 */
public class DataVisChannelHandler extends FMLIndexedMessageToMessageCodec<DataVisIPacket> {

	@Override
	public void encodeInto(ChannelHandlerContext ctx, DataVisIPacket packet, ByteBuf data) throws Exception {
		packet.writeBytes(data);
    }

    @Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, DataVisIPacket packet) {
	    packet.readBytes(data);
	    switch (FMLCommonHandler.instance().getEffectiveSide()) {
	        case CLIENT:
	            packet.executeClient(Minecraft.getMinecraft().thePlayer);
	            break;
	        case SERVER:
	            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
	            packet.executeServer(((NetHandlerPlayServer) netHandler).playerEntity);
	            break;
	    }
    }

}
