package com.google.firebase.udacity.friendlychat.Managers;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.udacity.friendlychat.Objects.ChatRoomUserObject;
import com.google.firebase.udacity.friendlychat.Objects.User;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.udacity.friendlychat.SearchForUser.ConversationRequest.CHAT_ROOM;
import static com.google.firebase.udacity.friendlychat.SearchForUser.ConversationRequest.PARTICIPANTS;


public class UserManager {

	public static final String USERS = "users";
	private static final String AVATAR_URI = "avatarUri";

	static User currentUser;

	public static void removeConversationName(final String newName, final String conversationID) {
		if (newName != null && conversationID != null) {

			DatabaseReference pseudonymReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID).child("conversationName");
			pseudonymReference.setValue(newName);
		}
	}

	public static void updateUserPseudonym(final String newPseudonym, final String conversationID, final String userID) {

		if (newPseudonym != null && conversationID != null && userID != null) {
			DatabaseReference pseudonymReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID).child(PARTICIPANTS).child(userID);
			Map<String, Object> pseudonymToUpdate = ChatRoomUserObject.updateName(newPseudonym);
			pseudonymReference.updateChildren(pseudonymToUpdate);
		}
	}

	public static void removeConversationName(final String conversationID) {
		DatabaseReference pseudonymReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOM).child(conversationID).child("conversationName");
		pseudonymReference.removeValue();
	}

	public static void setCurrentUserAvatarUri(Uri photoUri) {
		Map<String, Object> photoUpdate = new HashMap<>();
		currentUser.avatarUri = photoUri.toString();
		photoUpdate.put(AVATAR_URI, photoUri.toString());
		DatabaseReference referenceToUserAvatarUri = FirebaseDatabase.getInstance().getReference().child(USERS).child(currentUser.User_ID);
		referenceToUserAvatarUri.updateChildren(photoUpdate);
	}

	public static String getCurrentUserID() {
		return currentUser.User_ID;
	}

	public static String getCurrentUserName() {
		return currentUser.User_Name;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User downloadedCurrentUser) {
		currentUser = downloadedCurrentUser;
	}

	public static String getCurrentUserAvatarUri() {
		return currentUser.avatarUri;
	}
}