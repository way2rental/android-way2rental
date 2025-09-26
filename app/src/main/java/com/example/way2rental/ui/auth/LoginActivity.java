package com.example.way2rental.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
// import android.provider.Settings; // No longer used directly, can be removed if not needed elsewhere
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
    private String phoneNumber = "";
    private String authorizationId = "";

    public static final String USER_PREFS = "USER_PREFS";
    public static final String AUTH_TOKEN_KEY = "AUTH_TOKEN";
    public static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN";
    public static final String TOKEN_TYPE_KEY = "TOKEN_TYPE";
    public static final String APP_CODE_KEY = "APP_CODE";
    public static final String IDENTIFIER_KEY = "IDENTIFIER_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String existingToken = sharedPreferences.getString(AUTH_TOKEN_KEY, null);

        if (existingToken != null && !existingToken.isEmpty()) {
            Log.d("LOGIN_FLOW_CHECK", "Existing token found. Navigating to MainActivity.");
            navigateToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        setPhoneNumberEntryState(true); // Initial UI state

        binding.btnSendOtp.setOnClickListener(v -> {
            phoneNumber = binding.etPhone.getText().toString().trim();
            if (phoneNumber.isEmpty() || phoneNumber.length() < 10) { // Basic validation
                binding.tilPhone.setError("Enter a valid phone number");
                return;
            }
            binding.tilPhone.setError(null);
            sendOtp(phoneNumber);
        });

        binding.btnVerifyOtp.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString().trim();
            if (otp.isEmpty() || otp.length() < 6) { // Basic validation
                binding.tilOtp.setError("Enter a valid 6-digit OTP");
                return;
            }
            binding.tilOtp.setError(null);
            loginWithOtp(phoneNumber, otp);
        });

        binding.ibEditPhone.setOnClickListener(v -> {
            setPhoneNumberEntryState(true); // Revert to phone number entry UI
        });
    }

    private void setPhoneNumberEntryState(boolean enabled) {
        binding.tilPhone.setEnabled(enabled);
        binding.etPhone.setEnabled(enabled);
        binding.btnSendOtp.setVisibility(enabled ? View.VISIBLE : View.GONE);
        binding.ibEditPhone.setVisibility(enabled ? View.GONE : View.VISIBLE);

        // When returning to phone entry, hide and disable OTP fields
        if (enabled) {
            binding.tilOtp.setVisibility(View.GONE);
            binding.tilOtp.setEnabled(false);
            binding.etOtp.setText(""); // Clear previous OTP
            binding.btnVerifyOtp.setVisibility(View.GONE);
            binding.btnVerifyOtp.setEnabled(false);
            binding.tilOtp.setError(null); // Clear any previous OTP error
        }
    }

    private void setOtpVerificationState(boolean otpSentSuccessfully) {
        // Disable phone input, hide Send OTP, show Edit Phone
        binding.tilPhone.setEnabled(!otpSentSuccessfully);
        binding.etPhone.setEnabled(!otpSentSuccessfully);
        binding.btnSendOtp.setVisibility(otpSentSuccessfully ? View.GONE : View.VISIBLE);
        binding.ibEditPhone.setVisibility(otpSentSuccessfully ? View.VISIBLE : View.GONE);

        // Show and enable OTP fields if OTP was sent successfully
        binding.tilOtp.setVisibility(otpSentSuccessfully ? View.VISIBLE : View.GONE);
        binding.tilOtp.setEnabled(otpSentSuccessfully);
        binding.btnVerifyOtp.setVisibility(otpSentSuccessfully ? View.VISIBLE : View.GONE);
        binding.btnVerifyOtp.setEnabled(otpSentSuccessfully);

        if (!otpSentSuccessfully) {
            // If OTP send failed, ensure phone entry is fully re-enabled
            // and OTP fields are hidden/cleared.
            setPhoneNumberEntryState(true);
        } else {
            binding.etOtp.requestFocus(); // Focus on OTP field
        }
    }

    private void sendOtp(String phone) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSendOtp.setEnabled(false); // Disable button during API call

        apiService.sendOtp(new OtpRequest(phone)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<APIResponse<OtpResponse>> call, Response<APIResponse<OtpResponse>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSendOtp.setEnabled(true); // Re-enable on response

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<OtpResponse> apiResponse = response.body();
                    if (apiResponse.getData() != null && apiResponse.getStatus() == 200) { // Assuming 200 means success
                        OtpResponse otpResponse = apiResponse.getData();
                        authorizationId = otpResponse.getReferenceId();
                        ToastUtils.show(LoginActivity.this, otpResponse.getMessage());
                        setOtpVerificationState(true); // OTP sent, switch UI
                    } else {
                        ToastUtils.show(LoginActivity.this, apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to send OTP.");
                        setOtpVerificationState(false); // OTP send failed, revert UI
                    }
                } else {
                    ToastUtils.show(LoginActivity.this, "Failed to send OTP. Please try again.");
                    setOtpVerificationState(false); // OTP send failed, revert UI
                }
            }

            @Override
            public void onFailure(Call<APIResponse<OtpResponse>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSendOtp.setEnabled(true); // Re-enable on failure
                ToastUtils.show(LoginActivity.this, "Error: " + t.getMessage());
                setOtpVerificationState(false); // Error, revert UI
            }
        });
    }

    private void loginWithOtp(String currentPhoneNumberIdentifier, String otp) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerifyOtp.setEnabled(false); // Disable button during API call

        apiService.loginWithOtp(new LoginRequest(currentPhoneNumberIdentifier, otp, authorizationId)).enqueue(new Callback<APIResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<LoginResponse>> call, Response<APIResponse<LoginResponse>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true); // Re-enable on response

                if (response.isSuccessful() && response.body() != null) {
                    APIResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.getData() != null && apiResponse.getStatus() == 200) { // Assuming 200 means success
                        LoginResponse loginResponse = apiResponse.getData();
                        Log.d("LOGIN_FLOW", "Login API call successful.");

                        if (!loginResponse.isNewRegistration()) {
                            saveAuthDetails(loginResponse, currentPhoneNumberIdentifier);
                            ToastUtils.show(LoginActivity.this, "Welcome back!");
                            navigateToMain();
                        } else {
                            ToastUtils.show(LoginActivity.this, "New user! Please complete your profile.");
                            Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_TOKEN, loginResponse.getToken());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_REFRESH_TOKEN, loginResponse.getRefreshToken());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_TOKEN_TYPE, loginResponse.getTokenType());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_APP_CODE, loginResponse.getAppCode());
                            registrationIntent.putExtra(RegistrationActivity.EXTRA_IDENTIFIER, currentPhoneNumberIdentifier);
                            startActivity(registrationIntent);
                            finish();
                        }
                    } else {
                        String errorMessage = (apiResponse.getMessage() != null) ? apiResponse.getMessage() : "Invalid OTP or Login Failed.";
                         if (apiResponse.getData() == null && apiResponse.getMessage() == null && response.code() >=400){
                            errorMessage = "Invalid OTP or Login Failed. Please try again."; // More generic for certain errors
                        }
                        ToastUtils.show(LoginActivity.this, errorMessage);
                        binding.tilOtp.setError(errorMessage); // Show error on OTP field
                        binding.etOtp.setText(""); // Clear OTP on failure
                    }
                } else {
                    String errorMessage = "Login failed. Please check your OTP and try again.";
                     try {
                        if (response.errorBody() != null) {
                            // You could try to parse the error body here if it's JSON
                            // For now, using a generic message.
                            Log.e("LOGIN_ERROR_BODY", response.errorBody().string());
                        }
                    } catch (Exception e) { Log.e("LOGIN_ERROR_BODY", "Error reading error body", e); }
                    ToastUtils.show(LoginActivity.this, errorMessage);
                    binding.tilOtp.setError(errorMessage);
                    binding.etOtp.setText("");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<LoginResponse>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true); // Re-enable on failure
                ToastUtils.show(LoginActivity.this, "Login Error: " + t.getMessage());
                Log.d("LOGIN_FLOW", "Login API call failed: " + t.getMessage());
                binding.tilOtp.setError("Login Error: " + t.getMessage());
                 binding.etOtp.setText("");
            }
        });
    }

    private void saveAuthDetails(LoginResponse loginResponse, String identifier) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, loginResponse.getToken());
        editor.putString(REFRESH_TOKEN_KEY, loginResponse.getRefreshToken());
        editor.putString(TOKEN_TYPE_KEY, loginResponse.getTokenType());
        editor.putString(APP_CODE_KEY, loginResponse.getAppCode());
        editor.putString(IDENTIFIER_KEY, identifier);
        editor.commit();
        Log.d("AUTH_SAVE", "Auth details saved.");
    }

    private void navigateToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public static void clearAuthDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.remove(TOKEN_TYPE_KEY);
        editor.remove(APP_CODE_KEY);
        editor.remove(IDENTIFIER_KEY);
        editor.commit();
        Log.d("AUTH_CLEAR", "All auth details cleared from SharedPreferences.");
    }
}
