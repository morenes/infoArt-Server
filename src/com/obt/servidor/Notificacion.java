package com.obt.servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONException;
import org.json.JSONObject;

public class Notificacion {
	public static void main(String[] args){

	}
	public static void android(String title,String text,String token){
		//BasicConfigurator.configure();
		JSONObject auth=new JSONObject();
		JSONObject not=new JSONObject();
		try {
			not.put("title",title);
			not.put("text",text);
			//not.put("click_action", "OPEN_ACTIVITY_1");
			auth.put("data", not);
			auth.put("to",token);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://fcm.googleapis.com/fcm/send");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization", "key=AIzaSyCfDs5I7jQ52QqI_fXFKbCT3UBaYM9xahI ");
		try {
			httppost.setEntity(new StringEntity(auth.toString()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//db.insertLog("and1_exc",e.toString());
		}
		HttpResponse response = null;
		try {
			System.out.println("ENVIADO");
			response = httpclient.execute(httppost);
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response==null){
			System.out.println("la respuesta ha sido nula");
			return;
		}
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream instream = null;
			try {
				instream = entity.getContent();
			} catch (Exception e) {
				e.printStackTrace();
				//db.insertLog("and2_exc",e.toString());
			}
			finally {
				try {
					instream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(instream);
		}
	}
}
