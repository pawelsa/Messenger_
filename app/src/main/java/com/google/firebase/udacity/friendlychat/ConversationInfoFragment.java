package com.google.firebase.udacity.friendlychat;

<<<<<<< HEAD
=======
import android.app.Fragment;
import android.app.FragmentManager;
>>>>>>> github/1.00-starting-point
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

<<<<<<< HEAD
import me.yokeyword.swipebackfragment.SwipeBackFragment;

=======
>>>>>>> github/1.00-starting-point
/**
 * Created by Paweł on 17.04.2018.
 */

<<<<<<< HEAD
public class ConversationInfoFragment extends SwipeBackFragment {
=======
public class ConversationInfoFragment extends Fragment {
>>>>>>> github/1.00-starting-point
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
<<<<<<< HEAD

		setHasOptionsMenu(true);
=======
		
		setHasOptionsMenu(true);
		
>>>>>>> github/1.00-starting-point
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
<<<<<<< HEAD
		View view = inflater.inflate(R.layout.conversation_info, container, false);
		return attachToSwipeBack(view);
=======
		return inflater.inflate(R.layout.conversation_info, container, false);
>>>>>>> github/1.00-starting-point
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
<<<<<<< HEAD
				android.support.v4.app.FragmentManager fm = getFragmentManager();
=======
				FragmentManager fm = getFragmentManager();
>>>>>>> github/1.00-starting-point
				if (fm.getBackStackEntryCount() > 0) {
					fm.popBackStack();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
