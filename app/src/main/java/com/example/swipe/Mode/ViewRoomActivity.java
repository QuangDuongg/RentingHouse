package com.example.swipe.Mode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.Main.Cards;
import com.example.swipe.R;
import com.example.swipe.Utils.SearchFilter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewRoomActivity extends AppCompatActivity {
    private static final int VIEW_ROOM_DETAIL_REQUEST_CODE = 12;
    private static final String TAG = "ViewRoomActivity";
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Cards> roomList;

    // Firebase Variable
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userId;
    private SearchFilter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room);
        searchFilter = SearchFilter.getInstance();

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRoom);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true); // Improve performance if the layout size is fixed

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, userId);
        userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("LFR");

        // Initialize Room list
        roomList = new ArrayList<>();
        fetchRoomData();  // Fetch initial room data
    }

    // Fetch room data from Firebase
    private void fetchRoomData() {
        roomList.clear(); // Clear the current list to avoid duplication
        List<Integer> indexRoomsList = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Integer roomIndex = Integer.valueOf(snapshot.getKey());
                    indexRoomsList.add(roomIndex);
                }

                DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
                roomList.clear(); // Clear the list before refilling it
                for (int roomIndex : indexRoomsList) {
                    roomsRef.child(String.valueOf(roomIndex)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            try {
                                String district = dataSnapshot2.child("district").getValue(String.class);
                                int districtIndex = getDistrictIndex(district);
                                if (districtIndex < 0 || districtIndex > 12 || !searchFilter.getIsDistrictIndex(districtIndex)) {
                                    Log.d(TAG, "Not valid district");
                                }

                                Double latitude = dataSnapshot2.child("latitude").getValue(Double.class);
                                Double longitude = dataSnapshot2.child("longitude").getValue(Double.class);
                                if (searchFilter.calculateDistance(latitude, longitude) > searchFilter.getMaxDistance()) {
                                    Log.d(TAG, "Not valid distance");
                                }

                                String priceString = dataSnapshot2.child("price").getValue(String.class);
                                int price = Integer.parseInt(priceString);
                                if (price > searchFilter.getBudget()) {
                                    Log.d(TAG, "Not valid budget");
                                }

                                String idHost = dataSnapshot2.child("idHost").getValue(String.class);
                                String address = dataSnapshot2.child("address").getValue(String.class);
                                String DPD = dataSnapshot2.child("description").getValue(String.class);
                                if (DPD == null) DPD = "No description";

                                List<String> roomImageUrl = new ArrayList<>();
                                for (DataSnapshot imageSnapshot : dataSnapshot2.child("imageUrls").getChildren()) {
                                    String imageUrl = imageSnapshot.getValue(String.class);
                                    roomImageUrl.add(imageUrl);
                                }

                                Cards roomCard = new Cards(DPD, district, roomImageUrl, address, price, searchFilter.calculateDistance(latitude, longitude), userId, roomIndex);
                                roomList.add(roomCard);
                                setupRoomAdapter();  // Setup the adapter after the roomList is updated

                            } catch (Exception e) {
                                Log.e(TAG, "Error processing dataSnapshot2: " + dataSnapshot2.getKey(), e);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data", databaseError.toException());
            }
        });
    }

    // Setup RecyclerView adapter
    private void setupRoomAdapter() {
        if (roomAdapter == null) {
            roomAdapter = new RoomAdapter(this, roomList, view -> {
                Intent intent = new Intent(ViewRoomActivity.this, AddRoomActivity.class);
                startActivityForResult(intent, VIEW_ROOM_DETAIL_REQUEST_CODE);
            });
            recyclerView.setAdapter(roomAdapter);
        } else {
            roomAdapter.notifyDataSetChanged();  // Notify adapter if data is updated
        }
    }

    // Handle the result from the AddRoomActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_ROOM_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // The user edited the room, so fetch the data again
            fetchRoomData();  // Re-fetch the data after returning from detail
        }
    }

    private int getDistrictIndex(String district) {
        district = district.toLowerCase().trim();
        switch (district) {
            case "district 1":
            case "quận 1":
            case "quan 1":
                return 1;
            case "district 2":
            case "quan 2":
            case "quận 2":
                return 2;
            case "district 3":
            case "quận 3":
            case "quan 3":
                return 3;
            case "district 4":
            case "quận 4":
            case "quan 4":
                return 4;
            case "district 5":
            case "quận 5":
            case "quan 5":
                return 5;
            case "district 6":
            case "quận 6":
            case "quan 6":
                return 6;
            case "district 7":
            case "quận 7":
            case "quan 7":
                return 7;
            case "district 8":
            case "quận 8":
            case "quan 8":
                return 8;
            case "district 9":
            case "quận 9":
            case "quan 9":
                return 9;
            case "district 10":
            case "quận 10":
            case "quan 10":
                return 10;
            case "district 11":
            case "quận 11":
            case "quan 11":
                return 11;
            case "district 12":
            case "quận 12":
            case "quan 12":
                return 12;
            default:
                return -1;  // Invalid district
        }
    }
}
