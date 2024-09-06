package com.example.swipe.Mode;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.Main.Cards;
import com.example.swipe.Main.MainActivity;
import com.example.swipe.Main.PhotoAdapter;
import com.example.swipe.R;
import com.example.swipe.Utils.SearchFilter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ViewRoomActivity extends AppCompatActivity {
    private static final String TAG = "ViewRoomActivity";
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Cards> roomList;

    // Firebase Variable
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private String userId;
    private SearchFilter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room);
        searchFilter = SearchFilter.getInstance();

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRoom);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, userId);
        // specific to test
        userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId) // will use the userId tFI9FlXwVTWb1tgG7Nu4WS47Eq33
                .child("LFR");

        // Set GridLayoutManager with 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set fixed size to improve performance (if the size is fixed)
        recyclerView.setHasFixedSize(true);

        // Initialize Room list and add some data (you can dynamically add items here)
        roomList = new ArrayList<>();

        // get the arrayIndexRoom from Firebase
        List<Integer> indexRoomsList = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the list of indexRooms
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Integer roomIndex = Integer.valueOf(snapshot.getKey());
                    indexRoomsList.add(roomIndex);
                }
                DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
                roomList.clear();
                Log.d(TAG, "Init roomList");
                roomList = new ArrayList<>();
                // Use indexRoomsList to fetch room data
                for (int i = 0; i < indexRoomsList.size(); i++) {
                    int roomIndex = indexRoomsList.get(i); // Get the correct room index

                    Log.d(TAG, "For roomIndex: " + roomIndex);

                    roomsRef.child(String.valueOf(roomIndex)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            Log.d(TAG, "DataSnapshot received from Firebase for room index " + roomIndex);

                            // Process data from Firebase and populate roomList
                            try {
                                String district = dataSnapshot2.child("district").getValue(String.class);
                                // Map the district to the corresponding index in isDistrict
                                int districtIndex = getDistrictIndex(district);
                                // Check if the district is enabled in SearchFilter
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

                                // Retrieve the list of image URLs
                                List<String> roomImageUrl = new ArrayList<>();
                                for (DataSnapshot imageSnapshot : dataSnapshot2.child("imageUrls").getChildren()) {
                                    String imageUrl = imageSnapshot.getValue(String.class);
                                    roomImageUrl.add(imageUrl);
                                }

                                // Create the room card and add it to the list
                                Cards roomCard = new Cards(DPD, district, roomImageUrl, address, price, searchFilter.calculateDistance(latitude, longitude), userId);
                                Log.d(TAG, "Calculate Distance: from (" + searchFilter.getLatitudeUser() + ", " + searchFilter.getLongitudeUser() + ")" + " to (" + latitude + ", " + longitude + ") is: " + searchFilter.calculateDistance(latitude, longitude));
                                roomList.add(roomCard);
                                Log.d(TAG, "Size RoomList: " + String.valueOf(roomList.size()));

                                roomAdapter = new RoomAdapter(ViewRoomActivity.this, roomList, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new  Intent(ViewRoomActivity.this, AddRoomActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                recyclerView.setAdapter(roomAdapter);
                                roomAdapter.notifyDataSetChanged();

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

                Log.d(TAG, "Outside Size RoomList: " + String.valueOf(roomList.size()));
                /*roomAdapter = new RoomAdapter(ViewRoomActivity.this, roomList, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new  Intent(ViewRoomActivity.this, AddRoomActivity.class);
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(roomAdapter);
                roomAdapter.notifyDataSetChanged();*/

                // Log the indexRoomsList to confirm fetching
                Log.d(TAG, "Firebase IndexRooms: " + indexRoomsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data", databaseError.toException());
            }
        });
    }

    private int getDistrictIndex(String district) {
        // Convert the district name to lowercase to make the comparison case-insensitive
        district = district.toLowerCase().trim();

        // Handle Vietnamese and English district names
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
                return -1; // Invalid district
        }
    }

    // Function to dynamically add rooms to the list
    private void addRoom(String roomName) {
       // addRoom Act
    }
}
