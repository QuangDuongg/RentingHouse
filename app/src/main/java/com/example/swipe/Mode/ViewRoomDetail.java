package com.example.swipe.Mode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewRoomDetail extends AppCompatActivity {

    private Context mContext;
    ArrayList<String> profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_detail);

        mContext = com.example.swipe.Mode.ViewRoomDetail.this;

        EditText DPD = findViewById(R.id.DPD_beforematch);
        EditText profileDistrict = findViewById(R.id.District_main);
        EditText profileAddress = findViewById(R.id.address_beforematch);
        EditText profilePrice = findViewById(R.id.price_beforematch);
        TextView profileDistance = findViewById(R.id.distance_main);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        Intent intent = getIntent();
        String district = intent.getStringExtra("district");
        String address = intent.getStringExtra("address");
        int price = intent.getIntExtra("price", 1000);
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

        profilePrice.setText(pricePrefix + SearchFilter.getInstance().ManipPrice(price));

        profilePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the "Price: " prefix and show only the price
                if (profilePrice.getText().toString().startsWith(pricePrefix)) {
                    profilePrice.setText(String.valueOf(price));  // Show only the price value
                    profilePrice.setHint(priceHint);  // Show the hint when clicked
                }
            }
        });

        profilePrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // When focus is lost, check if the price is empty
                    String priceText = profilePrice.getText().toString().trim();

                    // Add the "Price: " prefix again if it's not empty and doesn't already start with the prefix
                    if (!priceText.isEmpty() && !priceText.startsWith(pricePrefix)) {
                        profilePrice.setText(pricePrefix + SearchFilter.getInstance().ManipPrice(price));
                        profilePrice.setHint("");  // Clear the hint after editing is done
                    }
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

    }

    public void DislikeBtn(View v) {
        Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
        btnClick.putStringArrayListExtra("url", profileImageUrl);
        startActivity(btnClick);
    }

    public void LikeBtn(View v) {
        Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
        btnClick.putStringArrayListExtra("url", profileImageUrl);
        startActivity(btnClick);
    }
}
