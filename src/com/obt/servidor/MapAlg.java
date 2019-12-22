package com.obt.servidor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;

import com.obt.servidor.DataBase;
import com.obt.servidor.UDPServer;
import model.Frame;
import model.Punto;
import model.Trayectoria;
import model.TrayectoriaSegmentada;
import model.clusterRes.Clusters;
import model.clusterRes.TipoCluster.Var;
import model.mapa.Mapa;
import model.mapa.MapaXML;
import model.mapa.TipoAjuste;
import model.Beacon;

public class MapAlg {
	//CONSTANTES
	private static final int MAX_PIXEL = 255;
	
	//VALORES POR DEFECTO
	private static int NEAR = 25;
	private static int MOVE = 40;
	private static int MOVE2 = 20;
	private static double FACTOR = 0.8;
	public static int MIN_BEACONS = 3;
	private static int TIPO = 1; //1 - Punto medio entre calcFrame (con referencia) e ideal// 2 - CalcFrame con referencia del ideal // 3 - Solo ruta ideal // 4 - Solo calcFrame
	
	//VARIABLES
	private Mapa mapa;
	private boolean[] muros;
	private List<Frame> ajuste;
	private HashMap<Integer,Integer> detecAjuste;
	int W, H;
	int place;

	public static void main(String[] args) throws IOException {
		int place=3;
		long id=51;
		DataBase db=DataBase.getSingleton();
		MapAlg alg = new MapAlg(place);
		System.out.println("Frames"+db.getNumberFramesTotal());
//		for (MOVE2 = 20; MOVE2 < 40; MOVE2 += 10) {// 2
//			for (MOVE = 35; MOVE < 55; MOVE += 5) {// 4
//				for (FACTOR = 0.70; FACTOR < 1; FACTOR += 0.1f) {// 3
//					for (TIPO = 1; TIPO < 5; TIPO++) {// 4
//						Trayectoria tray = alg.getTrayectoria(id, place);
//						System.out.println(tray);
//						alg.detect(tray);
//						alg.showOrden(tray, id);
//					}
//				}
//			}
		// }
		/// CALOR
		Set<Long> ids = db.getFramesId(place);
		List<Trayectoria> ts = new LinkedList<Trayectoria>();
		for (Long i : ids) {
			System.out.println("id: " + i);
			ts.add(alg.getTrayectoria(i, place));
		}
		alg.showTemp(ts, id);
		db.killThread();
	}


	public MapAlg(int place) throws IOException {
		super();
		mapa = new Mapa(UDPServer.RUTA+"mapa"+place+".xml");
		this.place=place;
		
		W = mapa.getWidth();
		H = mapa.getHeight();
		MapaXML xml=mapa.getXml();
		NEAR=xml.getNEAR();
		MOVE=xml.getMOVE();
		MOVE2=xml.getMOVE2();
		FACTOR=xml.getFACTOR();
		TIPO=xml.getCONF_ALG();
		MIN_BEACONS=xml.getMIN_BEA();
		muros = new boolean[W * H];
		setMuros(UDPServer.RUTA+"muros"+place+".bmp");
		if (mapa.getXml().getAjuste()!=null) ajusteGlobal();
	}

	public Mapa getMapa() {
		return mapa;
	}

	private void setMuros(String path) throws IOException {
		BufferedImage img = ImageIO.read(new File(path));
		int[] array = img.getRGB(0, 0, W, H, null, 0, W);

		for (int i = 0; i < array.length; i++) {
			array[i] = array[i] & 0x00FFFFFF;
			if (array[i] == 0)
				muros[i] = true;
			else
				muros[i] = false;
		}
	}
	private void ajusteGlobal() throws IOException{
		List<TipoAjuste> lista=mapa.getXml().getAjuste();
		ajuste=new LinkedList<Frame>();
		for (TipoAjuste a : lista) {
			int [] c=calibrate(UDPServer.RUTA+"ruta"+a.getId()+".bmp",a.getX(),a.getY());
			List<Frame> aux = calculaRuta(c,a.getId(),place);
			ajuste.addAll(aux);
		}
	}
	
	public HashMap<Integer,Integer> ajustePilas(){
		long id=mapa.getXml().getAjuste().get(0).getId();
		Trayectoria tray=getTrayectoria(id, place);
		List<Integer> orden=detect(tray);
		DataBase db=DataBase.getSingleton();
		detecAjuste=new HashMap<Integer,Integer>();
		for (Integer idBea : orden) {
			int count=Integer.valueOf(db.isBeaconOn(id, place, idBea));
			detecAjuste.put(idBea, count);
			System.out.println("Ajuste IdBea: "+idBea+" Num: "+count);
		}
		return detecAjuste;
	}
	public Trayectoria getTrayectoria(long id, int place){
		List<Frame> list = DataBase.getSingleton().selectFrame(id, place,mapa);
		List<Punto> puntos = new LinkedList<Punto>();

		if (list == null)
			return null;
		Punto p=null;
		Frame f=null;
		for (Frame frame : list){
			if (frame.getBeacons().size() >= MIN_BEACONS) {
				p=getPunto(frame,p,f);
				f = frame;
				puntos.add(p);
			}
		}
		return new Trayectoria(puntos, new Random().nextInt(10000));
	}

	public Punto getPunto(Frame frame, Punto p, Frame ant) {
		Frame f = null;
		double error = 0;
		double mejor;
		int dX = 0, dY = 0;
		mejor = Double.MAX_VALUE;
		Punto pMB = null, pMX = null;

		if (TIPO < 4) {
			for (Frame aj : ajuste) {
				double dis = MOVE;
				if (p != null) {
					dX = aj.getIdBea() - p.getX();
					dY = aj.getDuring() - p.getY();
					dis = Math.sqrt(dX * dX + dY * dY);
					if (dis > MOVE)
						error = 100000;
					else
						error = aj.compare(frame.getBeacons());
				} else
					error = aj.compare(frame.getBeacons());
				boolean mejora = error * FACTOR < mejor && (dis < (MOVE / 2));
				if (error < mejor || mejora) {
					mejor = error;
					f = aj;
				}
			}
			pMB = new Punto(f.getIdBea(), f.getDuring(), frame.getTime());
		}

		if (TIPO == 1 || TIPO == 4)
			pMX = calcFrame(frame, p, ant);
		if (TIPO == 2)
			pMX = calcFrame(frame, pMB, ant);

		if (TIPO == 1) {
			p = new Punto((pMB.getX() + pMX.getX()) / 2, (pMB.getY() + pMX.getY()) / 2, frame.getTime());
		} else if (TIPO == 2 || TIPO == 4) {
			p = new Punto(pMX.getX(), pMX.getY(), frame.getTime());
		} else if (TIPO == 3) {
			p = new Punto(pMB.getX(), pMB.getY(), frame.getTime());
		}
		p.setIdEsc(frame.getIdBea());
		p.setDuring(frame.getDuring());
		return p;
	}
	public LinkedList<Integer> detect(Trayectoria t) {
		List<Punto> lista = t.getLista();
		Collection<Beacon> beacons = mapa.getMap().values();
		int dis;
		LinkedList<Integer> orden = new LinkedList<Integer>();
		orden.add(0);
		for (Punto punto : lista) {
			int min = Integer.MAX_VALUE;
			int idMin = -1;
			for (Beacon b : beacons) {
				dis = b.distancia(punto);
				if (dis < min) {
					min = dis;
					idMin = b.getKey();
				}
			}
			if (min < NEAR) {
				if (orden.getLast() != idMin)
					orden.addLast(idMin);
			}
		}
		System.out.println(orden);
		return orden;
	}


	private int[] calibrate(String path,int x,int y) throws IOException{
		int sal[]=new int[10000];
		BufferedImage img = ImageIO.read(new File(path));
		int W=img.getWidth();
		int H=img.getHeight();
		int[] array = img.getRGB(0, 0, W, H, null, 0, W);
		//System.out.println("W: "+W+" H: "+H+" array: "+array.length);
		int i=1;
		
		sal[0]=x;
		sal[1]=y;

		int L,R,U,D;
		L=array[y*W+x-1] & 0x00FFFFFF;
		R=array[y*W+x+1] & 0x00FFFFFF;
		U=array[(y-1)*W+x] & 0x00FFFFFF;
		D=array[(y+1)*W+x] & 0x00FFFFFF;
		
		while(L==0 || R==0 || U==0 || D==0){
			if (L==0){
				x--;
				R=1;
				L=array[y*W+x-1] & 0x00FFFFFF;
				U=array[(y-1)*W+x] & 0x00FFFFFF;
				D=array[(y+1)*W+x] & 0x00FFFFFF;
			}
			else if (R==0){
				x++;
				L=1;
				R=array[y*W+x+1] & 0x00FFFFFF;
				U=array[(y-1)*W+x] & 0x00FFFFFF;
				D=array[(y+1)*W+x] & 0x00FFFFFF;
			}
			else if (U==0){
				y--;
				D=1;
				L=array[y*W+x-1] & 0x00FFFFFF;
				R=array[y*W+x+1] & 0x00FFFFFF;
				U=array[(y-1)*W+x] & 0x00FFFFFF;
			}
			else if (D==0){
				y++;
				U=1;
				L=array[y*W+x-1] & 0x00FFFFFF;
				R=array[y*W+x+1] & 0x00FFFFFF;
				D=array[(y+1)*W+x] & 0x00FFFFFF;
			}else System.out.println("PROBLEM");
			
			sal[i*2]=x;
			sal[i*2+1]=y;
			i++;
			
			//System.out.println("i: "+i+" x: "+x+" y: "+y);
		}
		return Arrays.copyOfRange(sal,0,i*2);
	}
	public List<Frame> calculaRuta(int [] c,int id, int place){
		List<Frame> list = DataBase.getSingleton().selectFrame(id,place,mapa);
		List<Frame> res=new LinkedList<Frame>();
		
		if (list == null)
			return null;

		int i=0;
		for (Frame frame : list){
			frame.setIdBea(c[i]);
			frame.setDuring(c[i+1]);
			i+=6;
			res.add(frame);
		}
		return list;
	}
	public String showOrden(Trayectoria tray,long id) throws IOException {
		String entrada=UDPServer.RUTA+"mapa"+place+".bmp";
		//String salida=UDPServer.RUTA+"res/id"+id+"p"+place+"tl"+tray.getId()+".bmp";
		String salida=UDPServer.RUTA+"res/mb"+MOVE+"mx"+MOVE2+"f"+(int)(FACTOR*100)+"t"+TIPO+"id"+id+".bmp";
		File fileEntrada=new File(entrada);
		if (fileEntrada==null) System.out.println("El fichero entrada es null");
		System.out.println("El fichero de entrada es:"+fileEntrada.getAbsolutePath());
		BufferedImage img = ImageIO.read(fileEntrada);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.BLACK);
		List<Punto> lista = new LinkedList<Punto>(tray.getLista());
		Punto primero = lista.remove(0);
		int x1 = primero.getX();
		int y1 = primero.getY();
		for (Punto punto : lista) {
			g.drawLine(x1, y1, punto.getX(), punto.getY());
			x1 = punto.getX();
			y1 = punto.getY();
		}
		System.out.println("El nombre de salida es:"+salida);
		File outputfile = new File(salida);
		System.out.println("El fichero de salida es:"+outputfile.getAbsolutePath());
		if (outputfile==null) System.out.println("Es el output");
		if (img==null) System.out.println("Es el img");
		ImageIO.write(img, "bmp", outputfile);
		return salida;
	}

	public String showTemp(List<Trayectoria> ts,long id) throws IOException {
		String entrada=UDPServer.RUTA+"mapa"+place+".bmp";
		String salida=UDPServer.RUTA+"/res/ID"+id+"P"+place+".bmp";
		BufferedImage img = ImageIO.read(new File(entrada));
		int H = img.getHeight();
		int W = img.getWidth();

		int[] array = img.getRGB(0, 0, W, H, null, 0, W);

		int[] pixeles = new int[H * W];
		int[][] plot = new int[W][H];
		for (int i = 0; i < W; i++)
			for (int j = 0; j < H; j++)
				plot[i][j] = 0;
		
		int tam=0;
		for (Trayectoria t : ts) {	
			for (Punto p : t.getLista()) {
				plot[p.getX()][p.getY()]++;
			}
			tam+=t.getLista().size();
		}
		
		//calculamos el valor maximo
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < W; i++)
			for (int j = 0; j < H; j++){
				if (plot[i][j]> max)
					max = plot[i][j];
			}
		
		//Hacemos el histograma de los valores
		int[] hist=new int[MAX_PIXEL+1];
		for(int i = 0;i<hist.length;i++) hist[i]=0;
		
		for (int i = 0; i < W; i++)
			for (int j = 0; j < H; j++){
				plot[i][j]=(int) Math.ceil((double) (plot[i][j] * 255.0) / (double) (max));
				hist[plot[i][j]]++;
			}
		//SE APLICA PERCENTILES 5%, ANTES DE NORMALIZAR
		int perc=(int)(tam*0.05);
		int vInit=0;
		int acum=0;
		while(acum<perc) acum+=hist[vInit++];
		vInit--;
		int vFin=hist.length-1;
		acum=0;
		while(acum<perc) acum+=hist[vFin--];
		vFin++;
		System.out.println("init "+vInit+" fin"+vFin);
		for (int i = 0; i < W; i++)
			for (int j = 0; j < H; j++){
				if (plot[i][j]<vInit) plot[i][j]=0;
				else if (plot[i][j]>vFin) plot[i][j]=MAX_PIXEL;
				else plot[i][j]=((plot[i][j]-vInit)*MAX_PIXEL)/(vFin-vInit);
			}
		//////////			
		int y, r, g, b, aux;
		for (int i = 0; i < W; i++)
			for (int j = 0; j < H; j++) {
				y = plot[i][j];
				aux = array[j * W + i];
				b = aux % 256;
				aux = aux >> 8;
				g = aux % 256;
				aux = aux >> 8;
				r = aux % 256;
				double lum = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
				pixeles[j * W + i] = byteToCalor(y, lum / 255);
			}
		img.setRGB(0, 0, W, H, pixeles, 0, W);
		File outputfile = new File(salida);
		ImageIO.write(img, "bmp", outputfile);
		return salida;
	}

	private static int byteToCalor(int y, double lum) {
		int r, g, b;
		if (y <= 51) {// ROJO-AMARILLO
			r = MAX_PIXEL;
			g = y * 5;
			b = 0;
		} else if (y <= 102) {// AMARILLO-VERDE
			r = MAX_PIXEL - (y - 51) * 5;
			g = MAX_PIXEL;
			b = 0;
		} else if (y <= 153) {// VERDE-CYAN
			r = 0;
			g = MAX_PIXEL;
			b = (y - 102) * 5;
		} else if (y <= 204) {// CYAN-AZUL
			r = 0;
			g = MAX_PIXEL - (y - 153) * 5;
			b = MAX_PIXEL;
		} else {// AZUL-MAGENTA
			r = (y - 204) * 5;
			g = 0;
			b = MAX_PIXEL;
		}
		int rgb = (int) (r * lum);
		rgb = (rgb << 8) + (int) (g * lum);
		rgb = (rgb << 8) + (int) (b * lum);
		return rgb;
	}

	private Punto calcFrame(Frame frame,Punto ant,Frame fAnt) {
		int j0,j1,i0,i1;
		if (ant!=null){
			int y=ant.getY();
			int x=ant.getX();
			if((y-MOVE2)<0) j0=0; else j0=y-MOVE2;
			if((y+MOVE2)>H) j1=H; else j1=y+MOVE2;
			if((x-MOVE2)<0) i0=0; else i0=x-MOVE2;
			if((x+MOVE2)>W) i1=W; else i1=x+MOVE2;
		}else{
			i0=0;i1=W; 
			j0=0;j1=H;
		}
		double min = Double.POSITIVE_INFINITY, coste;
		int bestX = 0, bestY = 0;
		double media;
		double pesoActual = 0.8;
		LinkedList<Beacon> puntos = new LinkedList<Beacon>();
		if (fAnt != null) {
			List<Beacon> anteriores = fAnt.getBeacons();
			for (Beacon bAnt : anteriores) {
				double bAntDist = bAnt.getDist();
				if (bAntDist != -1) {
					for (Beacon beacon : frame.getBeacons()) {
						if (beacon.equals(bAnt) && beacon.getDist() != -1) {
							media = bAntDist * (1 - pesoActual) + beacon.getDist() * pesoActual;
							beacon.setDist(media);
							puntos.add(beacon);
						}
					}
					if (!puntos.contains(bAnt))
						puntos.add(bAnt);
				}
			}
		}
		for (Beacon beacon : frame.getBeacons()) {
			if (!puntos.contains(beacon) && beacon.getDist() != -1)
				puntos.add(beacon);
		}

		for (int i = i0; i < i1; i++)
			for (int j = j0; j < j1; j++)
				if (muros[j * W + i]) {
					//System.out.println("i: "+i+" j: "+j);
					coste = coste(i, j, puntos, min);
					if (coste < min) {
						min = coste;
						bestX = i;
						bestY = j;
					}
				}
		Punto p=new Punto(bestX, bestY, frame.getTime(),frame.getIdBea(),frame.getDuring());
		return p;
	}

	
	private double coste(int x, int y, List<Beacon> puntos, double poda) {
		Beacon pivote = new Beacon(x, y);
		double accum = 0;
		double error, dist;
		
		for (Beacon p : puntos) {
			dist = p.getDist();
			error = pivote.distancia(p) - dist;
			accum += error * error;

			if (accum > poda)
				return accum;
		}
		return accum;
	}
	
	public InputStream getImagenCalor() throws IOException{
		Set<Long> ids = DataBase.getSingleton().getFramesId(place);
		List<Trayectoria> ts=new LinkedList<Trayectoria>();
		for (Long i : ids) {
			System.out.println("id: "+i);
			ts.add(getTrayectoria(i,place));
		}
		String image=showTemp(ts,System.currentTimeMillis()%1000);
		byte[] imageData=getImage(image);
		return new ByteArrayInputStream(imageData);
	}
	
	public InputStream getImagenTrayectoria(int id) throws IOException{
		Trayectoria tray = getTrayectoria(id, place);
		String image=showOrden(tray, id);
		byte[] imageData=getImage(image);
	    return new ByteArrayInputStream(imageData);
	}
	
	private byte[] getImage(String ruta){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    BufferedImage image;
		try {
			image = ImageIO.read(new File(ruta));
			
		    ImageIO.write(image, "bmp", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    byte[] imageData = baos.toByteArray();
	    return imageData;
	}
}
