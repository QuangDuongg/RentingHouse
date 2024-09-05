package com.example.swipe.Main;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.swipe.Utils.GPS;

import android.telephony.CarrierConfigManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.example.swipe.R;
import com.example.swipe.Utils.GPS;
import com.example.swipe.Utils.OpenCageGeocoder;
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
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Add this constant

    ListView listView;
    List<Cards> rowItems;
    FrameLayout cardFrame, moreFrame;
    private Context mContext = MainActivity.this;
    private NotificationHelper mNotificationHelper;
    private PhotoAdapter arrayAdapter;
    private DatabaseReference roomsRef;
    private DatabaseReference readInfoUser;
    private String userID;
    private SearchFilter searchFilter;
    private GPS gps;
    private com.example.swipe.Utils.OpenCageGeocoder OpenCageGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchFilter = SearchFilter.getInstance();

        cardFrame = findViewById(R.id.card_frame);
        moreFrame = findViewById(R.id.more_frame);
        // start pulsator
        PulsatorLayout mPulsator = findViewById(R.id.pulsator);
        mPulsator.start();
        mNotificationHelper = new NotificationHelper(this);

        setupTopNavigationView();

        // get the temporary coordinate
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Granted");
            // Initialize GPS only if permissions are granted
            gps = new GPS(getApplicationContext());
            Location location = gps.getLocation();
            if (location != null) {
                Log.d(TAG, "Coordinate: " + String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
                // Use the location data
                searchFilter.setLatitudeUser(gps.getLocation().getLatitude());
                searchFilter.setLongitudeUser(gps.getLocation().getLongitude());
            } else {
                Log.d(TAG, "Location is null, unable to get coordinates");
                Toast.makeText(this, "Waiting for location updates...", Toast.LENGTH_SHORT).show();
                // GPS will fetch the location once available and log it
            }
        }

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        if(userID == null)
            userID = "ObTze76baPUz9kkXzguIlCg2u7F3";
        readInfoUser = FirebaseDatabase.getInstance().getReference("users").child(userID);
        readInfoUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("latitude").getValue(Double.class) != null)
                    searchFilter.setLatitudeUser(snapshot.child("latitude").getValue(Double.class));
                if(snapshot.child("longitude").getValue(Double.class) != null)
                    searchFilter.setLongitudeUser(snapshot.child("longitude").getValue(Double.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d(TAG, userID);
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        /////////////////// Insert from FireBase /////////////////////////////////////////////////////////////////
        rowItems = new ArrayList<Cards>();
        insertFromFirebase();
    }

    private void insertFromFirebase() {
        Log.d(TAG, "Attempting to retrieve data from Firebase");

        // Clear the current list of Cards
        rowItems = new ArrayList<>();

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
                        rowItems.add(roomCard);

                    } catch (Exception e) {
                        Log.e(TAG, "Error processing roomSnapshot: " + roomSnapshot.getKey(), e);
                    }
                }

                Log.d(TAG, "Finished processing all rooms");
                Log.d(TAG, "right after fetch size: " + String.valueOf(rowItems.size()));
                // Notify adapter of the new data

                arrayAdapter = new PhotoAdapter(MainActivity.this, R.layout.item, rowItems);
                checkRowItem();
                updateSwipeCard();
                arrayAdapter.notifyDataSetChanged();

                // Ensure frames are correctly shown or hidden based on data
                checkRowItem();
                updateSwipeCard();  // You can update the UI after data is loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read data from Firebase", databaseError.toException());
            }
        });
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions granted, initialize GPS
                    gps = new GPS(getApplicationContext());
                } else {
                    // Permissions denied, handle appropriately
                    Toast.makeText(this, "Location permission is required to use this feature.", Toast.LENGTH_SHORT).show();
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

    // Helper function to map district string to the corresponding index in isDistrict
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


    @Override
    public void onBackPressed() {

    }




}
