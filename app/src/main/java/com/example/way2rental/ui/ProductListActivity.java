package com.example.way2rental.ui;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.way2rental.R;
import com.example.way2rental.adapter.RentalItemAdapter;
// Import the new Product model
import com.example.way2rental.model.Product; // << MODIFIED: Import Product
// Removed: import com.example.way2rental.model.RentalItem; // << MODIFIED: Remove old RentalItem

import org.json.JSONObject; // For parsing the top-level JSON object

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ProductListActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "com.example.way2rental.ui.CATEGORY_NAME";
    private static final String TAG = "ProductListActivity";

    private RecyclerView rvProductList;
    private RentalItemAdapter rentalItemAdapter;
    private List<Product> productList = new ArrayList<>(); // << MODIFIED: Changed to List<Product>
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar_product_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        if (categoryName != null) {
            setTitle(categoryName + " Listings");
        } else {
            setTitle("Product Listings");
            Log.e(TAG, "Category name not passed to ProductListActivity");
        }

        rvProductList = findViewById(R.id.rv_product_list);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        // Initialize adapter with List<Product>
        rentalItemAdapter = new RentalItemAdapter(productList); // << MODIFIED: Adapter now takes List<Product>
        rvProductList.setAdapter(rentalItemAdapter);

        loadProductsForCategory(categoryName);
    }

    private void loadProductsForCategory(String category) {
        if (category == null || category.isEmpty()) {
            productList.clear();
            if(rentalItemAdapter != null) {
                 // rentalItemAdapter.notifyDataSetChanged(); // Adapter will be updated with empty list
            }
            Log.e(TAG, "Cannot load products for null or empty category name.");
            updateAdapterWithData(Collections.emptyList());
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
            if (topLevelJson.has(category)) {
                String categoryJsonArrayString = topLevelJson.getJSONArray(category).toString();
                Gson gson = new Gson();
                // TypeToken for List<Product>
                TypeToken<List<Product>> productListType = new TypeToken<List<Product>>() {}; // << MODIFIED
                List<Product> items = gson.fromJson(categoryJsonArrayString, productListType.getType()); // << MODIFIED

                if (items == null) {
                    items = Collections.emptyList(); // Ensure items is not null
                }
                updateAdapterWithData(items);

                if (items.isEmpty()) {
                    Log.w(TAG, "No products found for category: " + category + " in JSON.");
                }
            } else {
                Log.w(TAG, "Category '" + category + "' not found as a key in products_by_category.json");
                updateAdapterWithData(Collections.emptyList());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading or parsing products_by_category.json for category: " + category, e);
            updateAdapterWithData(Collections.emptyList());
        }
    }

    // Helper method to update productList and notify adapter
    private void updateAdapterWithData(List<Product> newProducts) {
        productList.clear();
        if (newProducts != null) {
            productList.addAll(newProducts);
        }
        if (rentalItemAdapter != null) { // Ensure adapter is initialized
            rentalItemAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
