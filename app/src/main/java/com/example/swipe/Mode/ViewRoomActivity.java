package com.example.swipe.Mode;

import static android.content.ContentValues.TAG;

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
       /* mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();*/
        // specific to test
        userRef = FirebaseDatabase.getInstance().getReference("users")
                .child("tFI9FlXwVTWb1tgG7Nu4WS47Eq33") // will use the userId
                .child("indexRooms");

        // Test add arr index Rooms
       /* List<Integer> idRooms = new ArrayList<>();
        idRooms.add(1);
        idRooms.add(2);
        idRooms.add(3);
        idRooms.add(4);
        userRef.child("indexRooms").setValue(idRooms).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Indexed rooms saved successfully.");
                } else {
                    Log.e("Firebase", "Error saving indexed rooms: ", task.getException());
                }
            }
        });;*/
        // End test

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
                    Integer roomIndex = snapshot.getValue(Integer.class);
                    indexRoomsList.add(roomIndex);
                }
                DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
                roomList.clear();
                roomList = new ArrayList<>();
                roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "DataSnapshot received from Firebase");

                        // Process data from Firebase and populate rowItems
                        for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                            try {
                                String district = roomSnapshot.child("district").getValue(String.class);
                                // Map the district to the corresponding index in isDistrict
                                int districtIndex = getDistrictIndex(district);
                                // Check if the district is enabled in SearchFilter
                                Log.d(TAG, "Check condition ");
                                if (districtIndex < 0 || districtIndex > 12 || !searchFilter.getIsDistrictIndex(districtIndex)) {
                                    Log.d(TAG, "Not valid district");
                                    Log.d(TAG, district);
                                    //continue;
                                }


                                Double latitude = roomSnapshot.child("latitude").getValue(Double.class);
                                Double longitude = roomSnapshot.child("longitude").getValue(Double.class);
                                if(searchFilter.calculateDistance(latitude,longitude) > searchFilter.getMaxDistance()) {
                                    Log.d(TAG, "Not valid distance");
                                    //continue;
                                }

                                String priceString = roomSnapshot.child("price").getValue(String.class);
                                int price = Integer.parseInt(priceString) / 1000;
                                if(price > searchFilter.getBudget()) {
                                    Log.d(TAG, "Not valid budget");
                                    // continue;
                                }

                                String idHost = roomSnapshot.child("idHost").getValue(String.class);
                                String address = roomSnapshot.child("address").getValue(String.class);
                                String DPD = roomSnapshot.child("DPD").getValue(String.class);
                                if(DPD == null)
                                    DPD = "No description";
                                List<String> roomImageUrl = new ArrayList<>();
                                for (DataSnapshot imageSnapshot : roomSnapshot.child("imageUrls").getChildren()) {
                                    String imageUrl = imageSnapshot.getValue(String.class);
                                    roomImageUrl.add(imageUrl);
                                }
                                // Check condition

                                Cards roomCard = new Cards(DPD, district, roomImageUrl, address, price, searchFilter.calculateDistance(latitude, longitude));
                                Log.d(TAG, "Calculate Distance: from (" + searchFilter.getLatitudeUser() + ", " + searchFilter.getLongitudeUser() + ")" + " to ( " + latitude + ", " + longitude + ") is: " + String.valueOf(searchFilter.calculateDistance(latitude, longitude)));
                                roomList.add(roomCard);

                            } catch (Exception e) {
                                Log.e(TAG, "Error processing roomSnapshot: " + roomSnapshot.getKey(), e);
                            }
                        }

                        Log.d(TAG, "Finished processing all rooms");
                        Log.d(TAG, "right after fetch size: " + String.valueOf(roomList.size()));
                        // Notify adapter of the new data

                        roomAdapter = new RoomAdapter(ViewRoomActivity.this, roomList, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Handle "Add Room" button click here
                                List<String> tmpLink = new ArrayList<>();
                                tmpLink.add("https://www.thespruce.com/thmb/iMt63n8NGCojUETr6-T8oj-5-ns=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/PAinteriors-7-cafe9c2bd6be4823b9345e591e4f367f.jpg");
                                roomList.add(new Cards("New Room", "Unknown km", tmpLink,"",1000,2.0));
                                roomAdapter.notifyItemInserted(roomList.size());  // Notify adapter of new item
                            }
                        });
                        recyclerView.setAdapter(roomAdapter);
                        roomAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
                    }
                });

                // Now you have the indexRoomsList with all the room indexes
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
