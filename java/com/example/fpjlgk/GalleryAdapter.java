package com.example.fpjlgk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final String TAG = "GalleryAdapter";
    private final List<String> imageList;
    private final Context context;
    private final Activity activity;

    public GalleryAdapter(List<String> imageList, Activity activity) {
        this.imageList = imageList;
        this.context = activity;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imageList.get(position);

        try {
            if (imagePath.startsWith("android.resource://")) {
                int resId = context.getResources().getIdentifier(
                        imagePath.substring(imagePath.lastIndexOf("/") + 1),
                        "drawable",
                        context.getPackageName()
                );
                holder.imgThumbnail.setImageResource(resId);

                holder.imgThumbnail.setOnClickListener(v -> {
                    Intent intent = new Intent(context, FullscreenImageActivity.class);
                    intent.putExtra("resource_id", resId);
                    activity.startActivityForResult(intent, ActGaleria1.REQUEST_DELETE_IMAGE);
                });
            } else {
                Glide.with(context).load(imagePath).placeholder(R.drawable.image2).into(holder.imgThumbnail);

                holder.imgThumbnail.setOnClickListener(v -> {
                    Intent intent = new Intent(context, FullscreenImageActivity.class);
                    intent.putExtra("image_path", imagePath);
                    activity.startActivityForResult(intent, ActGaleria1.REQUEST_DELETE_IMAGE);
                });
            }

            holder.imgThumbnail.setContentDescription("Imagen capturada por la cámara");
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar la imagen en miniatura", e);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateImages(List<String> newImageList) {
        imageList.clear();
        imageList.addAll(newImageList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}
