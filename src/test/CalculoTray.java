package test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.obt.servidor.DataBase;
import com.obt.servidor.MapAlg;

import model.Trayectoria;
import model.TrayectoriaSegmentada;

public class CalculoTray {
		
	public static List<TrayectoriaSegmentada> ts;
	
	public static void main(String[] args) throws IOException {
		ts=calculo();
		System.out.println("DONE");
	}
	
	public static List<TrayectoriaSegmentada> calculo()throws IOException {
		ts=new LinkedList<TrayectoriaSegmentada>();
		int place = 3;
		MapAlg alg = new com.obt.servidor.MapAlg(place);
		DataBase db=DataBase.getSingleton();
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(50);ids.add(49); ids.add(51); ids.add(53); ids.add(54);
		for(int i=0;i<=15;i++)ids.add(300+i);
		for(int i=0;i<=15;i++)ids.add(400+i);
		for(int i=0;i<=15;i++)ids.add(500+i);
		for(int i=0;i<=15;i++)ids.add(600+i);

		int segmentos = 10;
		for (int id : ids) {
			System.out.println("ID: " + id);
			Trayectoria tray = alg.getTrayectoria(id, place);
			System.out.println(tray.toString());
			TrayectoriaSegmentada seg = new TrayectoriaSegmentada(tray, segmentos);
			System.out.println(seg.toString());
			ts.add(seg);
			//seg.setId(id);
			db.createTray(seg);
		}
		return ts;
	}
}
