package com.obt.servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Log {
	private Connection c;
	private static Log singleton;
	private static DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss.SSS");
	private static final int LENGTH=200;
	public static Log getSingleton(){
		if (singleton==null) singleton=new Log();
		return singleton;
	}
	
	private Log(){
		System.out.println("Constructor log");
		createLogDB();
		createLogTable();
	}
	public boolean createLogDB(){
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:log.db");
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		return true;
	}
	
	public boolean createLogTable(){
		try{
		Statement stmt = c.createStatement();
	      String sql = "CREATE TABLE LOG " +
	                   "(DATE 			   CHAR(30)    NOT NULL," +
	                   " TIPO            CHAR(3)    NOT NULL, " +
	                   " TEXTO          CHAR("+LENGTH+")    NOT NULL)";
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		return true;
	}
	
	public boolean insertLog(String tipo,String texto){
		if (texto==null){
			System.out.println("El texto es null");
			return false;
		}
		if (texto.length()>LENGTH) texto.substring(0, LENGTH);
		Date date = Calendar.getInstance().getTime();
		String now = formatter.format(date);
		//System.out.println("TIPO: "+tipo+" DATE: "+now+" TEXTO: "+texto);

		String sql = "INSERT INTO LOG (DATE,TIPO,TEXTO) " +
					"VALUES ('"+now+"','"+tipo+"','"+texto+"');"; 
		try{
			c = DriverManager.getConnection("jdbc:sqlite:log.db");
			Statement stmt = c.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		System.out.println("Record log: "+texto);
		return true;
	}
	
}
