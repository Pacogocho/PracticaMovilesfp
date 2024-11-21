package com.example.fpjlgk;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ActCamara1 extends AppCompatActivity implements View.OnClickListener {

    Button btnTakeFoto, btnSave;
    ImageView imageView;
    Bitmap bitmap;

    private static final int REQUEST_PERMISSION_CAMARA = 100;
    private static final int TAKE_PICTURE = 101;

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallacamara); // Asegúrate de usar el layout correspondiente
        initUI();
        btnTakeFoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);



        View layout = findViewById(R.id.foto);

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

    private void initUI() {
        btnSave = findViewById(R.id.btnSave);
        btnTakeFoto = findViewById(R.id.btnTakeFoto);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnTakeFoto) {
            checkPermissionCamara();
        } else if (id == R.id.btnSave) {
            checkPermissionStorage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMARA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        } else if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermissionCamara() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMARA
                );
            }
        } else {
            takePicture();
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    private void checkPermissionStorage() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_WRITE_STORAGE
                    );
                }
            }
        } else {
            saveImage();
        }
    }

    private void saveImage() {
        if (bitmap == null) {
            Toast.makeText(this, "No image to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        OutputStream fos = null;
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();

            // Nombre del archivo con marca de tiempo
            String fileName = System.currentTimeMillis() + "_image_example.jpg";

            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUri = resolver.insert(collection, values);

            try {
                fos = resolver.openOutputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);
        } else {
            String imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
            String filename = System.currentTimeMillis() + ".jpg";

            file = new File(imageDir, filename);

            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (fos != null) {
            boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (saved) {
                Toast.makeText(this, "Picture was saved successfully", Toast.LENGTH_SHORT).show();
            }

            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file != null) { // Esto es para las API < 29
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
        }
    }

}
