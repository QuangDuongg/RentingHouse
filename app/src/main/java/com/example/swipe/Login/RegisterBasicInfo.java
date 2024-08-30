package com.example.swipe.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swipe.Main.MainActivity;
import com.example.swipe.Mode.HostMode; // Import HostModeActivity
import com.example.swipe.Mode.TenantMode; // Import TenantModeActivity
import com.example.swipe.R;
import com.example.swipe.Matched.User_Copy;
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
    private Button btnRegister;
    private RadioGroup radioGroupRole;
    private RadioButton radioHost, radioTenant;
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
        if (email.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Invalid email address, enter a valid email id and click on Continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserInformation(String userId, String email, String username) {
        String role;
        if (radioHost.isChecked()) {
            role = "host";
        } else if (radioTenant.isChecked()) {
            role = "tenant";
        } else {
            role = "tenant"; // giá trị mặc định nếu không chọn gì
        }
        String defaultGender = "Not Specified";
        String defaultProfileImageUrl = "default_profile_image_url";
        String defaultDob = "01/01/2000";
        User_Copy user = new User_Copy(userId, username, email, defaultProfileImageUrl, defaultDob, defaultGender, role);
        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "User registered successfully!", Toast.LENGTH_SHORT).show();
                            navigateToRoleActivity(role);
                        } else {
                            Toast.makeText(mContext, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void navigateToRoleActivity(String role) {
        if (role.equals("host")) {
            Intent intent = new Intent(RegisterBasicInfo.this, HostMode.class);
            startActivity(intent);
        } else if (role.equals("tenant")) {
            Intent intent = new Intent(RegisterBasicInfo.this, TenantMode.class);
            startActivity(intent);
        }
        finish();
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets");
        mEmail = findViewById(R.id.input_email);
        mUsername = findViewById(R.id.input_username);
        mPassword = findViewById(R.id.input_password);
        btnRegister = findViewById(R.id.btn_register);
        radioGroupRole = findViewById(R.id.radio_group_role);
        radioHost = findViewById(R.id.radio_host);
        radioTenant = findViewById(R.id.radio_tenant);
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
