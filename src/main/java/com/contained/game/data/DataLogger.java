package com.contained.game.data;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.contained.game.util.Resources;

/*
 * Added Events:
 * Territory
 * Groups
 * Class Occupation
 * Potion Brewing
 * Item Enchanted
 * Animal Tamed
 * Portal
*/
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
	
	public static void insertInvitePlayer(String server, String user, String world, String player, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO INVITEPLAYER VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertKickPlayer(String server, String user, String world, String player, String date){
		if(!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO KICKPLAYER VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, player);
			preparedStatement.setString(5, date);
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
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO PROMOTEPALYER VALUES (?,?,?,?,?,?)");
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
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO DEMOTEPALYER VALUES (?,?,?,?,?,?)");
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
	
	public static void insertMove(String server, String user, String world, int x, int y, int z, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO POSITION VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, x);
			preparedStatement.setInt(5, y);
			preparedStatement.setInt(6, z);
			preparedStatement.setString(7, date);
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
	
	public static void insertConsume(String server, String user, String world, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CONSUME VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, item);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertUsed(String server, String user, String world, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO USED VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, item);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertSmelt(String server, String user, String world, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO SMELT VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, item);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertCraft(String server, String user, String world, String item, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CRAFT VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, item);
			preparedStatement.setString(5, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAnvil(String server, String user, String world, String left, int l_size, String right, int r_size, String out, int o_size, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ANVIL VALUES (?,?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, left);
			preparedStatement.setInt(5, l_size);
			preparedStatement.setString(6, right);
			preparedStatement.setInt(7, r_size);
			preparedStatement.setString(8, out);
			preparedStatement.setInt(9, o_size);
			preparedStatement.setString(10, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMine(String server, String user, String world, String block, int x, int y, int z, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO MINE VALUES (?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setString(4, block);
			preparedStatement.setInt(5, x);
			preparedStatement.setInt(6, y);
			preparedStatement.setInt(7, z);
			preparedStatement.setString(8, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertBuild(String server, String user, String world, String block, int x, int y, int z, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO BUILD VALUES (?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, world);
			preparedStatement.setInt(4, x);
			preparedStatement.setInt(5, y);
			preparedStatement.setInt(6, z);
			preparedStatement.setString(7, block);
			preparedStatement.setString(8, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertKill(String server, String world, String killer, String victim, String date){
		if (!Resources.LOGGING_ENABLED)
			return;
		
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO KILLED VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, world);
			preparedStatement.setString(3, killer);
			preparedStatement.setString(4, victim);
			preparedStatement.setString(5, date);
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
