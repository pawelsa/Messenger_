package com.google.firebase.udacity.friendlychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import static com.google.firebase.udacity.friendlychat.MainActivity.mUsername;

/**
 * Created by Paweł on 17.04.2018.
 */

public class MessagesFragment extends Fragment {
	
	public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
	private static final int RC_PHOTO_PICKER = 2;
	private ImageButton mPhotoPickerButton;
	private EditText mMessageEditText;
	private ImageView mSendButton;
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		View item = getActivity().findViewById(R.id.allInfo);
		if (item != null) item.setVisibility(View.INVISIBLE);
		
		initializeReferencesToViews();
		settingUpUIFunctionality();
	}
	
	private void initializeReferencesToViews() {
		
		mPhotoPickerButton = getActivity().findViewById(R.id.photoPickerButton);
		mMessageEditText = getActivity().findViewById(R.id.messageEditText);
		mSendButton = getActivity().findViewById(R.id.sendButton);
	}
	
	private void settingUpUIFunctionality() {
		
		photoPickerButtonFunctionality();
		
		messageEditTextFunctionality();
		
		sendButtonFunctionality();
	}
	
	
	private void photoPickerButtonFunctionality() {
		
		mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/jpeg");
				intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
				startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
			}
		});
	}
	
	private void messageEditTextFunctionality() {
		
		mMessageEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.toString().trim().length() > 0) {
					mSendButton.setEnabled(true);
				}
				else {
					mSendButton.setEnabled(false);
				}
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
	}
	
	private void sendButtonFunctionality() {
		
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				FriendlyMessage newMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null);
				
				if (newMessage.getText().trim().length() > 0) {
					
					// mDatabaseReference.push().setValue(newMessage);
					// Clear input box
					mMessageEditText.setText("");
				}
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.message_fragment, container, false);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

/*        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
			Uri photoUri = data.getData();
            final StorageReference photoReference = storagePhotosReference.child(photoUri.getLastPathSegment());

            photoReference.putFile(photoUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FriendlyMessage friendlyMessageWithPhoto = new FriendlyMessage(null, mUsername, taskSnapshot.getDownloadUrl().toString());
                    mDatabaseReference.push().setValue(friendlyMessageWithPhoto);
                }
            });
        }*/
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.clear();
		inflater.inflate(R.menu.conversation_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.allInfo: {
				ConversationInfoFragment conversationInfoFragment = new ConversationInfoFragment();
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right).replace(R.id.messageFragment, conversationInfoFragment, "infoFragment").addToBackStack(null).commit();
			}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
