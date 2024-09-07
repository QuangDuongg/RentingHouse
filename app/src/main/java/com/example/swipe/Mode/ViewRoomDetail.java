package com.example.swipe.Mode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swipe.Main.BtnDislikeActivity;
import com.example.swipe.Main.BtnLikeActivity;
import com.example.swipe.R;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.swipe.R;
import com.example.swipe.Utils.ImagePagerAdapter;
import com.example.swipe.Utils.SearchFilter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewRoomDetail extends AppCompatActivity {

    private Context mContext;
    ArrayList<String> profileImageUrl;

    // Firebase Variable
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference roomRef;
    private int index;

    private EditText DPD, profileDistrict, profileAddress, profilePrice;
    private int price;
    private ImageButton saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_detail);

        mContext = com.example.swipe.Mode.ViewRoomDetail.this;

        DPD = findViewById(R.id.DPD_beforematch);
        profileDistrict = findViewById(R.id.District_main);
        profileAddress = findViewById(R.id.address_beforematch);
        profilePrice = findViewById(R.id.price_beforematch);
        TextView profileDistance = findViewById(R.id.distance_main);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton = findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChange();
            }
        });

        Intent intent = getIntent();
        String district = intent.getStringExtra("district");
        String address = intent.getStringExtra("address");
        price = intent.getIntExtra("price", 1000);
        double distance = intent.getDoubleExtra("distance", 1.0);
        String description;
        description = intent.getStringExtra("DPD");
        if(!Objects.equals(description, "No description"))
            description = "Description: " + description;


        DPD.setText(description);
        DPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(DPD.getText()).equals("No description")) {
                    DPD.setText("");  // Clear the text
                }
            }
        });
        DPD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Check if the EditText is empty when focus is lost
                    if (DPD.getText().toString().trim().isEmpty()) {
                        DPD.setText("No description");  // Restore "No description"
                    }
                }
            }
        });
        profileDistance.setText(String.valueOf(distance) + " Km away");
        profileDistrict.setText(district);
        profileAddress.setText("Address: " + address);
        profileAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the "Address: " prefix and show only the address
                if (profileAddress.getText().toString().startsWith("Address: ")) {
                    profileAddress.setText(profileAddress.getText().toString().replace("Address: ", ""));
                }
            }
        });

        profileAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // When focus is lost, check if the address is empty
                    String addressText = profileAddress.getText().toString().trim();

                    // Add the "Address: " prefix again
                    if (!addressText.isEmpty() && !addressText.startsWith("Address: ")) {
                        profileAddress.setText("Address: " + addressText);
                    }
                }
            }
        });
        final String pricePrefix = "Price: ";  // Define the prefix
        final String priceHint = "(in 1000 VND unit)";  // Define the hint text

        // Initial setup with the formatted price
        profilePrice.setText(pricePrefix + SearchFilter.getInstance().ManipPrice(price));

        profilePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = profilePrice.getText().toString();
                // Remove the "Price: " prefix and show only the price
                if (currentText.startsWith(pricePrefix)) {
                    // Only remove the prefix if it exists
                    profilePrice.setText(String.valueOf(price));  // Show only the price value
                }
                profilePrice.setHint(priceHint);  // Show the hint when clicked
            }
        });

        profilePrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // When focus is lost, get the current text and trim spaces
                    String priceText = profilePrice.getText().toString().trim();

                    // Check if the price is not empty and does not start with the prefix
                    if (!priceText.isEmpty() && !priceText.startsWith(pricePrefix)) {
                        try {
                            // Parse the price
                            int updatedPrice = Integer.parseInt(priceText);
                            price = updatedPrice;  // Update the price variable

                            // Set the formatted text with the prefix
                            String formattedPrice = pricePrefix + SearchFilter.getInstance().ManipPrice(updatedPrice);
                            profilePrice.setText(formattedPrice);  // Update the EditText with formatted price

                            // Clear focus after updating text (this ensures focus loss is final)
                            profilePrice.clearFocus();

                        } catch (NumberFormatException e) {
                            Toast.makeText(ViewRoomDetail.this, "Invalid price format.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (priceText.isEmpty()) {
                        // Handle the case where the price is empty
                        profilePrice.setText(pricePrefix + "0");
                    }
                    profilePrice.setHint("");  // Clear the hint after editing is done
                } else {
                    // If gaining focus, show the hint
                    profilePrice.setHint(priceHint);
                }
            }
        });

        profileImageUrl = intent.getStringArrayListExtra("photo");


        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            ViewPager2 viewPager = findViewById(R.id.profileImage);
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, profileImageUrl);
            viewPager.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No images available", Toast.LENGTH_SHORT).show();
        }

        index = intent.getIntExtra("indexRoom", 0);

    }

    public void saveChange() {
        // Get the Firebase reference for the current room
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(String.valueOf(index));
        Log.d("ViewRoomDetail","Enter and change in room: " + index );
        // Capture the updated values from the fields
        String updatedDescription = DPD.getText().toString().replace("Description: ", "").trim();
        if (updatedDescription.equals("No description")) {
            updatedDescription = null;  // Set to null if "No description"
        }
        Log.d("ViewRoomDetail","updatedDescription: " + updatedDescription);
        String updatedAddress = profileAddress.getText().toString().replace("Address: ", "").trim();
        Log.d("ViewRoomDetail","updatedAddress: " + updatedAddress);
        String updatedDistrict = profileDistrict.getText().toString().trim();

        // Update each field individually, with logging
        roomRef.child("description").setValue(updatedDescription).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ViewRoomDetail", "Description updated successfully");
            } else {
                Log.e("ViewRoomDetail", "Failed to update description", task.getException());
            }
        });

        roomRef.child("address").setValue(updatedAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ViewRoomDetail", "Address updated successfully");
            } else {
                Log.e("ViewRoomDetail", "Failed to update address", task.getException());
            }
        });

        roomRef.child("district").setValue(updatedDistrict).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ViewRoomDetail", "District updated successfully");
            } else {
                Log.e("ViewRoomDetail", "Failed to update district", task.getException());
            }
        });

        roomRef.child("price").setValue(String.valueOf(price)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ViewRoomDetail", "Price updated successfully");
            } else {
                Log.e("ViewRoomDetail", "Failed to update price", task.getException());
            }
        });

        // Wait for all updates to complete before finishing the activity
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ViewRoomDetail", "Data saved successfully, closing activity");
                finish();  // Finish the activity only when data is saved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ViewRoomDetail", "Error updating data", error.toException());
                Toast.makeText(ViewRoomDetail.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {

        saveChange();
        super.onBackPressed();  // Call the default behavior
    }


}