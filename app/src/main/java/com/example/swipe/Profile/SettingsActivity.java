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
    List<SwitchCompat> location;
    TextView distance_text, budget_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "Check Init");

        SearchFilter searchFilter = SearchFilter.getInstance();

        // Set toolbar text
        TextView toolbar = findViewById(R.id.toolbartag);
        toolbar.setText("Search criteria");

        // Back button click listener
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize man/woman switches
        man = findViewById(R.id.switch_man);
        woman = findViewById(R.id.switch_woman);

        // Set man/woman switches according to SearchFilter state
        man.setChecked(searchFilter.isForMan());
        woman.setChecked(searchFilter.isForWoman());

        // Man switch listener
        man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchFilter.setForMan(isChecked);
            }
        });

        // Woman switch listener
        woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchFilter.setForWoman(isChecked);
            }
        });

        // Initialize location switches
        location = new ArrayList<>(13);
        location.add(findViewById(R.id.switch_All));

        for (int i = 1; i <= 12; i++) {
            int resID = getResources().getIdentifier("switch_District" + i, "id", getPackageName());
            SwitchCompat switchCompat = findViewById(resID);
            location.add(switchCompat);

            // Set initial checked state based on SearchFilter
            switchCompat.setChecked(searchFilter.getIsDistrictIndex(i));

            // Set OnCheckedChangeListener
            final int index = i;
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    searchFilter.setIsDistrictIndex(index, isChecked);
                }
            });
        }

        // "Select All" switch listener
        location.get(0).setChecked(false); // Ensure it's unchecked initially
        location.get(0).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 1; i < location.size(); i++) {
                        searchFilter.setIsDistrictIndex(i, true);
                        location.get(i).setChecked(true);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            location.get(0).setChecked(false);
                        }
                    }, 500);
                }
            }
        });

        // Initialize distance SeekBar and text
        distance = findViewById(R.id.distance);
        distance_text = findViewById(R.id.distance_text);
        distance.setProgress((int) searchFilter.getMaxDistance());
        distance_text.setText((int) searchFilter.getMaxDistance() + " Km");

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance_text.setText(progress + " Km");
                searchFilter.setMaxDistance(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Initialize budget SeekBar and text
        budget = findViewById(R.id.budget);
        budget_text = findViewById(R.id.budget_text);
        budget.setProgress((int) searchFilter.getBudget());
        budget_text.setText(searchFilter.ManipPrice((int) searchFilter.getBudget()));

        budget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int bound_progress = progress - progress % 100;
                String manip_budget_text = searchFilter.ManipPrice(bound_progress);
                budget_text.setText(manip_budget_text);
                searchFilter.setBudget(bound_progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void Logout(View view) {
        startActivity(new Intent(getApplicationContext(), IntroductionMain.class));
        finish();
    }
}

