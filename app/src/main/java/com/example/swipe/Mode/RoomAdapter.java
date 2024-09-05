package com.example.swipe.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.R;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Room> roomList;
    private View.OnClickListener addButtonClickListener;

    // Constructor
    public RoomAdapter(List<Room> roomList, View.OnClickListener addButtonClickListener) {
        this.roomList = roomList;
        this.addButtonClickListener = addButtonClickListener; // Handle button clicks
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            // The first item is the header (Button)
            return TYPE_HEADER;
        }
        return TYPE_ITEM; // Other items are regular Room items
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // Inflate the header view (Button)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_room_button, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Inflate the room item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_room, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            // Set the click listener for the button in the header
            ((HeaderViewHolder) holder).addButton.setOnClickListener(addButtonClickListener);
        } else {
            // Bind the room data to the item view
            Room currentRoom = roomList.get(position - 1); // Subtract 1 for the header
            ((ItemViewHolder) holder).roomName.setText(currentRoom.getRoomName());
        }
    }

    @Override
    public int getItemCount() {
        // The number of items is the size of the room list + 1 (for the header)
        return roomList.size() + 1;
    }

    // ViewHolder for the header (Button)
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        Button addButton;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.buttonAddRoom);
        }
    }

    // ViewHolder for the room items
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.name);
        }
    }
}
