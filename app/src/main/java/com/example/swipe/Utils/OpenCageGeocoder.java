package com.example.swipe.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class OpenCageGeocoder {

    private static final String API_KEY = "49553b80a1694456a3d0c5bbf4f4dc33"; // Replace with your OpenCage API key
    private static final String BASE_URL = "https://api.opencagedata.com/geocode/v1/json";

    // Singleton instance
    private static OpenCageGeocoder instance = null;
    private OkHttpClient client;

    // Private constructor prevents instantiation from other classes
    private OpenCageGeocoder() {
        client = new OkHttpClient(); // Initialize OkHttp client
    }

    // Static method to get the single instance of OpenCageGeocoder
    public static synchronized OpenCageGeocoder getInstance() {
        if (instance == null) {
            instance = new OpenCageGeocoder();
        }
        return instance;
    }

    // Method to search location and get latitude/longitude
    public void searchLocation(String address, GeocodingCallback callback) {
        try {
            // Build the URL for the API request
            String url = BASE_URL + "?q=" + address + "&key=" + API_KEY;

            // Create the request
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            // Make the asynchronous API call
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray resultsArray = jsonObject.getJSONArray("results");

                            if (resultsArray.length() > 0) {
                                JSONObject result = resultsArray.getJSONObject(0);
                                JSONObject geometry = result.getJSONObject("geometry");

                                double latitude = geometry.getDouble("lat");
                                double longitude = geometry.getDouble("lng");

                                // Send the latitude and longitude to the callback
                                callback.onSuccess(latitude, longitude);
                            } else {
                                callback.onFailure("No results found.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onFailure(e.getMessage());
                        }
                    } else {
                        callback.onFailure("Request failed: " + response.message());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e.getMessage());
        }
    }

    // Callback interface for the response
    public interface GeocodingCallback {
        void onSuccess(double latitude, double longitude);
        void onFailure(String errorMessage);
    }
}
