package com.example.swipe.Mode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swipe.Message.MessageActivity;
import com.example.swipe.Profile.ProfileUser;
import com.example.swipe.R;

public class HostMode extends AppCompatActivity {

    private static final String TAG = "HostMode";
    private Button buttonAddRoom;
    private Button btnprofile,btn_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_mode);
        btn_message=findViewById(R.id.button_message);
        buttonAddRoom = findViewById(R.id.button_add_room);
        btnprofile=findViewById(R.id.button_profile);
        buttonAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add Room button clicked");
                Intent intent = new  Intent(HostMode.this, AddRoomActivity.class);
                startActivity(intent);
            }
        });
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Profile Room button clicked");
                Intent intent = new Intent(HostMode.this, ProfileUser.class);
                startActivity(intent);
            }
        });
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostMode.this, MessageActivity.class);
                startActivity(intent);
            }
        });
    }
}
