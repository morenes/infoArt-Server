package model;

import java.util.LinkedList;

import com.obt.servidor.ClusterAlg;

import models.fcsModel.FuzzyClusterSet;

public class TrayectoriaSegmentada extends Trayectoria {
	private int totalTime;
	public TrayectoriaSegmentada(Trayectoria tray){
		this.lista=tray.lista;
		if (lista!=null&&lista.size()>0)
			this.totalTime=(int)(lista.get(lista.size()-1).getTime()-lista.get(0).getTime());
	}
	public TrayectoriaSegmentada(Trayectoria tray,int segmentos){
		super(tray.getLista(), tray.getId());
		segmentar(segmentos);
	}
	private void segmentar(int segmentos){
		LinkedList<Punto> listaAux=(LinkedList<Punto>) getLista();
		lista=new LinkedList<Punto>();
		double inicio=listaAux.getFirst().getTime();
		long fin=listaAux.getLast().getTime();
		totalTime=(int) (fin-inicio);
		double gap=(double)totalTime/segmentos;
		//System.out.println("gap:"+gap);
		//System.out.println("listaAux: "+listaAux.size());
		
		@SuppressWarnings("unchecked")
		LinkedList<Punto> celdas[]=new LinkedList[segmentos];
		for(int i=0;i<segmentos;i++) celdas[i]=new LinkedList<Punto>();
		
		int listIndex=0;
		for (Punto punto : listaAux) {
			if(punto.getTime()<Math.round(inicio+gap))
				celdas[listIndex].add(punto);
			else{
				if ((listIndex+1)<segmentos) listIndex++;
				celdas[listIndex].add(punto);
				inicio+=gap;
			}
		}
		LinkedList<Punto> aux;
		long epoch=celdas[0].getFirst().getTime();
		for(int i=0;i<segmentos;i++){
			aux=celdas[i];
			int rows=aux.size();
			double[][] in=new double[rows][2];
			Punto punto;
			double audios=0;
			
			for(int j=0;j<rows;j++){
				punto=aux.get(j);
				audios+=punto.getDuring();
				in[j][0]=punto.getX();
				in[j][1]=punto.getY();
			}
			int idEsc=aux.get(rows/2).getIdEsc();
			FuzzyClusterSet fcs=ClusterAlg.cluster(in, 1);
			double[][] sal = fcs.getCentroids();
			fin=aux.getLast().getTime();
			inicio=aux.getFirst().getTime();
			long time=(long)((inicio-epoch)+(fin-inicio)/2);
			punto=new Punto((int)Math.round(sal[0][0]),(int)Math.round(sal[0][1]),time,idEsc,(int)(audios/rows));
			lista.add(punto);
		}
		for (Punto punto : lista) {
			System.out.println(punto);
		}
	}
	public int getSegmentos() {
		return lista.size();
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
}
