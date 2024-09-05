package com.example.swipe.Mode;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddRoomActivity extends AppCompatActivity {

    private static final String TAG = "AddRoomActivity";
    private static final int PICK_IMAGES_REQUEST = 1;

    private Spinner spinnerDistrict;
    private EditText editTextPrice, editTextAddress, editTextDescription;
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

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Thêm nút back vào Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Room for Rent");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        spinnerDistrict = findViewById(R.id.spinner_district);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextDescription = findViewById(R.id.edit_text_description);
        buttonChooseImages = findViewById(R.id.button_choose_images);
        buttonDone = findViewById(R.id.button_done);
        recyclerView = findViewById(R.id.recycler_view);

        mAuth = FirebaseAuth.getInstance();
        imageUriList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUriList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageAdapter);

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

        buttonChooseImages.setOnClickListener(v -> openFileChooser());

        buttonDone.setOnClickListener(v -> {
            saveRoomData();
            startActivity(new Intent(AddRoomActivity.this, HostMode.class));
        });
    }

    // Xử lý sự kiện khi nút Back trên Toolbar được nhấn
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Trở về màn hình HostMode
            Intent intent = new Intent(this, HostMode.class);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose image"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUriList.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageUriList.add(imageUri);
                }
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void saveRoomData() {
        String district = spinnerDistrict.getSelectedItem().toString();
        String price = editTextPrice.getText().toString().trim();
        String specificAddress = editTextAddress.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Gộp địa chỉ cụ thể + quận + Thành phố Hồ Chí Minh
        String fullAddress = specificAddress + ", " + district + ", Thành Phố Hồ Chí Minh";

        // Lấy idUser từ Firebase Authentication
        String userId = mAuth.getCurrentUser().getUid();

        if (price.isEmpty() || specificAddress.isEmpty() || description.isEmpty() || imageUriList.isEmpty()) {
            Toast.makeText(this, "Please fill in the information and select photos", Toast.LENGTH_SHORT).show();
            return;
        }

        roomCount++;
        String roomId = String.valueOf(roomCount);
        DatabaseReference roomRef = databaseReference.child(roomId);

        // Lưu thông tin cơ bản
        roomRef.child("district").setValue(district);
        roomRef.child("price").setValue(price);
        roomRef.child("address").setValue(specificAddress);
        roomRef.child("description").setValue(description);
        roomRef.child("idHost").setValue(userId); // Lưu idHost (là userId)

        // Lấy tọa độ từ địa chỉ đầy đủ
        getCoordinatesFromAddress(fullAddress, (latitude, longitude) -> {
            roomRef.child("latitude").setValue(latitude);
            roomRef.child("longitude").setValue(longitude);
        });

        // Lưu hình ảnh
        for (Uri imageUri : imageUriList) {
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            StorageReference storageRef = firebaseStorage.getReference().child("rooms/" + roomId + "/" + fileName);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> roomRef.child("imageUrls").push().setValue(uri.toString())))
                    .addOnFailureListener(e -> Toast.makeText(AddRoomActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }



    // Sử dụng Geocoder để lấy tọa độ từ địa chỉ
    private void getCoordinatesFromAddress(String address, OnCoordinatesObtainedListener listener) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                listener.onCoordinatesObtained(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Không tìm thấy tọa độ", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get coordinates", e);
        }
    }
    interface OnCoordinatesObtainedListener {
        void onCoordinatesObtained(double latitude, double longitude);
    }
}
