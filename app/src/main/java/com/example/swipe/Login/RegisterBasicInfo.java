package com.example.swipe.Login;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swipe.Main.MainActivity;
import com.example.swipe.R;
import com.example.swipe.Utils.GPS;
import com.example.swipe.Utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterBasicInfo extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Context mContext;
    private String email, username, password;
    private EditText mEmail, mPassword, mUsername;
    private TextView loadingPleaseWait;
    private Button btnRegister;
    private String append = "";
    private GPS gps;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started");
        setContentView(R.layout.activity_registerbasic_info);
        mContext = RegisterBasicInfo.this;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        gps = new GPS(getApplicationContext());

        initWidgets();
        init();
    }

    private void init() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                if (checkInputs(email, username, password)) {
                    // Đăng ký người dùng với Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterBasicInfo.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Đăng ký thành công, lấy user ID
                                        String userId = mAuth.getCurrentUser().getUid();
                                        saveUserInformation(userId, email, username);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        Toast.makeText(mContext, "Registration successfully. " , Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Nếu đăng ký thất bại, hiển thị thông báo lỗi
                                        Toast.makeText(mContext, "Registration failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (email.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(mContext, "All fields must be filed out.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra định dạng email
        if (!email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Invalid email address, enter valid email id and click on Continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserInformation(String userId, String email, String username) {
        // Tìm vị trí địa lý của người dùng
        Log.d(TAG,"HAHAHA");
        Location location = gps.getLocation();
        double latitude = 37.349642; // Giá trị mặc định
        double longitude = -121.938987; // Giá trị mặc định
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        Log.d("Location==>", longitude + "   " + latitude);

        // Tạo đối tượng người dùng với thông tin chi tiết
        User user = new User("", "", "", "", email, username, false, false, false, false, "", "", "", latitude, longitude);

        // Lưu thông tin người dùng vào Firebase Realtime Database
        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"HAHAHA1");
                            Toast.makeText(mContext, "User registered successfully!", Toast.LENGTH_SHORT).show();
                            // Điều hướng tới màn hình tiếp theo (ví dụ: chọn vai trò)
                            Intent intent = new Intent(RegisterBasicInfo.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(mContext, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets");
        mEmail = findViewById(R.id.input_email);
        mUsername = findViewById(R.id.input_username);
        btnRegister = findViewById(R.id.btn_register);
        mPassword = findViewById(R.id.input_password);
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
