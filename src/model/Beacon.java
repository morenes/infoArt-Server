package model;

import java.util.Arrays;

public class Beacon {
	private int x,y;
	private double distFake;
	private double distReal;
	private double[] coef;
	private int key;
	
	public Beacon(Beacon p, double media,int key) {
		this(p.x,p.y,media,p.coef,key);
	}
	public Beacon(int x, int y, double distancia,double [] coefi,int key) {
		this(x,y);
		this.coef=coefi;
		this.key=key;
		this.distReal=distancia;
		if (distancia>75) distFake=-1;
		else if (distancia<15) distFake=distancia;
		else{
			distancia=distancia/10;
			distFake=0;
			for(int i=0;i<coef.length;i++){
				distFake+=Math.pow(distancia,i)*coef[i];
			}
			distFake=distFake*10;
		}
	}
	public Beacon(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getDist() {
		return distFake;
	}
	
	public double getReal() {
		return distReal;
	}
	public void setReal(double real) {
		this.distReal = real;
	}
	public void setDist(double dist) {
		this.distFake = dist;
	}
	public int distancia(Beacon p) {
		int a = (p.x - x);
		int b = (p.y - y);
		return (int) Math.sqrt(a * a + b * b);
	}
	public int distancia(Punto p) {
		int a = (p.getX() - x);
		int b = (p.getY() - y);
		return (int) Math.sqrt(a * a + b * b);
	}
	public double[] getCoef() {
		return coef;
	}
	public void setCoef(double[] coef) {
		this.coef = coef;
	}
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "Beacon [x=" + x + ", y=" + y + ", dist=" + distReal + ", coef=" + Arrays.toString(coef) + ", key=" + key
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Beacon other = (Beacon) obj;
		if (key != other.key)
			return false;
		return true;
	}

	
}
