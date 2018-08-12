/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.friendlychat.Objects;

import com.google.firebase.database.ServerValue;
import com.google.firebase.udacity.friendlychat.Managers.Database.UserManager;

import java.util.HashMap;
import java.util.Map;

public class Message {

	public String text;
	public String userID;
	public String photoUrl;
	public Map<String, Object> timestamp;
/*    public Map usersWhoRead;
    public int status;*/


	public Message() {
	}

	public Message(String userID, String text, String photoUrl) {
		this.text = text;
		this.userID = userID;
		this.photoUrl = photoUrl;
		timestamp = new HashMap<>();
		timestamp.put("timestamp", ServerValue.TIMESTAMP);
	}

	public Message(String text) {
		this.text = text;
		this.userID = UserManager.getCurrentUserID();
		timestamp = new HashMap<>();
		timestamp.put("timestamp", ServerValue.TIMESTAMP);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public Map getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(HashMap timestamp) {
		this.timestamp = timestamp;
	}
}
