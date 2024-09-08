package com.example.swipe.Message;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private List<Message> messageList;
    private DatabaseReference chatsDatabase;
    private FirebaseAuth mAuth;
    private String chatId;
    private String receiverAvatarUrl;  // URL avatar của người nhận
    private String receiverName;

    // Views cho tên người dùng, avatar và nút back
    private ImageView avatarImageView;
    private TextView userNameTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Khởi tạo các view trong toolbar
        avatarImageView = findViewById(R.id.avatarImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        backButton = findViewById(R.id.backButton);

        // Xử lý nút back
        backButton.setOnClickListener(v -> finish());

        // Khởi tạo các view và adapter
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        messagesRecyclerView = findViewById(R.id.recyclerViewMessages);

        mAuth = FirebaseAuth.getInstance();
        chatsDatabase = FirebaseDatabase.getInstance().getReference("chats");

        messageList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messageList, null);  // Adapter sẽ được cập nhật với URL avatar sau
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        // Lấy thông tin từ intent
        String userId = getIntent().getStringExtra("userId");
        String messHouse= getIntent().getStringExtra("messHouse");
        receiverName = getIntent().getStringExtra("userName");

        // Hiển thị tên người nhận trên Toolbar
        userNameTextView.setText(receiverName);

        // Tạo chatId dựa trên hai userId
        chatId = createChatId(mAuth.getCurrentUser().getUid(), userId);

        // Lấy avatar và tên của người nhận tin nhắn
        loadReceiverDetails(userId);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageEditText.setText("");  // Reset lại trường nhập sau khi gửi tin nhắn
            }
        });

        // Tải danh sách tin nhắn
        loadMessages();
        if(!messHouse.isEmpty())
        {
            messageEditText.setText(messHouse);
        }
    }

    // Tải thông tin người nhận (người mà bạn đang chat)
    private void loadReceiverDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy URL avatar và tên của người nhận
                receiverAvatarUrl = snapshot.child("profileImageUrl").getValue(String.class);
                receiverName = snapshot.child("username").getValue(String.class);

                // Hiển thị avatar của người nhận
                Glide.with(ChatActivity.this)
                        .load(receiverAvatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.profile)  // Ảnh mặc định nếu không có URL
                        .into(avatarImageView);

                // Cập nhật tên người dùng
                userNameTextView.setText(receiverName);

                // Cập nhật avatar của người nhận trong adapter
                messagesAdapter.setSenderAvatarUrl(receiverAvatarUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void loadMessages() {
        chatsDatabase.child(chatId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);

                // Thêm tin nhắn vào danh sách và cập nhật giao diện
                messageList.add(message);
                messagesAdapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void sendMessage(String messageText) {
        String senderId = mAuth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        // Lấy URL avatar của người dùng hiện tại (nếu có)
        String profileImageUrl = getCurrentUserProfileImageUrl();

        // Tạo đối tượng tin nhắn với URL avatar
        Message message = new Message(senderId, messageText, timestamp, profileImageUrl);

        // Gửi tin nhắn vào Firebase
        DatabaseReference messageRef = chatsDatabase.child(chatId).child("messages").push();
        messageRef.setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Cập nhật tin nhắn cuối cùng và thời gian gửi
                Map<String, Object> lastMessageMap = new HashMap<>();
                lastMessageMap.put("lastMessage", messageText);
                lastMessageMap.put("lastMessageTimestamp", timestamp);

                // Cập nhật thông tin vào Firebase
                chatsDatabase.child(chatId).updateChildren(lastMessageMap);
            }
        });
    }

    // Hàm để tạo chatId duy nhất giữa hai user
    private String createChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) > 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    // Lấy URL avatar của người gửi tin nhắn hiện tại
    private String getCurrentUserProfileImageUrl() {
        final String[] profileImageUrl = {null};
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        userRef.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileImageUrl[0] = snapshot.getValue(String.class);  // Lấy URL từ Firebase
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
        return profileImageUrl[0];
    }
}
