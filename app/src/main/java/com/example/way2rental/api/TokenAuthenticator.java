package com.example.way2rental.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.way2rental.model.APIResponse;
import com.example.way2rental.model.LoginResponse;
import com.example.way2rental.model.RefreshTokenRequest;
import com.example.way2rental.ui.auth.LoginActivity; // For SharedPreferences keys and clearAuthDetails

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {

    private final Context context;
    private static final String TAG = "TokenAuthenticator";

    // To prevent multiple threads trying to refresh the token simultaneously
    private static final Object lock = new Object();


    public TokenAuthenticator(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        // This method is called when a request receives a 401 Unauthorized
        Log.d(TAG, "authenticate: 401 detected for request to " + response.request().url());

        // 1. Get the current (expired) access token to check if it's the one that failed
        SharedPreferences prefs = context.getSharedPreferences(LoginActivity.USER_PREFS, Context.MODE_PRIVATE);
        String currentAccessToken = prefs.getString(LoginActivity.AUTH_TOKEN_KEY, null);
        String tokenTypeFromPrefs = prefs.getString(LoginActivity.TOKEN_TYPE_KEY, "Bearer");


        // If the failed request was already made with a new token, or if no token, don't retry.
        String failedRequestAuthHeader = response.request().header("Authorization");

        // Construct what the current auth header should look like
        String expectedCurrentAuthHeader = null;
        if (currentAccessToken != null) {
            expectedCurrentAuthHeader = tokenTypeFromPrefs.trim();
            if (!expectedCurrentAuthHeader.isEmpty() && !expectedCurrentAuthHeader.endsWith(" ")) {
                expectedCurrentAuthHeader += " ";
            }
            expectedCurrentAuthHeader += currentAccessToken;
        }

        // If the failed request's auth header doesn't match our current one,
        // it might be an old request or a request that got its token from somewhere else.
        // Or, if there was no auth header in the failed request and we don't have a current token, also don't retry.
        if (failedRequestAuthHeader != null && expectedCurrentAuthHeader != null && !failedRequestAuthHeader.equals(expectedCurrentAuthHeader)) {
            // If current token is different than what caused the 401,
            // it means token was already refreshed and this request (with old token) failed.
            // Retry with the NEWEST token from prefs.
             String newestAccessToken = prefs.getString(LoginActivity.AUTH_TOKEN_KEY, null);
             String newestTokenType = prefs.getString(LoginActivity.TOKEN_TYPE_KEY, "Bearer");
             if (newestAccessToken != null && !newestAccessToken.equals(currentAccessToken)) {
                 Log.d(TAG, "authenticate: Failed token is old. Retrying with most recent token from prefs.");
                 return newRequestWithToken(response.request(), newestAccessToken, newestTokenType);
             }
             // If it's not different, then something else is wrong, or the token is genuinely bad.
             Log.d(TAG, "authenticate: Token that failed (" + failedRequestAuthHeader + ") is different from expected current ("+ expectedCurrentAuthHeader +") but not newer. No retry.");
             return null;
        } else if (failedRequestAuthHeader == null && currentAccessToken == null) {
             Log.d(TAG, "authenticate: No auth header in failed request and no current token. No retry.");
             return null;
        }
        // If failedRequestAuthHeader IS null but we DO have a currentAccessToken, it's an issue - API expected auth but none sent. Authenticator can't fix this.
        // If failedRequestAuthHeader matches expectedCurrentAuthHeader, then our current token IS the one that failed. Proceed to refresh.

        synchronized (lock) {
            // Re-check if another thread has already refreshed the token while this one was waiting
            // And also check if the token that failed is still the current one
            String refreshedAccessToken = prefs.getString(LoginActivity.AUTH_TOKEN_KEY, null);
            String refreshedTokenType = prefs.getString(LoginActivity.TOKEN_TYPE_KEY, "Bearer");

            if (currentAccessToken != null && !currentAccessToken.equals(refreshedAccessToken)) {
                Log.d(TAG, "authenticate: Token already refreshed by another thread while waiting for lock. Retrying with new token.");
                return newRequestWithToken(response.request(), refreshedAccessToken, refreshedTokenType);
            }

            // 2. Get refresh token and other necessary details from SharedPreferences
            String refreshToken = prefs.getString(LoginActivity.REFRESH_TOKEN_KEY, null);
            // tokenTypeFromPrefs is already fetched
            String appCode = prefs.getString(LoginActivity.APP_CODE_KEY, null);
            String identifier = prefs.getString(LoginActivity.IDENTIFIER_KEY, null);

            if (refreshToken == null || appCode == null || identifier == null) {
                Log.e(TAG, "authenticate: Missing refresh token, appCode, or identifier. Cannot refresh. Clearing auth details.");
                LoginActivity.clearAuthDetails(context);
                // Optionally, trigger logout here via EventBus or BroadcastReceiver
                return null; // Cannot refresh, do not retry
            }

            // 3. Make the synchronous refresh token API call
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (deviceId == null) deviceId = "unknown_device_id"; // Fallback

            RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(deviceId, refreshToken, tokenTypeFromPrefs, identifier, appCode);

            ApiService refreshService = RetrofitClient.getUnauthenticatedClient().create(ApiService.class);
            Call<APIResponse<LoginResponse>> refreshTokenCall = refreshService.refreshToken(refreshTokenRequest);

            try {
                Log.d(TAG, "authenticate: Attempting to refresh token synchronously...");
                retrofit2.Response<APIResponse<LoginResponse>> refreshResponse = refreshTokenCall.execute(); // Synchronous call

                if (refreshResponse.isSuccessful() && refreshResponse.body() != null && refreshResponse.body().getData() != null) {
                    LoginResponse loginResponse = refreshResponse.body().getData();
                    Log.d(TAG, "authenticate: Token refresh successful. New access token: " + loginResponse.getToken());

                    // 4. Save the new tokens and auth details
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(LoginActivity.AUTH_TOKEN_KEY, loginResponse.getToken());
                    if (loginResponse.getRefreshToken() != null) {
                        editor.putString(LoginActivity.REFRESH_TOKEN_KEY, loginResponse.getRefreshToken());
                    }
                    editor.putString(LoginActivity.TOKEN_TYPE_KEY, loginResponse.getTokenType());
                    editor.putString(LoginActivity.APP_CODE_KEY, loginResponse.getAppCode());
                    // Identifier usually doesn't change with refresh.
                    editor.apply();

                    // 5. Retry the original request with the new token
                    return newRequestWithToken(response.request(), loginResponse.getToken(), loginResponse.getTokenType());
                } else {
                    Log.e(TAG, "authenticate: Token refresh failed. Code: " + refreshResponse.code() + ", Message: " + refreshResponse.message());
                    if (refreshResponse.code() == 401 || refreshResponse.code() == 403) { // Or other codes indicating invalid refresh token
                        Log.e(TAG, "authenticate: Refresh token is invalid or expired. Clearing auth details and logging out.");
                        LoginActivity.clearAuthDetails(context);
                        // Optionally, trigger logout here
                    }
                    return null; // Do not retry
                }
            } catch (IOException e) {
                Log.e(TAG, "authenticate: IOException during token refresh: " + e.getMessage(), e);
                return null; // Do not retry
            }
        }
    }

    private Request newRequestWithToken(@NonNull Request originalRequest, @NonNull String accessToken, @NonNull String tokenType) {
        Log.d(TAG, "newRequestWithToken: Building new request with token type: " + tokenType + " and token: " + accessToken);
        String authHeaderValue = tokenType.trim();
        if (!authHeaderValue.isEmpty() && !authHeaderValue.endsWith(" ")) {
            authHeaderValue += " ";
        }
        authHeaderValue += accessToken;

        return originalRequest.newBuilder()
                .header("Authorization", authHeaderValue)
                .build();
    }
}
