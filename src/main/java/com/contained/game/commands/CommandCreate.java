package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.util.ObjectGenerator;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommandCreate implements ICommand {
	private final List<String> aliases;
	int targetX;
	int targetY;
	int targetZ;

	@Override
	public String getCommandName() {
		return "create";
	}

	public CommandCreate() {
		aliases = new ArrayList<String>();
		aliases.add(getCommandName());
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.command.ICommand#processCommand(net.minecraft.command.ICommandSender, java.lang.String[])
	 * @parameters: <object> <-option> <...>
	 */
	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if (!sender.getEntityWorld().isRemote) {
			String out = "";

			if (argString.length < 2) {
				out = getCommandUsage(sender);
			} else {
				switch(argString[1]){
				case "-v":{	//case1: create near most vulnerable player
					@SuppressWarnings("unchecked")
					List<EntityPlayer> playerList=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
					try{

						EntityPlayer target=playerList.get(0);
						if(playerList.get(0).equals((EntityPlayer)sender)){
							target=playerList.get(1);
						}
						float minHealth=playerList.get(0).getHealth();
						for(int i=0;i<playerList.size();i++){
							if(playerList.get(i).getHealth()<minHealth){
								minHealth=playerList.get(i).getHealth();
								target=playerList.get(i);
							}
						}
						if(argString.length == 2){	//para: object -v
							
							System.out.println("the most vulnerable player is: "+target.getDisplayName());
							this.targetX = target.getPlayerCoordinates().posX;
							this.targetY = target.getPlayerCoordinates().posY;
							this.targetZ = target.getPlayerCoordinates().posZ;
						}else{
							out = this.getCommandUsage(sender);
						}
					}catch(NullPointerException e){
						out = "No player online!";
					}	
					
				}
					break;
				case "-s":{	//case1: create near strongest player
					@SuppressWarnings("unchecked")
					List<EntityPlayer> playerList=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
					EntityPlayer target=playerList.get(0);
					float maxHealth=0;
					for(int i=0;i<playerList.size();i++){
						if(playerList.get(i).getHealth()>maxHealth){
							maxHealth=playerList.get(i).getHealth();
							target=playerList.get(i);
						}
					}
					
					if(argString.length == 2){	//para: object -v
						
						System.out.println("the strongest player is: "+target.getDisplayName());
						this.targetX = target.getPlayerCoordinates().posX;
						this.targetY = target.getPlayerCoordinates().posY;
						this.targetZ = target.getPlayerCoordinates().posZ;
					}else{
						out = this.getCommandUsage(sender);
					}
				}
					break;
				case "-c":{			//case2: create near closest player
					if(argString.length == 2){	//para: object -c
						@SuppressWarnings("unchecked")
						List<EntityPlayer> playerList=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
						EntityPlayer target=playerList.get(0);
						if(playerList.get(0).equals((EntityPlayer)sender)){
							target=playerList.get(1);
						}		
						float minDist=target.getDistanceToEntity((EntityPlayer)sender);
						for(int i=0;i<playerList.size();i++){
							if(playerList.get(i).getDistanceToEntity((EntityPlayer)sender)<minDist && !playerList.get(i).equals((EntityPlayer)sender)){
								minDist=playerList.get(i).getDistanceToEntity((EntityPlayer)sender);
								target=playerList.get(i);
							}
						}
						
						System.out.println("The closest player to Admin is: "+target.getDisplayName());
						this.targetX = target.getPlayerCoordinates().posX;
						this.targetY = target.getPlayerCoordinates().posY;
						this.targetZ = target.getPlayerCoordinates().posZ;

					}else{
						out = this.getCommandUsage(sender);
					}
				}
					break;
				case "-f":{			//case2: create near farthest player
					if(argString.length == 2){	//para: object -c
						@SuppressWarnings("unchecked")
						List<EntityPlayer> playerList=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
						EntityPlayer target=playerList.get(0);		
						float maxDist=0;
						for(int i=0;i<playerList.size();i++){
							if(playerList.get(i).getDistanceToEntity((EntityPlayer)sender)>maxDist){
								maxDist=playerList.get(i).getDistanceToEntity((EntityPlayer)sender);
								target=playerList.get(i);
							}
						}
						
						System.out.println("The farthest player to Admin is: "+target.getDisplayName());
						this.targetX = target.getPlayerCoordinates().posX;
						this.targetY = target.getPlayerCoordinates().posY;
						this.targetZ = target.getPlayerCoordinates().posZ;

					}else{
						out = this.getCommandUsage(sender);
					}
				}
					break;
				case "-h":{	//case1: create near hungriest player
					@SuppressWarnings("unchecked")
					List<EntityPlayer> playerList=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
					try{
						EntityPlayer target=playerList.get(0);
						if(target.equals((EntityPlayer)sender)){
							target=playerList.get(1);
						}
						float minHunger=target.getFoodStats().getSaturationLevel();
						for(int i=0;i<playerList.size();i++){
							if(playerList.get(i).getFoodStats().getSaturationLevel()<minHunger && !playerList.get(i).equals((EntityPlayer)sender)){
								minHunger=playerList.get(i).getFoodStats().getSaturationLevel();
								target=playerList.get(i);
							}
						}
						
						if(argString.length == 2){	//para: object -v
							
							System.out.println("the hungeriest player is: "+target.getDisplayName());
							this.targetX = target.getPlayerCoordinates().posX;
							this.targetY = target.getPlayerCoordinates().posY;
							this.targetZ = target.getPlayerCoordinates().posZ;
						}else{
							out = this.getCommandUsage(sender);
						}	
					}catch(NullPointerException e){
						out = "No other player's online!";
					}
				}
					break;
				case "-n":{		//case3: create near specified player
					if(argString.length == 3){	//para: object -n playerName
						try{
							this.targetX = sender.getEntityWorld().getPlayerEntityByName(argString[2]).getPlayerCoordinates().posX;
							this.targetY = sender.getEntityWorld().getPlayerEntityByName(argString[2]).getPlayerCoordinates().posY;
							this.targetZ = sender.getEntityWorld().getPlayerEntityByName(argString[2]).getPlayerCoordinates().posZ;

							
						}catch(NullPointerException e){
							out="Player: "+argString[2]+" does not exist.";
						}
						
					}else{
						out = "/" + getCommandName() + " <object> <-n> <playername>";
					}
				}	
					break;
				
				default:
					out = getCommandUsage(sender);
						
				
				}
				ObjectGenerator og=new ObjectGenerator();
				if(!og.generate(argString[0], sender.getEntityWorld(), targetX, targetY, targetZ)){
					out = this.getCommandUsage(sender);
				}
			}
			if (!out.equals("")) {
				sender.addChatMessage(new ChatComponentText(out));
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/" + getCommandName() + " <object> <-option>";
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List<String> getCommandAliases() {
		return this.aliases;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}
}
