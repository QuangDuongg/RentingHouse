package com.example.swipe.Profile;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swipe.Introduction.IntroductionMain;
import com.example.swipe.R;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    SeekBar distance;
    SwitchCompat man, woman;
    TextView gender, distance_text, age_rnge;
    List <SwitchCompat> location;
    TextView distance_text, budget_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        TextView toolbar = findViewById(R.id.toolbartag);
        toolbar.setText("Profile");
        ImageButton back = findViewById(R.id.back);
        distance = findViewById(R.id.distance);
        man = findViewById(R.id.switch_man);
        woman = findViewById(R.id.switch_woman);

        location = new ArrayList<>(13);
        location.add(findViewById(R.id.switch_All));
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
                    if (isChecked) {
                        location.get(index).setChecked(true);
                    }
                }
            });
        }

        location.get(0).setChecked(false);
        location.get(0).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i = 0; i < location.size(); i++)
                    {
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



        distance = findViewById(R.id.distance);
        budget = findViewById(R.id.budget);
        distance_text = findViewById(R.id.distance_text);

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance_text.setText(progress + " Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    man.setChecked(true);
                }
            }
        });
        woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
