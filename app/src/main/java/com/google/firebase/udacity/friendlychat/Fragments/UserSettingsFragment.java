package com.google.firebase.udacity.friendlychat.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Managers.UserOnlineStatus;
import com.google.firebase.udacity.friendlychat.R;

import static android.app.Activity.RESULT_OK;

public class UserSettingsFragment extends Fragment {

    private static final UserSettingsFragment ourInstance = new UserSettingsFragment();
    private final int AVATAR_RG = 1;
    RelativeLayout signoutSetting;
    ImageView userAvatar;
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

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signoutSetting = view.findViewById(R.id.sign_out_settings);
        userAvatar = view.findViewById(R.id.current_user_avatar);

        setAvatarPhoto(view);
    }

    private void setAvatarPhoto(View view) {
        if (UserManager.currentUser != null) {
            String avatarPhoto = UserManager.currentUser.avatarUri;
            if (avatarPhoto != null && !avatarPhoto.equals("null")) {
                Glide.with(view).load(UserManager.currentUser.avatarUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(userAvatar);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        signoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserOnlineStatus userOnlineStatus = UserOnlineStatus.getInstance();
                userOnlineStatus.logOut();
            }
        });

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, AVATAR_RG);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == AVATAR_RG && data != null) {
            Uri photo = data.getData();
            UserManager.currentUser.avatarUri = photo.toString();
            setAvatarPhoto(getView());
            StorageReference photosReference = FirebaseStorage.getInstance().getReference().child("Images").child(UserManager.getCurrentUserID());
            photosReference.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri photoUri = taskSnapshot.getDownloadUrl();
                    UserManager.setCurrentUserAvatarUri(photoUri);
                }
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
