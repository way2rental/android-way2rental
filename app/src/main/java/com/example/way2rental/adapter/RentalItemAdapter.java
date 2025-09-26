package com.example.way2rental.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.way2rental.R;
// Import the new Product model and its components
import com.example.way2rental.model.Product;
import com.example.way2rental.model.Meta;
import com.example.way2rental.model.Attributes;
import com.example.way2rental.ui.ProductDetailActivity;

import java.util.List;
import java.util.Locale; // For formatting strings like "3 Beds"

public class RentalItemAdapter extends RecyclerView.Adapter<RentalItemAdapter.RentalItemViewHolder> {

    private List<Product> productList; // Changed from RentalItem to Product

    public RentalItemAdapter(List<Product> productList) { // Changed from RentalItem to Product
        this.productList = productList;
    }

    @NonNull
    @Override
    public RentalItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental, parent, false);
        return new RentalItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalItemViewHolder holder, int position) {
        Product product = productList.get(position); // Changed from RentalItem to Product
        if (product == null) {
            // Should not happen if list is managed properly, but good for safety
            Log.e("RentalItemAdapter", "Product at position " + position + " is null.");
            return;
        }

        holder.tvRentalItemTitle.setText(product.getName()); // Use getName() from Product
        holder.tvRentalItemPrice.setText(product.getDisplayPrice()); // Use helper method
        holder.tvRentalItemLocation.setText(product.getDisplayAddress()); // Use helper method

        Context context = holder.itemView.getContext();
        String imageUrl = product.getPrimaryImageUrl(); // Use helper method

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder) // Ensure this placeholder exists
                .error(R.drawable.ic_image_broken) // Ensure this error drawable exists
                .into(holder.ivRentalItemImage);
        } else {
            Glide.with(context)
                .load(R.drawable.ic_image_placeholder) // Fallback placeholder
                .into(holder.ivRentalItemImage);
            Log.w("RentalItemAdapter", "Image URL is null or empty for product: " + product.getName());
        }

        // Set attributes like beds and baths
        Meta meta = product.getMeta();
        if (meta != null && meta.getAttributes() != null) {
            Attributes attributes = meta.getAttributes();
            holder.tvRentalItemBeds.setText(String.format(Locale.getDefault(), "%d Beds", attributes.getBedrooms()));
            holder.tvRentalItemBaths.setText(String.format(Locale.getDefault(), "%d Baths", attributes.getBathrooms()));
            holder.tvRentalItemBeds.setVisibility(View.VISIBLE);
            holder.tvRentalItemBaths.setVisibility(View.VISIBLE);
        } else {
            // Hide or set default text if attributes are not available
            holder.tvRentalItemBeds.setVisibility(View.GONE);
            holder.tvRentalItemBaths.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // Pass the entire Product object (ensure Product and its nested classes are Serializable)
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ITEM, product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class RentalItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRentalItemImage;
        TextView tvRentalItemTitle;
        TextView tvRentalItemPrice;
        TextView tvRentalItemLocation;
        TextView tvRentalItemBeds; // Added for Bed attribute
        TextView tvRentalItemBaths; // Added for Bath attribute

        public RentalItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRentalItemImage = itemView.findViewById(R.id.ivRentalItemImage);
            tvRentalItemTitle = itemView.findViewById(R.id.tvRentalItemTitle);
            tvRentalItemPrice = itemView.findViewById(R.id.tvRentalItemPrice);
            tvRentalItemLocation = itemView.findViewById(R.id.tvRentalItemLocation);
            tvRentalItemBeds = itemView.findViewById(R.id.tvRentalItemBeds); // Initialize
            tvRentalItemBaths = itemView.findViewById(R.id.tvRentalItemBaths); // Initialize
        }
    }
}
