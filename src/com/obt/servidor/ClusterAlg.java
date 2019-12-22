package com.obt.servidor;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import com.obt.servidor.DataBase;
import com.obt.servidor.UDPServer;

import algorithms.PartitionedSpace;
import algorithms.AHC.AHCParams;
import algorithms.BestCentroids.BestCentroids;
import algorithms.BestCentroids.BestCentroidsParams;
import model.Punto;
import model.Trayectoria;
import model.TrayectoriaSegmentada;
import model.cluster.Clustering;
import model.clusterRes.Clusters;
import model.clusterRes.TipoCluster;
import model.clusterRes.TipoCluster.Var;
import model.clusterRes.TipoMember;
import model.clusterRes.TipoMember.Cluster;
import models.fcsModel.Constants.DistanceType;
import models.fcsModel.FCSException;
import models.fcsModel.FuzzyClusterSet;

public class ClusterAlg {
	
	//Params por defecto
	public static int SEGMENTOS = 10;
	public static int VAR_SEG = 4;
	public static int NCLUSTERS = 4;
	public static int FACTOR_TIEMPO = 5;
	public static int VALOR_AUDIO = 30;
	
	int rutaPilas;
	double sensibilidad;
	
	private static ClusterAlg instance;
	public static ClusterAlg getInstance(){
		if (instance==null) instance=new ClusterAlg();
		return instance;
	}
	
	public static void main(String[] args) throws IOException, FCSException, JAXBException {
		ClusterAlg.getInstance().getClusters(3);
	}
	
	private ClusterAlg(){
		try {
			JAXBContext contexto = JAXBContext.newInstance("model.cluster");
			Unmarshaller unmarshaller = contexto.createUnmarshaller();
			Clustering xml = (Clustering) unmarshaller.unmarshal(new File(UDPServer.RUTA+"cluster_conf.xml"));
			if (xml != null) {
				SEGMENTOS = xml.getSegmentos();
				VAR_SEG = xml.getVarSeg();
				NCLUSTERS= xml.getClusters();
				FACTOR_TIEMPO=xml.getFactorTiempo();
				VALOR_AUDIO=xml.getFactorAudio();
				
				rutaPilas=xml.getPilas().getRuta();
				sensibilidad=xml.getPilas().getSensibilidad();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public Clusters getClusters(int place) throws IOException, FCSException, JAXBException{
		return getClusters(3,49,1000);
	}
	public Clusters getClusters(int place,int idMenor,int idMayor) throws IOException, FCSException, JAXBException{
		Set<Long> ids=DataBase.getSingleton().getFramesId(place);
		List<Integer> lista=new LinkedList<Integer>();
		for (Long id : ids) {
			if (id>=idMenor&&id<=idMayor) lista.add(id.intValue());
		}
		return getClusters(place, lista);
	}
	private Clusters getClusters(int place,List<Integer> ids) throws IOException, FCSException, JAXBException{
		MapAlg alg = new MapAlg(place);
		DataBase db=DataBase.getSingleton();
		Clusters clusters=new Clusters();
		clusters.setPlace(place);
		LinkedList<TrayectoriaSegmentada> listaTray=new LinkedList<TrayectoriaSegmentada>();
		
		for (int id : ids) {
			TrayectoriaSegmentada t=db.getTrayectoria(id);
			if (t.getLista().isEmpty()){
				Trayectoria tray = alg.getTrayectoria(id, place);
				t = new TrayectoriaSegmentada(tray,SEGMENTOS);
				db.createTray(t);
			}
			listaTray.add(t);
		}
		
		int nRow = ids.size();
		int nVar = SEGMENTOS * VAR_SEG;
		double[][] in = new double[nRow][nVar];
		int cRow = 0;
		for (TrayectoriaSegmentada seg : listaTray) {
			int cPunto = 0;
			for (Punto p : seg.getLista()) {
				in[cRow][cPunto++] = (double)p.getX();
				in[cRow][cPunto++] = (double)p.getY();
			}
			if (VAR_SEG>2)
			for (Punto p : seg.getLista()) {
				in[cRow][cPunto++] = (double)p.getTime()/FACTOR_TIEMPO;
			}
			if (VAR_SEG>3)
			for (Punto p : seg.getLista()) {
				if (p.getDuring()>0) in[cRow][cPunto++] = VALOR_AUDIO;
				else in[cRow][cPunto++] = 0;
			}
			cRow++;
		}
		
		FuzzyClusterSet fcs=ClusterAlg.cluster(in, NCLUSTERS);
		double[][] sal = fcs.getCentroids();
		double[] fuzzy=fcs.getMembership(in[ids.indexOf(rutaPilas)]);
		//////RUTA IDEAL
		System.out.println("Fuzzy Ruta ideal: ");
		int index=0;
		double max=0;
		int indexMax=0;
		for (double d : fuzzy) {
			System.out.println("Cluster "+(index)+": "+d);
			if (d>max){
				max=d;
				indexMax=index;
			}
			index++;
		}
		
		///////AJUSTE PILAS
		Map<Integer,Integer> ajuste=alg.ajustePilas();
		ids.remove((Object)new Integer(rutaPilas));
		double contAjuste=Integer.parseInt(db.getNumFrames(rutaPilas,place));
		System.out.println("Total ruta ideal: "+contAjuste);

		int i=1;
		TipoMember m=null;
		for (long id : ids) {
			////MEMBERSHIP
			double [] member=fcs.getMembership(in[i]);
			System.out.println("ID: "+id+" membership");
			m=new TipoMember();
			m.setId((int)id);
			Cluster cluster = null;
			for (int j=0;j<member.length;j++) {
				System.out.println(member[j]);
				cluster=new Cluster();
				cluster.setId(j);
				cluster.setValue(member[j]);
				m.getCluster().add(cluster);
			}
			clusters.getMember().add(m);
			
			//SENSIBILIDAD
			if (member[indexMax]>sensibilidad){
				double total=Integer.parseInt(db.getNumFrames(id,place));
				double prop=contAjuste/total;
				System.out.println("Totales id:"+id+" total:"+total+" prop: "+prop);
				
				for (Integer idBea : ajuste.keySet()) {
					int count=Integer.valueOf(db.isBeaconOn(id, place, idBea));
					System.out.println("Clus IdBea: "+idBea+" Num: "+count);
					if (count==0&&idBea!=0){
						clusters.getPilaGastada().add(idBea);
						System.out.println("BEACON "+idBea+" ROTO");
					}
					else if ((double)(count*prop)<(double)(ajuste.get(idBea))/2){
						clusters.getPilaDebil().add(idBea);
						System.out.println("BEACON "+idBea+" BATERIA BAJA");
					}
				}
			}
			i++;
		}

		//////SHOW CLUSTERS
		for (int j = 0; j <sal.length ; j++){
			List<Punto> lista=new LinkedList<Punto>();
			for (int k = 0; k < SEGMENTOS*2; k+=2) {
				lista.add(new Punto((int)(sal[j][k]),(int)(sal[j][k+1])));
			}
			alg.showOrden(new Trayectoria(lista,j),j);//j == numCluster
		}
		
		TipoCluster c = null;
		for (int j = 0; j < sal.length; j++){
			c=new TipoCluster();
			c.setId(j);
			Var var = null;
			for(int k=0;k<nVar;k++){
				var=new Var();
				var.setId(k);
				var.setValue(sal[j][k]);
				c.getVar().add(var);	
			}
			clusters.getCluster().add(c);
		}
		System.out.println("Termina");
		db.killThread();
		toXMLFile(clusters);
		return clusters;
	}

	public static void toXMLFile(Clusters clusters) throws JAXBException{
		JAXBContext contexto = JAXBContext.newInstance("model.clusterRes");
		Marshaller marshaller = contexto.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.setProperty("jaxb.schemaLocation","clusterRes.xsd");
		marshaller.marshal(clusters, new File(UDPServer.RUTA+"clusters.xml"));
	}
	public static FuzzyClusterSet cluster(double[][] inData, int cmax) {
		double umbral = 0.05;
		double alfa = 0.07;
		double epsilon = 0.0001;
		int maxIteration = 200;
		String outputDir =  UDPServer.RUTA + "sal.txt";
		BestCentroidsParams bcd = new BestCentroidsParams(outputDir, umbral, alfa, cmax, epsilon, maxIteration);
		AHCParams params = new AHCParams();
		String executionName = UDPServer.RUTA + "123";
		params.setExecutionName(executionName);
		params.setXMLPath("");
		bcd.setAhcParams(params);
		BestCentroids bc = new BestCentroids(bcd);

		int nVar = inData[0].length;
		int nRows = inData.length;
		double[] outData = new double[nRows];
		double[] fi = new double[nVar];

		FuzzyClusterSet fcs = bc.execute(inData, outData, PartitionedSpace.I, DistanceType.euclidean, fi,
				executionName);
		
		return fcs;
	}
}
