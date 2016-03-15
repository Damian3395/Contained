package com.contained.game.ui.components;

import com.contained.game.util.Resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ProgressBar extends Gui{
	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final int GREEN = 2;
	public static final int WHITE = 3;
	public static final int BLACK = 4;
	public static final int LIGHT_BLUE = 5;
	public static final int PINK = 6;
	public static final int YELLOW = 7;
	
	public int tx, ty;
	public int x, y, status;
	private ResourceLocation img;
	private Minecraft mc;
	
	public ProgressBar(int x, int y, int color, int status, int cap, Minecraft mc){
		this.x = x;
		this.y = y;
		this.mc = mc;
		
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/ui.png");
		
		switch(color){
		case RED:
			this.tx = 0;
			this.ty = 192;
			break;
		case BLUE:
			this.tx = 0;
			this.ty = 208;
			break;
		case GREEN:
			this.tx = 0;
			this.ty = 224;
			break;
		case WHITE:
			this.tx = 0;
			this.ty = 240;
			break;
		case BLACK:
			this.tx = 96;
			this.ty = 192;
			break;
		case LIGHT_BLUE:
			this.tx = 96;
			this.ty = 208;
			break;
		case PINK:
			this.tx = 96;
			this.ty = 224;
			break;
		case YELLOW:
			this.tx = 96;
			this.ty = 240;
			break;
		default:
			this.tx = 0;
			this.ty = 192;
		}
		
		if(status < cap){
			this.status = ((int)(96.0 * ((double)status/(double)cap)));
		}else{
			this.status = 96;
		}
	}
	
	public void render(){
		this.mc.getTextureManager().bindTexture(img);
		this.drawTexturedModalRect(x, y, 0, 176, 96, 16);
		this.drawTexturedModalRect(x, y, this.tx, this.ty, 1 * this.status, 16);
	}
}
