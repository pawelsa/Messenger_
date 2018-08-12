package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.PseudonymBottomSheet;

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
import com.google.firebase.udacity.friendlychat.Managers.Database.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoom;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PseudonymRecyclerViewAdapter extends RecyclerView.Adapter<PseudonymRecyclerViewAdapter.PseudonymViewHolder> {

	private final String conversationID;
	private List<User> users;
	private List<String> pseudonyms;
	private Context context;

	private ChatRoom chatRoom;

	private Disposable updatingPseudonyms;

	PseudonymRecyclerViewAdapter(Context context, String conversationID) {
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

		holder.bind(position);
	}

	public void add(ChatRoom chatRoom) {

		this.chatRoom = chatRoom;

		if (chatRoom.getConversationID().equals(conversationID)) {
			if (users.size() > 0) {
				users.clear();
				pseudonyms.clear();
				notifyDataSetChanged();
			}

			users = chatRoom.conversationalist;
			users.add(UserManager.getCurrentUser());

			if (updatingPseudonyms == null || updatingPseudonyms.isDisposed())
				updatingPseudonyms = startUpdatingPseudonyms(chatRoom.chatRoomObject.participants);

			notifyDataSetChanged();
		}
	}

	private Disposable startUpdatingPseudonyms(Map participants) {
		return Observable.fromIterable(users)
				.subscribeOn(Schedulers.computation())
				.map(user -> user.User_ID)
				.map(userID -> (Map<String, String>) participants.get(userID))
				.map(userMap -> userMap.get("Name"))
				.subscribe(userName -> pseudonyms.add(userName));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return users.size();
	}

	public void onResume() {
		if (chatRoom != null && (updatingPseudonyms == null || updatingPseudonyms.isDisposed()))
			updatingPseudonyms = startUpdatingPseudonyms(chatRoom.chatRoomObject.participants);
	}

	public void onPauseAndonDestroy() {

		if (updatingPseudonyms != null && !updatingPseudonyms.isDisposed())
			updatingPseudonyms.dispose();
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

		void bind(int position) {

			User user = users.get(position);

			username.setText(user.User_Name);
			if (checkIfUserHasAvatar(user))
				Glide.with(context).load(user.avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(avatar);

			layout.setOnClickListener(v ->
					buildAlertDialog(user, position));
		}

		private boolean checkIfUserHasAvatar(User user) {
			return user.avatarUri != null && !user.avatarUri.equals("null");
		}

		private void buildAlertDialog(User user, int position) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle(context.getResources().getString(R.string.settings_pseudonym));

			final EditText input = buildEditText();
			input.setText(pseudonyms.get(position));

			alertDialogBuilder
					.setView(input)
					.setPositiveButton(context.getResources().getString(R.string.settings_confirm), (dialog, which) -> {
						final String newPseudonym = input.getText().toString();
						UserManager.updateUserPseudonym(newPseudonym, conversationID, user.User_ID);
					})
					.setNeutralButton(context.getResources().getString(R.string.settings_original), (dialog, which) ->
							UserManager.updateUserPseudonym(user.User_Name, conversationID, user.User_ID))
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
	}
}
