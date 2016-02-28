package com.contained.game.data;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraftforge.event.entity.EntityEvent;

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
	
	public static void insertChat(String server, String user, String message, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CHAT VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, message);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMove(String server, String user, int x, int y, int z, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO POSITION VALUES (?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setInt(3, x);
			preparedStatement.setInt(4, y);
			preparedStatement.setInt(5, z);
			preparedStatement.setString(6, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertConsume(String server, String user, String item, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CONSUME VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, item);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertUsed(String server, String user, String item, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO USED VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, item);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertSmelt(String server, String user, String item, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO SMELT VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, item);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertCraft(String server, String user, String item, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO CRAFT VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, item);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAnvil(String server, String user, String left, int l_size, String right, int r_size, String out, int o_size, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ANVIL VALUES (?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, left);
			preparedStatement.setInt(4, l_size);
			preparedStatement.setString(5, right);
			preparedStatement.setInt(6, r_size);
			preparedStatement.setString(7, out);
			preparedStatement.setInt(8, o_size);
			preparedStatement.setString(9, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertMine(String server, String user, String block, int x, int y, int z, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO MINE VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, block);
			preparedStatement.setInt(4, x);
			preparedStatement.setInt(5, y);
			preparedStatement.setInt(6, z);
			preparedStatement.setString(7, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertBuild(String server, String user, String block, int x, int y, int z, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO BUILD VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setInt(3, x);
			preparedStatement.setInt(4, y);
			preparedStatement.setInt(5, z);
			preparedStatement.setString(6, block);
			preparedStatement.setString(7, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertKill(String server, String killer, String victim, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO KILLED VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, killer);
			preparedStatement.setString(3, victim);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertLogin(String server, String user, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO LOGIN VALUES (?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertLogOut(String server, String user, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO LOGOUT VALUES (?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertAchievement(String server, String user, String type, String date){
		try{
			PreparedStatement preparedStatement = DB.prepareStatement("INSERT INTO ACHIEVEMENT VALUES (?,?,?,?)");
			preparedStatement.setString(1, server);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, type);
			preparedStatement.setString(4, date);
			preparedStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private void connectDataBase(){
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
		try{
			if(DB != null && !DB.isClosed())
				DB.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
