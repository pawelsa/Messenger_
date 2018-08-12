package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.AddUser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Managers.Database.ConversationRequest;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AddUserRecyclerViewAdapter extends RecyclerView.Adapter<AddUserRecyclerViewAdapter.AddUserViewHolder> {

	private List<User> userList;

	private Context context;

	public AddUserRecyclerViewAdapter(Context context) {
		userList = new ArrayList<>();
		this.context = context;
	}

	@Override
	public AddUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_request_child_item, parent, false);
		return new AddUserViewHolder(v);
	}


	@Override
	public void onBindViewHolder(AddUserViewHolder holder, final int position) {

		holder.bind(userList.get(position));
	}

	@Override
	public int getItemCount() {
		return userList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void add(User user) {
		userList.add(user);
		notifyItemChanged(userList.size() - 1);
	}

	public void addAll(List<User> userList) {
		userList.addAll(userList);
		notifyDataSetChanged();
	}

	public boolean isEmpty() {
		return userList.isEmpty();
	}

	class AddUserViewHolder extends RecyclerView.ViewHolder {

		TextView userName;
		ImageView userAvatar;
		Button accept;

		AddUserViewHolder(View view) {
			super(view);

			userName = view.findViewById(R.id.user_request_name);
			userAvatar = view.findViewById(R.id.user_request_avatar);
			accept = view.findViewById(R.id.user_request_accept);
		}

		void bind(User user) {

			userName.setText(user.User_Name);
			if (user.avatarUri != null && !user.avatarUri.equals("null"))
				Glide.with(context).load(user.avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(userAvatar);

			accept.setOnClickListener(v -> {
						ConversationRequest.requestAccepted(user)
								.subscribeOn(Schedulers.io())
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(result -> {
											Log.i("Accepting request", "Accepted");
											Toast.makeText(context, "Accepted request from " + user.User_Name + "  " + user.User_ID, Toast.LENGTH_LONG).show();
										},
										Throwable::printStackTrace,
										() -> Log.i("Accepting request", "Complete"));
					}
			);

		}
	}
}
