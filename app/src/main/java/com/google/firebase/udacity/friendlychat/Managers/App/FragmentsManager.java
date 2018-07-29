package com.google.firebase.udacity.friendlychat.Managers.App;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.AllConversations.AllConversationsFragment;
import com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.ConversationInfoFragment;
import com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.Messages.MessagesFragment;
import com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.SearchUser.SearchUserFragment;
import com.google.firebase.udacity.friendlychat.R;

import static com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.Messages.MessagesFragment.CONVERSATION_ID;

public class FragmentsManager {

	private static final FragmentsManager ourInstance = new FragmentsManager();
	private AllConversationsFragment conversationsFragment;
	private SearchUserFragment searchUserFragment;


	public static final String CONVERSATIONID = "ConversationID";
	private static final String BASE_FRAGMENT = "main_fragment";
	private ConversationInfoFragment conversationInfoFragment;
	private MessagesFragment messagesFragment;

	private FragmentsManager() {
	}

	public static FragmentsManager getInstance() {
		return ourInstance;
	}

	public static void goBack(Activity activity) {
		FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
		//Log.i("Fragment quantity", Integer.toString(fm.getBackStackEntryCount()));
		if (fm.getBackStackEntryCount() > 0) {
			if (fm.getBackStackEntryCount() > 1 && fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals("ConversationInfoFragment"))
				fm.popBackStack("ConversationInfoFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			else
				fm.popBackStack();
		}
	}

	public void startMessageFragment(AppCompatActivity activity, String conversationID) {
		if (ourInstance.messagesFragment == null) {
			ourInstance.messagesFragment = new MessagesFragment();
			Bundle bundle = new Bundle();
			bundle.putString(CONVERSATION_ID, conversationID);
			ourInstance.messagesFragment.setArguments(bundle);
		}

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
				.replace(R.id.messageFragment, ourInstance.messagesFragment, "MessageFragment")
				.addToBackStack("MessageFragment")
				.commit();
	}

	public void startBaseFragment(AppCompatActivity activity) {
		FragmentManager fm = activity.getSupportFragmentManager();
		Log.i("Opened fragments", Integer.toString(fm.getBackStackEntryCount()));
		if (fm.findFragmentByTag(BASE_FRAGMENT) == null) {

			if (ourInstance.conversationsFragment == null)
				ourInstance.conversationsFragment = new AllConversationsFragment();
			FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.messageFragment, ourInstance.conversationsFragment, BASE_FRAGMENT).commit();
		}
	}

	public void startSearchUserFragment(AppCompatActivity activity) {
		if (ourInstance.searchUserFragment == null)
			ourInstance.searchUserFragment = new SearchUserFragment();
		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
				.replace(R.id.messageFragment, ourInstance.searchUserFragment, "SearchUserFragment")
				.addToBackStack("SearchUserFragment")
				.commit();
	}

	public void startConversationInfoFragment(AppCompatActivity activity, Bundle args) {
		if (ourInstance.conversationInfoFragment == null) {
			ourInstance.conversationInfoFragment = new ConversationInfoFragment();
			ourInstance.conversationInfoFragment.setArguments(args);
		}

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
				.replace(R.id.messageFragment, ourInstance.conversationInfoFragment, "ConversationInfoFragment")
				.addToBackStack("ConversationInfoFragment")
				.commit();
	}

	public static void destroy(AppCompatActivity activity) {
		Log.i("Fragment manager", "destroy");
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.popBackStack();
	}
}
