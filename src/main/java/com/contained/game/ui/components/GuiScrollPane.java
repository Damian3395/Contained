package com.contained.game.ui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiScrollPane {
	private GuiScreen gui;
	private int x, y, scroll_x, scroll_y, scrollSect, scrollCap;
	private List<String> list;
	private ResourceLocation img;
	private int selected = -1;
	private int index = -1;
	
	public GuiScrollPane(GuiScreen gui, int x, int y, List<String> list){
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.scroll_x = x + 133;
		this.scroll_y = y + 3;
		this.scrollSect = 0;
		this.scrollCap = ((int) Math.ceil((double) list.size()/6.0));
		this.list = list;
		img = new ResourceLocation(Resources.MOD_ID, "textures/gui/GuiScrollPane.png");
	}
	
	public void setList(ArrayList<String> list){
		this.list = list;
	}
	
	public void mouseClicked(int x , int y, int button){
		for(int i = 0; i < 6; i++) //Check If One Of Pane Elements Is Selected
			if((x > this.x && x < this.x+130) 
					&& (y > (this.y+(i*16)) && (y < (this.y+((i+1) * 16)))) 
					&& button == 0 
					&& (i + (6 * this.scrollSect)) < this.list.size()){
				selected = i;
				index = (i + (6 * this.scrollSect));
			}
	}
	
	public void mouseClickMove(int x, int y, int button, long ticks){
		for(int i = 0; i < 6; i++) //Check If One Of Pane Elements Is Selected
			if((x > this.x && x < this.x+130) && (y > (this.y+(i*16)) 
					&& (y < (this.y+((i+1) * 16)))) 
					&& button == 0 
					&& (i + (6 * this.scrollSect)) < this.list.size()){
				selected = i;
				index = (i + (6 * this.scrollSect));
			}
	}
	
	private void wheelScroll(){
		int wheel = Mouse.getDWheel();
		
		int scroll = 100 / this.scrollCap;
		if(wheel < 0){
			if((this.scroll_y + 15)+scroll <= (y+100))
				this.scroll_y += scroll;
			else
				this.scroll_y = y+85;
			if(this.scrollSect < scrollCap-1)
				this.scrollSect++;
		}else if(wheel > 0){
			if((this.scroll_y - scroll) >= (y+3))
				this.scroll_y -= scroll;
			else
				this.scroll_y = y+3;
			if(this.scrollSect > 0)
				this.scrollSect--;
		}
	}
	
	public void render(){
		if(this.scrollCap != 0)
			wheelScroll();
		
		this.gui.mc.getTextureManager().bindTexture(this.img);
		this.gui.drawTexturedModalRect(x, y, 0, 0, 146, 100);
		this.gui.drawTexturedModalRect(this.scroll_x, this.scroll_y, 146, 3, 12, 15);
			
		if(selected != -1 && (selected + (6 * this.scrollSect)) == this.index)
			this.gui.drawTexturedModalRect(x,y+(selected*16), 0, 112, 128, 128);
		
		for(int i = 0; i < 6; i++)
			if((i + (6 * this.scrollSect)) < list.size())
				renderFont(x + 10, y+6+(i * 16), list.get(i + (6 * this.scrollSect)).toString(), Color.WHITE);
	}
	
	public String getText(){
		if(selected != -1)
			return this.list.get(selected + (6 * this.scrollSect)).toString();
		return "";
	}
	
	public int getIndex(){
		return selected + (6 * this.scrollSect);
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.gui.mc.fontRenderer.drawStringWithShadow(text, x, y, color.hashCode());
	}
}
