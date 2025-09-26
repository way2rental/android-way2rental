package com.example.way2rental;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.way2rental.adapter.BannerSliderAdapter;
import com.example.way2rental.adapter.CategoryAdapter;
import com.example.way2rental.adapter.RentalItemAdapter;
import com.example.way2rental.databinding.FragmentHomeBinding;
import com.example.way2rental.model.Category;
// Import the new Product model
import com.example.way2rental.model.Product; // << MODIFIED: Import Product
// Removed: import com.example.way2rental.model.RentalItem; // << MODIFIED: Remove old RentalItem
import com.example.way2rental.utility.JsonAssetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    // For Popular Rentals
    private RentalItemAdapter popularRentalsAdapter;
    private List<Product> popularProductList; // << MODIFIED: Changed from RentalItem to Product

    // For Deals of the Day
    private RentalItemAdapter dealsOfTheDayAdapter;
    private List<Product> dealsOfTheDayList; // << MODIFIED: Changed from RentalItem to Product

    private BannerSliderAdapter bannerAdapter;
    private List<String> bannerImageUrls;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategoryRecyclerView();
        setupPopularRentalsRecyclerView();
        setupDealsOfTheDayRecyclerView();
        setupBannerSlider();
    }

    private void setupCategoryRecyclerView() {
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryList = JsonAssetUtils.loadListFromJsonAsset(requireContext(), "categories.json", Category.class);
        categoryAdapter = new CategoryAdapter(categoryList);
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupPopularRentalsRecyclerView() {
        binding.rvPopularRentals.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        // Load Product objects now
        popularProductList = JsonAssetUtils.loadListFromJsonAsset(requireContext(), "popular_rentals.json", Product.class); // << MODIFIED
        popularRentalsAdapter = new RentalItemAdapter(popularProductList); // Pass List<Product>
        binding.rvPopularRentals.setAdapter(popularRentalsAdapter);
    }

    private void setupDealsOfTheDayRecyclerView() {
        binding.rvDealsOfTheDay.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        // Load Product objects now
        dealsOfTheDayList = JsonAssetUtils.loadListFromJsonAsset(requireContext(), "deals_of_the_day.json", Product.class); // << MODIFIED
        dealsOfTheDayAdapter = new RentalItemAdapter(dealsOfTheDayList); // Pass List<Product>
        binding.rvDealsOfTheDay.setAdapter(dealsOfTheDayAdapter);
    }

    private void setupBannerSlider() {
        // Load banner URLs from JSON asset
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.reflect.TypeToken<List<String>> stringListType = new com.google.gson.reflect.TypeToken<List<String>>() {};
        String jsonString = null;
        try {
            java.io.InputStream is = requireContext().getAssets().open("banners.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, java.nio.charset.StandardCharsets.UTF_8);
            bannerImageUrls = gson.fromJson(jsonString, stringListType.getType());
        } catch (java.io.IOException ex) {
            android.util.Log.e("HomeFragment", "Error reading banners.json", ex);
            bannerImageUrls = Collections.emptyList();
        } catch (com.google.gson.JsonSyntaxException ex) {
            android.util.Log.e("HomeFragment", "Error parsing banners.json", ex);
            bannerImageUrls = Collections.emptyList();
        }

        if (bannerImageUrls == null || bannerImageUrls.isEmpty()) {
            bannerImageUrls = new ArrayList<>();
            android.util.Log.w("HomeFragment", "Banner images failed to load or were empty. No banners will be shown.");
        }

        bannerAdapter = new BannerSliderAdapter(requireContext(), bannerImageUrls);
        binding.bannerSlider.setAdapter(bannerAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
