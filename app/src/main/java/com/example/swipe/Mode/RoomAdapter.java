package com.example.swipe.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swipe.Main.Cards;
import com.example.swipe.Main.ProfileCheckinMain;
import com.example.swipe.R;

import java.util.List;

import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private List<Cards> roomList;
    private View.OnClickListener addButtonClickListener;

    public RoomAdapter(Context context, List<Cards> roomList, View.OnClickListener addButtonClickListener) {
        this.mContext = context;
        this.roomList = roomList;
        this.addButtonClickListener = addButtonClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER; // The first item is the header
        } else {
            return TYPE_ITEM; // Other items are regular room items
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // Inflate header layout (Button layout)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_room_button, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Inflate room item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_room, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            // Set click listener for the button
            ((HeaderViewHolder) holder).addButton.setOnClickListener(addButtonClickListener);
        } else if (holder instanceof ItemViewHolder) {
            final Cards card_item = roomList.get(position - 1); // Adjust for header at position 0

            ((ItemViewHolder) holder).name.setText(card_item.getDistrict() + ",              " + card_item.getDistance() + " km");

            ((ItemViewHolder) holder).btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewRoomDetail.class);
                    intent.putExtra("district", card_item.getDistrict());
                    if (card_item.isAnyImageRoom()) {
                        ArrayList<String> roomImageUrls = new ArrayList<>(card_item.getRoomImageUrl());
                        intent.putStringArrayListExtra("photo", roomImageUrls);
                    }
                    intent.putExtra("address", card_item.getAddress());
                    intent.putExtra("price", card_item.getPrice());
                    intent.putExtra("distance", card_item.getDistance());
                    intent.putExtra("DPD", card_item.getDPD());
                    mContext.startActivity(intent);
                }
            });

            if (card_item.getRoomImageUrl() != null && !card_item.getRoomImageUrl().isEmpty()) {
                String imageUrl = card_item.getRoomImageUrl().get(0);
                if (imageUrl.equals("defaultRoom")) {
                    Glide.with(mContext).load(R.drawable.default_man).into(((ItemViewHolder) holder).image);
                } else {
                    Glide.with(mContext).load(imageUrl).into(((ItemViewHolder) holder).image);
                }
            } else {
                Glide.with(mContext).load(R.drawable.default_man).into(((ItemViewHolder) holder).image); // Fallback image
            }
        }
    }

    @Override
    public int getItemCount() {
        // Room list + 1 for the header
        return roomList.size() + 1;
    }

    // ViewHolder for header (Button)
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        Button addButton;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.buttonAddRoom);
        }
    }

    // ViewHolder for room items
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        ImageButton btnInfo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            btnInfo = itemView.findViewById(R.id.checkInfoBeforeMatched);
        }
    }
}
