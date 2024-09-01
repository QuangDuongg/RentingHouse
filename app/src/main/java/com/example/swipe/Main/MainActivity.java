package com.example.swipe.Main;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.example.swipe.R;
import com.example.swipe.Utils.PulsatorLayout;
import com.example.swipe.Utils.SearchFilter;
import com.example.swipe.Utils.TopNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 1;
    final private int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    ListView listView;
    List<Cards> rowItems;
    FrameLayout cardFrame, moreFrame;
    private Context mContext = MainActivity.this;
    private NotificationHelper mNotificationHelper;
    private Cards cards_data[];
    private PhotoAdapter arrayAdapter;
    private DatabaseReference roomsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cardFrame = findViewById(R.id.card_frame);
        moreFrame = findViewById(R.id.more_frame);
        // start pulsator
        PulsatorLayout mPulsator = findViewById(R.id.pulsator);
        mPulsator.start();
        mNotificationHelper = new NotificationHelper(this);


        setupTopNavigationView();

        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        /////////////////// Insert from FireBase /////////////////////////////////////////////////////////////////
        rowItems = new ArrayList<Cards>();
        insertFromFirebase();

        arrayAdapter = new PhotoAdapter(this, R.layout.item, rowItems);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        checkRowItem();
        updateSwipeCard();
    }

    private void insertFromFirebase() {
        // Get a reference to your Firebase Realtime Database

        Log.d(TAG, "Attempting to retrieve data from Firebase");
        // Attach a listener to read the data at your "rooms" reference
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot received from Firebase");

                // Clear the current list of Cards
                rowItems.clear();
                Log.d(TAG, "rowItems list cleared");

                // Loop through each child in the "rooms" node
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Processing room ID: " + roomSnapshot.getKey());

                    try {
                        // Extract data for each room
                        String address = roomSnapshot.child("address").getValue(String.class);
                        Log.d(TAG, "Address: " + address);

                        String district = roomSnapshot.child("district").getValue(String.class);
                        Log.d(TAG, "District: " + district);

                        // Retrieve image URLs
                        List<String> roomImageUrl = new ArrayList<>();
                        for (DataSnapshot imageSnapshot : roomSnapshot.child("imageUrls").getChildren()) {
                            String imageUrl = imageSnapshot.getValue(String.class);
                            roomImageUrl.add(imageUrl);
                            Log.d(TAG, "Image URL: " + imageUrl);
                        }

                        // Check if roomImageUrl is empty
                        if (roomImageUrl.isEmpty()) {
                            Log.d(TAG, "No image URLs found for this room");
                        }

                        // Get latitude and longitude
                        Double latitude = roomSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = roomSnapshot.child("longitude").getValue(Double.class);
                        Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);

                        // Get the price as a String and convert it to int, considering the 1K ratio
                        String priceString = roomSnapshot.child("price").getValue(String.class);
                        int price = Integer.parseInt(priceString) / 1000;
                        Log.d(TAG, "Price (in thousands): " + price);

                        // Create a new Cards object with the retrieved data
                        Cards roomCard = new Cards(null, district, roomImageUrl, address, price, latitude, longitude);
                        Log.d(TAG, "Created Cards object: " + roomCard.toString());

                        // Add the Cards object to the list
                        rowItems.add(roomCard);
                        Log.d(TAG, "Added Cards object to rowItems list");
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing roomSnapshot: " + roomSnapshot.getKey(), e);
                    }
                }

                Log.d(TAG, "Finished processing all rooms");

                // Notify the adapter that the data has changed
                arrayAdapter.notifyDataSetChanged();
                checkRowItem(); // Ensure frames are correctly shown or hidden based on data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
            }
        });

        List <String> imgRooms = new ArrayList<>();
        imgRooms.add("https://im.idiva.com/author/2018/Jul/shivani_chhabra-_author_s_profile.jpg");
        Cards cards = new Cards("1", "District 5",imgRooms, "75 Nguyen Van Cu Street", 6000, 2);
        rowItems.add(cards);
    }



    private void checkRowItem() {
        if (rowItems.isEmpty()) {
            moreFrame.setVisibility(View.VISIBLE);
            cardFrame.setVisibility(View.GONE);
        }
    }

    private void updateLocation() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        updateLocation();
                    } else {
                        Toast.makeText(MainActivity.this, "Location Permission Denied. You have to give permission inorder to know the user range ", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateSwipeCard() {
        final SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                checkRowItem();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;

                //check matches
                checkRowItem();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here


            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void sendNotification() {
        NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(mContext.getString(R.string.app_name), mContext.getString(R.string.match_notification));

        mNotificationHelper.getManager().notify(1, nb.build());
    }


    public void DislikeBtn(View v) {
        if (rowItems.size() != 0) {
            Cards card_item = rowItems.get(0);

            // String userId = card_item.getUserId();

            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();

            Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
            btnClick.putExtra("url", card_item.getRoomImageUrl().get(0));
            startActivity(btnClick);
        }
    }

    public void LikeBtn(View v) {
        if (rowItems.size() != 0) {
            Cards card_item = rowItems.get(0);

            // String userId = card_item.getUserId();

            //check matches

            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();

            Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
            btnClick.putExtra("url", card_item.getRoomImageUrl().get(0));
            startActivity(btnClick);
        }
    }


    /**
     * setup top tool bar
     */
    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationView tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    @Override
    public void onBackPressed() {

    }


}
