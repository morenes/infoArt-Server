package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.obt.servidor.RxMensaje;
import com.obt.servidor.TxMensaje;

public class Test {

	public static void main(String[] args) {
		//testURL();
		testLIKE();
		testHIGH();
		//testBUZON();
		//testCODECHECK();
		//testCODEGEN();
		//testFRAME();
	}
	public static void testURL(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.URL);
			tx.addLong(16777210); //id
			tx.addInt(1); //place
			tx.addLong(16777210); //time
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			String mensaje=new String(receivePacket.getData());
			System.out.println(mensaje);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testLIKE(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.LIKE);
			tx.addLong(16777200); //id
			tx.addInt(2); //place
			tx.addLong(16777210); //time
			tx.addByte((byte) 3);
			tx.addByte((byte) 1);
			tx.addByte((byte) 4);
			tx.addByte((byte) 1);
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			String mensaje=new String(receivePacket.getData());
			System.out.println(mensaje);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testHIGH(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.HIGH);
			tx.addLong(16777210); //id
			tx.addInt(2); //place
			tx.addLong(16777210); //time
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			String mensaje=new String(receivePacket.getData());
			System.out.println(mensaje);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testBUZON(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.BUZON);
			tx.addLong(5434); //id
			tx.addInt(1); //place
			tx.addLong(16777210); //time
			tx.addStr("perico los palotes");
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			String mensaje=new String(receivePacket.getData());
			System.out.println(mensaje);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testCODECHECK(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.CODECHECK);
			tx.addLong(16777210); //id
			tx.addInt(1); //place
			tx.addLong(16777210); //time
			tx.addLong(66100);
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			String mensaje=new String(receivePacket.getData());
			System.out.println(mensaje);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testCODEGEN(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.CODEGEN);
			tx.addLong(16777210); //id
			tx.addInt(1); //place
			tx.addLong(16777210); //time
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			String mensaje=new String(receivePacket.getData());
			System.out.println(mensaje);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testFRAME(){
		try {
			DatagramSocket socket = new DatagramSocket();
			InetSocketAddress iPAddress = new InetSocketAddress("192.168.0.134", 8009);
			TxMensaje tx=new TxMensaje();
			tx.inicia();
			tx.setTipo(RxMensaje.FRAME);
			tx.addLong(16777213); //id
			tx.addInt(1); //place
			tx.addLong(16777213); //time
			tx.addByte((byte)2); //NUM
			tx.addByte((byte)3); //idBea
			tx.addByte((byte)5); //dist
			tx.addByte((byte)250); //idBea
			tx.addByte((byte)6); //dist
			tx.addByte((byte)3);
			tx.addByte((byte)200);
			tx.addByte((byte)1);
			tx.finMensaje();
			
			DatagramPacket sendPacket = new DatagramPacket(tx.getMensaje(), tx.longitudMensaje(), iPAddress);
			socket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			System.out.println("Escuchando...");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			
			byte [] buffer=receivePacket.getData();
			long id = (buffer[9]& 0xFF) + ((buffer[10]& 0xFF) * 256)+ ((buffer[11]& 0xFF) * 65536)+ ((buffer[12]& 0xFF) * 16777216);
			
			System.out.println("La respuesta id es: "+id);
			
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
