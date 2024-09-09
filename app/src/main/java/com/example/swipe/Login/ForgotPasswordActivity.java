package com.example.swipe.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.input_email);
        Button btnResetPassword = findViewById(R.id.btn_reset_password);

        // Gửi liên kết đặt lại mật khẩu
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "A password reset link has been sent to your email.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, Login.class));
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Link sending failed. Please check your email again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    public void onLoginClicked(View view) {
        startActivity(new Intent(ForgotPasswordActivity.this, Login.class));  // Sửa lại context đúng
    }

    public void onRegisterClicked(View view) {
        startActivity(new Intent(ForgotPasswordActivity.this, RegisterBasicInfo.class));  // Sửa lại context đúng
    }

}
