package com.example.fpjlgk;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActSensores1 extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private SensorEventListener gyroscopeListener;
    private TextView xValue, yValue, zValue;

    private float[] orientationAngles = {0f, 0f, 0f}; // Orientación acumulada en grados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallasensores);

        // Enlazar los TextView del diseño XML
        xValue = findViewById(R.id.Eje_X);
        yValue = findViewById(R.id.Eje_Y);
        zValue = findViewById(R.id.Eje_Z);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        if (gyroscope == null) {
            Toast.makeText(this, "El dispositivo no tiene giroscopio", Toast.LENGTH_LONG).show();
            finish();
        } else {
            gyroscopeListener = new SensorEventListener() {
                private long lastTimestamp = 0;

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (lastTimestamp != 0) {
                        // Calcular el tiempo transcurrido desde la última lectura en segundos
                        float deltaTime = (event.timestamp - lastTimestamp) * 1.0e-9f;

                        // Calcular el cambio angular en cada eje (rad/s * tiempo -> rad)
                        float deltaX = event.values[0] * deltaTime;
                        float deltaY = event.values[1] * deltaTime;
                        float deltaZ = event.values[2] * deltaTime;

                        // Actualizar la orientación acumulada en grados
                        orientationAngles[0] += Math.toDegrees(deltaX); // Eje X
                        orientationAngles[1] += Math.toDegrees(deltaY); // Eje Y
                        orientationAngles[2] += Math.toDegrees(deltaZ); // Eje Z
                    }

                    // Actualizar los TextView con los valores acumulados
                    xValue.setText("Eje X: "+String.format("%.2f°", orientationAngles[0]));
                    yValue.setText("Eje Y: "+String.format("%.2f°", orientationAngles[1]));
                    zValue.setText("Eje Z: "+String.format("%.2f°", orientationAngles[2]));

                    // Guardar el timestamp actual
                    lastTimestamp = event.timestamp;
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // No es necesario manejar este evento para el giroscopio
                }
            };
        }
        View layout = findViewById(R.id.layoutsensores);

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

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener para el giroscopio
        if (gyroscope != null) {
            sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener para ahorrar batería
        if (gyroscopeListener != null) {
            sensorManager.unregisterListener(gyroscopeListener);
        }
    }
}
