package com.example.swipe.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.swipe.Login.Login;
import com.example.swipe.Mode.HostMode;
import com.example.swipe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileUser extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ImageView profileImage;
    private TextView usernameText,email_text;
    private Button personalInfoButton, logoutButton,backtomain;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        email_text=findViewById(R.id.email_text);
        backtomain=findViewById(R.id.btn_backtomain);
        profileImage = findViewById(R.id.profile_image);
        usernameText = findViewById(R.id.username_text);
        personalInfoButton = findViewById(R.id.button_personal_info);
        logoutButton = findViewById(R.id.button_logout);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference().child("avatar").child(userId);

        loadUserProfile();

        personalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUser.this, PersonalInforActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileUser.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUser.this, HostMode.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadUserProfile() {
        // Load username
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username != null) {
                    usernameText.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load username", error.toException());
            }
        });
        userRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.getValue(String.class);
                if (email != null) {
                    email_text.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load username", error.toException());
            }
        });

        // Load profile image
        storageRef.child("avatar.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileUser.this).load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to load profile image", e);
            }
        });
    }
}
