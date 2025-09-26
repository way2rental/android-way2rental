package com.example.way2rental.ui.auth;

import android.content.Context; // Added
import android.content.Intent;
import android.content.SharedPreferences; // Added
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.way2rental.MainActivity;
import com.example.way2rental.api.ApiService;
import com.example.way2rental.api.RetrofitClient;
import com.example.way2rental.databinding.ActivityRegistrationBinding;
import com.example.way2rental.model.APIResponse;
import com.example.way2rental.model.RegistrationRequest;
import com.example.way2rental.utility.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    private ApiService apiService;

    // Fields to store data received from LoginActivity
    private String userAccessToken;
    private String userRefreshToken;
    private String userTokenType;
    private String userAppCode;
    private String userIdentifier; // e.g., phone number

    // Constants for Intent Extras
    public static final String EXTRA_TOKEN = "com.example.way2rental.ui.auth.TOKEN";
    public static final String EXTRA_REFRESH_TOKEN = "com.example.way2rental.ui.auth.REFRESH_TOKEN"; // Added
    public static final String EXTRA_TOKEN_TYPE = "com.example.way2rental.ui.auth.TOKEN_TYPE";     // Added
    public static final String EXTRA_APP_CODE = "com.example.way2rental.ui.auth.APP_CODE";         // Added
    public static final String EXTRA_IDENTIFIER = "com.example.way2rental.ui.auth.IDENTIFIER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Update RetrofitClient call to pass context
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        Intent intent = getIntent();
        if (intent != null) {
            userAccessToken = intent.getStringExtra(EXTRA_TOKEN);
            userRefreshToken = intent.getStringExtra(EXTRA_REFRESH_TOKEN); // Can be null
            userTokenType = intent.getStringExtra(EXTRA_TOKEN_TYPE);
            userAppCode = intent.getStringExtra(EXTRA_APP_CODE);         // Can be null
            userIdentifier = intent.getStringExtra(EXTRA_IDENTIFIER);
        }

        // Check if essential data is present. Refresh token and App code can be null for new registrations.
        if (TextUtils.isEmpty(userAccessToken) ||
            TextUtils.isEmpty(userTokenType) ||
            TextUtils.isEmpty(userIdentifier)) {
            ToastUtils.show(this, "Error: Missing critical registration data. Please try logging in again.");
            Log.e("REG_ACTIVITY", "Missing critical data: Token=" + userAccessToken +
                                     ", Type=" + userTokenType + ", Identifier=" + userIdentifier +
                                     " (RefreshToken=" + userRefreshToken + ", AppCode=" + userAppCode + " are optional here)");
            finish(); // Go back, as registration cannot proceed
            return;
        }

        binding.btnRegister.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String country = binding.etCountry.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            binding.tilFirstName.setError("First name is required");
            return;
        } else {
            binding.tilFirstName.setError(null);
        }

        if (TextUtils.isEmpty(lastName)) {
            binding.tilLastName.setError("Last name is required");
            return;
        } else {
            binding.tilLastName.setError(null);
        }

        if (TextUtils.isEmpty(email)) { // Basic email validation can be added here
            binding.tilEmail.setError("Email is required");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(city)) {
            binding.tilCity.setError("City is required");
            return;
        } else {
            binding.tilCity.setError(null);
        }

        if (TextUtils.isEmpty(country)) {
            binding.tilCountry.setError("Country is required");
            return;
        } else {
            binding.tilCountry.setError(null);
        }

        binding.progressBarRegistration.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId == null) {
            deviceId = "unknown_device_id"; // Fallback
        }

        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName(firstName);
        registrationRequest.setLastName(lastName);
        registrationRequest.setEmail(email);
        registrationRequest.setCity(city);
        registrationRequest.setCountry(country);

        // These are passed to the registration API. The API might use them to link the profile
        // with the session/tokens already issued by the login/OTP step.
        registrationRequest.setIdentifier(userIdentifier);
        registrationRequest.setPrimaryContact(userIdentifier); // Assuming primary contact is the identifier
        registrationRequest.setTokenValue(userAccessToken);      // The access token received from login
        registrationRequest.setTokenType("USER_REGISTRATION_TOKEN"); // This seems specific to your registration API's expectation
        registrationRequest.setAppCode(userAppCode); // Use appCode received from login (can be null)
        registrationRequest.setDeviceId(deviceId);

        Log.d("REG_REQUEST", "Registration Request Payload: " + new com.google.gson.Gson().toJson(registrationRequest));

        Call<APIResponse<Object>> call = apiService.registerUser(registrationRequest);

        call.enqueue(new Callback<APIResponse<Object>>() {
            @Override
            public void onResponse(Call<APIResponse<Object>> call, Response<APIResponse<Object>> response) {
                binding.progressBarRegistration.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<Object> apiResponse = response.body();
                    // Assuming status 200 means success for registration completion
                    if (apiResponse.getStatus() == 200) {
                        ToastUtils.show(RegistrationActivity.this, "Registration Successful!");

                        // IMPORTANT: Save all auth details received from LoginActivity
                        saveAuthDetailsFromIntent();

                        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Registration failed. Please try again.";
                        ToastUtils.show(RegistrationActivity.this, errorMessage + " (Status: " + apiResponse.getStatus() + ")");
                    }
                } else {
                    String errorMessage = "Registration API error. Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            Log.e("REG_API_ERROR", "Error body: " + errorBodyString);
                            errorMessage += " - " + errorBodyString.substring(0, Math.min(errorBodyString.length(), 100));
                        }
                    } catch (Exception e) {
                        Log.e("REG_API_ERROR", "Error reading error body", e);
                    }
                    ToastUtils.show(RegistrationActivity.this, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Object>> call, Throwable t) {
                binding.progressBarRegistration.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
                Log.e("REG_API_FAILURE", "Registration API call failed", t);
                ToastUtils.show(RegistrationActivity.this, "Registration Error: " + t.getMessage());
            }
        });
    }

    // Method to save auth details (similar to LoginActivity's one)
    private void saveAuthDetailsFromIntent() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginActivity.AUTH_TOKEN_KEY, userAccessToken);
        editor.putString(LoginActivity.REFRESH_TOKEN_KEY, userRefreshToken); // Will store null if it is null
        editor.putString(LoginActivity.TOKEN_TYPE_KEY, userTokenType);
        editor.putString(LoginActivity.APP_CODE_KEY, userAppCode);             // Will store null if it is null
        editor.putString(LoginActivity.IDENTIFIER_KEY, userIdentifier);
        editor.commit(); // Changed from apply() to commit()

        Log.d("AUTH_SAVE_REG", "Auth details saved from RegistrationActivity: Token, Refresh Token, Type, AppCode, Identifier");
    }
}
