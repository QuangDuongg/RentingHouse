package com.example.swipe.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.R;

import java.util.List;
import java.util.Map;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> userList;
    private Map<String, String> lastMessagesMap; // Map to hold last messages
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getUserName());

        String lastMessage = lastMessagesMap != null ? lastMessagesMap.get(user.getUserId()) : "";
        holder.lastMessageTextView.setText(lastMessage);

        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView lastMessageTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
            lastMessageTextView = itemView.findViewById(R.id.textViewLastMessage);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}

