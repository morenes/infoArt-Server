package com.obt.servidor;

public class TxMensaje {
	int i, longitud, tipo;
	byte[] mensaje = new byte[1024];	

	public void inicia() {
		longitud = 0;
		mensaje[longitud++] = (byte) 79;
		mensaje[longitud++] = (byte) 66;
		mensaje[longitud++] = (byte) 73;
		mensaje[longitud++] = (byte) 65;
		mensaje[longitud++] = (byte) 78;
		mensaje[longitud++] = (byte) 68;
		mensaje[longitud++] = 0;
		mensaje[longitud++] = 0;
		mensaje[longitud++] = 0;
	}

	public void addByte( byte num )
	{
		//System.out.println("byte: "+num);
		mensaje[longitud++] = num;
	}

	public void addInt( int num )
	{
		//System.out.println("short: "+num);
		mensaje[longitud++] = (byte) (num & 0xFF);
		mensaje[longitud++] = (byte) ((num >> 8) & 0xFF);
	}
	
	public void addLong( long num )
	{
		//System.out.println("long: "+num);
		mensaje[longitud++] = (byte) (num & 0xFF);
		mensaje[longitud++] = (byte) ((num >> 8) & 0xFF);
		mensaje[longitud++] = (byte) ((num >> 16) & 0xFF);
		mensaje[longitud++] = (byte) ((num >> 24) & 0xFF);
	}
	
	public void addStr(String str)
	{
		//System.out.println("str: "+str);
		byte[] bytes = str.getBytes();
		this.addByte((byte) str.length());		
		for (int i=0; i <= (int) (str.length() - 1); i++)
			addByte(bytes[i]);
	}
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public byte[] getMensaje() {
		return mensaje;
	}
	
	public int longitudMensaje(){
		return longitud + 1;
	}
	public void finMensaje() {
		mensaje[6] = (byte) ((longitud-9) & 0xFF);
		mensaje[7] = (byte) (((longitud-9) >> 8) & 0xFF);
		mensaje[8] = (byte) (tipo & 0xFF); 
		
		mensaje[longitud++] = (byte) 79;
		mensaje[longitud++] = (byte) 66;
		mensaje[longitud++] = (byte) 70;
		mensaje[longitud++] = (byte) 65;
		mensaje[longitud++] = (byte) 78;
		mensaje[longitud++] = (byte) 68;
	}
}
