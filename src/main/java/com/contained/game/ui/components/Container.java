package com.contained.game.ui.components;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.contained.game.util.Resources;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class Container extends Gui{
	private GuiScreen gui;
	private int x, y, w, h;
	private String title = "";
	ResourceLocation img;
	
	public Container(int x, int y, int w, int h, String file, GuiScreen gui){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.gui = gui;
		this.img = new ResourceLocation(Resources.MOD_ID, "textures/gui/" + file);
	}
	
	public Container(int x, int y, int w, int h, String file, String title, GuiScreen gui){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.title = title;
		this.gui = gui;
		this.img = new ResourceLocation(Resources.MOD_ID, "textures/gui/" + file);
	}
	
	public void render(){
		this.gui.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.gui.mc.getTextureManager().bindTexture(this.img);
		this.drawTexturedModalRect(x, y, 0, 0, w, h);
		
		if(!this.title.isEmpty()){
			this.gui.mc.fontRenderer.drawStringWithShadow(title, 
					(x + w/2 - this.gui.mc.fontRenderer.getStringWidth(title))/2,
					y+10, Color.WHITE.hashCode());
		}
	}
}
