package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.udacity.friendlychat.Managers.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 26.03.2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private UserManager userManager;
    private Context context;
    private List<ChatRoom> chatRoomList;
    private List<ChatRoom> finalListChatRoom;

    UsersAdapter(Context context, UserManager.OnUserDownloadListener onUserDownloadListener) {
        this.context = context;
        this.chatRoomList = new ArrayList<>();
        this.finalListChatRoom = new ArrayList<>();
        this.userManager = new UserManager(onUserDownloadListener);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        setTexts(holder, position);

        Glide.with(context).load("http://digitalspyuk.cdnds.net/17/25/980x490/landscape-1498216547-avatar-neytiri.jpg").apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.userAvatarImageView);

        onLayoutClick(holder, position);
    }

    private void setTexts(final ViewHolder holder, final int position) {
        holder.userNameTextView.setText(finalListChatRoom.get(position).conversationalist.User_Name);

        String time = null;
        holder.lastMessage.setText(Boolean.toString(finalListChatRoom.get(position).conversationalist.isOnline));

        if (finalListChatRoom.get(position).chatRoomObject.lastMessageSendTime != null) {
            time = (String) finalListChatRoom.get(position).chatRoomObject.lastMessageSendTime.get("timestamp");
        }
        holder.lastMessageSendTime.setText(time);
    }

    private void onLayoutClick(final ViewHolder holder, final int position) {

        holder.userItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

                if (finalListChatRoom.get(position).isEmpty()) {
                    //ListOfConversationsManager.openChatRoomWith(finalListChatRoom.get(position).conversationalist.User_ID);
                }
                MessagesFragment messagesFragment = MessagesFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("conversationID", finalListChatRoom.get(position).chatRoomObject.conversationID);
                bundle.putString("displayName", finalListChatRoom.get(position).conversationalist.User_Name);
                messagesFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = ((Activity) context).getFragmentManager().beginTransaction();
                fragmentTransaction
                        //.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out)
                        .setCustomAnimations(R.animator.enter_from_right, R.animator.none, R.animator.none, R.animator.exit_to_right)
                        .replace(R.id.messageFragment, messagesFragment, "messageFragment")
                        .addToBackStack("main_fragment_replace")
                        .commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return finalListChatRoom.size();
    }

    private void add(ChatRoom chatRoom) {

        if (chatRoom.conversationalist == null) {
            chatRoomList.add(chatRoom);
            startLookingForConversationalist(getConversationalistID(chatRoom));
        } else {
            finalListChatRoom.add(chatRoom);
            notifyDataSetChanged();
        }
    }

    private String getConversationalistID(ChatRoom chatRoom) {

        if (!chatRoom.chatRoomObject.myID.equals(UserManager.getCurrentUserID())) {
            return chatRoom.chatRoomObject.myID;
        } else if (!chatRoom.chatRoomObject.conversationalistID.equals(UserManager.getCurrentUserID())) {
            return chatRoom.chatRoomObject.conversationalistID;
        } else {
            return null;
        }
    }

    private void startLookingForConversationalist(String conversationalistID) {
        if (conversationalistID != null) userManager.findUser(conversationalistID);
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
        return chatRoomList.get(i).chatRoomObject != null && chatRoomList.get(i).chatRoomObject.conversationID != null && chatRoomList.get(i).chatRoomObject.conversationID.equals(chatRoom.chatRoomObject.conversationID);
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
        TextView lastMessage;
        TextView lastMessageSendTime;
        RelativeLayout userItemLayout;

        ViewHolder(View view) {
            super(view);
            userNameTextView = view.findViewById(R.id.username);
            userAvatarImageView = view.findViewById(R.id.avatar);
            lastMessage = view.findViewById(R.id.lastMessage);
            lastMessageSendTime = view.findViewById(R.id.time);
            userItemLayout = view.findViewById(R.id.user_item_layout);
        }
    }
}
