package com.contained.game.util;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.BlockCoord;

import com.contained.game.ui.territory.TerritoryEdge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public class RenderUtil {
	//Generate a color code from a String
	public static int colorHash(String s) {
        return (s.hashCode() & 11184810) + 4473924;
    }
	
	//Generate a hue spectrum over a number range
	public static Color hueGrad(float val, float max) {
		float hue = val/max;
		// Tweak saturation & val in green/blue area of spectrum to 
		// increase visibility
		float sat = 0.6f;
		float bright = 1.0f;
		if (hue >= 0.25 && hue <= 0.5) {
			sat = 0.6f + (hue - 0.25f);
			bright = 1.0f + (hue - 0.5f);
		}
		return Color.getHSBColor(hue, sat, bright);
	}
	
    public static void drawNameTag(Entity ent, String tag, double x, double y, double z, int range)
    {
    	RenderManager renderManager = RenderManager.instance;
        double d3 = ent.getDistanceSqToEntity(renderManager.livingPlayer);

        if (d3 <= (double)(range * range))
        {
            FontRenderer fontrenderer = renderManager.getFontRenderer();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.0F, (float)y + ent.height + 0.5F, (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.instance;
            byte b0 = 0;

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int j = fontrenderer.getStringWidth(tag) / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontrenderer.drawString(tag, -fontrenderer.getStringWidth(tag) / 2, b0, 553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fontrenderer.drawString(tag, -fontrenderer.getStringWidth(tag) / 2, b0, -1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }
    
    public static void drawPillar(int x, int z, float radius, int color, float ox, float oy, float oz) {
		Tessellator tes = Tessellator.instance;
		float x1 = x+0.5f-radius-ox;
		float x2 = x+0.5f+radius-ox;
		float y1 = 0-oy;
		float y2 = 255-oy;
		float z1 = z+0.5f-radius-oz;
		float z2 = z+0.5f+radius-oz;
	
		tes.startDrawing(GL11.GL_TRIANGLE_STRIP);
		tes.setColorRGBA_I(color, 100);

		tes.addVertex(x1, y1, z1); tes.addVertex(x1, y2, z1);
		tes.addVertex(x2, y1, z1); tes.addVertex(x2, y2, z1);
		tes.addVertex(x2, y1, z2); tes.addVertex(x2, y2, z2);
		tes.addVertex(x1, y1, z2); tes.addVertex(x1, y2, z2);
		tes.addVertex(x1, y1, z1); tes.addVertex(x1, y2, z1);

		tes.draw();
    }
    
    public static float getOriginX(Minecraft mc, float dt) {
		float px = (float)mc.thePlayer.prevPosX;
		return px + ((float)mc.thePlayer.posX - px) * dt;
    }
    
    public static float getOriginY(Minecraft mc, float dt) {
		float py = (float)mc.thePlayer.prevPosY;
		return py + ((float)mc.thePlayer.posY - py) * dt;
    }
  
    public static float getOriginZ(Minecraft mc, float dt) {
		float pz = (float)mc.thePlayer.prevPosZ;
		return pz + ((float)mc.thePlayer.posZ - pz) * dt;
    }
}
