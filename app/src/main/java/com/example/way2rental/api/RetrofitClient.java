package com.example.way2rental.api;

import android.content.Context; // Added import for Context

import com.example.way2rental.api.converter.LocalDateTimeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://way2rental.ddns.net:8080/api/"; // Or "http://10.0.2.2:8080/api/" for localhost from emulator

    private static Retrofit retrofitWithAuth;
    private static Retrofit retrofitUnauthenticated;

    // Gson instance can be shared
    private static Gson gson;

    private static Gson getGsonInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                    .create();
        }
        return gson;
    }

    /**
     * Gets a Retrofit client instance that INCLUDES the TokenAuthenticator.
     * Use this for all normal authenticated API calls.
     *
     * @param context The application or activity context.
     * @return Retrofit instance with authentication.
     */
    public static Retrofit getClient(Context context) {
        if (retrofitWithAuth == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(logging);
            // Add the TokenAuthenticator
            httpClientBuilder.authenticator(new TokenAuthenticator(context.getApplicationContext()));

            OkHttpClient clientWithAuth = httpClientBuilder.build();

            retrofitWithAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGsonInstance()))
                    .client(clientWithAuth)
                    .build();
        }
        return retrofitWithAuth;
    }

    /**
     * Gets a Retrofit client instance WITHOUT the TokenAuthenticator.
     * Use this specifically for the token refresh API call from within TokenAuthenticator
     * to avoid authenticator loops.
     *
     * @return Retrofit instance without authentication.
     */
    public static Retrofit getUnauthenticatedClient() {
        if (retrofitUnauthenticated == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient clientUnauthenticated = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build(); // No authenticator here

            retrofitUnauthenticated = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGsonInstance()))
                    .client(clientUnauthenticated)
                    .build();
        }
        return retrofitUnauthenticated;
    }

    // Optional: A method to clear instances if base URL or other fundamental settings change (e.g., on logout for re-init)
    public static void resetClients() {
        retrofitWithAuth = null;
        retrofitUnauthenticated = null;
        // gson = null; // Gson instance can typically persist
    }
}
