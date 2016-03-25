package com.contained.game.ui;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.network.ServerPacketHandler;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiSurvey extends GuiScreen {

	private static final ResourceLocation bookGuiTextures = new ResourceLocation("minecraft", "textures/gui/book.png");
	
	private int disableTime = 0;
	private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    public int lastProgress;
    private GuiButton buttonFinish;
	private GuiButton buttonStart;
	private GuiButton buttonOptionA;
	private GuiButton buttonOptionB;
	private GuiButton buttonOptionC;
	private GuiButton buttonOptionD;
	private GuiButton buttonOptionE;
	private PlayerTeamIndividual playerCopy;
	
	public GuiSurvey(PlayerTeamIndividual pdata) {
		playerCopy = new PlayerTeamIndividual(pdata);
		lastProgress = pdata.surveyProgress;
	}
	
	@Override
    public void initGui()
    {
		int bottomMargin = -42;
		int leftMargin = -6;
		
    	this.buttonFinish = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, "Done");
    	this.buttonStart = new GuiButton(1, this.width / 2 - 50 + leftMargin, bottomMargin + this.bookImageHeight, 100, 20, "Begin!");
    	this.buttonOptionA = new GuiButton(2, this.width / 2 - 50 + leftMargin, bottomMargin-(20*4) + this.bookImageHeight, 100, 16, "Strongly Agree");
    	this.buttonOptionB = new GuiButton(3, this.width / 2 - 50 + leftMargin, bottomMargin-(20*3) + this.bookImageHeight, 100, 16, "Agree");
    	this.buttonOptionC = new GuiButton(4, this.width / 2 - 50 + leftMargin, bottomMargin-(20*2) + this.bookImageHeight, 100, 16, "Neutral");
    	this.buttonOptionD = new GuiButton(5, this.width / 2 - 50 + leftMargin, bottomMargin-(20*1) + this.bookImageHeight, 100, 16, "Disagree");
    	this.buttonOptionE = new GuiButton(6, this.width / 2 - 50 + leftMargin, bottomMargin-(20*0) + this.bookImageHeight, 100, 16, "Strongly Disagree");
    
    	this.updateButtons();
    }
    
    public void updateScreen()
    {
        super.updateScreen();
        if (!this.buttonOptionA.enabled) {
        	this.disableTime--;
        	if (this.disableTime <= 0) {
				this.buttonOptionA.enabled = true;
				this.buttonOptionB.enabled = true;
				this.buttonOptionC.enabled = true;
				this.buttonOptionD.enabled = true;
				this.buttonOptionE.enabled = true;
        	}
        }
    }
	
    private void updateButtons() {    	
    	this.buttonList.clear();
    	if (playerCopy.surveyProgress == 0) {
    		//Intro Page
    		this.buttonList.add(this.buttonStart);
    	} else if (playerCopy.surveyProgress <= SurveyData.data.length) {
    		//Question Page
    		this.buttonList.add(this.buttonOptionA);
    		this.buttonList.add(this.buttonOptionB);
    		this.buttonList.add(this.buttonOptionC);
    		this.buttonList.add(this.buttonOptionD);
    		this.buttonList.add(this.buttonOptionE);
    	} else {
    		//Survey Complete Page
    	}
    	this.buttonList.add(this.buttonFinish);
    }
    
    @Override
    protected void actionPerformed(GuiButton b)
    {
    	if (b.enabled) {
    		if (b.id == 0) //Done button
                this.mc.displayGuiScreen(null);
    		else if (b.id == 1) //Start button
			{
				playerCopy.surveyProgress = 1;
				//Disable the survey buttons for a brief period to prevent
				//the button click from being double-counted.
				this.disableTime = 10;
				this.buttonOptionA.enabled = false;
				this.buttonOptionB.enabled = false;
				this.buttonOptionC.enabled = false;
				this.buttonOptionD.enabled = false;
				this.buttonOptionE.enabled = false;
				updateButtons();
			}
    		else if (b.id >= 2) //Option buttons
    		{
    			playerCopy.surveyResponses[playerCopy.surveyProgress-1] = b.id-2;
    			playerCopy.surveyProgress++;
    			updateButtons();
    		}
    	}
    }
    
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (this.lastProgress != playerCopy.surveyProgress) {
			PacketCustom packet = ServerPacketHandler.packetUpdateSurvey(playerCopy);
			ServerPacketHandler.sendToServer(packet.toPacket());
			
			PlayerTeamIndividual localPData = PlayerTeamIndividual.get(playerCopy.playerName);
			localPData.surveyProgress = playerCopy.surveyProgress;
			localPData.surveyResponses = playerCopy.surveyResponses;
		}
	}
    
    public void drawScreen(int par1, int par2, float par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookGuiTextures);
        int x = (this.width - this.bookImageWidth) / 2;
        byte y = 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.bookImageWidth, this.bookImageHeight);

    	if (playerCopy.surveyProgress == 0) {
    		//Intro Page
    		String title = "Personality Survey";
    		String body = "Please take a moment to answer this brief personality survey. For each statement, state how much you feel it reflects yourself. Your responses will be visible only to the server administrators.";
    		this.fontRendererObj.drawString("§l§n"+title, x - this.fontRendererObj.getStringWidth(title)/2 + (this.bookImageWidth-36)/2 + 7, y + 16, 0);
    		this.fontRendererObj.drawSplitString(body, x + 36, y + 16 + 16, 116, 0);
    	} 
    	
    	else if (playerCopy.surveyProgress <= SurveyData.data.length) {
    		//Question Page
    		String headerStr = "== "+playerCopy.surveyProgress+" of "+SurveyData.data.length+" ==";
    		this.fontRendererObj.drawString(headerStr, x - this.fontRendererObj.getStringWidth(headerStr) + this.bookImageWidth - 44, y + 16, 0);
    		this.fontRendererObj.drawSplitString(SurveyData.data[playerCopy.surveyProgress-1].question, x + 36, y + 16 + 16, 116, 0);
    	} 
    	
    	else {
    		//Survey Complete Page
    		String title = "Survey Complete!";
    		String body = "Your results:";
    		this.fontRendererObj.drawString("§l§n"+title, x - this.fontRendererObj.getStringWidth(title)/2 + (this.bookImageWidth-36)/2 + 7, y + 16, 0);
    		this.fontRendererObj.drawString(body, x + 36, y + 16 + 16, 0);
    		
    		this.fontRendererObj.drawString("Openness: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.OPENNESS, playerCopy.surveyResponses)), x + 36, y + 16 + 16*2, 0);
    		this.fontRendererObj.drawString("Conscientious: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.CONSCIENTIOUSNESS, playerCopy.surveyResponses)), x + 36, y + 16 + 16*3, 0);
    		this.fontRendererObj.drawString("Extraversion: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.EXTRAVERSION, playerCopy.surveyResponses)), x + 36, y + 16 + 16*4, 0);
    		this.fontRendererObj.drawString("Agreeableness: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.AGREEABLENESS, playerCopy.surveyResponses)), x + 36, y + 16 + 16*5, 0);
    		this.fontRendererObj.drawString("Neuroticism: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.NEUROTICISM, playerCopy.surveyResponses)), x + 36, y + 16 + 16*6, 0);
    	}
        
        super.drawScreen(par1, par2, par3);
    }
    
    public String signedFormat(float value) {
    	if (value >= 0)
    		return "+"+(new DecimalFormat("0.00").format(value));
    	else
    		return ""+(new DecimalFormat("0.00").format(value));
    }
	
}
