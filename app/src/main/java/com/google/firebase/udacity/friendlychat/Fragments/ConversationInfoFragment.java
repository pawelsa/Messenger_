package com.google.firebase.udacity.friendlychat.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.R;

import static com.google.firebase.udacity.friendlychat.Fragments.MessagesFragment.CONVERSATION_ID;


public class ConversationInfoFragment extends Fragment {

    Bundle bundle;
    private LinearLayout colorSettings;
    private LinearLayout pseudonymSettings;
    private String conversationID;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        setupActionBar();

        bundle = getArguments();
        if (bundle != null) {
            conversationID = bundle.getString(CONVERSATION_ID);
        } else {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }

        colorSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorBottomSheet colorBottomSheet = new ColorBottomSheet();
                Bundle args = new Bundle();
                args.putString(CONVERSATION_ID, conversationID);
                colorBottomSheet.setArguments(args);
                colorBottomSheet.show(getActivity().getSupportFragmentManager(), colorBottomSheet.getTag());
            }
        });

        pseudonymSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PseudonymBottomSheet pseudonymBottomSheet = new PseudonymBottomSheet();
                pseudonymBottomSheet.setArguments(bundle);
                pseudonymBottomSheet.show(getActivity().getSupportFragmentManager(), pseudonymBottomSheet.getTag());


            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.settings_details));
            actionBar.setSubtitle(null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_info, container, false);

        final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colorSettings = view.findViewById(R.id.settings_color);
        pseudonymSettings = view.findViewById(R.id.settings_pseudonym);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("State", "OnDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LeftToRightDetector.goBack(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
