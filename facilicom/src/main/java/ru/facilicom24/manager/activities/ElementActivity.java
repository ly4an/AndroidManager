package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.ElementMark;

public class ElementActivity
		extends BaseActivity
		implements
		AdapterView.OnItemClickListener,
		View.OnClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	public static final String FORM_INFO_KEY = "form";

	public static final String OBJECT_KEY = "object";

	public static final String ELEMENT_NUMBER_KEY = "element_number";

	public static final String ZONE_NAME = "zone_name";

	public static final String ELEMENT_NAME_KEY = "element_name";

	public static final String ELEMENT_MARKS = "marks";

	List<ElementMark> marks;

	List<ElementMark> marksToDelete = new ArrayList<ElementMark>();

	HListView marksView;

	TextView mCommentView;

	View mCommentPane;

	MarksAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_element);

		((TextView) findViewById(R.id.form)).setText(getIntent().getStringExtra(FORM_INFO_KEY));
		((TextView) findViewById(R.id.object)).setText(getIntent().getStringExtra(OBJECT_KEY));
		((TextView) findViewById(R.id.zone)).setText(getIntent().getStringExtra(ZONE_NAME));
		((TextView) findViewById(R.id.number)).setText(getIntent().getStringExtra(ELEMENT_NUMBER_KEY));
		((TextView) findViewById(R.id.element)).setText(getIntent().getStringExtra(ELEMENT_NAME_KEY));

		marks = (List<ElementMark>) getIntent().getSerializableExtra(ELEMENT_MARKS);
		if (marks == null) finish();

		marksView = (HListView) findViewById(R.id.marks);
		marksView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new MarksAdapter();
		marksView.setAdapter(mAdapter);
		marksView.setOnItemClickListener(this);

		findViewById(R.id.btn_remove_1).setOnClickListener(this);
		findViewById(R.id.btn_remove_2).setOnClickListener(this);

		mCommentPane = findViewById(R.id.comment_pane);
		mCommentView = (TextView) mCommentPane.findViewById(R.id.comment);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		marksView.setItemChecked(position, true);

		ElementMark mark = marks.get(position);
		if (!TextUtils.isEmpty(mark.getComment())) {
			mCommentPane.setVisibility(View.VISIBLE);
			mCommentView.setText(mark.getComment());
		} else {
			mCommentPane.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		int markPosition = marksView.getCheckedItemPosition();
		if (markPosition != -1) {
			marksToDelete.add(marks.remove(markPosition));
			if (marks.isEmpty()) {
				onBackPressed();
			} else {
				int checkedItem = marksView.getCheckedItemPosition();
				if (checkedItem != -1)
					marksView.setItemChecked(checkedItem, false);
				mAdapter.notifyDataSetChanged();
				mCommentPane.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra(ELEMENT_MARKS, (ArrayList<ElementMark>) marksToDelete);
		setResult(RESULT_OK, data);
		super.onBackPressed();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class MarksAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return marks.size();
		}

		@Override
		public ElementMark getItem(int i) {
			return marks.get(i);
		}

		@Override
		public long getItemId(int i) {
			return getItem(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ElementMark mark = getItem(i);

			switch (mark.getValue()) {
				case 0:
					view = getLayoutInflater().inflate(R.layout.mark_zero_layout, viewGroup, false);
					break;
				case 1:

					view = getLayoutInflater().inflate(R.layout.mark_one_layout, viewGroup, false);
					break;

				case 2:
					view = getLayoutInflater().inflate(R.layout.mark_two_layout, viewGroup, false);
					break;
			}

			return view;
		}
	}
}
