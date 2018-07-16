package com.google.firebase.udacity.friendlychat.Managers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.udacity.friendlychat.Fragments.AllConversationsFragment;
import com.google.firebase.udacity.friendlychat.Fragments.ConversationInfoFragment;
import com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment;
import com.google.firebase.udacity.friendlychat.Fragments.SearchUserFragment;
import com.google.firebase.udacity.friendlychat.R;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;

public class FragmentsManager {

	public static final String CONVERSATIONID = "ConversationID";
	private static final String BASE_FRAGMENT = "main_fragment";

	public static void startMessageFragment(AppCompatActivity activity, String conversationID) {

		MessagesFragment messagesFragment = MessagesFragment.getInstance();
		Bundle bundle = new Bundle();
		bundle.putString(CONVERSATION_ID, conversationID);
		messagesFragment.setArguments(bundle);

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
				.replace(R.id.messageFragment, messagesFragment, "messageFragment")
				.addToBackStack("main_fragment_replace")
				.commit();

	}

	public static void startBaseFragment(AppCompatActivity activity) {
		AllConversationsFragment conversationsFragment = new AllConversationsFragment();
		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.messageFragment, conversationsFragment, BASE_FRAGMENT).commit();
	}

	public static void startSearchUserFragment(AppCompatActivity activity) {
		SearchUserFragment searchUserFragment = new SearchUserFragment();
		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
				.replace(R.id.messageFragment, searchUserFragment, "messageFragment")
				.addToBackStack("main_fragment_replace")
				.commit();
	}

	public static void startConversationInfoFragment(AppCompatActivity activity, Bundle args) {
		ConversationInfoFragment conversationInfoFragment = new ConversationInfoFragment();

		conversationInfoFragment.setArguments(args);

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
				.replace(R.id.messageFragment, conversationInfoFragment, "infoFragment")
				.addToBackStack(null)
				.commit();
	}

	public static void goBack(Activity activity) {
		FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
		//Log.i("Fragment quantity", Integer.toString(fm.getBackStackEntryCount()));
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
		}
	}

	public static void destroy(AppCompatActivity activity) {
		Log.i("Fragment manager", "destroy");
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.popBackStack();
	}
}
