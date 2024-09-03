package com.example.swipe.Message;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> userList;
    private Map<String, String> lastMessagesMap;
    private DatabaseReference chatsDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_chats, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        lastMessagesMap = new HashMap<>();
        usersAdapter = new UsersAdapter(userList, lastMessagesMap, this::startChat);
        recyclerView.setAdapter(usersAdapter);

        mAuth = FirebaseAuth.getInstance();
        chatsDatabase = FirebaseDatabase.getInstance().getReference("chats");

        loadMyChats();

        return view;
    }

    private void loadMyChats() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        chatsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                lastMessagesMap.clear();
                Set<String> addedUserIds = new HashSet<>(); // Dùng để theo dõi các userId đã thêm vào danh sách

                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    if (chatId != null && chatId.contains(currentUserId)) {
                        String lastMessage = chatSnapshot.child("lastMessage").getValue(String.class);

                        // Tìm ID của người dùng khác trong cuộc trò chuyện
                        String[] userIds = chatId.split("_");
                        String otherUserId = userIds[0].equals(currentUserId) ? userIds[1] : userIds[0];

                        // Chỉ lấy thông tin người dùng nếu chưa có trong danh sách
                        if (!addedUserIds.contains(otherUserId)) {
                            addedUserIds.add(otherUserId);

                            // Lấy tên người dùng từ Firebase dựa trên otherUserId
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(otherUserId);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    String userName = userSnapshot.child("username").getValue(String.class);
                                    User user = new User(otherUserId, userName, null);
                                    userList.add(user);
                                    lastMessagesMap.put(user.getUserId(), lastMessage);
                                    usersAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Xử lý lỗi nếu có
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải danh sách cuộc trò chuyện", Toast.LENGTH_SHORT).show();
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