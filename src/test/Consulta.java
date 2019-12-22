package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Consulta {
	public static final String RUTA_USER="C:\\MySiri\\USERS.txt";
	
	public static void main(String[] args) {
		//save(new File(RUTA_USER),.getBytes());
		
		System.out.println("===TABLE CODE");
		String codes=selectCode();
		System.out.println(codes);
		
		System.out.println("===TABLE PLACE");
		String places=selectPlace();
		System.out.println(places);
		
		System.out.println("===TABLE LIKE");
		String likes=selectLike();
		System.out.println(likes);
		
		System.out.println("===TABLE BUZON");
		String buzones=selectBuzon();
		System.out.println(buzones);
		
		System.out.println("===TABLE FRAME");
		String frames=selectAllFrame();
		//String frames=selectFrameId(500);
		save(new File(RUTA_USER),frames.getBytes());
		System.out.println(frames);
		
		System.out.println("===LOG");
		String log=selectFromLog();
		System.out.println(log);
	}
	public static void save(File nombre, byte[] data) {
		try {
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(nombre));
			BufferedInputStream is = new BufferedInputStream(
					new ByteArrayInputStream(data));
			copy(is, os);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error1"+"IOException");
		}
	}

	private static void copy(InputStream is, OutputStream os) {
		final byte[] buf = new byte[1024];
		int numBytes;
		try {
			while (-1 != (numBytes = is.read(buf))) {
				os.write(buf, 0, numBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				System.out.println("errro2"+"IOException");

			}
		}
	}
	
	public static String selectPlace(){
		String res="";
		Statement stmt = null;
	    try {
	    Connection c = DriverManager.getConnection("jdbc:sqlite:infoart.db");
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM PLACE;" );
	      while ( rs.next() ) {
	    	 String  place = rs.getString("place");
	         String  name = rs.getString("name");
	         String  url = rs.getString("url");
	         
	         res+="PLACE = " + place +"\n";
	         res+="NAME = " + name +"\n";
	         res+= "URL = " + url +"\n\n";
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return "";
	    }
	    return res;
	}
	public static String selectCode(){
		String res="";
		Statement stmt = null;
	    try {
	    Connection c = DriverManager.getConnection("jdbc:sqlite:infoart.db");
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM CODE;" );
	      while ( rs.next() ) {
	    	 String  code = rs.getString("code");
	         String  place = rs.getString("place");
	         String  data = rs.getString("data");
	         
	         res+="PLACE = " + place +"\n";
	         res+="CODE = " + code +"\n";
	         res+= "DATA = " + data +"\n\n";
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return "";
	    }
	    return res;
	}
	
	public static String selectLike(){
		String res="";
		Statement stmt = null;
	    try {
	    Connection c = DriverManager.getConnection("jdbc:sqlite:infoart.db");
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM LIKE;" );
	      while ( rs.next() ) {
	    	 String  id = rs.getString("id");
	    	 String  time = rs.getString("time");
	         String  place = rs.getString("place");
	         String  idBea = rs.getString("id_bea");
	         
	         res+="ID = " + id +"\n";
	         res+="PLACE = " + place +"\n";
	         res+="TIME = " + time +"\n";
	         res+= "ID_BEA = " + idBea +"\n\n";
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return "";
	    }
	    return res;
	}
	
	public static String selectBuzon(){
		String res="";
		Statement stmt = null;
	    try {
	    Connection c = DriverManager.getConnection("jdbc:sqlite:infoart.db");
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM BUZON;" );
	      while ( rs.next() ) {
	    	 String  id = rs.getString("id");
	    	 String  time = rs.getString("time");
	         String  place = rs.getString("place");
	         String  msg = rs.getString("msg");
	         
	         res+="ID = " + id +"\n";
	         res+="PLACE = " + place +"\n";
	         res+="TIME = " + time +"\n";
	         res+= "MSG = " + msg +"\n\n";
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return "";
	    }
	    return res;
	}
	
	public static String selectAllFrame(){
		return selectFrame("SELECT * FROM FRAME;");
	}
	public static String selectFrameId(int id){
		return selectFrame("SELECT * FROM FRAME WHERE ID="+id+";");
	}
	public static String selectFrame(String sql) {
		String res = "";
		Statement stmt = null;
		Statement stmt2 = null;
		try {
			Connection c = DriverManager.getConnection("jdbc:sqlite:infoart.db");
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSet rs2 = null;
			while (rs.next()) {
				String id = rs.getString("id");
				String time = rs.getString("time");
				String place = rs.getString("place");
				String idBea = rs.getString("id_bea");
				String during = rs.getString("during");
				String frameId = rs.getString("frame_id");
				
				res += "ID = " + id + "\n";
				res += "PLACE = " + place + "\n";
				res += "TIME = " + time + "\n";
				res += "ID_ESC = " + idBea + "\n";
				res += "DURING = " + during + "\n";
				res += "FRAME_ID = " + frameId + "\n";
				if (frameId!=null){
					stmt2 = c.createStatement();
					rs2 = stmt2.executeQuery("SELECT * FROM FRAME_BEACON where FRAME_ID='"+frameId+"';");
					while (rs2.next()) {
						idBea = rs2.getString("id_bea");
						String distance = rs2.getString("distance");
						res += "ID_BEA = " + idBea + "\n";
						res += "DISTANCE = " + distance + "\n";
					}
					rs2.close();
					stmt2.close();
				}
				res+="\n";
			}
			res+="\n";
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return "";
		}
		return res;
	}
	
	public static String selectFromLog(){
		Statement stmt = null;
		String res="";
	    try {
	      Connection c = DriverManager.getConnection("jdbc:sqlite:log.db");
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM LOG;" );
	      while ( rs.next() ) {
	    	 String  date = rs.getString("date");
	    	 String  tipo = rs.getString("tipo");
	         String  texto = rs.getString("texto");

	         res+="DATE = " + date +"\n";
	         res+="TIPO = " + tipo +"\n";
	         res+= "TEXTO = " + texto +"\n\n";
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return null;
	    }
	    return res;
	}
}
