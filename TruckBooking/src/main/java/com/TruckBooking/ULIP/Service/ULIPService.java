package com.TruckBooking.ULIP.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import com.TruckBooking.ULIP.Authentication.ULIPAuthentication;
import com.TruckBooking.ULIP.Entity.FastagEntity;
import com.TruckBooking.ULIP.Entity.SarathiEntity;
import com.TruckBooking.ULIP.Entity.VahanEntity;


@Service
public class ULIPService {
	
	 @Autowired
     public ULIPAuthentication authentication;
	 
	 @Value("${ULIP_VAHAN_URL}") 
	    String vahanUrl;

	 @Value ("${ULIP_FASTAG_URL}") 
	    String fastagUrl;

	 @Value ("${ULIP_SARATHI_URL}") 
	    String sarathiUrl;
	 

	public  String getvahanApi(VahanEntity vahanEntity) throws IOException
	{
			
			 String vehiclenumber=vahanEntity.getVehiclenumber();
	         JSONObject jsonData1 = new JSONObject();
	         HttpURLConnection webConnection=null;
		
			 URL weburl1=new URL(vahanUrl);
             webConnection = (HttpURLConnection) weburl1.openConnection();
             webConnection.setRequestMethod("POST");
             webConnection.setRequestProperty("accept", "application/json");
             webConnection.setRequestProperty("Content-Type", "application/json");
             webConnection.setRequestProperty("authorization", "Bearer "+authentication.getId());
             webConnection.setDoOutput(true);

             jsonData1.put("vehiclenumber", vehiclenumber);
             try (OutputStream outStream = webConnection.getOutputStream())
             {
                    byte[] reqBody = jsonData1.toString().getBytes(StandardCharsets.UTF_8);
                    outStream.write(reqBody, 0, reqBody.length);
             }

             try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) 
             {
                StringBuilder resp = new StringBuilder();
                String respLine = null;
                while ((respLine = br.readLine()) != null) {
                    resp.append(respLine.trim());
             }            
             JSONObject response= new JSONObject(resp.toString());
             return response.toString(4);
            }
    }       
	
	public  String getfastagApi(FastagEntity fastagEntity) throws IOException
	{
			
			 String vehiclenumber=fastagEntity.getVehiclenumber();
	         JSONObject jsonData2 = new JSONObject();
	         HttpURLConnection webConnection=null;
		
			 URL weburl2=new URL(fastagUrl);
             webConnection = (HttpURLConnection) weburl2.openConnection();
             webConnection.setRequestMethod("POST");
             webConnection.setRequestProperty("accept", "application/json");
             webConnection.setRequestProperty("Content-Type", "application/json");
             webConnection.setRequestProperty("authorization", "Bearer "+authentication.getId());
             webConnection.setDoOutput(true);

             jsonData2.put("vehiclenumber", vehiclenumber);
             try (OutputStream outStream = webConnection.getOutputStream())
             {
                    byte[] reqBody = jsonData2.toString().getBytes(StandardCharsets.UTF_8);
                    outStream.write(reqBody, 0, reqBody.length);
             }

             try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) 
             {
                StringBuilder resp = new StringBuilder();
                String respLine = null;
                while ((respLine = br.readLine()) != null) {
                    resp.append(respLine.trim());
             }            
             JSONObject response= new JSONObject(resp.toString());
             return response.toString(4);
            }
    }     
	
	public  String getsarathiApi(SarathiEntity sarathiEntity) throws IOException
	{
			
			 String dlnumber=sarathiEntity.getDlnumber();
			 String dob=sarathiEntity.getDob();
	         JSONObject jsonData3 = new JSONObject();
	         HttpURLConnection webConnection=null;
		
			 URL weburl3=new URL(sarathiUrl);
             webConnection = (HttpURLConnection) weburl3.openConnection();
             webConnection.setRequestMethod("POST");
             webConnection.setRequestProperty("accept", "application/json");
             webConnection.setRequestProperty("Content-Type", "application/json");
             webConnection.setRequestProperty("authorization", "Bearer "+authentication.getId());
             webConnection.setDoOutput(true);

             jsonData3.put("dlnumber", dlnumber);
             jsonData3.put("dob", dob);
             try (OutputStream outStream = webConnection.getOutputStream())
             {
                    byte[] reqBody = jsonData3.toString().getBytes(StandardCharsets.UTF_8);
                    outStream.write(reqBody, 0, reqBody.length);
             }

             try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) 
             {
                StringBuilder resp = new StringBuilder();
                String respLine = null;
                while ((respLine = br.readLine()) != null) {
                    resp.append(respLine.trim());
             }            
             JSONObject response= new JSONObject(resp.toString());
             return response.toString(4);
            }
    }     
              
           
	


}

