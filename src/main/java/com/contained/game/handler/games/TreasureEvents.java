package com.contained.game.handler.games;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.RenderUtil;
import com.contained.game.util.Util;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class TreasureEvents {
	private Minecraft mc;
	
	public TreasureEvents(){
		this.mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onTreasureChestOpen(PlayerInteractEvent event){
		if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer != null)
			handleTreasureChest(event.world, event.entityPlayer, event.x, event.y, event.z);		
	}
	
	@SubscribeEvent
	public void onTreasureChestBreak(BlockEvent.BreakEvent event){
		if(event.getPlayer() != null)
			handleTreasureChest(event.world, event.getPlayer(), event.x, event.y, event.z);		
	}
	
	public void handleTreasureChest(World w, EntityPlayer p, int x, int y, int z) {
		if(!w.isRemote && (w.getBlock(x, y, z) instanceof BlockChest)) {	
			BlockCoord eventLocation = new BlockCoord(x, y, z);
			int dimID = w.provider.dimensionId;
			if (Contained.getActiveTreasures(dimID).contains(eventLocation)) {
				ClientPacketHandlerUtil.removeTreasureAndSync(dimID, eventLocation);
				//TODO: Increase team score by 1.
				MiniGameUtil.generateChest(w, 1, ContainedRegistry.CUSTOM_CHEST_LOOT);
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderScreen(RenderWorldLastEvent ev) {
		if (mc.theWorld != null) {
			renderChests(RenderUtil.getOriginX(mc, ev.partialTicks),
						 RenderUtil.getOriginY(mc, ev.partialTicks),
						 RenderUtil.getOriginZ(mc, ev.partialTicks), ev.partialTicks);
		}
	}
	
	public void renderChests(float ox, float oy, float oz, float dt) {
		ArrayList<BlockCoord> chestPositions = Contained.getActiveTreasures(0);
	
		for (BlockCoord point : chestPositions) {	
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_BLEND);
			//GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
			RenderUtil.drawPillar(point.x, point.z, 0.4f, Color.yellow.hashCode(),
									RenderUtil.getOriginX(mc, dt),
									RenderUtil.getOriginY(mc, dt),
									RenderUtil.getOriginZ(mc, dt));
			RenderUtil.drawPillar(point.x, point.z, 0.15f, Color.white.hashCode(),
									RenderUtil.getOriginX(mc, dt),
									RenderUtil.getOriginY(mc, dt),
									RenderUtil.getOriginZ(mc, dt));
			int dist = (int)Util.euclidDist((float)mc.thePlayer.posX, (float)mc.thePlayer.posY, (float)mc.thePlayer.posZ, 
											(float)point.x, (float)point.y, (float)point.z);
			RenderUtil.drawClampedWorldLabel("["+dist+"m]", point.x, point.y+1, point.z, 50,
					RenderUtil.getOriginX(mc, dt),
					RenderUtil.getOriginY(mc, dt),
					RenderUtil.getOriginZ(mc, dt));
		}
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);	
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
