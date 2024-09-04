package com.example.swipe.Profile;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swipe.Introduction.IntroductionMain;
import com.example.swipe.R;
import com.example.swipe.Utils.SearchFilter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    SeekBar distance, budget;
    SwitchCompat man, woman;
    List <SwitchCompat> location;
    TextView distance_text, budget_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "Check Init");

        Log.d(TAG, "Check toolbar");
        SearchFilter searchFilter = SearchFilter.getInstance();
        TextView toolbar = findViewById(R.id.toolbartag);
        toolbar.setText("Profile");
        Log.d(TAG, "Check back btn");
        ImageButton back = findViewById(R.id.back);
        Log.d(TAG, "Check man/woman bar");
        man = findViewById(R.id.switch_man);
        woman = findViewById(R.id.switch_woman);
        Log.d(TAG, "Check create Array");
        location = new ArrayList<>(13);
        location.add(findViewById(R.id.switch_All));

        Log.d(TAG, "Check find view");
        for (int i = 1; i <= 12; i++) {
            // Dynamically get the resource ID
            int resID = getResources().getIdentifier("switch_District" + i, "id", getPackageName());

            // Find the SwitchCompat by its ID and add it to the list
            SwitchCompat switchCompat = findViewById(resID);
            location.add(switchCompat);

            // Set an OnCheckedChangeListener
            final int index = i; // Capture the current value of i
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    searchFilter.setIsDistrictIndex(index, isChecked);
                    if (isChecked) {
                        location.get(index).setChecked(true);
                    }
                }
            });
        }
        Log.d(TAG, "Check array location");
        location.get(0).setChecked(false);
        location.get(0).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i = 0; i < location.size(); i++)
                    {
                        if(i >= 1)
                            searchFilter.setIsDistrictIndex(i, true);
                        location.get(i).setChecked(true);
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to execute after the delay
                            location.get(0).setChecked(false);
                        }
                    }, 500);

                }
            }
        });

        Log.d(TAG, "Check location ALL");

        distance = findViewById(R.id.distance);
        budget = findViewById(R.id.budget);
        distance_text = findViewById(R.id.distance_text);
        budget_text = findViewById(R.id.budget_text);

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance_text.setText(progress + " Km");
                searchFilter.setMaxDistance(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Log.d(TAG, "Check distance bar");
        budget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int bound_progress = progress - progress % 100;
                String manip_budget_text = searchFilter.ManipPrice(bound_progress);
                budget_text.setText(manip_budget_text);
                searchFilter.setBudget(bound_progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Log.d(TAG, "Check budget bar");
        man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchFilter.setForMan(isChecked);
                if (isChecked) {
                    man.setChecked(true);
                }
                else searchFilter.setForMan(false);
            }
        });
        woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchFilter.setForWoman(isChecked);
                if (isChecked) {
                    woman.setChecked(true);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void Logout(View view) {
        startActivity(new Intent(getApplicationContext(), IntroductionMain.class));
        finish();

    }


}
