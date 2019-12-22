package model;

public class Punto {
	private long time;
	private int x;
	private int y;
	//
	private int idEsc;
	private int during;
	
	public Punto(){
		super();
	}
	public Punto(int x, int y,long time) {
		this(x,y);
		this.time = time;
	}
	public Punto(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Punto(int x, int y, long time,int idEsc,int during) {
		this(x,y,time);
		this.idEsc=idEsc;
		this.during=during;
	}
	public int getIdEsc() {
		return idEsc;
	}
	public void setIdEsc(int idEsc) {
		this.idEsc = idEsc;
	}
	public int getDuring() {
		return during;
	}
	public void setDuring(int during) {
		this.during = during;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
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
	@Override
	public String toString() {
		return "Punto [time=" + time + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
