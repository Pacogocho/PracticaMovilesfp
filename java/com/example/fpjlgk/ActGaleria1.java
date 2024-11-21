package com.example.fpjlgk;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActGaleria1 extends AppCompatActivity {

    private RecyclerView recyclerGallery;
    private TextView txtEmptyGallery;
    private List<String> imageList;
    private GalleryAdapter adapter;
    static final int REQUEST_DELETE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallagaleria);

        recyclerGallery = findViewById(R.id.recyclerGallery);
        txtEmptyGallery = findViewById(R.id.txtEmptyGallery);

        loadImages();
        updateGalleryView();


        View layout = findViewById(R.id.layoutgaleria);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DELETE_IMAGE && resultCode == RESULT_OK && data != null) {
            String deletedImagePath = data.getStringExtra("deleted_image_path");
            if (deletedImagePath != null) {
                onImageDeleted(deletedImagePath);
            }
        }
    }

    private void loadImages() {
        imageList = new ArrayList<>();

        // Directorio para Android 10 (API 29) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
            loadImagesFromDirectory(directory);
        }

        // Directorio para versiones anteriores a Android 10 (API 29)
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyAppImages");
        loadImagesFromDirectory(directory);
    }

    private void loadImagesFromDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            // Listar todos los archivos en el directorio
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Solo añadimos archivos que son imágenes
                    if (file.isFile() && isImageFile(file.getName())) {
                        // Añadir la ruta de la imagen a la lista
                        imageList.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private boolean isImageFile(String fileName) {
        String[] extensions = {".jpg", ".jpeg", ".png", ".bmp"};
        for (String ext : extensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private void updateGalleryView() {
        if (imageList.isEmpty()) {
            txtEmptyGallery.setVisibility(TextView.VISIBLE);
            recyclerGallery.setVisibility(RecyclerView.GONE);
        } else {
            txtEmptyGallery.setVisibility(TextView.GONE);
            recyclerGallery.setVisibility(RecyclerView.VISIBLE);

            if (adapter == null) {
                recyclerGallery.setLayoutManager(new GridLayoutManager(this, 3));
                recyclerGallery.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));
                adapter = new GalleryAdapter(imageList, this);
                recyclerGallery.setAdapter(adapter);
            } else {
                adapter.updateImages(imageList);
            }
        }
    }

    public void onImageDeleted(String imagePath) {
        imageList.remove(imagePath);
        loadImages(); // Recargar todas las imágenes para asegurar que la lista esté actualizada.
        updateGalleryView();
    }
}
