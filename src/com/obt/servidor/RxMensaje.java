package com.obt.servidor;

import java.util.HashMap;

public class RxMensaje {
	public static final int FRAME=1;
	public static final int LIKE=2;
	public static final int HIGH=3;
	public static final int URL=4;
	public static final int BUZON=5;
	public static final int CODECHECK=6;
	public static final int CODEGEN=7;
	
	private final int MSG_MAXRXLEN = 1500;
	
	private byte[] mensajeOk = new byte[MSG_MAXRXLEN];
	private long time,id,code;
	private int place;
	private int num,idBea,dist,idEsc,duracion,tipoAcceso;
	private HashMap<Integer,Integer> map;
	private int tipo;
	
	public RxMensaje(){
		map=new HashMap<Integer, Integer>();
	}
	private int buscaInicioMensaje(byte[] buffer)
	{
	  String str = new String(buffer);	
	  return str.indexOf("OBIAND");
	}

	private int buscaFinMensaje(byte[] buffer)
	{
	  String str = new String(buffer);	
	  return str.indexOf("OBFAND");
	}

	public int procesaMensaje(byte[] buffer) {
		int i,j,lngMsg,posicion;
		int lngmensajeOk;
		i = buscaInicioMensaje(buffer); if (i < 0) return 0;
	    j = buscaFinMensaje(buffer);    if (j < 0) return 0;
		
//	    System.out.println("i: "+i);
//	    System.out.println("j: "+j);
	    lngmensajeOk = j-i;
	    
	    if (lngmensajeOk > MSG_MAXRXLEN) return 0;
	    
	    posicion = i + 6;
		lngMsg = (buffer[posicion++]& 0xFF) + ((buffer[posicion++]& 0xFF) * 256);
//		System.out.println("Longitud que dice el mensaje:"+ lngMsg);
//		System.out.println("Longitud observada:"+ lngmensajeOk);
		if (lngmensajeOk <(lngMsg)) {return 0;}
		
		tipo = (buffer[posicion++] & 0xFF);
		id = (buffer[posicion++]& 0xFF) + ((buffer[posicion++]& 0xFF) * 256)+ ((buffer[posicion++]& 0xFF) * 65536)+ ((buffer[posicion++]& 0xFF) * 16777216);
		place = (buffer[posicion++]& 0xFF) + ((buffer[posicion++]& 0xFF) * 256);
		time = (buffer[posicion++]& 0xFF) + ((buffer[posicion++]& 0xFF) * 256)+ ((buffer[posicion++]& 0xFF) * 65536)+ ((buffer[posicion++]& 0xFF) * 16777216);
		//System.out.println("tipo: "+tipo);
		//System.out.println("id: "+id);
		
		switch(tipo){
			case FRAME: 
				num = (buffer[posicion++] & 0xFF);
				for(int k=0;k<num;k++){
					idBea = (buffer[posicion++] & 0xFF);
					dist= (buffer[posicion++] & 0xFF);
					map.put(idBea, dist);
				}
				
				idEsc = (buffer[posicion++] & 0xFF);
				duracion = (buffer[posicion++] & 0xFF);
				tipoAcceso = (buffer[posicion++] & 0xFF);
				break;
			case LIKE: 
				idBea = (buffer[posicion++] & 0xFF);
				num= (buffer[posicion++] & 0xFF);
				break;
			case BUZON: 
				num = (buffer[posicion++] & 0xFF);
				mensajeOk=new byte[num];
				for (int m=0; m < num; m++){
					mensajeOk[m] = buffer[posicion++];
				}
				break;
			case CODECHECK:
				code = (buffer[posicion++]& 0xFF) + ((buffer[posicion++]& 0xFF) * 256)+ ((buffer[posicion++]& 0xFF) * 65536)+ ((buffer[posicion++]& 0xFF) * 16777216);
			default:
				break;
		}
		return tipo;
	}
	public byte[] getMensajeOk() {
		return mensajeOk;
	}
	public long getTime() {
		return time;
	}
	public long getId() {
		return id;
	}
	public long getCode() {
		return code;
	}
	public int getPlace() {
		return place;
	}
	public int getNum() {
		return num;
	}
	public int getIdBea() {
		return idBea;
	}
	public int getDist() {
		return dist;
	}
	public int getIdEsc() {
		return idEsc;
	}
	public int getDuracion() {
		return duracion;
	}
	public int getTipoAcceso() {
		return tipoAcceso;
	}
	public HashMap<Integer, Integer> getMap() {
		return map;
	}
	public int getTipo() {
		return tipo;
	}
	@Override
	public String toString() {
		String res= "Rx [time=" + time + ", id=" + id + ", place=" + place + ", tipo=" + tipo + "]";
		switch (tipo){
		case FRAME: 
			res+="num="+num+" \n";
			for (Integer key : map.keySet()) {
				res+="idBea="+key+" dist="+map.get(key)+"\n";
			}
			res+="idEsc="+idEsc+" ";
			res+="dura="+duracion+" ";
			res+="tipoAc="+tipoAcceso+" ";
			break;
		case LIKE: 
			res+="idBea="+idBea+" value="+num;
			
			break;
		case BUZON: 
			res+="msg="+new String(mensajeOk);
			break;
		case CODECHECK:
			res+="code= "+code;
		default:
			break;
		}
		return res;
	}
	
}
