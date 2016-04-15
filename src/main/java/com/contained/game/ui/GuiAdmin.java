package com.contained.game.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.network.ServerPacketHandler;
import com.contained.game.network.ServerPacketHandlerUtil;
import com.contained.game.ui.components.Container;
import com.contained.game.ui.components.GuiScrollPane;
import com.contained.game.ui.components.GuiTab;
import com.contained.game.util.ObjectGenerator;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class GuiAdmin extends GuiScreen{
	
	private Container adminWindow,onlinePlayerWindow;
	private GuiScrollPane playerInfo;
	private int x,y;
	private EntityPlayer player;
	List<EntityPlayer> playerList;
	List<String> onlinePlayerNames;
	
	
	private GuiTextField tf_password;
	
	private boolean isObjectDefaultText=false;
	private boolean isPlayerDefaultText=false;
	
	//TextFields
	private GuiTextField tf_objectName;
	private GuiTextField tf_healthPercentage;
	private GuiTextField tf_hungerPercentage;
	private GuiTextField tf_targetPlayer;	

	//Buttons
	private GuiButton btn_Login;
	private GuiButton btn_LoginCancel;
	private GuiButton btn_Create;
	private GuiButton btn_Change;
	private GuiButton btn_getMostVulnerable;
	private GuiButton btn_getHungeriest;
	private GuiButton btn_getClosest;
	private GuiButton btn_getFarthest;
	
	//Button ID
	private final int BTN_LOGIN=0;
	private final int BTN_LOGIN_CANCEL=1;
	private final int BTN_CREATE=2;
	private final int BTN_CHANGE=3;
	private final int BTN_VUL=4;
	private final int BTN_HUN=5;
	private final int BTN_CLS=6;
	private final int BTN_FAR=7;
	
	
	@SuppressWarnings("unchecked")
	public GuiAdmin(EntityPlayer p){
		this.player=p;
		this.playerList=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		this.onlinePlayerNames= new ArrayList<String>();
		for(EntityPlayer ep : playerList){
			System.out.println("online player:"+ep.getDisplayName());
			this.onlinePlayerNames.add(ep.getDisplayName().toString());
			System.out.println("add to list:"+ep.getDisplayName());
		}
		
	}
	
	@Override
    public void initGui(){
		this.x=this.width/2;
		this.y=this.height/2;
		
		this.adminWindow = new Container((this.width-256)/2, ((this.height-200)/2), 256, 200, "adminWindow.png", this);
		this.playerInfo = new GuiScrollPane(this, x-120, y-5,onlinePlayerNames);
		
		
		this.tf_password = new GuiTextField(this.mc.fontRenderer, x-50, y-40, 100, 20);
		this.tf_password.setTextColor(Color.WHITE.hashCode());
		
		
		this.tf_healthPercentage = new GuiTextField(this.mc.fontRenderer, x-120, y-70, 30, 20);
		this.tf_healthPercentage.setTextColor(Color.WHITE.hashCode());
		
		
		this.tf_hungerPercentage = new GuiTextField(this.mc.fontRenderer, x-85, y-70, 30, 20);
		this.tf_hungerPercentage.setTextColor(Color.WHITE.hashCode());
		
		
		this.tf_objectName = new GuiTextField(this.mc.fontRenderer, x-50, y-70, 60, 20);
		this.tf_objectName.setTextColor(Color.WHITE.hashCode());
		
		
		this.tf_targetPlayer = new GuiTextField(this.mc.fontRenderer, x+30, y-70, 90, 20);
		this.tf_targetPlayer.setTextColor(Color.WHITE.hashCode());
		
		
		this.btn_Login = new GuiButton(BTN_LOGIN, x-65, y, 50, 20, "Login");
		this.btn_LoginCancel = new GuiButton(BTN_LOGIN_CANCEL, x+15, y, 50, 20, "Cancel");
		this.btn_Create = new GuiButton(BTN_CREATE, x+35, y+75, 40, 20, "Create");
		this.btn_Change = new GuiButton(BTN_CHANGE, x+75, y+75, 40, 20, "Change");
		this.btn_getMostVulnerable = new GuiButton(BTN_VUL, x+35, y+55, 80, 20, "Most Vulnerable");
		this.btn_getHungeriest = new GuiButton(BTN_HUN, x+35, y+35, 80, 20, "Hungriest");
		this.btn_getClosest = new GuiButton(BTN_CLS, x+35, y+15, 80, 20, "Closest");
		this.btn_getFarthest = new GuiButton(BTN_FAR, x+35, y-5, 80, 20, "Farthest");
		
		this.updateButtons();

    }
    
    public void updateScreen(){
    	super.updateScreen();
    }
    
    @SuppressWarnings("unchecked")
	private void updateButtons() {    	
    	this.buttonList.clear();
    	if (!ExtendedPlayer.get(this.player).isAdmin()) {
    		//Login Page
    		
    		this.buttonList.add(this.btn_Login);
    		this.buttonList.add(this.btn_LoginCancel);
    	} else {
    		//Admin Control Page    		
    		this.buttonList.add(this.btn_Create);
    		this.buttonList.add(this.btn_Change);
    		this.buttonList.add(this.btn_getMostVulnerable);
    		this.buttonList.add(this.btn_getHungeriest);
    		this.buttonList.add(this.btn_getClosest);
    		this.buttonList.add(this.btn_getFarthest);
    	}
    }
    @Override
    public void mouseClicked(int i, int j, int k) {
    	// handle mouseClicked events on textFields
    	this.tf_password.mouseClicked(i,j,k);
    	this.tf_healthPercentage.mouseClicked(i,j,k);
    	this.tf_hungerPercentage.mouseClicked(i,j,k);
    	this.tf_objectName.mouseClicked(i,j,k);
    	this.tf_targetPlayer.mouseClicked(i,j,k);
    	this.playerInfo.mouseClicked(i, j, k);
    	if(this.playerInfo.isElementSelected){
    		this.tf_targetPlayer.setText(this.playerInfo.getText());
    	}
    	
    	// handle other mouseClicked events
		super.mouseClicked(i, j, k);
	}
    
   
    
    @Override
    public void keyTyped(char c, int i) {
    	
    	// handle keyTyped events on tf_password
		if (this.tf_password.isFocused()) {
			
				this.tf_password.textboxKeyTyped(c, i);
			
		} else if(this.tf_healthPercentage.isFocused()){
	    	// handle keyTyped events on tf_health
			
				this.tf_healthPercentage.textboxKeyTyped(c, i);
			
		} else if(this.tf_hungerPercentage.isFocused()){
	    	// handle keyTyped events on tf_hunger
			
				this.tf_hungerPercentage.textboxKeyTyped(c, i);
			
		} else if(this.tf_objectName.isFocused()){
	    	// handle keyTyped events on tf_object
			if(this.isObjectDefaultText){
				this.tf_objectName.setText(""+c);
				this.isObjectDefaultText=false;
			}else{
				this.tf_objectName.textboxKeyTyped(c, i);
			}
		} else if(this.tf_targetPlayer.isFocused()){
	    	// handle keyTyped events on tf_player
			if(this.isPlayerDefaultText){
				this.tf_targetPlayer.setText(""+c);
				this.isPlayerDefaultText=false;
			}else{
				this.tf_targetPlayer.textboxKeyTyped(c, i);
			}
		}else{
	    	// handle other keyTyped events
			super.keyTyped(c, i);
		}
	}
    
    @Override
    protected void actionPerformed(GuiButton b){	// handle button click events
    	if(b.enabled){
    		switch(b.id){
	    	case BTN_LOGIN : 
	    		if(this.tf_password.getText().equals("password")){
	    			System.out.println("Password right.");
	    			this.player.setInvisible(true);
	    			this.player.capabilities.disableDamage = true;
	    			this.player.capabilities.allowFlying = true;
					ExtendedPlayer.get(this.player).setAdminRights(true);
					PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.BECOME_ADMIN);
					Contained.channel.sendToServer(adminPacket.toPacket());
					this.updateButtons();
	    		} else {
//	    			this.fontRendererObj.drawString("Wrong Password!", x -120, y -80, 0);  //show wrong password warning
	    			System.out.println("Wrong Password!");
	    		}
	    		
	    		break;
	    	
	    	case BTN_LOGIN_CANCEL : 
	    		//close GUI
	    		break;
	    	
	    	case BTN_CREATE : 
	    		if(!this.tf_objectName.getText().isEmpty() && !this.tf_targetPlayer.getText().isEmpty()){
	    			try{
		    				PacketCustom adminCreatePacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.ADMIN_CREATE);
							adminCreatePacket.writeString(this.tf_objectName.getText());
							adminCreatePacket.writeString(this.tf_targetPlayer.getText());
							Contained.channel.sendToServer(adminCreatePacket.toPacket());
							
							
					}catch(NullPointerException e){
//						System.out.println("ERROR!");
						this.tf_objectName.setText("Check Object Name");
						this.tf_targetPlayer.setText("Check Player Name");
						this.isObjectDefaultText=true;
						this.isPlayerDefaultText=true;
					}catch(Exception e){
						this.tf_objectName.setText("Check Object Name");
						this.tf_targetPlayer.setText("Check Player Name");
						this.isObjectDefaultText=true;
						this.isPlayerDefaultText=true;
					}
	    		}
	    		break;
	    	
	    	case BTN_CHANGE :
	    		int healthPercentage,hungerPercentage;
	    		PacketCustom adminChangePacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.ADMIN_CHANGE);
	    		if(!this.tf_targetPlayer.getText().isEmpty() && !this.isPlayerDefaultText){
	    			if(this.tf_healthPercentage.getText().isEmpty()){
	    				healthPercentage=-1;
	    			}else{
	    				healthPercentage=Integer.parseInt(this.tf_healthPercentage.getText());
	    			}
	    			if(this.tf_hungerPercentage.getText().isEmpty()){
	    				hungerPercentage=-1;
	    			}else{
	    				hungerPercentage=Integer.parseInt(this.tf_hungerPercentage.getText());
	    			}
	    			adminChangePacket.writeInt(healthPercentage);
	    			adminChangePacket.writeInt(hungerPercentage);
	    			adminChangePacket.writeString(this.tf_targetPlayer.getText());
					Contained.channel.sendToServer(adminChangePacket.toPacket());
	    		}
	    		break;
	    	
	    	case BTN_VUL :	// look for the most vulnerable player
	    		
				try{
					if(this.playerList.size()>1){//only if there are more players online
						EntityPlayer target=this.playerList.get(0);
						
						if(this.playerList.get(0).equals(this.player)){
							target=playerList.get(1);
						}
						float minHealth=target.getHealth();
						for(int i=0;i<this.playerList.size();i++){
							if(this.playerList.get(i).getHealth()<minHealth && !this.playerList.get(i).equals(this.player)){
								minHealth=playerList.get(i).getHealth();
								target=playerList.get(i);
							}
						}
						
						this.tf_targetPlayer.setText(target.getDisplayName());
						
					}else{
						this.tf_targetPlayer.setText("No Player Online.");
					}
				}catch(Exception e){
					System.out.println("ERROR in BTN_VUL");
				}
					
	    		break;
	    	case BTN_HUN :		// look for the hungriest player
	    		try{
					if(this.playerList.size()>1){//only if there are more players online
						EntityPlayer target=this.playerList.get(0);
						
						if(this.playerList.get(0).equals(this.player)){
							target=playerList.get(1);
						}
						float minHunger=target.getFoodStats().getFoodLevel();
						for(int i=0;i<this.playerList.size();i++){
							if(this.playerList.get(i).getFoodStats().getFoodLevel()<minHunger && !this.playerList.get(i).equals(this.player)){
								minHunger=playerList.get(i).getFoodStats().getFoodLevel();
								target=playerList.get(i);
							}
						}
						
						this.tf_targetPlayer.setText(target.getDisplayName());
						
					}else{
						this.tf_targetPlayer.setText("No Player Online.");
					}
				}catch(Exception e){
					System.out.println("ERROR in BTN_HUN");
				}
	    		break;
	    	case BTN_CLS :		// look for the closest player
	    		try{
					if(this.playerList.size()>1){//only if there are more players online
						EntityPlayer target=this.playerList.get(0);
						
						if(this.playerList.get(0).equals(this.player)){
							target=playerList.get(1);
						}
						float minDistance=target.getDistanceToEntity(this.player);
						for(int i=0;i<this.playerList.size();i++){
							if(this.playerList.get(i).getDistanceToEntity(this.player)<minDistance && !this.playerList.get(i).equals(this.player)){
								minDistance=playerList.get(i).getDistanceToEntity(this.player);
								target=playerList.get(i);
							}
						}
						
						this.tf_targetPlayer.setText(target.getDisplayName());
						
					}else{
						this.tf_targetPlayer.setText("No Player Online.");
					}
				}catch(Exception e){
					System.out.println("ERROR in BTN_CLS");
				}
	    		break;

	    	case BTN_FAR :		// look for the farthest player
	    		try{
					if(this.playerList.size()>1){//only if there are more players online
						EntityPlayer target=this.playerList.get(0);
						
						if(this.playerList.get(0).equals(this.player)){
							target=playerList.get(1);
						}
						float maxDistance=0.0f;
						for(int i=0;i<this.playerList.size();i++){
							if(this.playerList.get(i).getDistanceToEntity(this.player)>maxDistance && !this.playerList.get(i).equals(this.player)){
								maxDistance=playerList.get(i).getDistanceToEntity(this.player);
								target=playerList.get(i);
							}
						}
						
						this.tf_targetPlayer.setText(target.getDisplayName());
						
					}else{
						this.tf_targetPlayer.setText("No Player Online.");
					}
				}catch(Exception e){
					System.out.println("ERROR in BTN_FAR");
				}
	    		break;
	    		
    		}
    	}
    }
    
	@Override
	public void onGuiClosed() {
	}
	
	@Override
    public void drawScreen(int w, int h, float ticks)
    {
    	this.adminWindow.render();
    	if(ExtendedPlayer.get(this.player).isAdmin()){
    		renderAction();
    	}else{
    		renderLogin(w,h);
    	}
    	 super.drawScreen(w, h, ticks);
    }
    private void renderLogin(int w, int h){		//render Admin Login UI
    	this.tf_password.drawTextBox();
    	this.fontRendererObj.drawString("Please Enter Admin Password:", x -65, y -50, 0);    	
    }
    private void renderAction(){	//render Admin control UI
    	    	
    	this.tf_password.setVisible(false);
    	this.tf_healthPercentage.drawTextBox();
    	this.tf_hungerPercentage.drawTextBox();
    	this.tf_objectName.drawTextBox();
    	this.tf_targetPlayer.drawTextBox();
    	this.fontRendererObj.drawString("HP(%)  FP(%)   Object", x -120, y -85, Color.WHITE.hashCode());
		this.fontRendererObj.drawString("Target Player", x+40, y-85, Color.WHITE.hashCode());
		this.fontRendererObj.drawString("You may click or use filter to select target.", x-120, y-25, Color.WHITE.hashCode());
		this.playerInfo.render();
		
    }
    
}

