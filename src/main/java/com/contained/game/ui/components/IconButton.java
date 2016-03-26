package com.contained.game.ui.components;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.contained.game.util.Resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class IconButton extends GuiButton {	
	private ResourceLocation img;
	private int x2, y2;
	public int color = -1;
	private String description = "";
	
	public IconButton(int id, int x, int y, int w, int h, String icon, int x2, int y2) {
		super(id, x, y, w, h, icon);
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/button/" + icon);
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public IconButton(int id, int x, int y, int w, int h, String icon, int x2, int y2, String description){
		super(id, x, y, w, h, icon);
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/button/" + icon);
		this.x2 = x2;
		this.y2 = y2;
		this.description = description;
	}
	
	public IconButton(int id, int x, int y, int w, int h, int color){
		super(id, x, y, w, h, "");
		this.color = color;
	}
	
	public void addDescription(String text){
		this.description = text;
	}

	@Override
	public void drawButton(Minecraft mc, int x, int y){
        if (this.visible){
        	FontRenderer fontrenderer = mc.fontRenderer;
        	mc.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            
            if(this.img != null){
            	mc.getTextureManager().bindTexture(this.img);
                this.drawTexturedModalRect(this.xPosition+2, this.yPosition+2, x2, y2, 16, 16);
            }
            
            if(this.color != -1)
            	Gui.drawRect(this.xPosition+4, this.yPosition+4, this.xPosition+16, this.yPosition+16, color);
            
            if(!this.description.isEmpty() && this.getHoverState(this.field_146123_n) == 2){
            	Gui.drawRect(this.xPosition + 20, this.yPosition + 5, 
            			this.xPosition + fontrenderer.getStringWidth(this.description) + 30, 
            			this.yPosition + fontrenderer.FONT_HEIGHT + 10, Color.DARK_GRAY.hashCode());
            	fontrenderer.drawStringWithShadow(this.description, this.xPosition + 23, this.yPosition + 7, Color.WHITE.hashCode());
            }
            
            this.mouseDragged(mc, x, y);
        }
    }
}
