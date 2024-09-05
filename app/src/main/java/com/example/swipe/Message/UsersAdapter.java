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

import java.util.List;
import java.util.Map;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> userList;
    private Map<String, String> lastMessagesMap; // Map để giữ tin nhắn cuối cùng
    private OnUserClickListener onUserClickListener;

    // Constructor
    public UsersAdapter(List<User> userList, Map<String, String> lastMessagesMap, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.lastMessagesMap = lastMessagesMap;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_user
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Lấy đối tượng User ở vị trí hiện tại
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getUserName());

        // Tải và hiển thị ảnh đại diện (avatar) từ URL bằng Glide
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfileImageUrl())
                    .circleCrop()// Tải URL ảnh đại diện từ Firebase
                    .placeholder(R.drawable.profile)  // Hiển thị ảnh mặc định nếu không có URL
                    .into(holder.avatarImageView);  // Gán vào ImageView
        } else {
            holder.avatarImageView.setImageResource(R.drawable.profile);  // Sử dụng ảnh mặc định
        }

        // Hiển thị tin nhắn cuối cùng nếu có
        String lastMessage = lastMessagesMap != null ? lastMessagesMap.get(user.getUserId()) : "";
        holder.lastMessageTextView.setText(lastMessage);

        // Đặt sự kiện click cho item
        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView lastMessageTextView;
        ImageView avatarImageView;  // ImageView cho avatar

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liên kết các view với layout
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
            lastMessageTextView = itemView.findViewById(R.id.textViewLastMessage);
            avatarImageView = itemView.findViewById(R.id.avatar_image);  // Liên kết với ImageView từ item_user.xml
        }
    }

    // Giao diện để xử lý sự kiện click vào một user
    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}
