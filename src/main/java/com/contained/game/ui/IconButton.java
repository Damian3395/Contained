package com.contained.game.ui;

import org.lwjgl.opengl.GL11;

import com.contained.game.util.Resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class IconButton extends GuiButton {	
	private ResourceLocation img;
	private int x2, y2;
	
	public IconButton(int id, int x, int y, int w, int h, String icon, int x2, int y2) {
		super(id, x, y, w, h, icon);
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/button/" + icon);
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public void drawButton(Minecraft mc, int x, int y){
        if (this.visible){
            mc.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            
            mc.getTextureManager().bindTexture(this.img);
            this.drawTexturedModalRect(this.xPosition+2, this.yPosition+2, x2, y2, 16, 16);
            
            this.mouseDragged(mc, x, y);
        }
    }
}
