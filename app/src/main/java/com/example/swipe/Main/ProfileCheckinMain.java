package com.example.swipe.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ProfileCheckinMain extends AppCompatActivity {

    private Context mContext;
    ArrayList<String> profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_checkin_main);

        mContext = ProfileCheckinMain.this;

        TextView DPD = findViewById(R.id.DPD_beforematch);
        TextView profileDistrict = findViewById(R.id.District_main);
        TextView profileAddress = findViewById(R.id.address_beforematch);
        TextView profilePrice = findViewById(R.id.price_beforematch);
        TextView profileDistance = findViewById(R.id.distance_main);


        Intent intent = getIntent();
        String district = intent.getStringExtra("district");
        String address = intent.getStringExtra("address");
        int price = intent.getIntExtra("price", 1000);
        int distance = intent.getIntExtra("distance", 2);
        String description;
        description = intent.getStringExtra("DPD");
        if(!Objects.equals(description, "No description"))
            description = "Description: " + description;

        DPD.setText(description);
        profileDistance.setText(String.valueOf(distance) + " Km away");
        profileDistrict.setText(district);
        profileAddress.setText("Address: " + address);
        profilePrice.setText("Price: " + SearchFilter.getInstance().ManipPrice(price));



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
