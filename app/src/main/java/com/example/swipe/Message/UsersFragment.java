package com.example.swipe.Message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> userList;
    private DatabaseReference usersDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Loi cho nay","Loi 1");
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(userList, null, this::startChat);
        recyclerView.setAdapter(usersAdapter);

        mAuth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        loadUsers();

        return view;
    }

    private void loadUsers() {
        Log.d("Loi cho nay1","Loi 1");
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Loi cho nay2","Loi 1");
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    Log.d("Loi cho nay3",userSnapshot.getKey());
                    if (user != null && !user.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                        Log.d("Loi cho nay4",user.getUserId());
                        userList.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải danh sách người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startChat(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", user.getUserId());
        intent.putExtra("userName", user.getUserName());
        startActivity(intent);
    }
}
