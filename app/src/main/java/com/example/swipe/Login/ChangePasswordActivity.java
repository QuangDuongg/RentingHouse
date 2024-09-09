package com.example.swipe.Login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText mOldPassword, mNewPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mOldPassword = findViewById(R.id.input_old_password);
        mNewPassword = findViewById(R.id.input_new_password);
        Button btnChangePassword = findViewById(R.id.btn_change_password);

        // Nút để thay đổi mật khẩu
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = mOldPassword.getText().toString().trim();
                String newPassword = mNewPassword.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please enter your old password and new password in full.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Xác thực mật khẩu cũ và thay đổi mật khẩu
                changePassword(oldPassword, newPassword);
            }
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            // Re-authenticate người dùng để đảm bảo mật khẩu cũ đúng
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Mật khẩu cũ đã xác thực thành công, tiến hành thay đổi mật khẩu
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Password has been changed successfully.", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại màn hình trước đó
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Password change failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
