package com.google.firebase.udacity.friendlychat;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.udacity.friendlychat.Managers.OpenChatRoomWith.CHAT_ROOM;

public class ColorRecyclerViewAdapter extends RecyclerView.Adapter<ColorRecyclerViewAdapter.ColorViewHolder> {

    private final String conversationID;
    private List<Integer> colorList;

    public ColorRecyclerViewAdapter(String conversationID) {
        colorList = new ArrayList<>();
        this.conversationID = conversationID;
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_circle_layout, parent, false);
        return new ColorViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ColorViewHolder holder, final int position) {
        GradientDrawable circle = (GradientDrawable) holder.circle.getDrawable();
        String hex = Integer.toHexString(colorList.get(position));
        while (hex.length() < 6) {
            hex = "0" + hex;
        }
        hex = "#" + hex;
        circle.setColor(Color.parseColor(hex));
        circle.invalidateSelf();

        holder.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference colorReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID).child("chatColor");
                colorReference.setValue(colorList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(int color) {
        colorList.add(color);
        notifyDataSetChanged();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {

        ImageView circle;

        ColorViewHolder(View v) {
            super(v);
            circle = v.findViewById(R.id.color_circle);
        }
    }
}
