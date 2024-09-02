package com.example.swipe.Mode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swipe.Profile.ProfileUser;
import com.example.swipe.R;

public class HostMode extends AppCompatActivity {

    private static final String TAG = "HostMode";
    private Button buttonAddRoom;
    private Button btnprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_mode);

        buttonAddRoom = findViewById(R.id.button_add_room);
        btnprofile=findViewById(R.id.button_profile);
        buttonAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add Room button clicked");
                Intent intent = new  Intent(HostMode.this, AddRoomActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Profile Room button clicked");
                Intent intent = new Intent(HostMode.this, ProfileUser.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
