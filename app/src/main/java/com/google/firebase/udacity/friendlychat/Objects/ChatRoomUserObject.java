package com.google.firebase.udacity.friendlychat.Objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paweł on 21.05.2018.
 */

public class ChatRoomUserObject {
	
	public static Map<String, Object> createUser(String userID) {
		
		return createUser(userID, null);
	}
	
	public static Map<String, Object> createUser(String userID, String userName) {
		
		Map<String, Object> newUser = new HashMap<>();
		newUser.put("ID", userID);
		if (userName != null) newUser.put("Name", userName);
		
		return newUser;
	}
	
	public static Map<String, Object> updateName(String userName) {
		
		Map<String, Object> newUser = new HashMap<>();
		if (userName != null) newUser.put("Name", userName);
		
		return newUser;
	}
}
