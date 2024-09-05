package com.example.swipe.Mode;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ViewRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;

    // Firebase Variable
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRoom);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase components
       /* mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();*/
        // userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("LFR");
        // specific to test
        userRef = FirebaseDatabase.getInstance().getReference("users").child("tFI9FlXwVTWb1tgG7Nu4WS47Eq33");

        // test add arr index Rooms
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
        // end test

        // Initialize Room list and add some data (you can dynamically add items here)

        // Set GridLayoutManager with 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Set fixed size to improve performance (if the size is fixed)
        recyclerView.setHasFixedSize(true);

        // Initialize Room list and add some data (you can dynamically add items here)
        roomList = new ArrayList<>();
        roomList.add(new Room("Room A"));
        roomList.add(new Room("Room B"));
        roomList.add(new Room("Room C"));
        roomList.add(new Room("Room D"));
        roomList.add(new Room("Room E"));
        roomList.add(new Room("Room F"));

        // Initialize Adapter and set it to RecyclerView
        // Set Adapter
        roomAdapter = new RoomAdapter(roomList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click
                Toast.makeText(ViewRoomActivity.this, "Add Room clicked", Toast.LENGTH_SHORT).show();
                // Add a new room dynamically
                roomList.add(new Room("New Room"));
                roomAdapter.notifyItemInserted(roomList.size()); // Notify the adapter that a new item is added
            }
        });
        recyclerView.setAdapter(roomAdapter);
    }

    // Function to dynamically add rooms to the list
    private void addRoom(String roomName) {
        roomList.add(new Room(roomName));
        roomAdapter.notifyItemInserted(roomList.size() - 1);
    }
}
