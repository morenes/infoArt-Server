package model;

import java.util.List;

public class Frame {
	private List<Beacon> beacons;
	private long time;
	private long id;
	private int idBea;
	private int during;
	
	public Frame(List<Beacon> beacons, long time,long id, int idBea, int during) {
		super();
		this.id=id;
		this.beacons = beacons;
		this.time = time;
		this.idBea = idBea;
		this.during = during;
	}
	
	public List<Beacon> getBeacons() {
		return beacons;
	}
	public void setBeacons(List<Beacon> beacons) {
		this.beacons = beacons;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getIdBea() {
		return idBea;
	}
	public void setIdBea(int idBea) {
		this.idBea = idBea;
	}
	public int getDuring() {
		return during;
	}
	public void setDuring(int during) {
		this.during = during;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double compare(List<Beacon> lista){

		double error = 0;
		double [] array=new double[30];
		for(int i=0;i<array.length;i++)
			array[i]=0;
		for (Beacon b : beacons) {
			array[b.getKey()]=-b.getReal();
		}
		for (Beacon b :lista) {
			if(array[b.getKey()]==0) array[b.getKey()]=-b.getReal();
			else array[b.getKey()]=Math.abs(Math.abs(array[b.getKey()])-b.getReal());
		}
		double aux=0;
		for (double d : array) {
			if(d<0) aux=(250+d);
			else aux=d;
			error+=aux;//Math.sqrt(aux);
		}
		return error;
	}

	@Override
	public String toString() {
		return "Frame [beacons=" + beacons + ", time=" + time + ", id=" + id + ", idBea=" + idBea + ", during=" + during
				+ "]";
	}
	
}
