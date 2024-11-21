package com.example.fpjlgk;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ActAccesoInternet1 extends AppCompatActivity {

    TextView tvResult;
    Button botonBusqueda;
    TextView etCityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallaaccesoainternet);
        etCityName =findViewById(R.id.IngresaCiudad);
        botonBusqueda=findViewById(R.id.BotonBusqueda);
        tvResult=findViewById(R.id.textoRespuesta);

        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = etCityName.getText().toString().trim();
                if (cityName.isEmpty()) {
                    Toast.makeText(ActAccesoInternet1.this, "Por favor, introduce el nombre de la ciudad", Toast.LENGTH_SHORT).show();
                } else {
                    fetchCityData(cityName);
                }
            }
        });


        View layout = findViewById(R.id.layoutaccinternet);

        // Crea un ObjectAnimator para animar el color de fondo
        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(
                layout,                                // La vista a animar
                "backgroundColor",                     // La propiedad a cambiar
                new ArgbEvaluator(),                   // Interpolador para colores
                //Color.RED,                             // Color inicial
                Color.CYAN ,                            // Color intermedio
                //Color.GREEN,                           // Color final
                //Color.MAGENTA
                Color.parseColor("#e9f542")
        );

        // Configura la duración y el comportamiento de la animación
        colorAnimator.setDuration(20000);           // Duración de x segundos
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE); // Animación infinita
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);   // Reversa al terminar

        // Inicia la animación
        colorAnimator.start();
    }


    private void fetchCityData(String cityName) {
        String query = "SELECT ?city ?population ?latitude ?longitude WHERE {" +
                "?city wdt:P31 wd:Q515;" +
                "rdfs:label \"" + cityName + "\"@es;" +
                "wdt:P1082 ?population;" +
                "wdt:P625 ?location." +
                "BIND(geof:latitude(?location) AS ?latitude)." +
                "BIND(geof:longitude(?location) AS ?longitude)." +
                "}";

        String endpointUrl = "https://query.wikidata.org/sparql?query=" + Uri.encode(query);
        new SparqlTask().execute(endpointUrl);
    }


    class SparqlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseJson(result);
            } else {
                tvResult.setText("Error en la conexión o ciudad no encontrada.");
            }
        }
    }
    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray bindings = jsonObject.getJSONObject("results").getJSONArray("bindings");

            if (bindings.length() > 0) {
                JSONObject cityData = bindings.getJSONObject(0);

                String population = cityData.getJSONObject("population").getString("value");
                String latitude = cityData.getJSONObject("latitude").getString("value");
                String longitude = cityData.getJSONObject("longitude").getString("value");

                tvResult.setText("Ciudad: " + etCityName.getText().toString() +
                        "\nPoblación: " + population +
                        "\nLatitud: " + latitude +
                        "\nLongitud: " + longitude);
            } else {
                tvResult.setText("Ciudad no encontrada. Intente otra vez.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            tvResult.setText("Error al procesar la respuesta.");
        }
    }

}


