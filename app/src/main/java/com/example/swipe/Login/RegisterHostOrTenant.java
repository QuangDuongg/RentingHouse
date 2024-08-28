package com.example.swipe.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swipe.Main.MainActivity;
import com.example.swipe.R;
import com.example.swipe.Utils.User;

public class RegisterHostOrTenant extends AppCompatActivity {

    String password;
    User user;
    boolean host = true;
    private Button genderContinueButton;
    private Button hostSelectionButton;
    private Button tenantSelectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_host_or_tenant);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        hostSelectionButton = findViewById(R.id.hostSelectionButton);
        tenantSelectionButton = findViewById(R.id.tenantSelectionButton);
        genderContinueButton = findViewById(R.id.genderContinueButton);

        //By default host has to be selected so below code is added

        tenantSelectionButton.setAlpha(.5f);
        tenantSelectionButton.setBackgroundColor(Color.GRAY);


        hostSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostButtonSelected();
            }
        });

        tenantSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenantButtonSelected();
            }
        });

        genderContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreferenceEntryPage();
            }
        });

    }

    public void hostButtonSelected() {
        host = true;
        hostSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
        hostSelectionButton.setAlpha(1.0f);
        tenantSelectionButton.setAlpha(.5f);
        tenantSelectionButton.setBackgroundColor(Color.GRAY);
    }

    public void tenantButtonSelected() {
        host = false;
        tenantSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
        tenantSelectionButton.setAlpha(1.0f);
        hostSelectionButton.setAlpha(.5f);
        hostSelectionButton.setBackgroundColor(Color.GRAY);
    }

    public void openPreferenceEntryPage() {

        String ownSex = host ? "host" : "tenant";
        user.setSex(ownSex);
        //set default photo
        String defaultPhoto = host ? "defaulthost" : "defaulttenant";
        user.setProfileImageUrl(defaultPhoto);

        //----------------------------------------Firebase----------------------------------------
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("password", password);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }
}