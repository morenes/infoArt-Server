package test;

import java.io.IOException;
import java.util.List;
import com.obt.servidor.DataBase;
import com.obt.servidor.MapAlg;

import model.Frame;

public class CloneTray {

	public static void main(String[] args) throws IOException {
		DataBase db = DataBase.getSingleton();
		int place = 3;
		MapAlg alg = new MapAlg(place);
		
//		List<Frame> lista = db.selectFrame(54, place, alg.getMapa());
//		for (Frame frame : lista) {
//			db.createFrameClone(frame,60, place);
//		}
		
		List<Frame> lista = db.selectFrame(54, place, alg.getMapa());
		for (int i = 600; i <= 615; i++) {
			for (Frame frame : lista) {
				db.createFrameClone(frame,i, place);
			}
		}
		
		lista = db.selectFrame(51, place, alg.getMapa());
		for (int i = 400; i <= 415; i++) {
			for (Frame frame : lista) {
				db.createFrameClone(frame,i, place);
			}
		}
		
		lista = db.selectFrame(49, place, alg.getMapa());
		for (int i = 300; i <= 315; i++) {
			for (Frame frame : lista) {
				db.createFrameClone(frame,i, place);
			}
		}
		
		db.killThread();
	}

}
