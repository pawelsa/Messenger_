package com.google.firebase.udacity.friendlychat.Managers.Database;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.udacity.friendlychat.Objects.Message;

public class MessageSender {

	private static StorageReference storagePhotosReference = FirebaseStorage.getInstance().getReference().child("chat_photos");
	private static DatabaseReference databaseMessagesReference = FirebaseDatabase.getInstance().getReference().child("messages");

	public static void sendPhoto(Uri photoUri, String conversationID) {

		final DatabaseReference conversationReference = databaseMessagesReference.child(conversationID);
		String key = conversationReference.push().getKey();

		final StorageReference photoReference = storagePhotosReference.child(conversationID).child(key);

		photoReference.child(conversationID).putFile(photoUri).addOnSuccessListener(taskSnapshot -> {
			Message friendlyMessageWithPhoto = new Message(UserManager.getCurrentUserID(), null, taskSnapshot.getDownloadUrl().toString());
			conversationReference.child(key).setValue(friendlyMessageWithPhoto).addOnSuccessListener(listener ->
					Log.i("Message Send", "Photo"));
		});
	}

	public static void sendMessage(String text, String conversationID) {

		Message newMessage = new Message(UserManager.getCurrentUserID(), text);

		final DatabaseReference messageReference = databaseMessagesReference.child(conversationID);

		messageReference.push().setValue(newMessage).addOnSuccessListener(listener ->
				Log.i("Message Send", "Message"));
	}


}
