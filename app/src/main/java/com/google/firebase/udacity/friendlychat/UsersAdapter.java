package com.google.firebase.udacity.friendlychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 26.03.2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context context;

    private List<String> usernameList;

    public UsersAdapter(Context context) {
        this.context = context;
        this.usernameList = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.userNameTextView.setText(usernameList.get(position));
    }

    @Override
    public int getItemCount() {
        return usernameList.size();
    }

    public void add(String username) {

        usernameList.add(username);
        Log.i("UserAdded", username);
        notifyDataSetChanged();
    }

    public void clear() {

        usernameList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;
        ImageView userAvatarImageView;

        ViewHolder(View view) {
            super(view);
            userNameTextView = view.findViewById(R.id.userName);
            userAvatarImageView = view.findViewById(R.id.userAvatar);
        }
    }
}
