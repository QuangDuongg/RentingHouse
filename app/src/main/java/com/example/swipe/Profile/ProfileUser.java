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
import com.example.swipe.Login.ChangePasswordActivity;
import com.example.swipe.Login.Login;
import com.example.swipe.Main.MainActivity;
import com.example.swipe.Message.MessageActivity;
import com.example.swipe.Mode.HostMode;
import com.example.swipe.Mode.TenantMode;
import com.example.swipe.R;
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
    private TextView usernameText, email_text;
    private Button personalInfoButton, logoutButton, backtomain,changepassword;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private String userId;
    private String userRole; // Biến lưu trữ vai trò của người dùng (host hoặc tenant)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Initialize UI elements
        email_text = findViewById(R.id.email_text);
        backtomain = findViewById(R.id.btn_backtomain);
        profileImage = findViewById(R.id.profile_image);
        usernameText = findViewById(R.id.username_text);
        changepassword=findViewById(R.id.button2);
        personalInfoButton = findViewById(R.id.button_personal_info);
        logoutButton = findViewById(R.id.button_logout);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Load the user profile information
        loadUserProfile();

        // Personal information button click listener
        personalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUser.this, PersonalInforActivity.class);
                startActivity(intent);
            }
        });
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUser.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // Logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileUser.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Back to main button click listener
        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserRoleAndNavigate();
            }
        });
    }

    // Tải thông tin người dùng (username, email, profile image)
    private void loadUserProfile() {
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
                Log.e(TAG, "Failed to load email", error.toException());
            }
        });

        userRef.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageUrl = snapshot.getValue(String.class);
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    // Dùng Glide với circleCrop() để hiển thị ảnh hình tròn
                    Glide.with(ProfileUser.this)
                            .load(profileImageUrl)
                            .circleCrop() // Làm cho ảnh thành hình tròn
                            .placeholder(R.drawable.profile) // Ảnh mặc định nếu không có URL
                            .into(profileImage);
                } else {
                    // Ảnh mặc định nếu không có ảnh từ Firebase
                    profileImage.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load profile image URL", error.toException());
                profileImage.setImageResource(R.drawable.profile);
            }
        });

    }

    // Kiểm tra vai trò người dùng và điều hướng
    private void checkUserRoleAndNavigate() {
        userRef.child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRole = snapshot.getValue(String.class);
                if ("host".equals(userRole)) {
                    Intent intent = new Intent(ProfileUser.this, HostMode.class);
                    startActivity(intent);
                } else if ("tenant".equals(userRole)) {
                    Intent intent = new Intent(ProfileUser.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileUser.this, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
