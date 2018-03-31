package com.google.firebase.udacity.friendlychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.udacity.friendlychat.Managers.ListOfConversationsManager;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 26.03.2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context context;

    private List<ChatRoom> chatRoomList;
    private List<ChatRoom> finalListChatRoom;

    UsersAdapter(Context context) {
        this.context = context;
        this.chatRoomList = new ArrayList<>();
        this.finalListChatRoom = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.userNameTextView.setText(finalListChatRoom.get(position).conversationalist.User_Name);
        holder.userOnlineStatusTextView.setText(Boolean.toString(finalListChatRoom.get(position).conversationalist.isOnline));

        holder.userItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                Log.i("Arraysize", Integer.toString(finalListChatRoom.size()) + "   " + Integer.toString(chatRoomList.size()));
                if (finalListChatRoom.get(position).isEmpty()) {
                    ListOfConversationsManager.openChatRoomWith(finalListChatRoom.get(position).conversationalist.User_ID);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return finalListChatRoom.size();
    }

    void add(ChatRoom chatRoom) {

        if (chatRoom.conversationalist == null) {
            chatRoomList.add(chatRoom);
            startLookingForConversationalist(getConversationalistID(chatRoom));
        } else
            finalListChatRoom.add(chatRoom);
        notifyDataSetChanged();
    }

    private String getConversationalistID(ChatRoom chatRoom) {

        if (!chatRoom.chatRoomObject.myID.equals(UserManager.currentUser.User_ID)) {
            return chatRoom.chatRoomObject.myID;
        } else if (!chatRoom.chatRoomObject.conversationalistID.equals(UserManager.currentUser.User_ID)) {
            return chatRoom.chatRoomObject.conversationalistID;
        } else
            return null;
    }

    private void startLookingForConversationalist(String conversationalistID) {
        if (conversationalistID != null)
            UserManager.findUser(conversationalistID);
    }

    void updateList(ChatRoom chatRoom) {

        for (int i = 0; i < chatRoomList.size(); i++) {
            if (chatRoomEquals(i, chatRoom)) {
                chatRoomList.set(i, chatRoom);
                notifyDataSetChanged();
                return;
            }
        }
        add(chatRoom);
    }

    private boolean chatRoomEquals(int i, ChatRoom chatRoom) {
        if (chatRoomList.get(i).chatRoomObject != null && chatRoomList.get(i).chatRoomObject.conversationID != null)
            return chatRoomList.get(i).chatRoomObject.conversationID.equals(chatRoom.chatRoomObject.conversationID);
        else
            return false;
    }

    //TODO: Maybe there is better way to check it
    void pushUser(User pushedUser) {

        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.chatRoomObject.conversationalistID.equals(pushedUser.User_ID) || chatRoom.chatRoomObject.myID.equals(pushedUser.User_ID)) {
                chatRoom.conversationalist = pushedUser;
                finalListChatRoom.add(chatRoom);
                chatRoomList.remove(chatRoom);
                notifyDataSetChanged();
                return;
            }
        }
        for (ChatRoom chatRoom : finalListChatRoom) {
            if (chatRoom.conversationalist != null && chatRoom.conversationalist.User_ID.equals(pushedUser.User_ID)) {
                chatRoom.conversationalist = pushedUser;
                notifyDataSetChanged();
                return;
            }
        }
    }

    void clear() {
        finalListChatRoom.clear();
        chatRoomList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;
        ImageView userAvatarImageView;
        TextView userOnlineStatusTextView;
        LinearLayout userItemLayout;

        ViewHolder(View view) {
            super(view);
            userNameTextView = view.findViewById(R.id.userName);
            userAvatarImageView = view.findViewById(R.id.userAvatar);
            userOnlineStatusTextView = view.findViewById(R.id.onlineStatus);
            userItemLayout = view.findViewById(R.id.user_item_layout);
        }
    }
}
