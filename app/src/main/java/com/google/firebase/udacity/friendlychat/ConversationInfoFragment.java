package com.google.firebase.udacity.friendlychat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paweł on 17.04.2018.
 */

public class ConversationInfoFragment extends Fragment {
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
		
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.conversation_info, container, false);
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
				FragmentManager fm = getFragmentManager();
				if (fm.getBackStackEntryCount() > 0) {
					fm.popBackStack();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
