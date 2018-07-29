package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.Managers.App.ColorManager;
import com.google.firebase.udacity.friendlychat.Managers.App.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.Database.UserManager;
import com.google.firebase.udacity.friendlychat.Managers.Database.UserOnlineStatus;
import com.google.firebase.udacity.friendlychat.R;

import static android.app.Activity.RESULT_OK;

public class UserSettingsFragment extends Fragment {

	private static final UserSettingsFragment ourInstance = new UserSettingsFragment();
	private final int AVATAR_RG = 1;
	private ConstraintLayout signOutSetting;
	private ImageView userAvatar;

	public UserSettingsFragment() {
	}

	public static UserSettingsFragment getInstance() {
		return ourInstance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_user_settings, container, false);

		final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

		v.setOnTouchListener((v1, event) -> gesture.onTouchEvent(event));

		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		signOutSetting = view.findViewById(R.id.sign_out_settings);
		userAvatar = view.findViewById(R.id.current_user_avatar);

		setAvatarPhoto(view);
	}

	private void setAvatarPhoto(View view) {
		if (UserManager.getCurrentUser() != null) {
			String avatarPhoto = UserManager.getCurrentUserAvatarUri();
			if (avatarPhoto != null && !avatarPhoto.equals("null")) {
				Glide.with(view).load(UserManager.getCurrentUserAvatarUri()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(userAvatar);
			}
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		manageActionBar();

		signOutSetting.setOnClickListener(v -> {
			UserOnlineStatus userOnlineStatus = UserOnlineStatus.getInstance();
			userOnlineStatus.signOut();
		});

		userAvatar.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, AVATAR_RG);
		});
	}

	private void manageActionBar() {
		Toolbar toolbar = getActivity().findViewById(R.id.user_settings_toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setDisplayUseLogoEnabled(true);
		}
		if (toolbar != null) {
			toolbar.setTitle(R.string.settings);
			toolbar.setSubtitle("");
			toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
			int statusBarColor = ColorManager.getStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
			if (statusBarColor != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				getActivity().getWindow().setStatusBarColor(statusBarColor);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == AVATAR_RG && data != null && data.getData() != null) {
			Uri photo = data.getData();
			UserManager.setCurrentUserAvatarUri(photo);
			setAvatarPhoto(getView());
			StorageReference photosReference = FirebaseStorage.getInstance().getReference().child("Images").child(UserManager.getCurrentUserID());
			photosReference.putFile(photo).addOnSuccessListener(taskSnapshot -> {
				Uri photoUri = taskSnapshot.getDownloadUrl();
				UserManager.setCurrentUserAvatarUri(photoUri);
			});
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		menu.clear();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				FragmentsManager.goBack(getActivity());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
