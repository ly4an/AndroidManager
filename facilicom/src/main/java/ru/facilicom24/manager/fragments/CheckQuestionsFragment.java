package ru.facilicom24.manager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.TextActivity;

public class CheckQuestionsFragment
		extends BaseFragment {

	TextView mResultsView;

	public CheckQuestionsFragment() {
	}

	public static CheckQuestionsFragment newInstance(String results) {
		Bundle bundle = new Bundle();
		bundle.putString("R", results);

		CheckQuestionsFragment fragment = new CheckQuestionsFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_check_questions, container, false);

		mResultsView = (TextView) view.findViewById(R.id.results);

		mResultsView.setText(getArguments().getString("R"));
		mResultsView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), TextActivity.class);

				intent.putExtra("Caption", getString(R.string.confirmation_result_label));
				intent.putExtra("Text", mResultsView.getText().toString());

				startActivityForResult(intent, 101);
			}
		});

		return view;
	}

	public String getCommunication() {
		return mResultsView.getText().toString();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 101 && data != null) {
			ArrayList<String> words = new ArrayList<>();
			for (String word : data.getStringExtra("Text").trim().split(" ")) {
				if (word.length() > 0) {
					words.add(word);
				}
			}

			mResultsView.setText(TextUtils.join(" ", words));
		}
	}
}
