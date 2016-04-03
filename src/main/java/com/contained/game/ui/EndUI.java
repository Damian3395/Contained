package com.contained.game.ui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.contained.game.ui.components.Container;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class EndUI extends GuiScreen {
	private final int FILL_SURVEY = 0;
	private final int EXIT_GAME = 1;
	
	private GuiButton survey, exit;
	private Container background;
	private int x, y;
	private ResourceLocation thankYou, authors;
	private boolean surveyFilledOut;
	
	public EndUI(){}
	
	@Override
	public void initGui(){
		x = this.width/2;
		y = this.height/2;
		
		thankYou = new ResourceLocation(Resources.MOD_ID, "textures/gui/thank_you.png");
		authors = new ResourceLocation(Resources.MOD_ID, "textures/gui/authors.png");
		
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(mc.thePlayer);
		surveyFilledOut = pdata.surveyResponses.progress <= SurveyData.getSurveyLength() ? false : true;
		
		background = new Container(x+60, y, 256, 176, "ui.png", this);
		
		if(!surveyFilledOut)
			this.buttonList.add(survey = new GuiButton(FILL_SURVEY, x+70, y+140, 60, 20, "Fill Survey"));
		this.buttonList.add(exit = new GuiButton(EXIT_GAME, x+230,y+140, 60, 20, "Exit Game"));
	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
	}
	
	@Override
	public void drawScreen(int w, int h, float ticks){	
		background.render();
		
		renderFont(x+100, y+10, "Thank You For Playing Contained", Color.BLACK);
		renderFont(x+65, y+30, "If you haven't filled out the survey yet, please do. After finishing the survey, you will be granted 2 more lives to continue playing the game. We hope you had a great time playing on our servers. If you had any issues please contact us at Contained.Rutgers@gmail.com", Color.BLACK);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(this.thankYou);
		this.drawTexturedModalRect(x-128, -10, 0, 0, 256,256);
		
		this.mc.getTextureManager().bindTexture(this.authors);
		this.drawTexturedModalRect(0, y, 0, 0, 256, 256);
		
		super.drawScreen(w, h, ticks);
	}
	
	private void renderFont(int x, int y, String text, Color color){
		this.mc.fontRenderer.drawSplitString(text, x, y, 250, Color.BLACK.hashCode());
	}
	
	@Override
	protected void keyTyped(char c, int i){
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
		case FILL_SURVEY:
			this.mc.displayGuiScreen(new GuiSurvey(PlayerTeamIndividual.get(mc.thePlayer)));
		break;
		case EXIT_GAME:
			
		break;
		}
	}

	@Override
	public void onGuiClosed(){

	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
}
