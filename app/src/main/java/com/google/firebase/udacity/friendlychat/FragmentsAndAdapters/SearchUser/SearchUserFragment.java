package com.google.firebase.udacity.friendlychat.FragmentsAndAdapters.SearchUser;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.google.firebase.udacity.friendlychat.Managers.App.ColorManager;
import com.google.firebase.udacity.friendlychat.Managers.App.FragmentsManager;
import com.google.firebase.udacity.friendlychat.Managers.Database.SearchForUser;
import com.google.firebase.udacity.friendlychat.R;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import io.reactivex.disposables.Disposable;

public class SearchUserFragment extends Fragment {

	private FoundUsersAdapter adapter;
	private RecyclerView recyclerView;
	private SearchView searchView;

	private Disposable editTextDisposable;
	private Disposable searchForUser;

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
		int statusBarColor = ColorManager.getStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		if (statusBarColor != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			getActivity().getWindow().setStatusBarColor(statusBarColor);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		adapter = new FoundUsersAdapter(getContext());
		recyclerView.setAdapter(adapter);

		observeEditText();
	}


	private void observeEditText() {

		editTextDisposable = RxSearchView.queryTextChanges(searchView)
				.startWith("")
				.map(String::valueOf)
				.subscribe(query -> {
					adapter.clear();

					if (!query.equals(""))
						searchForUser = SearchForUser.searchUserByName(query)
								.subscribe(user -> adapter.pushUser(user));
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

		if (searchForUser != null && !searchForUser.isDisposed())
			searchForUser.dispose();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (editTextDisposable != null && !editTextDisposable.isDisposed())
			editTextDisposable.dispose();

		if (searchForUser != null && !searchForUser.isDisposed())
			searchForUser.dispose();
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

