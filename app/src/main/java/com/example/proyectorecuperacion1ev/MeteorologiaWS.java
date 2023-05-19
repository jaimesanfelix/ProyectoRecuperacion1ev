package com.example.proyectorecuperacion1ev;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MeteorologiaWS {

    private static final String BASE_URL = "http://api.open-meteo.com/v1/forecast?";
    private float temperatura;
    private float velocidadViento;

    private static String getUrl(float latitud, float longitud){

        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("latitude=" + latitud);
        sb.append("&longitude=" + longitud);
        sb.append("&current_weather=true");

        return sb.toString();
    }

    public float getTemperatura(){
        return temperatura;
    }

    public float getVelocidadViento(){
        return velocidadViento;
    }

    public void consultarDatosMeteo(float latitud, float longitud){

        try {
            URL url = new URL(getUrl(latitud, longitud));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String linea;
            StringBuilder sb = new StringBuilder();

            while ((linea = br.readLine()) != null){
                sb.append(linea + "\n");
            }

            JSONObject json = new JSONObject(sb.toString());
            JSONObject clima = json.getJSONObject("current_weather");
            temperatura = (float) clima.getDouble("temperature");
            velocidadViento = (float) clima.getDouble("windspeed");

            br.close();
            isr.close();
            is.close();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
