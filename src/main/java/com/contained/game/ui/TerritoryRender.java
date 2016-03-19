package com.contained.game.ui;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TerritoryRender {

	private final Minecraft mc = Minecraft.getMinecraft(); //Reference to client
	public HashMap<TerritoryEdge, String> teamEdges;
	public PlayerTeam currentTerritory = null;
	public boolean doRender = true;
	
	public TerritoryRender() {
		this.teamEdges = new HashMap<TerritoryEdge, String>();
	}
	
	@SubscribeEvent
	public void onRenderExperienceBar(RenderGameOverlayEvent ev) {
		if (ev.isCancelable() || ev.type != ElementType.EXPERIENCE)
			return;
		
		GL11.glDisable(GL11.GL_LIGHTING);
		this.currentTerritory = 
				getTerritory((int)mc.thePlayer.posX, (int)mc.thePlayer.posZ);
				
		String teamName = "Wilderness";
		int teamColor = Color.WHITE.hashCode();
		if (currentTerritory != null) {
			teamName = currentTerritory.displayName+"'s Territory";
			teamColor = currentTerritory.getColor();
		}
		mc.fontRenderer.drawStringWithShadow(teamName, 5, 5, teamColor);
	}
	
	@SubscribeEvent
	public void onRenderScreen(RenderWorldLastEvent ev) {
		if (mc.theWorld != null && doRender) {
			float dt = ev.partialTicks;
			float px = (float)mc.thePlayer.prevPosX;
			float py = (float)mc.thePlayer.prevPosY;
			float pz = (float)mc.thePlayer.prevPosZ;
			drawTerritoryEdges(px + ((float)mc.thePlayer.posX - px) * dt,
								py + ((float)mc.thePlayer.posY - py) * dt,
								pz + ((float)mc.thePlayer.posZ - pz) * dt);
		}
	}
	
	private void drawTerritoryEdges(float x, float y, float z) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
		Tessellator tes = Tessellator.instance;
		
		for (TerritoryEdge te : teamEdges.keySet()) {
			PlayerTeam t = PlayerTeam.get(teamEdges.get(te));
			float margin = 0.01f;
			float alpha = 1f-(Util.euclidDist(te.blockX, te.blockZ, x, z)/128.0f);
			if (alpha <= 0)
				continue;
			
			float x1 = te.blockX-x+margin;
			float x2 = x1+1.0f-margin*2;
			float y1 = 0-y;
			float y2 = 255-y;
			float z1 = te.blockZ-z+margin;
			float z2 = z1+1.0f-margin*2;
			
			tes.startDrawing(GL11.GL_TRIANGLE_STRIP);
			tes.setColorRGBA_I(t.getColor(), (int)(48f * alpha));

			if (te.direction == TerritoryEdge.NORTH) {
				tes.addVertex(x2, y2, z2); tes.addVertex(x1, y2, z2);
				tes.addVertex(x2, y1, z2); tes.addVertex(x1, y1, z2);
			}
			else if (te.direction == TerritoryEdge.SOUTH) {
				tes.addVertex(x2, y2, z1); tes.addVertex(x1, y2, z1);
				tes.addVertex(x2, y1, z1); tes.addVertex(x1, y1, z1);
			}
			else if (te.direction == TerritoryEdge.WEST) {
				tes.addVertex(x1, y2, z2); tes.addVertex(x1, y2, z1);
				tes.addVertex(x1, y1, z2); tes.addVertex(x1, y1, z1);
			}
			else if (te.direction == TerritoryEdge.EAST) {
				tes.addVertex(x2, y2, z2); tes.addVertex(x2, y2, z1);
				tes.addVertex(x2, y1, z2); tes.addVertex(x2, y1, z1);
			}

			tes.draw();
		}
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * Returns the team occupying the given block coordinates, or null
	 * if the area is not claimed by any groups.
	 */
	private PlayerTeam getTerritory(int x, int z) {
		Point probe = new Point(x, z);
		if (Contained.territoryData.containsKey(probe))
			return PlayerTeam.get(Contained.territoryData.get(probe));
		return null;
	}
	
	/**
	 * Finds the perimeter of the territory owned by the given team, and adds those
	 * edges to the list that should be visually rendered.
	 */
	public void regenerateEdges() {
		teamEdges.clear();
		Point probe = new Point(0,0);		
		for (Point p : Contained.territoryData.keySet()) {			
			probe.x = p.x; 
			probe.y = p.y+1;
			if (!Contained.territoryData.containsKey(probe) 
					|| !Contained.territoryData.get(probe).equals(Contained.territoryData.get(p)))
				teamEdges.put(new TerritoryEdge(TerritoryEdge.NORTH, p.x, p.y), Contained.territoryData.get(p));
			
			probe.x = p.x; 
			probe.y = p.y-1;
			if (!Contained.territoryData.containsKey(probe) 
					|| !Contained.territoryData.get(probe).equals(Contained.territoryData.get(p)))
				teamEdges.put(new TerritoryEdge(TerritoryEdge.SOUTH, p.x, p.y), Contained.territoryData.get(p));
			
			probe.x = p.x-1; 
			probe.y = p.y;
			if (!Contained.territoryData.containsKey(probe) 
					|| !Contained.territoryData.get(probe).equals(Contained.territoryData.get(p)))
				teamEdges.put(new TerritoryEdge(TerritoryEdge.WEST, p.x, p.y), Contained.territoryData.get(p));
			
			probe.x = p.x+1; 
			probe.y = p.y;
			if (!Contained.territoryData.containsKey(probe) 
					|| !Contained.territoryData.get(probe).equals(Contained.territoryData.get(p)))
				teamEdges.put(new TerritoryEdge(TerritoryEdge.EAST, p.x, p.y), Contained.territoryData.get(p));
		}
	}
}
