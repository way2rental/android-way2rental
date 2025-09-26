package com.example.way2rental;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.way2rental.databinding.ActivityMyBinding; // Import ViewBinding
import com.google.android.material.navigation.NavigationBarView; // Import for listener

public class MyActivityActivity extends AppCompatActivity {

    private ActivityMyBinding binding; // Declare binding variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBinding.inflate(getLayoutInflater()); // Inflate using ViewBinding
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Activity");
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Set "My Activity" as selected
        binding.bottomNavigationMyActivity.setSelectedItemId(R.id.navigation_my_activity);

        binding.bottomNavigationMyActivity.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MyActivityActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // finish(); // Optional: finish current activity if you don't want it in backstack when going Home
                return true;
            } else if (itemId == R.id.navigation_my_activity) {
                // Already on My Activity, do nothing or refresh
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(MyActivityActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the correct item is selected when returning to this activity
        binding.bottomNavigationMyActivity.setSelectedItemId(R.id.navigation_my_activity);
    }
}
