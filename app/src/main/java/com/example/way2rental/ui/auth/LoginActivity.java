package com.example.way2rental.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings; // Added for deviceId
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.way2rental.MainActivity;
import com.example.way2rental.api.ApiService;
import com.example.way2rental.api.RetrofitClient;
import com.example.way2rental.databinding.ActivityLoginBinding;
import com.example.way2rental.model.*;
import com.example.way2rental.utility.ToastUtils;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ApiService apiService;
    private String phoneNumber = ""; // This will serve as the 'identifier'
    private String authorizationId = "";

    public static final String USER_PREFS = "USER_PREFS";
    public static final String AUTH_TOKEN_KEY = "AUTH_TOKEN"; // Access Token
    public static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN";
    public static final String TOKEN_TYPE_KEY = "TOKEN_TYPE";
    public static final String APP_CODE_KEY = "APP_CODE";
    public static final String IDENTIFIER_KEY = "IDENTIFIER_KEY"; // For phone number/username
    // DEVICE_ID will be fetched on the fly, not stored.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for existing login token FIRST
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String existingToken = sharedPreferences.getString(AUTH_TOKEN_KEY, null);

        if (existingToken != null && !existingToken.isEmpty()) {
            // Token exists, user is likely already logged in
            Log.d("LOGIN_FLOW_CHECK", "Existing token found. Navigating to MainActivity.");
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish(); // Important: Finish LoginActivity so user can't navigate back to it
            return;   // Skip the rest of the login UI setup
        }

        // No valid token found, proceed with login UI setup
        Log.d("LOGIN_FLOW_CHECK", "No existing token. Displaying login screen.");
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getClient(this).create(ApiService.class); // Updated this line

        binding.etOtp.setVisibility(View.GONE);
        binding.btnVerifyOtp.setVisibility(View.GONE);

        binding.btnSendOtp.setOnClickListener(v -> {
            phoneNumber = binding.etPhone.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                ToastUtils.show(this, "Enter phone number");
                return;
            }
            // phoneIdentifier is already assigned to phoneNumber here
            sendOtp(phoneNumber);
        });

        binding.btnVerifyOtp.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString().trim();
            if (otp.isEmpty()) {
                ToastUtils.show(this, "Enter OTP");
                return;
            }
            // Pass phoneNumber (which is the identifier) to loginWithOtp
            loginWithOtp(phoneNumber, otp);
        });
    }

    private void sendOtp(String phone) { // phone parameter is the phone number (identifier)
        binding.progressBar.setVisibility(View.VISIBLE);
        apiService.sendOtp(new OtpRequest(phone)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<APIResponse<OtpResponse>> call, Response<APIResponse<OtpResponse>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<OtpResponse> apiResponse = response.body();
                    if (Objects.isNull(apiResponse.getData())) {
                        ToastUtils.show(LoginActivity.this, apiResponse.getMessage());
                        binding.etOtp.setVisibility(View.GONE);
                        binding.btnVerifyOtp.setVisibility(View.GONE);
                    } else {
                        OtpResponse otpResponse = apiResponse.getData();
                        authorizationId = otpResponse.getReferenceId();
                        ToastUtils.show(LoginActivity.this, otpResponse.getMessage());
                        binding.etOtp.setVisibility(View.VISIBLE);
                        binding.btnVerifyOtp.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.show(LoginActivity.this, "Failed to send OTP. Please try again.");
                    binding.etOtp.setVisibility(View.GONE);
                    binding.btnVerifyOtp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<OtpResponse>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                ToastUtils.show(LoginActivity.this, "Error: " + t.getMessage());
                binding.etOtp.setVisibility(View.GONE);
                binding.btnVerifyOtp.setVisibility(View.GONE);
            }
        });
    }

    private void loginWithOtp(String currentPhoneNumberIdentifier, String otp) {
        binding.progressBar.setVisibility(View.VISIBLE);
        // currentPhoneNumberIdentifier is passed as phoneIdentifier to LoginRequest
        apiService.loginWithOtp(new LoginRequest(currentPhoneNumberIdentifier, otp, authorizationId)).enqueue(new Callback<APIResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<LoginResponse>> call, Response<APIResponse<LoginResponse>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<LoginResponse> apiResponse = response.body();
                    if (Objects.isNull(apiResponse.getData())) {
                        ToastUtils.show(LoginActivity.this, apiResponse.getMessage());
                    } else {
                        LoginResponse loginResponse = apiResponse.getData();
                        Log.d("LOGIN_FLOW", "Login API call successful. Token: " + loginResponse.getToken());
                        Log.d("LOGIN_FLOW", "Refresh Token: " + loginResponse.getRefreshToken());
                        Log.d("LOGIN_FLOW", "Token Type: " + loginResponse.getTokenType());
                        Log.d("LOGIN_FLOW", "App Code: " + loginResponse.getAppCode());
                        Log.d("LOGIN_FLOW", "Identifier for storage: " + currentPhoneNumberIdentifier);
                        Log.d("LOGIN_FLOW", "Is new registration? " + loginResponse.isNewRegistration());

                        if (!loginResponse.isNewRegistration()) {
                            // Save all relevant details for existing user
                            saveAuthDetails(loginResponse, currentPhoneNumberIdentifier);
                            Log.d("LOGIN_FLOW", "Existing user. Auth details saved.");
                            ToastUtils.show(LoginActivity.this, "Welcome back!");
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Log.d("LOGIN_FLOW", "New registration path triggered.");
                            ToastUtils.show(LoginActivity.this, "New user! Please complete your profile.");
                            Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                            // Pass all necessary details to RegistrationActivity so it can save them after successful registration
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_TOKEN, loginResponse.getToken());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_REFRESH_TOKEN, loginResponse.getRefreshToken());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_TOKEN_TYPE, loginResponse.getTokenType());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_APP_CODE, loginResponse.getAppCode());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_IDENTIFIER, currentPhoneNumberIdentifier);
                            startActivity(registrationIntent);
                            finish();
                        }
                    }
                } else {
                    String errorMessage = "Invalid OTP or Login Failed.";
                    if (response.errorBody() != null) {
                        errorMessage = "Login failed. Please check your OTP and try again.";
                    } else if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    ToastUtils.show(LoginActivity.this, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<LoginResponse>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                ToastUtils.show(LoginActivity.this, "Login Error: " + t.getMessage());
                Log.d("LOGIN_FLOW", "Login API call failed: " + t.getMessage());
            }
        });
    }

    // Renamed and updated to save all necessary details
    private void saveAuthDetails(LoginResponse loginResponse, String identifier) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, loginResponse.getToken());
        editor.putString(REFRESH_TOKEN_KEY, loginResponse.getRefreshToken());
        editor.putString(TOKEN_TYPE_KEY, loginResponse.getTokenType());
        editor.putString(APP_CODE_KEY, loginResponse.getAppCode());
        editor.putString(IDENTIFIER_KEY, identifier); // Save the identifier (phone number)
        editor.commit(); // Changed from apply() to commit()

        Log.d("AUTH_SAVE", "Auth details saved: Token, Refresh Token, Type, AppCode, Identifier");
    }

    // Method to clear auth details (e.g., on logout or when refresh fails permanently)
    public static void clearAuthDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.remove(TOKEN_TYPE_KEY);
        editor.remove(APP_CODE_KEY);
        editor.remove(IDENTIFIER_KEY);
        editor.commit(); // Changed from apply() to commit()
        Log.d("AUTH_CLEAR", "All auth details cleared from SharedPreferences.");
    }
}
