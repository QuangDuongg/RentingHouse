package com.example.swipe.Message;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        messagesRecyclerView = findViewById(R.id.recyclerViewMessages);

        mAuth = FirebaseAuth.getInstance();
        chatsDatabase = FirebaseDatabase.getInstance().getReference("chats");

        messageList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        String userId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");

        setTitle("Chat with " + userName);

        chatId = createChatId(mAuth.getCurrentUser().getUid(), userId);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageEditText.setText("");
            }
        });

        loadMessages();
    }

    private void loadMessages() {
        chatsDatabase.child(chatId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
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
        Message message = new Message(senderId, messageText, timestamp);

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

    private String createChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) > 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
}