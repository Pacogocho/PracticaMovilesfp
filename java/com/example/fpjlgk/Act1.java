package com.example.fpjlgk;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Act1 extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LAUNCH_COUNT = "launch_count";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallaprincipal1);
        TextView textoInicial = findViewById(R.id.textoInicioPPal1);
        Button botonAccesoInternet = findViewById(R.id.ConexionAInternet);
        Button botonCamara = findViewById(R.id.Camara);
        Button botonGaleria = findViewById(R.id.Galeria);
        Button botonSensores = findViewById(R.id.Sensores);

        // ------Numero de accesos------------------------------------------------------------------
        // Obtenemos las preferencias compartidas
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Verificamos si es la primera vez que se abre la aplicación
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            // Si es la primera vez, no incrementamos el contador, solo lo marcamos como no primera vez
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);  // Ya no es la primera vez
            editor.apply();
        } else {
            // No es la primera vez, así que incrementamos el contador
            int launchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0);
            launchCount++;  // Aumentamos el contador

            // Guardamos el nuevo valor del contador
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_LAUNCH_COUNT, launchCount);
            editor.apply();

            // Mostramos el número de inicios que ha tenido la aplicación
            String texto="Número de accesos a la app: "+launchCount;
            textoInicial.setText(texto);
        }

        // ------Boton Acceso a Internet------------------------------------------------------------
        botonAccesoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Act1.this, ActAccesoInternet1.class);
                startActivity(intent); // Inicia la nueva actividad
            }
        });
        // ------Boton Camara------------------------------------------------------------
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Act1.this, ActCamara1.class);
                startActivity(intent); // Inicia la nueva actividad
            }
        });// ------Boton Galeria------------------------------------------------------------
        botonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Act1.this, ActGaleria1.class);
                startActivity(intent); // Inicia la nueva actividad
            }
        });// ------Boton Sensores------------------------------------------------------------
        botonSensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Act1.this, ActSensores1.class);
                startActivity(intent); // Inicia la nueva actividad
            }
        });

        View layout = findViewById(R.id.layout_principal);

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
}

