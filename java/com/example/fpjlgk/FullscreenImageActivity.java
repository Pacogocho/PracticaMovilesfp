package com.example.fpjlgk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;

public class FullscreenImageActivity extends AppCompatActivity {

    private static final String TAG = "FullscreenImageActivity";
    private String imagePath;
    private int resourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        PhotoView imageView = findViewById(R.id.fullscreenImage);
        Button btnDelete = findViewById(R.id.btnDelete);

        try {
            imagePath = getIntent().getStringExtra("image_path");
            resourceId = getIntent().getIntExtra("resource_id", 0);
            Log.d(TAG, "Image path: " + imagePath);
            Log.d(TAG, "Resource ID: " + resourceId);

            if (resourceId != 0) {
                imageView.setImageResource(resourceId);
            } else if (imagePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "Error al cargar el bitmap desde la ruta: " + imagePath);
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "La ruta de la imagen es nula");
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }

            btnDelete.setOnClickListener(v -> {
                try {
                    if (resourceId != 0) {
                        Toast.makeText(this, "Imagen predeterminada no puede ser eliminada", Toast.LENGTH_SHORT).show();
                    } else if (imagePath != null) {
                        File file = new File(imagePath);
                        if (file.exists() && file.delete()) {
                            Toast.makeText(this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("deleted_image_path", imagePath);
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Cerrar la actividad después de la eliminación
                        } else {
                            Log.e(TAG, "Error al eliminar el archivo o el archivo no existe");
                            Toast.makeText(this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error al eliminar la imagen", e);
                    Toast.makeText(this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            Toast.makeText(this, "Ocurrió un error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
