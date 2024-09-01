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

import com.bumptech.glide.Glide;
import com.example.swipe.R;
import com.example.swipe.Utils.SearchFilter;

import java.util.ArrayList;
import java.util.List;


public class ProfileCheckinMain extends AppCompatActivity {

    private Context mContext;
    ArrayList<String> profileImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_checkin_main);

        mContext = ProfileCheckinMain.this;

       /* ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/

        TextView profileDistrict = findViewById(R.id.District_main);
        ImageView profileImage = findViewById(R.id.profileImage);
        TextView profileAddress = findViewById(R.id.address_beforematch);
        TextView profilePrice = findViewById(R.id.price_beforematch);
        TextView profileDistance = findViewById(R.id.distance_main);

        Intent intent = getIntent();
        String district = intent.getStringExtra("district");
        String address = intent.getStringExtra("address");
        int price = intent.getIntExtra("price", 1000);
        int distance = intent.getIntExtra("distance", 2);
        // lack of photos

        Log.d("ProfileCheckinMain", "Check Distance");
        profileDistance.setText(String.valueOf(distance) + " Km away");
        Log.d("ProfileCheckinMain", "Check District");
        profileDistrict.setText(district);
        Log.d("ProfileCheckinMain", "Check Address");
        profileAddress.setText(address);
        Log.d("ProfileCheckinMain", "Check Price");
        Log.d("ProfileCheckinMain", SearchFilter.getInstance().ManipPrice(price));
        profilePrice.setText(SearchFilter.getInstance().ManipPrice(price));
        Log.d("ProfileCheckinMain", "Check Photo");
        profileImageUrl = intent.getStringArrayListExtra("photo");
        switch (profileImageUrl.get(0)) {
            case "defaultRoom":
                Glide.with(mContext).load(R.drawable.default_man).into(profileImage);
                break;
            default:
                Glide.with(mContext).load(profileImageUrl).into(profileImage);
                break;
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
