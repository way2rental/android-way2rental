package com.example.way2rental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.example.way2rental.R;
import java.util.List;

public class BannerSliderAdapter extends RecyclerView.Adapter<BannerSliderAdapter.BannerViewHolder> {

    private Context context;
    private List<String> imageUrlList; // Changed from List<Integer> to List<String>

    // Constructor updated to accept List<String>
    public BannerSliderAdapter(Context context, List<String> imageUrlList) {
        this.context = context;
        this.imageUrlList = imageUrlList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_slide, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imageUrl = imageUrlList.get(position);
        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.banner_placeholder_1) // Optional: placeholder while loading
                .error(R.drawable.banner_placeholder_3)       // Optional: image to show if loading fails
                .into(holder.ivBannerImage);
    }

    @Override
    public int getItemCount() {
        return imageUrlList != null ? imageUrlList.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
        }
    }
}
