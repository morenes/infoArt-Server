package com.obt.servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import model.Frame;
import model.Punto;
import model.Trayectoria;
import model.TrayectoriaSegmentada;
import model.cluster.Clustering;
import model.cluster.TipoRepresentante;
import model.clusterRes.Clusters;
import model.clusterRes.TipoCluster.Var;
import model.clusterRes.TipoMember;
import model.clusterRes.TipoMember.Cluster;
import model.mapa.Mapa;

public class UDPServer {
	static final int FACTOR = 100000;
	public static final String RUTA = "conf/";
	static final int PORT = 8015;
	private static long lastTime = 0;
	private DataBase db;
	private Log log;
	private Map<Integer, Map<Integer, Integer>> mapPlaces;
	private Map<Integer, MapAlg> mapas;
	private Map<Long,String> tokens;
	private Map<Long,Trayectoria> trays;
	private Map<Integer,String> frasesCluster;
	private Punto p;
	private Clusters cls;
	
	private long lastNot=0;
	
	public UDPServer(){
		db = DataBase.getSingleton();
		log = Log.getSingleton();
		mapPlaces = new HashMap<Integer, Map<Integer, Integer>>();
		mapas = leerConf(mapPlaces);
		tokens=new HashMap<Long, String>();
		trays=new HashMap<Long, Trayectoria>();
		frasesCluster = null;
		//
		cls=getClustersXML();
		createCodes();
	}
	public static void main(String[] args) throws Exception {
		UDPServer server = new UDPServer();
		server.runServer();
	}
	
	@SuppressWarnings("resource")
	public void runServer(){
		DatagramSocket serverSocket = null;
		
		try {
			serverSocket = new DatagramSocket(PORT);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		byte[] receiveData = new byte[1024];
		System.out.println("Encendido");
		
		while (true) {
			try {
				// System.out.println("Escuchando...");
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				RxMensaje rx = new RxMensaje();
				int tipo = rx.procesaMensaje(receivePacket.getData());
				System.out.println(rx);

				TxMensaje tx = new TxMensaje();
				tx.inicia();
				tx.setTipo(tipo);
				long respuesta = -1;

				switch (tipo) {
				case RxMensaje.CODECHECK: {
					String code = db.getCode(rx.getCode(), rx.getPlace());
					System.out.println("Code response: " + code);
					if (code != null)
						respuesta = Long.parseLong(code);
					if (respuesta == 0)
						db.updateCode(rx.getPlace(), rx.getCode(), rx.getId());
					tx.addLong(respuesta);
					break;
				}
				case RxMensaje.FRAME: {
					tx.addLong(rx.getTime());
					if (rx.getTime() == lastTime) {//
						tx.addInt(0);
						tx.addInt(0);
					} else {
						Object obj = new Integer(rx.getPlace());
						MapAlg alg = mapas.get(obj);
						Mapa mapInsta = alg.getMapa();
						Frame f = db.createFrame(rx, mapInsta.getMap());
						
						if (f.getBeacons().size()<MapAlg.MIN_BEACONS){
							p = null;
							tx.addLong(rx.getTime());
							tx.addInt(0);
							tx.addInt(0);
						}
						else{
							p = alg.getPunto(f,p,null);
							tx.addInt(p.getX());
							tx.addInt(p.getY());
							onTheFly(rx,alg);
						}
					}
					lastTime = rx.getTime();
					break;
				}
				case RxMensaje.BUZON: {
					String s=new String(rx.getMensajeOk());
					if (s.equals("49249071E"))
						return;
					else if (s.split(">")[0].equals("token")){
						tokens.put(rx.getId(),s.split(">")[1]);
					}else{
						db.createBuzon(rx);
						tx.addLong(0);
					}
					break;
				}
				case RxMensaje.URL: {
					String url = db.getUrl(rx.getPlace());
					if (url != null) {
						tx.addLong(0);
						tx.addStr(url);
					} else
						tx.addLong(-1);
					break;
				}
				case RxMensaje.CODEGEN: {
					long num = -1;
					String res = null;
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						num = (int) System.currentTimeMillis() % FACTOR;
						if (num < 0)
							num += FACTOR;

						res = db.getCode(num, rx.getPlace());
					} while (res != null);

					db.createCode(num, rx.getPlace(), 0);
					tx.addLong(num);
					log.insertLog("GEN", "Place " + rx.getPlace() + " code " + num);
					break;
				}
				case RxMensaje.LIKE: {
					db.treatLike(rx);
					Map<Integer, Integer> mapLikes = mapPlaces.get(rx.getPlace());
					if (mapLikes == null) { // por si acaso
						System.out.println("el mapa es null");
						mapLikes = new HashMap<Integer, Integer>();
						mapPlaces.put(rx.getPlace(), mapLikes);
					}
					Integer likes = mapLikes.get(rx.getIdBea());
					if (likes == null)
						likes = 0;
					if (rx.getNum() == 1)
						likes++;
					else
						likes--;
					mapLikes.put(rx.getIdBea(), likes);
					tx.addLong(0);
					break;
				}
				case RxMensaje.HIGH: {
					Map<Integer, Integer> mapLikes = mapPlaces.get(rx.getPlace());
					Integer value;
					int num = mapLikes.size();
					tx.addLong(num);
					for (Integer key : mapLikes.keySet()) {
						value = mapLikes.get(key);
						tx.addByte((byte) (int) key);
						tx.addByte((byte) (int) value);
						System.out.println("High: " + key + "-" + value);
					}
					break;
				}
				default: // consulta
					byte[] msg = rx.getMensajeOk();
					int index = 0;
					for (byte b : msg) {
						System.out.println("Byte " + (index++) + " = " + b);
					}
					break;
				}

				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();

				tx.finMensaje();
				// System.out.println("Ha tardado:
				// "+(System.currentTimeMillis()-lastStamp));

				DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), IPAddress, port);
				serverSocket.send(sendPacket);

			} catch (Exception e) {
				e.printStackTrace();
				log.insertLog("EXC", e.getMessage());
			}
		}
	}
	
	private Clusters getClustersXML(){
		//Clusters
		JAXBContext contexto;
		Clusters cls = null;
		try {
			contexto = JAXBContext.newInstance("model.clusterRes");
			Unmarshaller unmarshaller = contexto.createUnmarshaller();
			cls = (Clusters) unmarshaller.unmarshal(new File(UDPServer.RUTA+"clusters.xml"));
			
			contexto = JAXBContext.newInstance("model.cluster");
			unmarshaller = contexto.createUnmarshaller();
			Clustering xml = (Clustering) unmarshaller.unmarshal(new File(UDPServer.RUTA+"cluster_conf.xml"));
			frasesCluster=new HashMap<Integer, String>();
			if (xml != null) {
				TipoRepresentante rep=null;
				for(int i=0;i<xml.getRepresentante().size();i++){
					rep=xml.getRepresentante().get(i);
					int clusterRep = -1;
					for (TipoMember m : cls.getMember()) {
						if(m.getId().intValue()==rep.getId()){
							double mayor=-1;
							int mejor = -1;
							for (Cluster c : m.getCluster()) {
								if(c.getValue()>mayor){
									mayor=c.getValue();
									mejor=c.getId();
								}
							}
							clusterRep=mejor;
							break;
						}
					}
					frasesCluster.put(clusterRep,rep.getFrase());
				}
				for (Integer k : frasesCluster.keySet()) {
					System.out.println("El cluster: "+k+" va con: "+frasesCluster.get(k));
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			//no hay clusters
		}
		return cls;
	}
	
	private int getMejorCluster(Trayectoria t){
		if (cls==null) return -1;
		
		int rows=cls.getCluster().size();
		int vars=cls.getCluster().get(0).getVar().size();
		double [][] sal =new double[rows][vars];
		
		for(int i=0;i<rows;i++)
			for (Var v : cls.getCluster().get(i).getVar())
				sal[i][v.getId()]=v.getValue();

		
		List<Punto> l=t.getLista();
		long time=l.get(l.size()-1).getTime()-l.get(0).getTime();
		int bestRow = -1;
		double min=Double.MAX_VALUE;
		for(int i=0;i<rows;i++){
			double ultimo=sal[i][ClusterAlg.SEGMENTOS*3-1]*ClusterAlg.FACTOR_TIEMPO;
			double penultimo=sal[i][ClusterAlg.SEGMENTOS*3-2]*ClusterAlg.FACTOR_TIEMPO;
			double tiempo=ultimo+(ultimo-penultimo)/2;
			System.out.println("longitud tiempo: "+tiempo);
			
			double tiempoSeg=tiempo/ClusterAlg.SEGMENTOS;
			int seg=(int)(time/tiempoSeg);
			TrayectoriaSegmentada tSeg=new TrayectoriaSegmentada(t,seg);
			double [] in=new double[ClusterAlg.VAR_SEG*seg];
			double [] c=new double[ClusterAlg.VAR_SEG*seg];
			
			int index=0;
			for (Punto punto : tSeg.getLista()) {
				in[index++]=punto.getX();
				in[index++]=punto.getY();
			}
			if (ClusterAlg.VAR_SEG>2)
			for (Punto punto : tSeg.getLista())
				in[index++]=punto.getTime()/ClusterAlg.FACTOR_TIEMPO;
			
			if (ClusterAlg.VAR_SEG>3)
			for (Punto p : tSeg.getLista()) {
				if (p.getDuring()>0) in[index++] = ClusterAlg.VALOR_AUDIO;
				else in[index++] = 0;
			}
			index=0;
			for(int j=0;j<ClusterAlg.VAR_SEG/2*seg;j++){
					c[index++]=sal[i][j];
			}
			for(int j=2;j<ClusterAlg.VAR_SEG;j++){
				for(int k=0;k<seg;k++){
					c[index++]=sal[i][j*ClusterAlg.SEGMENTOS+k];
				}
			}
			String nuevo="";
			String cluster="";
			double diff=0;
			for(int j=0;j<in.length;j++){
				nuevo+=Math.round(in[j])+",";
				cluster+=Math.round(c[j])+",";
				diff+=Math.abs(in[j]-c[j]);
			}
			if (diff<min){
				min=diff;
				bestRow=i;
			}
			System.out.println("NUEVO: "+nuevo);
			System.out.println("CLUSTER: "+cluster);
		}
	
		return bestRow;
	}
	
	private void onTheFly(RxMensaje rx,MapAlg alg){
		///Recuperamos la trayectoria actual
		Trayectoria t=trays.get(rx.getId());
		if (t==null){
			t=new Trayectoria(new LinkedList<Punto>(),1);
			trays.put(rx.getId(), t);
		}
		
		//Añadimos el punto a la trayectoria actual
		LinkedList<Integer> orden = null;
		t.getLista().add(p);
		int sig=t.getId();
		Integer n1 = 0,n2;
		orden=alg.detect(t);
		if (orden.size()>sig) n1=orden.get(sig-1);
		System.out.println("inicio sig: "+sig);
		for(;sig<orden.size();sig++){
			n2=orden.get(sig);
			System.out.println("n1: "+n1+" n2: "+n2);
			if((n2-n1)>MapAlg.MIN_BEACONS){
				Notificacion.android("Te has saltado una sala","Pulsa para ir al mapa",tokens.get(rx.getId()));
				System.out.println("token "+tokens.get(rx.getId()));
				System.out.println("id: "+rx.getId());
			}
			else if ((n1-n2)>MapAlg.MIN_BEACONS) Notificacion.android("Has vuelto a lo ya visto. ¿Te has perdido?","Pulsa para ir al mapa",tokens.get(rx.getId()));
			n1=n2;
		}
		t.setId(sig);

		//ON-THE-FLY
		if (frasesCluster!=null&&orden!=null&&(getSize(orden)>=(alg.getMapa().getXml().getBeacon().size()/2))){
			int cluster=getMejorCluster(t);
			lastNot=System.currentTimeMillis();
			Notificacion.android(frasesCluster.get(new Integer(cluster)),"",tokens.get(rx.getId()));
		}
	}
	int getSize(List<Integer> orden){
		if ((System.currentTimeMillis()-lastNot)>60000){
			Set<Integer> set=new HashSet<Integer>();
			for (Integer integer : orden) {
				set.add(integer);
			}
			return set.size();
		} else return 0;
	}
	private Map<Integer, MapAlg> leerConf(Map<Integer, Map<Integer, Integer>> mapPlaces) {
		FileReader fr = null;
		BufferedReader br = null;
		String linea;
		String[] cad;
		Map<Integer, MapAlg> mapas = new HashMap<Integer, MapAlg>();
		DataBase db = DataBase.getSingleton();
		try {
			fr = new FileReader(new File(RUTA + "conf.txt"));
			br = new BufferedReader(fr);
			while ((linea = br.readLine()) != null) {
				cad = linea.split(">");
				Integer place = Integer.parseInt(cad[0]);
				mapPlaces.put(place, new HashMap<Integer,Integer>());
				db.createPlace(place, cad[1], cad[2]);
				if (place != 0) {
					MapAlg alg = new MapAlg(place);
					mapas.put(place, alg);
				}
				System.out.println("URL " + cad[2]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fr)
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return mapas;
	}

	private void createCodes() {
		DataBase db = DataBase.getSingleton();
		db.createCode(50, 0, 0);
		db.createCode(50, 1, 0);
		db.createCode(50, 2, 0);
		db.createCode(51, 3, 0);
		db.createCode(52, 2, 0);
		db.createCode(52, 3, 0);
	}
}
