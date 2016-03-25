package com.contained.game.ui;

import java.awt.Color;
import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.network.ServerPacketHandler;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

public class GuiSurvey extends GuiScreen {

	private static final ResourceLocation bookGuiTextures = new ResourceLocation("minecraft", "textures/gui/book.png");
	
	private int disableTime = 0;
	private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    public int lastProgress;
    private GuiButton buttonFinish;
	private GuiButton buttonStart;
	private GuiButton buttonNext;
	private GuiButton buttonMale;
	private GuiButton buttonFemale;
	private GuiButton buttonOptionA;
	private GuiButton buttonOptionB;
	private GuiButton buttonOptionC;
	private GuiButton buttonOptionD;
	private GuiButton buttonOptionE;
	private GuiTextField textResponseA;
	private GuiTextField textResponseB;
	private PlayerTeamIndividual playerCopy;
	
	public static final int PAGE_GENDER = 1;
	public static final int PAGE_AGE = 2;
	public static final int PAGE_ETHNICITY = 3;
	public static final int PAGE_MINECRAFT = 4;
	public static final int PAGE_PERSONALITY = 5;
	
	public GuiSurvey(PlayerTeamIndividual pdata) {
		playerCopy = new PlayerTeamIndividual(pdata);
		lastProgress = pdata.surveyResponses.progress;
	}
	
	@Override
    public void initGui()
    {
		super.initGui();
		int bottomMargin = -42;
		int leftMargin = -6;
		
    	this.buttonFinish = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, "Done");
    	this.buttonStart = new GuiButton(1, this.width / 2 - 50 + leftMargin, bottomMargin + this.bookImageHeight, 100, 20, "Begin!");
    	this.buttonOptionA = new GuiButton(2, this.width / 2 - 50 + leftMargin, bottomMargin-(20*4) + this.bookImageHeight, 100, 16, "Strongly Agree");
    	this.buttonOptionB = new GuiButton(3, this.width / 2 - 50 + leftMargin, bottomMargin-(20*3) + this.bookImageHeight, 100, 16, "Agree");
    	this.buttonOptionC = new GuiButton(4, this.width / 2 - 50 + leftMargin, bottomMargin-(20*2) + this.bookImageHeight, 100, 16, "Neutral");
    	this.buttonOptionD = new GuiButton(5, this.width / 2 - 50 + leftMargin, bottomMargin-(20*1) + this.bookImageHeight, 100, 16, "Disagree");
    	this.buttonOptionE = new GuiButton(6, this.width / 2 - 50 + leftMargin, bottomMargin-(20*0) + this.bookImageHeight, 100, 16, "Strongly Disagree");
    	this.buttonNext = new GuiButton(1, this.width / 2 - 50 + leftMargin, bottomMargin + this.bookImageHeight, 100, 20, "Next");
    	this.buttonMale = new GuiButton(7, this.width / 2 - 50 + leftMargin, bottomMargin-(24*3) + this.bookImageHeight, 100, 20, "Male");
    	this.buttonFemale = new GuiButton(8, this.width / 2 - 50 + leftMargin, bottomMargin-(24*2) + this.bookImageHeight, 100, 20, "Female");
    	
    	this.textResponseA = new GuiTextField(this.fontRendererObj, this.width / 2 - 50 + leftMargin, bottomMargin-(24*3) + this.bookImageHeight, 100, 20);
    	this.textResponseA.setTextColor(Color.WHITE.hashCode());
    	this.textResponseA.setEnableBackgroundDrawing(true);
    	this.textResponseA.setMaxStringLength(25);
    	this.textResponseA.setText("");
    	this.textResponseA.setFocused(false);
    	
    	this.textResponseB = new GuiTextField(this.fontRendererObj, this.width / 2 - 50 + leftMargin, bottomMargin-(int)(24.0*1.5) + this.bookImageHeight, 100, 20);
    	this.textResponseB.setTextColor(Color.WHITE.hashCode());
    	this.textResponseB.setEnableBackgroundDrawing(true);
    	this.textResponseB.setMaxStringLength(25);
    	this.textResponseB.setText("");
    	this.textResponseB.setFocused(false);
    	
    	this.updateButtons();
    }
	
	public boolean isTextFieldAEnabled() {
		int page = playerCopy.surveyResponses.progress;
		if (page == PAGE_AGE || page == PAGE_ETHNICITY || page == PAGE_MINECRAFT)
			return true;
		return false;
	}
	
	public boolean isTextFieldBEnabled() {
		int page = playerCopy.surveyResponses.progress;
		if (page == PAGE_MINECRAFT)
			return true;
		return false;
	}
    
	@Override
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
        
        // For pages that require numerical textfield inputs, disable the
        // next button until the text field contains a valid number.
        this.buttonNext.enabled = true;
        if (playerCopy.surveyResponses.progress == PAGE_AGE
        		|| playerCopy.surveyResponses.progress == PAGE_MINECRAFT) {
        	try {
        		int val = Integer.parseInt(this.textResponseA.getText().trim());
        		if (val < 0 || val > 150)
        			this.buttonNext.enabled = false;
        		if (playerCopy.surveyResponses.progress == PAGE_MINECRAFT && val > 12)
        			this.buttonNext.enabled = false;
        	} catch (Exception e) {
        		this.buttonNext.enabled = false;
        	}
        }
        if (playerCopy.surveyResponses.progress == PAGE_MINECRAFT) {
        	try {
        		int val = Integer.parseInt(this.textResponseB.getText().trim());
        		if (val < 0)
        			this.buttonNext.enabled = false;
        	} catch (Exception e) {
        		this.buttonNext.enabled = false;
        	}
        }
        if (playerCopy.surveyResponses.progress == PAGE_ETHNICITY
        		&& this.textResponseA.getText().trim().length() == 0)
        	this.buttonNext.enabled = false;
    }
    
    @Override
	public void mouseClicked(int i , int j, int k){
    	super.mouseClicked(i, j, k);
    	
    	if (isTextFieldAEnabled())
    		this.textResponseA.mouseClicked(i, j, k);
    	if (isTextFieldBEnabled())
    		this.textResponseB.mouseClicked(i, j, k);
	}
	
    @Override
	public void keyTyped(char c, int i){
    	super.keyTyped(c, i);
		if (isTextFieldAEnabled() && this.textResponseA.isFocused())
			this.textResponseA.textboxKeyTyped(c, i);
		if (isTextFieldBEnabled() && this.textResponseB.isFocused())
			this.textResponseB.textboxKeyTyped(c, i);
	}
	
    private void updateButtons() {    	
    	this.buttonList.clear();
    	this.textResponseA.setText("");
    	this.textResponseB.setText("");
    	
    	if (playerCopy.surveyResponses.progress == 0) {
    		//Intro Page
    		this.buttonList.add(this.buttonStart);
    	} else if (playerCopy.surveyResponses.progress == PAGE_GENDER) {
    		//Gender Page
    		this.buttonList.add(this.buttonMale);
    		this.buttonList.add(this.buttonFemale);
    	}
    	else if (playerCopy.surveyResponses.progress <= SurveyData.getSurveyLength()
    				&& playerCopy.surveyResponses.progress >= PAGE_PERSONALITY) {
    		//Personality Question Page
    		this.buttonList.add(this.buttonOptionA);
    		this.buttonList.add(this.buttonOptionB);
    		this.buttonList.add(this.buttonOptionC);
    		this.buttonList.add(this.buttonOptionD);
    		this.buttonList.add(this.buttonOptionE);
    	} else if (playerCopy.surveyResponses.progress <= SurveyData.getSurveyLength()) {
    		this.buttonList.add(this.buttonNext);
    	}
    	
    	this.buttonList.add(this.buttonFinish);
    }
    
    @Override
    protected void actionPerformed(GuiButton b)
    {
    	super.actionPerformed(b);
    	
    	if (b.enabled) {
    		if (b.id == 7) //Male Button
    			playerCopy.surveyResponses.isMale = true;
    		else if (b.id == 8) //Female Button
    			playerCopy.surveyResponses.isMale = false;
    		
    		if (b.id == 0) //Done Button
    			this.mc.displayGuiScreen(null);
    		else if (b.id == 1 || b.id == 7 || b.id == 8) //Start/Next Button 
    		{
    			if (playerCopy.surveyResponses.progress == PAGE_AGE) 
    				playerCopy.surveyResponses.age = Integer.parseInt(this.textResponseA.getText().trim());
    			else if (playerCopy.surveyResponses.progress == PAGE_ETHNICITY) 
    				playerCopy.surveyResponses.ethnicity = this.textResponseA.getText().trim();
    			else if (playerCopy.surveyResponses.progress == PAGE_MINECRAFT) { 
    				playerCopy.surveyResponses.mcMonths = Integer.parseInt(this.textResponseA.getText().trim());
    				playerCopy.surveyResponses.mcYears = Integer.parseInt(this.textResponseB.getText().trim());
    			}
    			
    			playerCopy.surveyResponses.progress++;
    			if (playerCopy.surveyResponses.progress == PAGE_PERSONALITY) {
    				//Disable the survey buttons for a brief period to prevent
    				//the button click from being double-counted.
    				this.disableTime = 10;
    				this.buttonOptionA.enabled = false;
    				this.buttonOptionB.enabled = false;
    				this.buttonOptionC.enabled = false;
    				this.buttonOptionD.enabled = false;
    				this.buttonOptionE.enabled = false;
    			}
    			updateButtons();
    		}
    		else if (b.id >= 2 && b.id <= 6) //Option buttons
    		{
    			playerCopy.surveyResponses.personality[playerCopy.surveyResponses.progress-PAGE_PERSONALITY] = b.id-2;
    			playerCopy.surveyResponses.progress++;
    			updateButtons();
    		}
    	}
    }

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (this.lastProgress != playerCopy.surveyResponses.progress) {
			PacketCustom packet = ServerPacketHandler.packetUpdateSurvey(playerCopy);
			ServerPacketHandler.sendToServer(packet.toPacket());
			
			PlayerTeamIndividual localPData = PlayerTeamIndividual.get(playerCopy.playerName);
			localPData.surveyResponses = playerCopy.surveyResponses;
		}
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
    
    public void drawScreen(int par1, int par2, float par3)
    {
    	super.drawScreen(par1, par2, par3);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookGuiTextures);
        int x = (this.width - this.bookImageWidth) / 2;
        byte y = 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.bookImageWidth, this.bookImageHeight);

    	if (playerCopy.surveyResponses.progress == 0) {
    		//Intro Page
    		String title = "Personality Survey";
    		String body = "Please take a moment to answer this brief personality survey. For each statement, state how much you feel it reflects yourself. Your responses will be visible only to the server administrators.";
    		this.fontRendererObj.drawString("§l§n"+title, x - this.fontRendererObj.getStringWidth(title)/2 + (this.bookImageWidth-36)/2 + 7, y + 16, 0);
    		this.fontRendererObj.drawSplitString(body, x + 36, y + 16 + 16, 116, 0);
    	} 
    	
    	else if (playerCopy.surveyResponses.progress <= SurveyData.getSurveyLength()) {
    		//Question Page
    		String headerStr = "== "+playerCopy.surveyResponses.progress+" of "+SurveyData.getSurveyLength()+" ==";
    		this.fontRendererObj.drawString(headerStr, x - this.fontRendererObj.getStringWidth(headerStr) + this.bookImageWidth - 44, y + 16, 0);
    		
    		String question = "";
    		if (playerCopy.surveyResponses.progress >= PAGE_PERSONALITY)
    			question = SurveyData.data[playerCopy.surveyResponses.progress-PAGE_PERSONALITY].question;
    		else if (playerCopy.surveyResponses.progress == PAGE_AGE)
    			question = "How old are you?";
    		else if (playerCopy.surveyResponses.progress == PAGE_ETHNICITY)
    			question = "What is your ethnicity?";
    		else if (playerCopy.surveyResponses.progress == PAGE_GENDER)
    			question = "Which gender do you identify as?";
    		else if (playerCopy.surveyResponses.progress == PAGE_MINECRAFT)
    			question = "How long have you been playing Minecraft?";
    		
    		this.fontRendererObj.drawSplitString(question, x + 36, y + 16 + 16, 116, 0);
    	
    		if (playerCopy.surveyResponses.progress == PAGE_MINECRAFT) {
    			this.fontRendererObj.drawString("§nMonths:", x+36, y+16+16*3, 0);
    			this.fontRendererObj.drawString("§nYears:", x+36, y+16+(int)(16.0*5.25), 0);
    		}
    		
    		if (isTextFieldAEnabled())
    			this.textResponseA.drawTextBox();
    		if (isTextFieldBEnabled())
    			this.textResponseB.drawTextBox();
    	} 
    	
    	else {
    		//Survey Complete Page
    		String title = "Survey Complete!";
    		String body = "Your results:";
    		this.fontRendererObj.drawString("§l§n"+title, x - this.fontRendererObj.getStringWidth(title)/2 + (this.bookImageWidth-36)/2 + 7, y + 16, 0);
    		this.fontRendererObj.drawString(body, x + 36, y + 16 + 16, 0);
    		
    		this.fontRendererObj.drawString("Openness: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.OPENNESS, playerCopy.surveyResponses.personality)), x + 36, y + 16 + 16*2, 0);
    		this.fontRendererObj.drawString("Conscientious: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.CONSCIENTIOUSNESS, playerCopy.surveyResponses.personality)), x + 36, y + 16 + 16*3, 0);
    		this.fontRendererObj.drawString("Extraversion: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.EXTRAVERSION, playerCopy.surveyResponses.personality)), x + 36, y + 16 + 16*4, 0);
    		this.fontRendererObj.drawString("Agreeableness: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.AGREEABLENESS, playerCopy.surveyResponses.personality)), x + 36, y + 16 + 16*5, 0);
    		this.fontRendererObj.drawString("Neuroticism: "+signedFormat(
    				SurveyData.scoreResponses(SurveyData.Q.NEUROTICISM, playerCopy.surveyResponses.personality)), x + 36, y + 16 + 16*6, 0);
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
