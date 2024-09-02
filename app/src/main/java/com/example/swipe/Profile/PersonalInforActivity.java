package com.example.swipe.Profile;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
    private EditText editUsername;
    private Button saveChangesButton;
    private String userId;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_infor);

        editProfileImage = findViewById(R.id.edit_profile_image);
        editUsername = findViewById(R.id.edit_username);
        saveChangesButton = findViewById(R.id.button_save_changes);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference().child("avatar").child(userId);

        editProfileImage.setOnClickListener(new View.OnClickListener() {
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
        // Load username
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username != null) {
                    editUsername.setText(username);
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
        if (!newUsername.isEmpty()) {
            userRef.child("username").setValue(newUsername);
            Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show();
        }

        if (imageUri != null) {
            StorageReference fileReference = storageRef.child("avatar.jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PersonalInforActivity.this, "Avatar updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PersonalInforActivity.this, "Failed to update avatar", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
