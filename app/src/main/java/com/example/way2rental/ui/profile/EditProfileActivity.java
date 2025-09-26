package com.example.way2rental.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.way2rental.api.ApiService;
import com.example.way2rental.api.RetrofitClient;
import com.example.way2rental.databinding.ActivityEditProfileBinding;
import com.example.way2rental.model.APIResponse;
import com.example.way2rental.model.EditProfileRequest;
import com.example.way2rental.model.StatusResponse;
import com.example.way2rental.model.UserProfile; // To receive current profile data
import com.example.way2rental.ui.auth.LoginActivity; // For SharedPreferences keys
import com.example.way2rental.utility.ToastUtils;
import com.google.gson.Gson;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_PROFILE = "EXTRA_USER_PROFILE";
    private ActivityEditProfileBinding binding;
    private ApiService apiService;
    private String userIdentifier;
    private UserProfile currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Edit Profile");
        }

        if (getIntent().hasExtra(EXTRA_USER_PROFILE)) {
            String userProfileJson = getIntent().getStringExtra(EXTRA_USER_PROFILE);
            currentUserProfile = new Gson().fromJson(userProfileJson, UserProfile.class);
            if (currentUserProfile != null) {
                userIdentifier = currentUserProfile.getPhone(); // Assuming phone is the identifier
                populateFields(currentUserProfile);
            }
        }

        // Fallback to get identifier from SharedPreferences if not passed via intent
        if (TextUtils.isEmpty(userIdentifier)) {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.USER_PREFS, Context.MODE_PRIVATE);
            userIdentifier = sharedPreferences.getString(LoginActivity.IDENTIFIER_KEY, null);
        }

        if (TextUtils.isEmpty(userIdentifier)) {
            ToastUtils.show(this, "User identifier not found. Cannot edit profile.");
            finish();
            return;
        }

        binding.btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void populateFields(UserProfile profile) {
        binding.etName.setText(profile.getName());
        binding.etEmail.setText(profile.getEmail());
        binding.etAlternatePhone.setText(profile.getAlternatePhone());
        binding.etAddress.setText(profile.getAddress());
        binding.etCity.setText(profile.getCity());
        binding.etState.setText(profile.getState());
        binding.etCountry.setText(profile.getCountry());
        binding.etZipCode.setText(profile.getZipCode());
    }

    private void saveProfileChanges() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String alternatePhone = binding.etAlternatePhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String state = binding.etState.getText().toString().trim();
        String country = binding.etCountry.getText().toString().trim();
        String zipCode = binding.etZipCode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError("Name cannot be empty");
            return;
        } else {
            binding.tilName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Email cannot be empty");
            return;
        } else {
            binding.tilEmail.setError(null);
        }
        // Add more validation as needed for other fields (e.g., email format)

        binding.pbEditProfile.setVisibility(View.VISIBLE);
        binding.btnSaveChanges.setEnabled(false);

        // userIdentifier is no longer part of EditProfileRequest constructor
        EditProfileRequest request = new EditProfileRequest(
                name, email, alternatePhone,
                address, city, state, country, zipCode
        );

        // userIdentifier is now passed as the first argument to editUserProfile
        apiService.editUserProfile(userIdentifier, request).enqueue(new Callback<APIResponse<StatusResponse>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<StatusResponse>> call, @NonNull Response<APIResponse<StatusResponse>> response) {
                binding.pbEditProfile.setVisibility(View.GONE);
                binding.btnSaveChanges.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<StatusResponse> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200 && apiResponse.getData() != null && "SUCCESS".equalsIgnoreCase(apiResponse.getData().getStatus())) {
                        ToastUtils.show(EditProfileActivity.this, apiResponse.getData().getMessage());
                        setResult(RESULT_OK); // To indicate success to ProfileFragment
                        finish();
                    } else {
                        String errorMessage = "Failed to update profile: ";
                        if (apiResponse.getData() != null && apiResponse.getData().getMessage() != null) {
                            errorMessage += apiResponse.getData().getMessage();
                        } else if (apiResponse.getMessage() != null){
                            errorMessage += apiResponse.getMessage();
                        } else {
                            errorMessage += "Unknown error from server.";
                        }
                        ToastUtils.show(EditProfileActivity.this, errorMessage);
                    }
                } else {
                     String errorDetail = "Update failed.";
                    if (response.errorBody() != null) {
                        try {
                            // You might want to parse this errorBody if it's JSON
                            errorDetail += " Error: " + response.errorBody().string();
                        } catch (Exception e) {
                            // ignore
                        }
                    } else {
                        errorDetail += " Code: " + response.code();
                    }
                    ToastUtils.show(EditProfileActivity.this, errorDetail);
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<StatusResponse>> call, @NonNull Throwable t) {
                binding.pbEditProfile.setVisibility(View.GONE);
                binding.btnSaveChanges.setEnabled(true);
                ToastUtils.show(EditProfileActivity.this, "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
