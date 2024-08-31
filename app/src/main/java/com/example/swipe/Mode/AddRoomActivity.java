package com.example.swipe.Mode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddRoomActivity extends AppCompatActivity {

    private static final String TAG = "AddRoomActivity";
    private static final int PICK_IMAGES_REQUEST = 1;

    private Spinner spinnerDistrict;
    private EditText editTextPrice, editTextAddress;
    private Button buttonChooseImages, buttonDone;
    private RecyclerView recyclerView;

    private List<Uri> imageUriList;
    private ImageAdapter imageAdapter;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth mAuth;
    private int roomCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        spinnerDistrict = findViewById(R.id.spinner_district);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextAddress = findViewById(R.id.edit_text_address);
        buttonChooseImages = findViewById(R.id.button_choose_images);
        buttonDone = findViewById(R.id.button_done);
        recyclerView = findViewById(R.id.recycler_view);
        mAuth = FirebaseAuth.getInstance();
        imageUriList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUriList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageAdapter);
        spinnerDistrict = findViewById(R.id.spinner_district);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.districts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(adapter);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");

        // Lấy số lượng phòng hiện tại từ Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomCount = (int) snapshot.getChildrenCount();
                Log.d(TAG, "Current room count: " + roomCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch room count", error.toException());
                Toast.makeText(AddRoomActivity.this, "Failed to fetch room count", Toast.LENGTH_SHORT).show();
            }
        });

        buttonChooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Choose Images button clicked");
                openFileChooser();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Done button clicked");
                saveRoomData();
                startActivity(new Intent(AddRoomActivity.this, HostMode.class));
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    Log.d(TAG, "Multiple images selected: " + count);
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUriList.add(imageUri);
                        Log.d(TAG, "Image added: " + imageUri.toString());
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageUriList.add(imageUri);
                    Log.d(TAG, "Single image added: " + imageUri.toString());
                }
                imageAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "No images selected or request cancelled");
            }
        }
    }


    private void saveRoomData() {
        String district = spinnerDistrict.getSelectedItem().toString();
        String price = editTextPrice.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (price.isEmpty() || address.isEmpty() || imageUriList.isEmpty()) {
            Log.w(TAG, "Required fields missing");
            Toast.makeText(this, "Please fill all fields and choose images", Toast.LENGTH_SHORT).show();
            return;
        }

        roomCount++; // Tăng số lượng phòng hiện tại lên 1
        String roomId = String.valueOf(roomCount);
        DatabaseReference roomRef = databaseReference.child(roomId);

        Log.d(TAG, "Saving room data: ID=" + roomId + ", District=" + district + ", Price=" + price + ", Address=" + address);

        roomRef.child("district").setValue(district);
        roomRef.child("price").setValue(price);
        roomRef.child("address").setValue(address);

        List<String> imageUrls = new ArrayList<>();
        for (Uri imageUri : imageUriList) {
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            StorageReference storageRef = firebaseStorage.getReference().child("rooms/" + roomId + "/" + fileName);

            Log.d(TAG, "Uploading image: " + imageUri.toString());

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Image upload successful");
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            Log.d(TAG, "Image URL obtained: " + uri.toString());
                            if (imageUrls.size() == imageUriList.size()) {
                                roomRef.child("imageUrls").setValue(imageUrls)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Room added successfully");
                                                Toast.makeText(AddRoomActivity.this, "Room added successfully!", Toast.LENGTH_SHORT).show();

                                                // Lưu roomId vào LFR của user
                                                saveRoomIdToUserLFR(roomId);

                                                // Quay lại màn hình trước đó sau khi lưu thành công
                                                finish();
                                            } else {
                                                Log.e(TAG, "Failed to add room to database");
                                                Toast.makeText(AddRoomActivity.this, "Failed to save room data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload image", e);
                        Toast.makeText(AddRoomActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveRoomIdToUserLFR(String roomId) {
        // Lấy userId từ Firebase Authentication
        String userId = mAuth.getCurrentUser().getUid();

        if (userId != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("LFR");

            // Thêm roomId vào danh sách LFR của user
            userRef.child(roomId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Room ID saved to user's LFR successfully");
                    } else {
                        Log.e(TAG, "Failed to save Room ID to user's LFR");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to save Room ID to user's LFR", e);
                }
            });
        } else {
            Log.e(TAG, "User ID is null, cannot save room ID to LFR");
        }
    }

}
