
package com.example.fpjlgk;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;

// FullscreenImageActivity.java
public class FullscreenImageActivity extends AppCompatActivity {
    private String imagePath;
    private int resourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ImageView imageView = findViewById(R.id.fullscreen_image);
        imagePath = getIntent().getStringExtra("image_path");
        resourceId = getIntent().getIntExtra("resource_id", 0);

        if (resourceId != 0) {
            imageView.setImageResource(resourceId);
        } else if (imagePath != null) {
            Glide.with(this).load(imagePath).into(imageView);
        }

        openImage(imageView);

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteImage());
    }

    public void openImage(ImageView imageView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        imageView.startAnimation(scaleAnimation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ImageView imageView = findViewById(R.id.fullscreen_image);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        imageView.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                supportFinishAfterTransition();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void deleteImage() {
        ImageView imageView = findViewById(R.id.fullscreen_image);
        imageView.animate()
                .translationX(imageView.getWidth())
                .setDuration(300)
                .withEndAction(() -> {
                    if (imagePath != null) {
                        File file = new File(imagePath);
                        if (file.exists() && file.delete()) {
                            Toast.makeText(this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("deleted_image_path", imagePath);
                            setResult(RESULT_OK, resultIntent);
                        } else {
                            Toast.makeText(this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                })
                .start();
    }
}
