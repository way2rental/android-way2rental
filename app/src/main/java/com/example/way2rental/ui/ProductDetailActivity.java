package com.example.way2rental.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.way2rental.R;
import com.example.way2rental.adapter.RentalItemAdapter;
import com.example.way2rental.model.Product;
import com.example.way2rental.model.Pricing;
import com.example.way2rental.model.ProductLocation;
import com.example.way2rental.model.Meta;
import com.example.way2rental.model.Attributes;
import com.example.way2rental.model.Availability;
import com.example.way2rental.model.Owner;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip; // Added for Chip
import com.google.android.material.chip.ChipGroup; // Added for ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ITEM = "com.example.way2rental.ui.PRODUCT_ITEM";
    private static final String TAG = "ProductDetailActivity";

    private Product currentProduct;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView ivProductDetailImage;

    // TextViews for product details
    private TextView tvProductName, tvProductAddress, tvProductStatus,
            tvProductPrimaryPrice, // New for header price
            tvProductBeds, tvProductBaths, tvProductFloor, tvProductArea, // Added tvProductArea
            tvProductDescription,
            tvProductBasePrice, tvProductSecurityDeposit, tvProductMaintenanceFee,
            tvProductCleaningFee, tvProductDiscount, tvProductNegotiable,
            tvProductRules, tvProductNearby,
            tvProductAvailability, tvProductOwnerStatus;

    // ChipGroups
    private ChipGroup chipGroupFacilities;
    private ChipGroup chipGroupTags;

    private FloatingActionButton fabContactOwner;

    private RecyclerView rvSimilarProducts;
    private RentalItemAdapter similarProductsAdapter;
    private List<Product> similarProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initializeViews();

        if (getIntent().hasExtra(EXTRA_PRODUCT_ITEM)) {
            currentProduct = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT_ITEM);
        }

        if (currentProduct != null) {
            populateProductDetails();
            String categoryForSimilar = currentProduct.getType() != null ? currentProduct.getType() : "Apartments";
            loadSimilarProducts(currentProduct.getId(), categoryForSimilar);
        } else {
            Log.e(TAG, "Product item not passed to ProductDetailActivity or is null.");
            collapsingToolbarLayout.setTitle("Product Not Found");
            if (tvProductName != null) tvProductName.setText("Product Not Found");
            hideAllDetailSections(); // Consider more robust hiding
        }

        setupSimilarProductsRecyclerView();
    }

    private void initializeViews() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_detail);
        ivProductDetailImage = findViewById(R.id.iv_product_detail_image);

        // Header Info
        tvProductName = findViewById(R.id.tv_product_detail_name);
        tvProductAddress = findViewById(R.id.tv_product_detail_address);
        tvProductPrimaryPrice = findViewById(R.id.tv_product_detail_primary_price); // New
        tvProductStatus = findViewById(R.id.tv_product_detail_status);

        // Key Features Card
        tvProductBeds = findViewById(R.id.tv_product_detail_beds);
        tvProductBaths = findViewById(R.id.tv_product_detail_baths);
        tvProductFloor = findViewById(R.id.tv_product_detail_floor);
        tvProductArea = findViewById(R.id.tv_product_detail_area); // New

        // Description
        tvProductDescription = findViewById(R.id.tv_product_detail_description);

        // Pricing Table
        tvProductBasePrice = findViewById(R.id.tv_product_detail_base_price);
        tvProductSecurityDeposit = findViewById(R.id.tv_product_detail_security_deposit);
        tvProductMaintenanceFee = findViewById(R.id.tv_product_detail_maintenance_fee);
        tvProductCleaningFee = findViewById(R.id.tv_product_detail_cleaning_fee);
        tvProductDiscount = findViewById(R.id.tv_product_detail_discount);
        tvProductNegotiable = findViewById(R.id.tv_product_detail_negotiable);

        // Chip Groups
        chipGroupFacilities = findViewById(R.id.chip_group_facilities); // Changed from tvProductFacilities
        chipGroupTags = findViewById(R.id.chip_group_tags); // New

        // Other Text sections
        tvProductRules = findViewById(R.id.tv_product_detail_rules);
        tvProductNearby = findViewById(R.id.tv_product_detail_nearby);
        tvProductAvailability = findViewById(R.id.tv_product_detail_availability);
        tvProductOwnerStatus = findViewById(R.id.tv_product_detail_owner_status);

        fabContactOwner = findViewById(R.id.fab_contact_owner);
    }

    private void hideAllDetailSections() {
        // This method needs to be updated to hide all new sections and views
        // For now, focusing on populating, which implicitly handles visibility.
        Log.w(TAG, "Product is null, all detail sections should be hidden or show 'Not Available'.");
        // Example:
        // if (tvProductPrimaryPrice != null) tvProductPrimaryPrice.setVisibility(View.GONE);
        // if (tvProductArea != null) tvProductArea.setVisibility(View.GONE);
        // if (chipGroupFacilities != null) chipGroupFacilities.setVisibility(View.GONE);
        // ... and so on for all relevant views and their parent containers.
    }

    private void populateProductDetails() {
        if (currentProduct == null) return;

        collapsingToolbarLayout.setTitle(currentProduct.getName());
        tvProductName.setText(currentProduct.getName());

        ProductLocation pLocation = currentProduct.getProductLocation();
        if (pLocation != null) {
            tvProductAddress.setText(String.format("%s, %s", pLocation.getAddressLine1(), pLocation.getCity()));
        } else {
            tvProductAddress.setText("Location not available");
        }
        tvProductStatus.setText(String.format("Status: %s", currentProduct.getStatus() != null ? currentProduct.getStatus() : "N/A"));

        String primaryImageUrl = currentProduct.getPrimaryImageUrl();
        if (primaryImageUrl != null && !primaryImageUrl.isEmpty()) {
            Glide.with(this).load(primaryImageUrl).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_broken).into(ivProductDetailImage);
        } else {
            ivProductDetailImage.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Meta and Attributes
        Meta meta = currentProduct.getMeta();
        Attributes attributes = (meta != null) ? meta.getAttributes() : null;

        if (attributes != null) {
            tvProductBeds.setText(String.format(Locale.getDefault(), "%d Beds", attributes.getBedrooms()));
            tvProductBaths.setText(String.format(Locale.getDefault(), "%d Baths", attributes.getBathrooms()));
            tvProductFloor.setText(String.format(Locale.getDefault(), "%d Floor", attributes.getFloor())); // Assuming getFloor() returns int
            tvProductArea.setText(String.format(Locale.getDefault(), "%d SqFt", attributes.getAreaSqFt())); // Assuming getAreaSqFt() returns int
            setVisibility(View.VISIBLE, tvProductBeds, tvProductBaths, tvProductFloor, tvProductArea);
        } else {
            setVisibility(View.GONE, tvProductBeds, tvProductBaths, tvProductFloor, tvProductArea);
        }

        tvProductDescription.setText(currentProduct.getDescription() != null ? currentProduct.getDescription() : "No description available.");

        Pricing pricing = currentProduct.getPricing();
        if (pricing != null) {
            String priceUnitText = pricing.getPriceUnit() != null ? pricing.getPriceUnit().replace("_", " ").toLowerCase() : "/month";
            tvProductPrimaryPrice.setText(String.format(Locale.getDefault(), "₹%.0f %s", pricing.getBasePrice(), priceUnitText));
            tvProductBasePrice.setText(String.format(Locale.getDefault(), "₹%.0f %s", pricing.getBasePrice(), priceUnitText));
            tvProductSecurityDeposit.setText(String.format(Locale.getDefault(), "₹%.0f", pricing.getSecurityDeposit()));
            tvProductMaintenanceFee.setText(String.format(Locale.getDefault(), "₹%.0f %s", pricing.getMaintenanceFee(), priceUnitText));
            tvProductCleaningFee.setText(String.format(Locale.getDefault(), "₹%.0f", pricing.getCleaningFee()));
            tvProductDiscount.setText(pricing.getDiscountPercent() > 0 ? String.format(Locale.getDefault(), "%.0f%% off", pricing.getDiscountPercent()) : "No discount");
            tvProductNegotiable.setText(pricing.isNegotiable() ? "Yes" : "No");
            setVisibility(View.VISIBLE, tvProductPrimaryPrice, tvProductBasePrice, tvProductSecurityDeposit, tvProductMaintenanceFee, tvProductCleaningFee, tvProductDiscount, tvProductNegotiable);
        } else {
            setVisibility(View.GONE, tvProductPrimaryPrice, tvProductBasePrice, tvProductSecurityDeposit, tvProductMaintenanceFee, tvProductCleaningFee, tvProductDiscount, tvProductNegotiable);
            tvProductPrimaryPrice.setText("Price not available"); // Fallback for header
        }
        
        // Populate Facilities ChipGroup
        populateChipGroup(chipGroupFacilities, meta != null ? meta.getFacilities() : null);

        // Populate Tags ChipGroup
        populateChipGroup(chipGroupTags, meta != null ? meta.getTags() : null);


        if (meta != null && meta.getRules() != null && !meta.getRules().isEmpty()) {
            tvProductRules.setText(TextUtils.join("\n", meta.getRules()));
            tvProductRules.setVisibility(View.VISIBLE);
        } else {
            tvProductRules.setText("No specific rules listed."); // Or hide
            // tvProductRules.setVisibility(View.GONE);
        }

        if (meta != null && meta.getNearby() != null && !meta.getNearby().isEmpty()) {
            StringBuilder nearbyText = new StringBuilder();
            for (Map.Entry<String, String> entry : meta.getNearby().entrySet()) {
                nearbyText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            tvProductNearby.setText(nearbyText.toString().trim());
            tvProductNearby.setVisibility(View.VISIBLE);
        } else {
            tvProductNearby.setText("No nearby places listed."); // Or hide
             // tvProductNearby.setVisibility(View.GONE);
        }

        Availability availability = currentProduct.getAvailability();
        if (availability != null) {
            tvProductAvailability.setText(String.format("From: %s\nTo: %s",
                availability.getFrom() != null ? availability.getFrom() : "N/A",
                availability.getTo() != null ? availability.getTo() : "N/A"));
            tvProductAvailability.setVisibility(View.VISIBLE);
        } else {
            tvProductAvailability.setText("Availability not specified."); // Or hide
            // tvProductAvailability.setVisibility(View.GONE);
        }

        Owner owner = currentProduct.getOwner();
        if (owner != null) {
            tvProductOwnerStatus.setText(owner.isVerified() ? "Verified Owner" : "Owner (Not Verified)");
            tvProductOwnerStatus.setVisibility(View.VISIBLE);
        } else {
            tvProductOwnerStatus.setText("Owner information not available."); // Or hide
            // tvProductOwnerStatus.setVisibility(View.GONE);
        }
    }

    private void populateChipGroup(ChipGroup chipGroup, List<String> items) {
        if (chipGroup == null) return;
        chipGroup.removeAllViews(); // Clear previous chips

        if (items != null && !items.isEmpty()) {
            chipGroup.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(this);
            for (String itemText : items) {
                Chip chip = (Chip) inflater.inflate(R.layout.item_chip_detail, chipGroup, false); // Assuming you have a item_chip_detail.xml
                // If you don't have a custom chip layout, create one:
                // Chip chip = new Chip(this);
                // chip.setChipBackgroundColorResource(R.color.your_chip_background_color); // Example
                // chip.setTextAppearance(R.style.YourChipTextStyle); // Example
                chip.setText(itemText);
                // chip.setClickable(false); // Make them non-interactive if just for display
                // chip.setFocusable(false);
                chipGroup.addView(chip);
            }
        } else {
            chipGroup.setVisibility(View.GONE);
        }
    }
    
    // Helper to set visibility for multiple views
    private void setVisibility(int visibility, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(visibility);
            }
        }
    }

    private void setupSimilarProductsRecyclerView() {
        rvSimilarProducts = findViewById(R.id.rv_similar_products);
        rvSimilarProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        similarProductsAdapter = new RentalItemAdapter(similarProductList); // Ensure RentalItemAdapter can take List<Product>
        rvSimilarProducts.setAdapter(similarProductsAdapter);
    }

    private void loadSimilarProducts(int currentProductIdToExclude, String categoryForSimilar) {
        if (categoryForSimilar == null || categoryForSimilar.isEmpty()) {
            Log.w(TAG, "Category for similar products is null or empty. Cannot load.");
            updateSimilarProductsAdapter(Collections.emptyList());
            return;
        }
        try {
            InputStream is = getAssets().open("products_by_category.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject topLevelJson = new JSONObject(jsonString);
            if (topLevelJson.has(categoryForSimilar)) {
                String categoryJsonArrayString = topLevelJson.getJSONArray(categoryForSimilar).toString();
                Gson gson = new Gson();
                TypeToken<List<Product>> productListType = new TypeToken<List<Product>>() {};
                List<Product> items = gson.fromJson(categoryJsonArrayString, productListType.getType());

                List<Product> filteredList = new ArrayList<>();
                if (items != null) {
                    for (Product item : items) {
                        if (item.getId() != currentProductIdToExclude) {
                            filteredList.add(item);
                        }
                        if (filteredList.size() >= 5) break; 
                    }
                }
                updateSimilarProductsAdapter(filteredList);
                if (filteredList.isEmpty()) {
                    Log.w(TAG, "No similar products found for category: " + categoryForSimilar + " (excluding current ID: " + currentProductIdToExclude + ")");
                }
            } else {
                Log.w(TAG, "Category '" + categoryForSimilar + "' not found for similar products.");
                updateSimilarProductsAdapter(Collections.emptyList());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading similar products for category: " + categoryForSimilar, e);
            updateSimilarProductsAdapter(Collections.emptyList());
        }
    }
    
    private void updateSimilarProductsAdapter(List<Product> newSimilarProducts) {
        similarProductList.clear();
        if (newSimilarProducts != null) {
            similarProductList.addAll(newSimilarProducts);
        }
        if (similarProductsAdapter != null) {
            similarProductsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
