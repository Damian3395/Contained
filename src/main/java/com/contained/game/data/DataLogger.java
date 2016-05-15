package com.contained.game.data;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.contained.game.util.Resources;

public class DataLogger {
	public static java.sql.Connection DB;
	public static String USERNAME = "root";
	public static String PASSWORD = "GameScience2016";
	public static String DB_NAME = "contained";
	public static String URL = "localhost";
	public static String PORT = "3306";
	
	public DataLogger(){
		connectDataBase();
	}
	
	public static void insertPersonality(String server, String player, int age, int year, int month, String ethnicity, double openness, double conscientiousness, double extraversion, double agreeableness, double neuroticism, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO PERSONALITY VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, player);
			preparedStatement.setInt(3, age);
			preparedStatement.setInt(4, year);
			preparedStatement.setInt(5, month);
			preparedStatement.setString(6, ethnicity);
			preparedStatement.setDouble(7, openness);
			preparedStatement.setDouble(8, conscientiousness);
			preparedStatement.setDouble(9, extraversion);
			preparedStatement.setDouble(10, agreeableness);
			preparedStatement.setDouble(11, neuroticism);
			preparedStatement.setString(12, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertGameScore(String server, int gameID, int gameMode, String team, int score, int time, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO TEAMSCORE VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setInt(3, gameMode);
			preparedStatement.setString(4, team);
			preparedStatement.setInt(5, score);
			preparedStatement.setInt(6, time);
			preparedStatement.setString(7, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	//Conditions: TIE, TERRITORY, HIGHEST_SCORE, MAX_KILLS, EMBLEMS
	public static void insertWinningTeam(String server, int gameID, int gameMode, String team, String condition, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO TEAMWON VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setInt(3, gameMode);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, condition);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAlter(String server, int gameID, String team, String player, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ALTER VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setString(3, team);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertRemoveTerritory(String server, String world, int gameID, int gameMode, String owner, String attacker, String player, int x, int z, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO REMOVETERRITORY VALUES (?,?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, world);
			preparedStatement.setInt(3, gameID);
			preparedStatement.setInt(4, gameMode);
			preparedStatement.setString(5, owner);
			preparedStatement.setString(6, attacker);
			preparedStatement.setString(7, player);
			preparedStatement.setInt(8, x);
			preparedStatement.setInt(9, z);
			preparedStatement.setString(10, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAddTerritory(String server, String world, int gameID, int gameMode, String team, String player, int x, int z, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ADDTERRITORY VALUES (?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, world);
			preparedStatement.setInt(3, gameID);
			preparedStatement.setInt(4, gameMode);
			preparedStatement.setString(5, team);
			preparedStatement.setString(6, player);
			preparedStatement.setInt(7, x);
			preparedStatement.setInt(8, z);
			preparedStatement.setString(9, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertRestoreLife(String server, String world, int gameID, String team, String player, String revive, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO REVIVE VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, world);
			preparedStatement.setInt(3, gameID);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, player);
			preparedStatement.setString(6, revive);
			preparedStatement.setString(7, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertTreasureScore(String server, int gameID, String player, String team, int score, int alters, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO SCORETREASURE VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setString(3, player);
			preparedStatement.setString(4, team);
			preparedStatement.setInt(5, score);
			preparedStatement.setInt(6, alters);
			preparedStatement.setString(7, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertPVPScore(String server, int gameID, String player, String team, int kills, int deaths, int antiTerritory, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO SCOREPVP VALUES (?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setString(3, player);
			preparedStatement.setString(4, team);
			preparedStatement.setInt(5, kills);
			preparedStatement.setInt(6, deaths);
			preparedStatement.setInt(7, antiTerritory);
			preparedStatement.setString(8, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMiniGamePlayer(String server, int gameID, int gameMode, String player, String team, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO MINIGAMEPLAYER VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setInt(3, gameMode);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, team);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void deleteMiniGamePlayer(String player){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("DELETE FROM MINIGAMEPLAYER WHERE PLAYER = ?");
			preparedStatement.setString(1, player);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertNewMiniGame(String server, int gameID, int gameMode, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO MINIGAME VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setInt(3, gameMode);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMiniGameChat(String server, int gameID, int gameMode, String player, String team, String chat, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO MINIGAMECHAT VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setInt(2, gameID);
			preparedStatement.setInt(3, gameMode);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, team);
			preparedStatement.setString(6, chat);
			preparedStatement.setString(7, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertTrade(String server, String creator, String world, int gameID, String acceptor, String offer, int offerSize, String request, int requestSize, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO TRADE VALUES (?,?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, creator);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, acceptor);
			preparedStatement.setString(6, offer);
			preparedStatement.setInt(7, offerSize);
			preparedStatement.setString(8, request);
			preparedStatement.setInt(9, requestSize);
			preparedStatement.setString(10, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertPerk(String server, String user, String world, int perkID, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO PERK VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, perkID);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertCreateTeam(String server, String user, String world, String team, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CREATETEAM VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertJoinTeam(String server, String user, String world, String team, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO JOINTEAM VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertInvitePlayer(String server, String user, String world, String player, String team, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO INVITEPLAYER VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, team);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertKickPlayer(String server, String user, String world, String player, String team, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO KICKPLAYER VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, team);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertDisbandTeam(String server, String user, String world, String team, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO DISBANDTEAM VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertPromoteTeamPlayer(String server, String user, String world, String team, String player, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO PROMOTEPLAYER VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, player);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertDemoteTeamPlayer(String server, String user, String world, String team, String player, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO DEMOTEPLAYER VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, team);
			preparedStatement.setString(5, player);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertGuildChat(String server, String user, String team, String world, String message, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO GUILDCHAT VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, team);
			preparedStatement.setString(4, world);
			preparedStatement.setString(5, message);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertChat(String server, String user, String world, String message, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CHAT VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, message);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMove(String server, String user, String world, int gameID, int x, int y, int z, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO POSITION VALUES (?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setInt(5, x);
			preparedStatement.setInt(6, y);
			preparedStatement.setInt(7, z);
			preparedStatement.setString(8, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertPortal(String server, String user, String oldWorld, String newWorld, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO PORTAL VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, oldWorld);
			preparedStatement.setString(4, newWorld);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertConsume(String server, String user, String world, int gameID, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CONSUME VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, item);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertUsed(String server, String user, String world, int gameID,String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO USED VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, item);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertSmelt(String server, String user, String world, int gameID, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO SMELT VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, item);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertCraft(String server, String user, String world, int gameID, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CRAFT VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, item);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAnvil(String server, String user, String world, int gameID, String left, int l_size, String right, int r_size, String out, int o_size, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ANVIL VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, left);
			preparedStatement.setInt(6, l_size);
			preparedStatement.setString(7, right);
			preparedStatement.setInt(8, r_size);
			preparedStatement.setString(9, out);
			preparedStatement.setInt(10, o_size);
			preparedStatement.setString(11, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMine(String server, String user, String world, int gameID, String block, int x, int y, int z, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO MINE VALUES (?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setString(5, block);
			preparedStatement.setInt(6, x);
			preparedStatement.setInt(7, y);
			preparedStatement.setInt(8, z);
			preparedStatement.setString(9, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertBuild(String server, String user, String world, int gameID, String block, int x, int y, int z, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO BUILD VALUES (?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, gameID);
			preparedStatement.setInt(5, x);
			preparedStatement.setInt(6, y);
			preparedStatement.setInt(7, z);
			preparedStatement.setString(8, block);
			preparedStatement.setString(9, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertKill(String server, String world, int gameID, String killer, String victim, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO KILLED VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, world);
			preparedStatement.setInt(3, gameID);
			preparedStatement.setString(4, killer);
			preparedStatement.setString(5, victim);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertLogin(String server, String user, String world, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO LOGIN VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertLogOut(String server, String user, String world, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO LOGOUT VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAchievement(String server, String user, String world, String type, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ACHIEVEMENT VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, type);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private void connectDataBase(){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		
		try{
			DB = DriverManager.getConnection("jdbc:mysql://" + URL + ":" + PORT + "/" + DB_NAME, USERNAME, PASSWORD);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void disconnectDataBase(){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			if(DB != null && !DB.isClosed())
				DB.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
