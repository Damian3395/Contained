package com.contained.game.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.*;

public interface IPacket {
	public void readBytes(ByteBuf bytes);
	public void writeBytes(ByteBuf bytes);
	public void executeClient(EntityPlayer player);
    public void executeServer(EntityPlayer player);
}
