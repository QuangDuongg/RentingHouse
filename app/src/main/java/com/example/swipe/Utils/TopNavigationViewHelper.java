package com.example.swipe.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.swipe.Main.MainActivity;
import com.example.swipe.Matched.Matched_Activity;
import com.example.swipe.Profile.Profile_Activity;
import com.example.swipe.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class TopNavigationViewHelper {

    private static final String TAG = "TopNavigationViewHelper";

    public static void setupTopNavigationView(BottomNavigationView tv) {
        Log.d(TAG, "setupTopNavigationView: setting up navigationview");


    }

    public static void enableNavigation(final Context context, BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.ic_profile) {
                    Intent intent2 = new Intent(context, Profile_Activity.class);
                    context.startActivity(intent2);
                } else if (id == R.id.ic_main) {
                    Intent intent1 = new Intent(context, MainActivity.class);
                    context.startActivity(intent1);
                } else if (id == R.id.ic_matched) {
                    Intent intent3 = new Intent(context, Matched_Activity.class);
                    context.startActivity(intent3);
                }
                return false;
            }
        });
    }

}
