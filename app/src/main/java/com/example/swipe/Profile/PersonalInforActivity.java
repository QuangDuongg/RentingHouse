package com.example.swipe.Profile;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;

public class PersonalInforActivity extends AppCompatActivity {

    private static final String TAG = "PersonalInfoActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView editProfileImage;
    private ImageButton buttonAddImage; // Thêm biến cho ImageButton
    private EditText editUsername;
    private Button saveChangesButton;
    private String userId;
    private DatabaseReference userRef;
    private EditText editDob, editGender;
    private TextView textEmail;
    private StorageReference storageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_infor);

        editProfileImage = findViewById(R.id.edit_profile_image);
        buttonAddImage = findViewById(R.id.button_add_image); // Gán ImageButton từ XML
        editUsername = findViewById(R.id.edit_username);
        saveChangesButton = findViewById(R.id.button_save_changes);
        editDob = findViewById(R.id.edit_dob);
        editGender = findViewById(R.id.edit_gender);
        textEmail = findViewById(R.id.text_email);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference().child("avatar").child(userId);

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        // Load existing user info
        loadUserInfo();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            editProfileImage.setImageURI(imageUri);
        }
    }

    private void loadUserInfo() {
        // Truy cập trực tiếp đến các thuộc tính của user
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String dob = snapshot.child("dob").getValue(String.class);
                String gender = snapshot.child("gender").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                if (username != null) {
                    editUsername.setText(username);
                }
                if (dob != null) {
                    editDob.setText(dob);
                }
                if (gender != null) {
                    editGender.setText(gender);
                }
                if (email != null) {
                    textEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load user info", error.toException());
            }
        });

        // Load profile image nếu có
        storageRef.child("avatar.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(PersonalInforActivity.this).load(uri).into(editProfileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to load profile image", e);
            }
        });
    }

    private void saveUserInfo() {
        String newUsername = editUsername.getText().toString().trim();
        String newDob = editDob.getText().toString().trim();
        String newGender = editGender.getText().toString().trim();

        if (!newUsername.isEmpty()) {
            userRef.child("username").setValue(newUsername);
        }
        if (!newDob.isEmpty()) {
            userRef.child("dob").setValue(newDob);
        }
        if (!newGender.isEmpty()) {
            userRef.child("gender").setValue(newGender);
        }

        if (imageUri != null) {
            StorageReference fileReference = storageRef.child("avatar.jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PersonalInforActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    goBackToPreviousScreen();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PersonalInforActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
            goBackToPreviousScreen();
        }
    }

    private void goBackToPreviousScreen() {
        // Quay lại màn hình trước đó
        Intent intent = new Intent(PersonalInforActivity.this, ProfileUser.class);
        startActivity(intent);
        finish();
    }
}
