package com.google.firebase.udacity.friendlychat.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.R;


public class ConversationInfoFragment extends Fragment {
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.conversation_info, container, false);

        final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i("State", "OnDestroy");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
                LeftToRightDetector.goBack(getActivity());
                return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
