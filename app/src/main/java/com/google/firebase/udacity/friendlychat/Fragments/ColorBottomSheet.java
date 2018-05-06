package com.google.firebase.udacity.friendlychat.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.udacity.friendlychat.ColorRecyclerViewAdapter;
import com.google.firebase.udacity.friendlychat.R;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;

public class ColorBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView colorRecyclerView;
    private String conversationID;

    public ColorBottomSheet() {

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.color_bottom_sheet_fragment, null);
        colorRecyclerView = contentView.findViewById(R.id.color_recyclerView);
        dialog.setContentView(contentView);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            conversationID = bundle.getString(CONVERSATION_ID);

            ColorRecyclerViewAdapter colorRecyclerViewAdapter = new ColorRecyclerViewAdapter(conversationID);
            colorRecyclerViewAdapter.add(4383220);
            colorRecyclerViewAdapter.add(314005);
            colorRecyclerViewAdapter.add(16767300);
            colorRecyclerViewAdapter.add(16016685);
            colorRecyclerViewAdapter.add(9481558);
            colorRecyclerViewAdapter.add(1712192);

            colorRecyclerView.setAdapter(colorRecyclerViewAdapter);
            colorRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
        }
    }
}