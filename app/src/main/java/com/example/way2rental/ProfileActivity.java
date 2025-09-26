package com.example.way2rental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Added for View
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.way2rental.databinding.ActivityProfileBinding; // Import ViewBinding
import com.example.way2rental.ui.auth.LoginActivity; // Added for LoginActivity
import com.google.android.material.navigation.NavigationBarView; // Import for listener

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding; // Declare binding variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater()); // Inflate using ViewBinding
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
        }

        setupBottomNavigation();

        // Logout Button Logic
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear authentication details
                LoginActivity.clearAuthDetails(ProfileActivity.this);

                // Navigate to LoginActivity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Finish ProfileActivity
            }
        });
    }

    private void setupBottomNavigation() {
        // Set "Profile" as selected
        binding.bottomNavigationProfile.setSelectedItemId(R.id.navigation_profile);

        binding.bottomNavigationProfile.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // finish(); // Optional: finish current activity if you don't want it in backstack when going Home
                return true;
            } else if (itemId == R.id.navigation_my_activity) {
                Intent intent = new Intent(ProfileActivity.this, MyActivityActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Already on Profile, do nothing or refresh
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the correct item is selected when returning to this activity
        binding.bottomNavigationProfile.setSelectedItemId(R.id.navigation_profile);
    }
}
