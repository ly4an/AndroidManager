package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.Reason;

public class CommentsActivity
		extends BaseActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	public static final String MARK_EXTRA = "mark";
	public static final String REASONS_ETRA = "reasonos";
	public static final String COMMENTS_EXTRA = "comment";

	List<Reason> mReasons;

	int mark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_comments);

		mark = getIntent().getIntExtra(MARK_EXTRA, -1);
		mReasons = (ArrayList<Reason>) getIntent().getSerializableExtra(REASONS_ETRA);

		if (mark == -1)
			finish();

		((ListView) findViewById(R.id.reasons)).setAdapter(new ReasonsAdapter());
		((ListView) findViewById(R.id.reasons)).setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Intent data = new Intent();

		data.putExtra(MARK_EXTRA, mark);
		data.putExtra(COMMENTS_EXTRA, mReasons.get(position).getName());

		setResult(RESULT_OK, data);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class ReasonsAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mReasons.size();
		}

		@Override
		public Reason getItem(int position) {
			return mReasons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.reason_item, parent, false);
			}

			((TextView) convertView).setText(getItem(position).getName());
			return convertView;
		}
	}
}
