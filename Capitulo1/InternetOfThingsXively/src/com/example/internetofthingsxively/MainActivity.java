package com.example.internetofthingsxively;

import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView textViewVoltaje;
	private TextView textViewAmperios;
	private TextView textViewWatios;
	private Button buttonEnviar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewVoltaje = (TextView) findViewById(R.id.textViewVoltaje);
		textViewAmperios = (TextView) findViewById(R.id.textViewAmperios);
		textViewWatios = (TextView) findViewById(R.id.textViewWatios);
		
		buttonEnviar = (Button) findViewById(R.id.buttonEnviar);
		buttonEnviar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// Generamos un valor aleatorio para voltaje.
				Random randVoltaje = new Random();
				int voltajeSim = randVoltaje.nextInt(380);
				
				// Generamos un valor aleatorio para amperios.
				Random randAmperios = new Random();
				int amperiosSim = randAmperios.nextInt(40);
				
				// Generamos un valor aleatorio para watios.
				Random randWatios = new Random();
				int watiosSim = randWatios.nextInt(4000);
				
				// Actualizamos los TextView's
				textViewVoltaje.setText("Voltaje: " + voltajeSim + " V");
				textViewAmperios.setText("Amperios: " + amperiosSim + " A");
				textViewWatios.setText("Watios: " + watiosSim + " W");
				
				// Enviamos los datos a Xively con un AsyncTask
				// ya que puede llevar algo de tiempo.
				EnviarXively enviarXively = new EnviarXively();
				enviarXively.execute(voltajeSim, amperiosSim, watiosSim);
				
			}
		});
	}

}

class EnviarXively extends AsyncTask<Integer, Integer, Boolean>{

	// TODO: Aqui copia tu APIKEY de Xively
	public static final String XIVELY_APIKEY =  "uIVAJ9pTPXB2GfCfwfTxzOcVEj0lzCVlIJtpGLwA7nbGVrzU";
	// TODO: Aqui copia el numero de tu feed
	public static final String XIVELY_FEED =  "184224238";	
	
	@Override
	protected Boolean doInBackground(Integer... values) {
		
		Log.d("TEST", "Ejecutando AsyncTask");
		
		try {
			
			// Objeto JSON para enviar a Xively.
			JSONObject dataJSON = new JSONObject();	
			dataJSON.put("version", "1.0.0");
			
			// Objeto JSON que representa el channel de voltaje
			JSONObject voltajeValue = new JSONObject();
			voltajeValue.put("id","voltaje");
			voltajeValue.put("current_value", ""+ values[0]);
			
			// Objeto JSON que representa el channel de amperios
			JSONObject amperiosValue = new JSONObject();
			amperiosValue.put("id","amperios");
			amperiosValue.put("current_value", ""+ values[1]);
			
			// Objeto JSON que representa el channel de watios
			JSONObject watiosValue = new JSONObject();
			watiosValue.put("id","watio");
			watiosValue.put("current_value", ""+ values[2]);
			
			// Los juntamos todos dentro de otro objeto JSON llamado datastreams
			JSONArray datastreamsJSON = new JSONArray();			
			datastreamsJSON.put(voltajeValue);
			datastreamsJSON.put(amperiosValue);
			datastreamsJSON.put(watiosValue);
			// Agragamos todo lo que hemos creado al JSON que enviaremos a Xively.
			dataJSON.put("datastreams", datastreamsJSON);
			
			Log.d("TEST", dataJSON.toString());
			
			// Conectar a Xively y enviar.
			HttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut("https://api.xively.com/v2/feeds/" + XIVELY_FEED);
			httpPut.setHeader("X-ApiKey", XIVELY_APIKEY);
			httpPut.setHeader("content-type", "application/json");
			
			StringEntity entity = new StringEntity(dataJSON.toString());
			httpPut.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httpPut);
			String responseString = EntityUtils.toString(httpResponse.getEntity());
			
			Log.d("TEST", responseString);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return null;
	}
}


