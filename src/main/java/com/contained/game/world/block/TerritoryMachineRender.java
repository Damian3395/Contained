package com.contained.game.world.block;

import org.lwjgl.opengl.GL11;

import com.contained.game.util.Resources;

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
			
			GL11.glPopMatrix();
		}
	}	
}
