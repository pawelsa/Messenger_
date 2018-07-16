package com.google.firebase.udacity.friendlychat;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class PseudonymRecyclerViewAdapter extends RecyclerView.Adapter<PseudonymRecyclerViewAdapter.PseudonymViewHolder> {

	private final String conversationID;
	private List<User> users;
	private List<String> pseudonyms;
	private Context context;

	public PseudonymRecyclerViewAdapter(Context context, String conversationID) {
		users = new ArrayList<>();
		pseudonyms = new ArrayList<>();
		this.conversationID = conversationID;
		this.context = context;
	}

	@Override
	public PseudonymViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pseudonym_item, parent, false);
		return new PseudonymViewHolder(v);
	}

	@Override
	public void onBindViewHolder(PseudonymViewHolder holder, final int position) {

		holder.username.setText(users.get(position).User_Name);
		if (checkIfUserHasAvatar(position)) {
			Glide.with(context).load(users.get(position).avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.avatar);
		}

		holder.layout.setOnClickListener(v ->
				buildAlertDialog(position));
	}

	private boolean checkIfUserHasAvatar(int position) {
		return users.get(position).avatarUri != null && !users.get(position).avatarUri.equals("null");
	}

	private void buildAlertDialog(final int position) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(context.getResources().getString(R.string.settings_pseudonym));

		final EditText input = buildEditText();
		input.setText(pseudonyms.get(position));

		alertDialogBuilder
				.setView(input)
				.setPositiveButton(context.getResources().getString(R.string.settings_confirm), (dialog, which) -> {
					final String newPseudonym = input.getText().toString();
					UserManager.updateUserPseudonym(newPseudonym, conversationID, users.get(position).User_ID);
				})
				.setNeutralButton(context.getResources().getString(R.string.settings_original), (dialog, which) ->
						UserManager.updateUserPseudonym(users.get(position).User_Name, conversationID, users.get(position).User_ID))
				.setNegativeButton(context.getResources().getString(R.string.settings_cancel), (dialog, which) ->
						dialog.cancel());
		alertDialogBuilder.show();
	}

	private EditText buildEditText() {
		EditText input = new EditText(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setFocusable(true);
		input.setSelection(input.getText().length());
		return input;
	}

	public void add(ChatRoom chatRoom) {

		if (chatRoom.chatRoomObject.conversationID.equals(conversationID)) {
			if (users.size() > 0) {
				users.clear();
				pseudonyms.clear();
				notifyDataSetChanged();
			}

			users = chatRoom.conversationalist;
			users.add(UserManager.getCurrentUser());


			Observable.fromIterable(users)
					.map(user -> user.User_ID)
					.map(userID -> (Map<String, String>) chatRoom.chatRoomObject.participants.get(userID))
					.map(userMap -> userMap.get("Name"))
					.subscribe(userName -> pseudonyms.add(userName));

			notifyDataSetChanged();
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return users.size();
	}

	class PseudonymViewHolder extends RecyclerView.ViewHolder {

		LinearLayout layout;
		ImageView avatar;
		TextView username;

		PseudonymViewHolder(View v) {
			super(v);
			layout = v.findViewById(R.id.settings_pseudonym_item);
			avatar = v.findViewById(R.id.settings_pseudonym_avatar);
			username = v.findViewById(R.id.settings_pseudonym_username);
		}
	}
}
