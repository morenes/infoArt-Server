package com.obt.servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.Beacon;
import model.Frame;
import model.Punto;
import model.Trayectoria;
import model.TrayectoriaSegmentada;
import model.mapa.Mapa;

public class DataBase {
	private Connection c;
	private long frameId = 0;
	private static DataBase singleton;
	private LinkedList<RxMensaje> cola;
	private Thread t;
	private String URL="jdbc:sqlite:"+"apache-tomcat-6.0.51/bin/"+"infoart.db";
	//private String URL="jdbc:sqlite:infoart.db";
	public static DataBase getSingleton() {
		if (singleton == null)
			singleton = new DataBase();
		return singleton;
	}

	private DataBase() {
		createDataBase();
		createTable();
		cola = new LinkedList<RxMensaje>();
		creaThread();
	}

	public boolean createDataBase() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(URL);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}

	//
	public boolean createTable() {
		try {
			Statement stmt = c.createStatement();
			String sql;
			sql = "CREATE TABLE PUNTO_TRAY " + "(ID 	 UNSIGNED INT   NOT NULL,"
					+ " X       UNSIGNED SMALLINT   NOT NULL, " + " Y       UNSIGNED SMALLINT   NOT NULL, "
					+ " TIME       UNSIGNED SMALLINT   NOT NULL, " + " DURING     UNSIGNED SMALLINT    NOT NULL)";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE PLACE " + "(PLACE 			UNSIGNED SMALLINT PRIMARY KEY NOT NULL,"
					+ " NAME           CHAR(32)," + " URL           CHAR(128))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE CODE " + "(CODE 			UNSIGNED INT    NOT NULL,"
					+ " PLACE          UNSIGNED SMALLINT  NOT NULL, " + " DATA           UNSIGNED INT,"
					+ "primary key(code,place))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE LIKE " + "(ID 			UNSIGNED INT    NOT NULL,"
					+ " PLACE          UNSIGNED SMALLINT NOT NULL, " + " TIME           UNSIGNED INT, "
					+ " ID_BEA         UNSIGNED TINYINT    NOT NULL, " + "primary key(id,place,id_bea))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE BUZON " + "(ID 			 UNSIGNED INT    NOT NULL,"
					+ " PLACE        UNSIGNED SMALLINT NOT NULL, " + " TIME         UNSIGNED INT, "
					+ " MSG          CHAR(256)    NOT NULL)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE FRAME " + "(ID             UNSIGNED INT   NOT NULL,"
					+ " PLACE          UNSIGNED SMALLINT NOT NULL, " + " TIME           UNSIGNED INT, "
					+ " ID_BEA         UNSIGNED TINYINT, " + " DURING         UNSIGNED TINYINT, "
					+ " FRAME_ID       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE FRAME_BEACON " + "(FRAME_ID 	 UNSIGNED INT   NOT NULL,"
					+ " ID_BEA       UNSIGNED TINYINT   NOT NULL, " + " DISTANCE     UNSIGNED TINYINT    NOT NULL)";
			stmt.executeUpdate(sql);
			
			

			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Tables created successfully");
		return true;
	}

	public boolean createPlace(int place, String name, String url) {
		String sql = "INSERT INTO PLACE (PLACE,NAME,URL) " + "VALUES ('" + place + "','" + name + "','" + url + "');";
		return insert(sql);
	}

	public boolean createCode(long code, int place, int data) {
		String sql = "INSERT INTO CODE (CODE,PLACE,DATA) " + "VALUES ('" + code + "','" + place + "','" + data + "');";
		return insert(sql);
	}

	public boolean treatLike(RxMensaje rx) {
		String sql;
		if (rx.getNum() == 1)
			sql = "INSERT INTO LIKE (ID,PLACE,TIME,ID_BEA) " + "VALUES ('" + rx.getId() + "','" + rx.getPlace() + "','"
					+ rx.getTime() + "','" + rx.getIdBea() + "');";
		else
			sql = "DELETE FROM LIKE where PLACE='" + rx.getPlace() + "' and ID='" + rx.getId() + "' and ID_BEA='"
					+ rx.getIdBea() + "';";
		return insert(sql);
	}

	public boolean createBuzon(RxMensaje rx) {
		String sql = "INSERT INTO BUZON (ID,PLACE,TIME,MSG) " + "VALUES ('" + rx.getId() + "','" + rx.getPlace() + "','"
				+ rx.getTime() + "','" + new String(rx.getMensajeOk()) + "');";
		return insert(sql);
	}

	public Frame createFrame(RxMensaje rx, Map<Integer, Beacon> map) {
		HashMap<Integer, Integer> mapa = rx.getMap();
		LinkedList<Beacon> puntos = new LinkedList<Beacon>();
		for (Integer key : mapa.keySet())
			puntos.add(new Beacon(map.get(key), mapa.get(key), key));
		cola.add(rx);
		return new Frame(puntos, rx.getTime(), rx.getId(), rx.getIdBea(), rx.getDuracion());
	}
	
	public Frame createFrameClone(Frame fra,int nuevoId,int place) {
		List<Beacon> puntos = fra.getBeacons();
		LinkedList<Beacon> nuevos = new LinkedList<Beacon>();
		Random r=new Random();
		
		for (Beacon b : puntos) {
			int d=(int)(b.getReal()*((r.nextFloat()/6)+0.917));
			nuevos.add(new Beacon(b.getX(),b.getY(),d,b.getCoef(),b.getKey()));
		}
		Frame frame=new Frame(nuevos, fra.getTime()+1000, nuevoId, fra.getIdBea(), fra.getDuring());
		String sql = "INSERT INTO FRAME (ID,PLACE,TIME,ID_BEA,DURING) " + "VALUES ('" + nuevoId + "','"
				+ place + "','" + frame.getTime() + "','" + frame.getIdBea() + "','" + frame.getDuring() + "');";

		long idFrame = insertAndGet(sql, frame.getTime());
		if (idFrame == 0) {
			System.out.println("Algo ha fallado idFrame=0");
			return null;
		}
		for (Beacon b : nuevos) {
			int d=(int)b.getReal();
			sql = "INSERT INTO FRAME_BEACON (FRAME_ID,ID_BEA,DISTANCE) " + "VALUES ('" + idFrame + "','" + b.getKey() + "','"
					+d+"');";
			if (!insert(sql))
				System.out.println("Insert ha fallado en sentencia: " + sql);
		}
		return frame;
	}
	
	public boolean createTray(Trayectoria tray) {
		boolean res=true;
		for (Punto p : tray.getLista()) {
			String sql = "INSERT INTO PUNTO_TRAY (ID,X,Y,TIME,DURING) " + "VALUES ('"+tray.getId() + "','"+ p.getX() + "','" + p.getY() + "','"+ p.getTime() + "','" + p.getDuring()+ "');";
			res=res&&insert(sql);
		}
		return res;
	}
	
	public void killThread(){
		t.interrupt();
	}
	public void creaThread() {
		if (t == null || !t.isAlive()) {
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							//e.printStackTrace();
							return;
						}
						if (!cola.isEmpty()) {
							RxMensaje rx = cola.removeFirst();
							HashMap<Integer, Integer> mapa = rx.getMap();
							String sql = "INSERT INTO FRAME (ID,PLACE,TIME,ID_BEA,DURING) " + "VALUES ('" + rx.getId()
									+ "','" + rx.getPlace() + "','" + rx.getTime() + "','" + rx.getIdBea() + "','"
									+ rx.getDuracion() + "');";

							long idFrame = insertAndGet(sql, rx.getTime());
							if (idFrame == 0) {
								System.out.println("Algo ha fallado idFrame=0");
								return;
							}
							for (Integer key : mapa.keySet()) {
								sql = "INSERT INTO FRAME_BEACON (FRAME_ID,ID_BEA,DISTANCE) " + "VALUES ('" + idFrame
										+ "','" + key + "','" + mapa.get(key) + "');";
								if (!insert(sql))
									System.out.println("Insert ha fallado en sentencia: " + sql);
							}
						}
					}
				}
			});
			t.start();
		}
	}

	private boolean insert(String sql) {
		try {
			c = DriverManager.getConnection(URL);
			Statement stmt = c.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}

	private long insertAndGet(String sql, long time) {
		try {
			c = DriverManager.getConnection(URL);
			Statement stmt = c.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("El frameId es " + frameId);
			if (frameId == 0) {
				ResultSet rs = stmt.executeQuery("SELECT FRAME_ID FROM FRAME where TIME='" + time + "';");
				while (rs.next()) {
					frameId = rs.getLong(1);

				}
				rs.close();
			} else
				frameId++;
			stmt.close();

			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return frameId;
	}

	public boolean updateCode(int place, long code, long data) {
		Statement stmt = null;
		try {
			c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			String sql = "UPDATE CODE set DATA = '" + data + "' where PLACE='" + place + "' and CODE='" + code + "';";
			stmt.executeUpdate(sql);
			// c.commit();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean updateFrame(long time1, long time2, int id) {
		Statement stmt = null;
		try {
			c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			String sql = "UPDATE FRAME set ID = '" + id + "' where TIME>'" + time1 + "' and TIME<'" + time2 + "';";
			int num = stmt.executeUpdate(sql);
			System.out.println("Mani: " + num);
			c.setAutoCommit(false);
			c.commit();
			c.setAutoCommit(true);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("EXITO");
		return true;
	}

	public boolean deleteFrameGreaterId(int id) {
		return delete("DELETE FROM FRAME where FRAME_ID>'"+id + "';");
	}
	public boolean deleteFrameId(int id) {
		return delete("DELETE FROM FRAME where FRAME_ID='"+id + "';");
	}
	public boolean deleteFrame(int id) {
		return delete("DELETE FROM FRAME where ID='"+id + "';");
	}
	public boolean delete(String sql) {
		Statement stmt = null;
		try {
			c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			int num = stmt.executeUpdate(sql);
			System.out.println("Mani: " + num);
			c.setAutoCommit(false);
			c.commit();
			c.setAutoCommit(true);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("EXITO");
		return true;
	}
	
	
	////// SELECT

	public Set<Long> getFramesId(int place) {
		Set<Long> set = new HashSet<Long>();
		Statement stmt = null;
		try {
			c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT ID FROM FRAME where PLACE='" + place + "';");
			while (rs.next()) {
				set.add(rs.getLong("id"));
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
		return set;
	}
	public List<Frame> selectFrame(long user, int place,Mapa mapa) {
		LinkedList<Frame> frames = new LinkedList<Frame>();
		Statement stmt = null;
		Statement stmt2 = null;
		try {
			Connection c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			ResultSet rs;
			if (user != -1)
				rs = stmt.executeQuery("SELECT * FROM FRAME where PLACE='" + place + "' and ID='" + user + "';");
			else
				rs = stmt.executeQuery("SELECT * FROM FRAME where PLACE='" + place + "';");
			ResultSet rs2 = null;
			Map<Integer, Beacon> map = mapa.getMap();
			while (rs.next()) {
				long id = rs.getLong("id");
				long time = rs.getLong("time");
				Byte idBea = (byte) rs.getInt("id_bea");
				Byte during = rs.getByte("during");
				String frameId = rs.getString("frame_id");
				LinkedList<Beacon> puntos = new LinkedList<Beacon>();
				if (frameId != null) {
					stmt2 = c.createStatement();
					rs2 = stmt2.executeQuery("SELECT * FROM FRAME_BEACON where FRAME_ID='" + frameId + "';");
					// System.out.println("Frame: "+frameId);
					while (rs2.next()) {
						int beacon = Integer.parseInt(rs2.getString("id_bea"));
						int distance = Integer.parseInt(rs2.getString("distance"));
						// System.out.println("Id_bea:" + beacon + " dis:" +
						// distance);
						Object index = new Integer(beacon);
						Beacon b = map.get(index);
						puntos.add(new Beacon(b, distance, beacon));
					}
					rs2.close();
					stmt2.close();
				}
				if(!puntos.isEmpty())
					frames.add(new Frame(puntos, time, id, idBea, during));
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
		return frames;
	}
	public TrayectoriaSegmentada getTrayectoria(int id){
		Trayectoria tray=new Trayectoria(null,id);
		List<Punto> lista=new LinkedList<Punto>();
		Statement stmt = null;
		try {
			c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM PUNTO_TRAY where ID='" + id + "';");
			while (rs.next()) {
				Punto p=new Punto();
				p.setX(rs.getInt("x"));
				p.setY(rs.getInt("y"));
				p.setTime(rs.getInt("time"));
				p.setDuring(rs.getInt("during"));
				lista.add(p);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
		tray.setLista(lista);
		return new TrayectoriaSegmentada(tray);
		
	}
	public String isBeaconOn(long id, int place,int idBea){
		return select("SELECT count(*) FROM FRAME F JOIN FRAME_BEACON FB ON F.FRAME_ID=FB.FRAME_ID where ID='"+id+"' and PLACE='" + place + "' and FB.ID_BEA='" + idBea + "';");
	}
	public String getNumFrames(long id, int place){
		return select("SELECT count(*) FROM FRAME where ID='"+id+"' and PLACE='" + place +"';");
	}
	public String getLikesBeacon(int place, int idBea) {
		return select("SELECT count(*) FROM LIKE where PLACE='" + place + "' and ID_BEA='" + idBea + "';");
	}

	public String getUrl(int place) {
		return select("SELECT URL FROM PLACE where PLACE='" + place + "';");
	}

	public String getCode(long code, int place) {
		return select("SELECT DATA FROM CODE where PLACE='" + place + "' and CODE='" + code + "';");
	}
	public String getNumberFramesTotal(){
		return select("SELECT count(*) FROM FRAME_BEACON;");
	}
	public String select(String sql) {
		Statement stmt = null;
		String id = null;
		try {
			c = DriverManager.getConnection(URL);
			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery(sql);
			while (rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return id;
	}

}