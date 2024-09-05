package com.example.swipe.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swipe.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private String currentUserId;
    private String senderAvatarUrl;  // Biến để lưu URL avatar người gửi

    // Constructor
    public MessagesAdapter(List<Message> messageList, String senderAvatarUrl) {
        this.messageList = messageList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.senderAvatarUrl = senderAvatarUrl;  // Gán URL avatar ban đầu (có thể null)
    }

    // Hàm để cập nhật URL avatar người gửi
    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;  // Cập nhật URL avatar của người gửi
        notifyDataSetChanged();  // Thông báo adapter để cập nhật giao diện
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.getSenderId().equals(currentUserId) ? 1 : 0; // 1 nếu là tin nhắn gửi, 0 nếu là tin nhắn nhận
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            // Tin nhắn gửi (không có avatar)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        } else {
            // Tin nhắn nhận (có avatar)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getMessage());

        // Nếu là tin nhắn nhận, hiển thị avatar của người gửi
        if (getItemViewType(position) == 0) {
            Glide.with(holder.itemView.getContext())
                    .load(senderAvatarUrl)
                    .circleCrop()// Tải avatar của người gửi từ URL
                    .placeholder(R.drawable.profile)  // Ảnh mặc định nếu không có URL
                    .into(holder.avatarImageView);  // Gán avatar vào ImageView
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder cho tin nhắn
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView avatarImageView;  // Chỉ cần khi là tin nhắn nhận

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textViewMessage);

            // Chỉ gán avatarImageView nếu là tin nhắn nhận
            if (viewType == 0) {
                avatarImageView = itemView.findViewById(R.id.avatar_image);
            }
        }
    }
}
