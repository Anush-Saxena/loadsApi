package com.TruckBooking.ULIP.Authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ULIPAuthentication {

    private String id;

    @Value ("${ULIP_USERNAME}") 
    String username;

    @Value ("${ULIP_PASSWORD}") 
    String password;

    @Value ("${ULIP_LOGIN_URL}") 
    String loginUrl;
   
   

    // 30 minutes
    @Scheduled(fixedRate = 1800000)
    public  void generateToken() throws URISyntaxException, IOException{

        URL weburl=new URL(loginUrl);
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("POST");
        webConnection.setRequestProperty("accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        webConnection.setDoOutput(true);

         JSONObject jsonData = new JSONObject();
            jsonData.put("username", this.username);
            jsonData.put("password", this.password);
            try (OutputStream outStream = webConnection.getOutputStream()) {
                byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                outStream.write(reqBody, 0, reqBody.length);
            }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder resp = new StringBuilder();
            String respLine = null;
            while ((respLine = br.readLine()) != null) {
                resp.append(respLine.trim()); 
            }    
            JSONObject respJson = new JSONObject(resp.toString());  
            
            this.id=respJson.getJSONObject("response").getString("id");
        }
    }
    public String getId(){
        return id;
    }
} 
