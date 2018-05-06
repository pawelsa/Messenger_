package com.google.firebase.udacity.friendlychat.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.udacity.friendlychat.Managers.UserManager;
import com.google.firebase.udacity.friendlychat.Objects.User;
import com.google.firebase.udacity.friendlychat.PseudonymRecyclerViewAdapter;
import com.google.firebase.udacity.friendlychat.R;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATIONALIST_PSEUDONYM;
import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;
import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.MY_PSEUDONYM;

public class PseudonymBottomSheet extends BottomSheetDialogFragment {

    User conversationalist;
    private RecyclerView pseudonymRecyclerView;

    public PseudonymBottomSheet() {
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.pseudonym_change_layout, null);
        pseudonymRecyclerView = contentView.findViewById(R.id.pseudonym_change_recycler_view);
        dialog.setContentView(contentView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {

            conversationalist = new User(bundle);
            PseudonymRecyclerViewAdapter adapter = new PseudonymRecyclerViewAdapter(getActivity(), bundle.getString(CONVERSATION_ID));
            adapter.add(conversationalist, bundle.getString(CONVERSATIONALIST_PSEUDONYM));
            adapter.add(UserManager.currentUser, bundle.getString(MY_PSEUDONYM));

            pseudonymRecyclerView.setAdapter(adapter);
            pseudonymRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
    }
}
