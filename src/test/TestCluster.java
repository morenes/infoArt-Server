package test;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import com.obt.servidor.DataBase;
import com.obt.servidor.MapAlg;
import com.obt.servidor.UDPServer;
import model.Trayectoria;
import model.clusterRes.Clusters;

public class TestCluster {

	public static void main(String[] args) throws Exception {
		MapAlg alg=new MapAlg(3);
		Trayectoria t=alg.getTrayectoria(27, 3);
		JAXBContext contexto;
		Clusters cls = null;
		contexto = JAXBContext.newInstance("model.clusterRes");
		Unmarshaller unmarshaller = contexto.createUnmarshaller();
		cls = (Clusters) unmarshaller.unmarshal(new File(UDPServer.RUTA+"clusters.xml"));
		//System.out.println("El mejor cluster es el: "+alg.getCluster(t,cls));
		DataBase.getSingleton().killThread();
	}

}
