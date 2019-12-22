package model.mapa;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import model.Beacon;

public class Mapa{
	private int width;
	private int height;
	private Map<Integer, Beacon> map;
	private long id;
	private int place;
	private MapaXML xml;
	public Mapa(String rutaConf) {
		super();
		leerBeaconsConf(rutaConf);
	}

	private void leerBeaconsConf(String ruta) {
		map=new HashMap<Integer, Beacon>();
		try{
			JAXBContext contexto=JAXBContext.newInstance("model.mapa");
			Unmarshaller unmarshaller= contexto.createUnmarshaller();
			xml=(MapaXML) unmarshaller.unmarshal(new File(ruta));
			if (xml!=null){
				height=xml.getHeight();
				width=xml.getWidth();
				id=xml.getId();
				place=xml.getPlace();
				for (TipoBeacon tBeacon : xml.getBeacon()) {
					List<Float> listaCoef=tBeacon.getCoef().getC();
					double[] coef=new double[listaCoef.size()];
					int cont=0;
					for (float c : listaCoef )
						coef[cont++]=c;
		
					map.put(new Integer(tBeacon.getId()),new Beacon(tBeacon.getX(),tBeacon.getY(),0,coef,tBeacon.getId()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Map<Integer, Beacon> getMap() {
		return map;
	}

	public void setMap(Map<Integer, Beacon> map) {
		this.map = map;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public MapaXML getXml() {
		return xml;
	}
	
	
}
