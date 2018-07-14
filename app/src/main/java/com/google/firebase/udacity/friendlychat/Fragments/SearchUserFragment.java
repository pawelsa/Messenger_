package com.google.firebase.udacity.friendlychat.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.udacity.friendlychat.Gestures.LeftToRightDetector;
import com.google.firebase.udacity.friendlychat.Managers.FragmentsManager;
import com.google.firebase.udacity.friendlychat.R;
import com.google.firebase.udacity.friendlychat.SearchForUser.FoundUsersAdapter;
import com.google.firebase.udacity.friendlychat.SearchForUser.SearchForUser;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import io.reactivex.disposables.Disposable;

public class SearchUserFragment extends Fragment {

	private FoundUsersAdapter adapter;
	private RecyclerView recyclerView;
	private SearchView searchView;
	private Disposable editTextDisposable;
	private SearchForUser searchForUser;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_search_user, container, false);

		final GestureDetector gesture = LeftToRightDetector.getInstance(getActivity());

		v.setOnTouchListener((v12, event) -> gesture.onTouchEvent(event));

		Toolbar toolbar = v.findViewById(R.id.searchToolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(v1 -> FragmentsManager.goBack(getActivity()));
		setupToolbar();

		recyclerView = v.findViewById(R.id.searchUsersResults);
		searchView = v.findViewById(R.id.searchView);

		return v;
	}

	private void setupToolbar() {

		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);

		actionBar.setTitle(null);
		actionBar.setSubtitle(null);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		adapter = new FoundUsersAdapter(getContext());
		recyclerView.setAdapter(adapter);
		searchForUser = new SearchForUser(adapter);

		observeEditText();
	}


	private void observeEditText() {

		editTextDisposable = RxSearchView.queryTextChanges(searchView)
				.startWith("")
				.map(cast -> String.valueOf(cast))
				.subscribe(query -> {
					adapter.clear();
					searchForUser.searchUserByName(query);
				});
	}

	@Override
	public void onResume() {
		super.onResume();

		observeEditText();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (editTextDisposable != null && !editTextDisposable.isDisposed())
			editTextDisposable.dispose();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				FragmentsManager.goBack(getActivity());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
