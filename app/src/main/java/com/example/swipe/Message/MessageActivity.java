package com.example.swipe.Message;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.swipe.Main.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.swipe.Mode.HostMode;
import com.example.swipe.Mode.TenantMode;
import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MainPagerAdapter mainPagerAdapter;
    private Toolbar toolbar;
    private String userRole;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        getUserRole();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        mainPagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(mainPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("My Chats");
                        break;
                    case 1:
                        tab.setText("Users");
                        break;
                }
            }
        }).attach();

        viewPager.setCurrentItem(0);
    }

    private void getUserRole() {
        userRef.child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRole = snapshot.getValue(String.class); // Lưu role của người dùng
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Kiểm tra role của người dùng và chuyển hướng
            if ("host".equals(userRole)) {
                Intent intent = new Intent(MessageActivity.this, HostMode.class);
                startActivity(intent);
            } else if ("tenant".equals(userRole)) {
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(intent);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
