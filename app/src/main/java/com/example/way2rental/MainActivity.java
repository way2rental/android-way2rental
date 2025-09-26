package com.example.way2rental;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment; // Added
import androidx.fragment.app.FragmentManager; // Added
import androidx.fragment.app.FragmentTransaction; // Added

import com.example.way2rental.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView; // Keep for listener type

// Removed unused imports for adapters and models as they are now in HomeFragment
// Removed Intent import as we are switching fragments, not activities for main nav

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Removed fields for adapters and lists as they are now in HomeFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Removed direct calls to setupCategoryRecyclerView, setupPopularRentalsRecyclerView, setupBannerSlider

        setupBottomNavigation();

        // Load the default fragment (HomeFragment)
        if (savedInstanceState == null) { // Important to avoid recreating fragment on config change
            loadFragment(new HomeFragment(), false); // Load HomeFragment initially
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_home); // Set Home as selected
        }
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_my_activity) {
                // Placeholder: We will create MyActivityFragment next
                selectedFragment = new MyActivityFragment(); // To be created
            } else if (itemId == R.id.navigation_profile) {
                // Placeholder: We will create ProfileFragment next
                selectedFragment = new ProfileFragment(); // To be created
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, false); // Don't add to backstack for main navigation items usually
                return true;
            }
            return false;
        });
    }

    // Removed setupCategoryRecyclerView, setupPopularRentalsRecyclerView, setupBannerSlider methods
    // as their logic is now in HomeFragment.java
}
