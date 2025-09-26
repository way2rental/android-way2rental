package com.example.way2rental.api.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    // The timestamp format from your JSON: "2025-09-25T10:43:16.028"
    // This is compatible with ISO_LOCAL_DATE_TIME
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        String dateString = json.getAsString();
        try {
            return LocalDateTime.parse(dateString, FORMATTER);
        } catch (DateTimeParseException e) {
            // Optional: Log the error or handle it more gracefully
            // android.util.Log.e("LocalDateTimeDeserializer", "Failed to parse date: " + dateString, e);
            throw new JsonParseException("Failed to parse LocalDateTime: " + dateString, e);
        }
    }
}
