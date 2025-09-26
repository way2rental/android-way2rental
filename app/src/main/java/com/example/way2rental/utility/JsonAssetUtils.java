package com.example.way2rental.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class JsonAssetUtils {

    private static final String TAG = "JsonAssetUtils";

    public static <T> List<T> loadListFromJsonAsset(Context context, String fileName, Class<T> itemType) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Error reading " + fileName + " from assets", ex);
            return Collections.emptyList(); // Return empty list on error
        }

        try {
            Gson gson = new Gson();
            // We need to construct a Type for List<T> for Gson
            Type listType = TypeToken.getParameterized(List.class, itemType).getType();
            return gson.fromJson(jsonString, listType);
        } catch (Exception ex) {
            Log.e(TAG, "Error parsing JSON from " + fileName, ex);
            return Collections.emptyList(); // Return empty list on error
        }
    }
}
