package com.contained.game.world.block;

import org.lwjgl.opengl.GL11;

import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Renders the gem progress bars on the front of the Territory and
 * Anti-Territory machines.
 */
public class TerritoryMachineRender extends TileEntitySpecialRenderer {

	public TerritoryMachineRender() {
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		ResourceLocation image;
		if (te instanceof TerritoryMachineTE) {
			TerritoryMachineTE machineData = (TerritoryMachineTE)te;
			int color = -1;
			
			if (machineData.shouldClaim) {
				image = new ResourceLocation(Resources.MOD_ID, "textures/items/color_gem.png");
				color = machineData.renderColor;
			}
			else
				image = new ResourceLocation(Resources.MOD_ID, "textures/items/antiterritory_gem.png");
		
			Tessellator tessellator = Tessellator.instance;
			float margin = 0.01f;
			float progress = machineData.getProgress();
		    int repeats = 1;
		    if (!machineData.shouldClaim)
		    	repeats = 2;
			
			this.bindTexture(image);
			RenderHelper.disableStandardItemLighting();
			
			GL11.glPushMatrix();
		    GL11.glTranslated(x, y, z); 	       
		 
		    	
		    for(int i=0; i<repeats; i+=1) {
		    	if (i == 1) {
		    		this.bindTexture(new ResourceLocation(Resources.MOD_ID, "textures/blocks/antiterritory_machine_color.png"));
		    		color = machineData.renderColor;
		    		progress = 1;
		    	}
		    	
			    tessellator.startDrawingQuads();
			    if (color != -1)
			    	tessellator.setColorOpaque_I(color);
		    		
			    tessellator.addVertexWithUV(0, 0, -margin, 0, 0);
			    tessellator.addVertexWithUV(0, progress, -margin, 0, progress);
			    tessellator.addVertexWithUV(1, progress, -margin, 1, progress);
			    tessellator.addVertexWithUV(1, 0, -margin, 1, 0);
			    tessellator.addVertexWithUV(0, 0, -margin, 0, 0);
			    tessellator.addVertexWithUV(1, 0, -margin, 1, 0);
			    tessellator.addVertexWithUV(1, progress, -margin, 1, progress);
			    tessellator.addVertexWithUV(0, progress, -margin, 0, progress);
				tessellator.draw();
				
				tessellator.startDrawingQuads();
			    tessellator.addVertexWithUV(0, 0, 1+margin, 0, 0);
			    tessellator.addVertexWithUV(0, progress, 1+margin, 0, progress);
			    tessellator.addVertexWithUV(1, progress, 1+margin, 1, progress);
			    tessellator.addVertexWithUV(1, 0, 1+margin, 1, 0);
			    tessellator.addVertexWithUV(0, 0, 1+margin, 0, 0);
			    tessellator.addVertexWithUV(1, 0, 1+margin, 1, 0);
			    tessellator.addVertexWithUV(1, progress, 1+margin, 1, progress);
			    tessellator.addVertexWithUV(0, progress, 1+margin, 0, progress);
				tessellator.draw();
				
				tessellator.startDrawingQuads();
			    tessellator.addVertexWithUV(-margin, 0, 0, 0, 0);
			    tessellator.addVertexWithUV(-margin, progress, 0, 0, progress);
			    tessellator.addVertexWithUV(-margin, progress, 1, 1, progress);
			    tessellator.addVertexWithUV(-margin, 0, 1, 1, 0);
			    tessellator.addVertexWithUV(-margin, 0, 0, 0, 0);
			    tessellator.addVertexWithUV(-margin, 0, 1, 1, 0);
			    tessellator.addVertexWithUV(-margin, progress, 1, 1, progress);
			    tessellator.addVertexWithUV(-margin, progress, 0, 0, progress);
				tessellator.draw();
				
				tessellator.startDrawingQuads();
			    tessellator.addVertexWithUV(1+margin, 0, 0, 0, 0);
			    tessellator.addVertexWithUV(1+margin, progress, 0, 0, progress);
			    tessellator.addVertexWithUV(1+margin, progress, 1, 1, progress);
			    tessellator.addVertexWithUV(1+margin, 0, 1, 1, 0);
			    tessellator.addVertexWithUV(1+margin, 0, 0, 0, 0);
			    tessellator.addVertexWithUV(1+margin, 0, 1, 1, 0);
			    tessellator.addVertexWithUV(1+margin, progress, 1, 1, progress);
			    tessellator.addVertexWithUV(1+margin, progress, 0, 0, progress);
				tessellator.draw();
		    }
			
		    // For a brief period of time during the machine's cycle,
		    // draw an xray wireframe around it that can be seen through
		    // blocks. This provides a window of opportunity for finding
		    // the machine in the event that it is buried underground, etc.
		    float xrayProgress = (progress-0.8f)/0.1f;
		    if (xrayProgress > 0)
		    	drawXrayWireframe(xrayProgress);
			
			GL11.glPopMatrix();
		}
	}	
	
	public void drawXrayWireframe(float progress) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glLineWidth(3f);
		Tessellator tes = Tessellator.instance;
		
		tes.startDrawing(GL11.GL_LINES);
		tes.setColorRGBA_I(0xFFFFFF, (int)(255f*Util.clamp(progress, 0f, 1f)));
		tes.setBrightness(200);
		float f0 = 0.0f;
		float f1 = 1.0f;
		
		// Bottom
		tes.addVertex(f0, f1, f0); tes.addVertex(f1,  f1, f0);
		tes.addVertex(f1, f1, f0); tes.addVertex(f1,  f1, f1); 
		tes.addVertex(f1, f1, f1); tes.addVertex(f0,  f1,  f1);
		tes.addVertex(f0, f1, f1); tes.addVertex(f0,  f1,  f0);

		// Top
		tes.addVertex(f1,  f0,  f0); tes.addVertex(f1,  f0,  f1);
		tes.addVertex(f1,  f0,  f1); tes.addVertex(f0,  f0,  f1);
		tes.addVertex(f0,  f0,  f1); tes.addVertex(f0,  f0,  f0);
		tes.addVertex(f0,  f0,  f0); tes.addVertex(f1,  f0,  f0);
		
		// Corners
		tes.addVertex(f1,  f0,  f1); tes.addVertex(f1,  f1,  f1);
		tes.addVertex(f1,  f0,  f0); tes.addVertex(f1,  f1,  f0);
		tes.addVertex(f0,  f0,  f1); tes.addVertex(f0,  f1,  f1);
		tes.addVertex(f0,  f0,  f0); tes.addVertex(f0,  f1,  f0);
		
		tes.draw();
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
