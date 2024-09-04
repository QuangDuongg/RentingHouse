package com.example.swipe.Main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.swipe.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Cards> {
    Context mContext;

    public PhotoAdapter(@NonNull Context context, int resource, @NonNull List<Cards> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Cards card_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);
        ImageButton btnInfo = convertView.findViewById(R.id.checkInfoBeforeMatched);

        name.setText(card_item.getDistrict() + ", " + card_item.getDistance() + " km");

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileCheckinMain.class);
                intent.putExtra("district", card_item.getDistrict());
                if (card_item.isAnyImageRoom()) {
                    ArrayList<String> roomImageUrls = new ArrayList<>(card_item.getRoomImageUrl());
                    intent.putStringArrayListExtra("photo", roomImageUrls);
                }
                intent.putExtra("address", card_item.getAddress());
                intent.putExtra("price", card_item.getPrice());
                intent.putExtra("distance", card_item.getDistance());
                mContext.startActivity(intent);
            }
        });

        if (card_item.getRoomImageUrl() != null && !card_item.getRoomImageUrl().isEmpty()) {
            switch (card_item.getRoomImageUrl().get(0)) {
                case "defaultRoom":
                    Glide.with(getContext()).load(R.drawable.default_man).into(image);
                    break;
                default:
                    Glide.with(getContext()).load(card_item.getRoomImageUrl().get(0)).into(image);
                    break;
            }
        } else {
            Glide.with(getContext()).load(R.drawable.default_man).into(image); // Fallback image
        }

        return convertView;
    }
}
