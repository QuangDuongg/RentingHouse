package com.example.swipe.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swipe.Mode.HostMode; // Chắc chắn rằng bạn đã có activity này
import com.example.swipe.Mode.TenantMode; // Chắc chắn rằng bạn đã có activity này
import com.example.swipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Context mContext;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mContext = Login.this;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        init();
    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");
        return string.equals("");
    }

    private void init() {
        // Initialize the button for logging in
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) || isStringNull(password)) {
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Sign in with Firebase Authentication
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, now check the user's role
                                        Log.d(TAG, "signInWithEmail:success");
                                        String userId = mAuth.getCurrentUser().getUid();
                                        checkUserRole(userId);
                                    } else {
                                        // If sign in fails, display a message to the user
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(mContext, "Authentication failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        TextView linkSignUp = findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(Login.this, RegisterBasicInfo.class);
                startActivity(intent);
            }
        });
    }

    private void checkUserRole(String userId) {
        mDatabase.child("users").child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.getValue(String.class);
                    if (role.equals("host")) {
                        Intent intent = new Intent(Login.this, HostMode.class);
                        startActivity(intent);
                        finish();
                    } else if (role.equals("tenant")) {
                        Intent intent = new Intent(Login.this, TenantMode.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(mContext, "Role not recognized. Please contact support.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Failed to retrieve user role. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUserRole:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
