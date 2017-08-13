package ru.facilicom24.manager.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.JobTitle;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.network.FacilicomNetworkParser;

public class JobTitleActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;

	JobTitleListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_jobtitle);

		Intent intent = getIntent();
		String subdivisionUID = intent.getStringExtra("SubdivisionUID");

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		final ProgressDialog dialog = new ProgressDialog(this);

		dialog.setMessage(getString(R.string.jobtitle_load));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();

		FacilicomNetworkClient.getJobTitle(subdivisionUID, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				dialog.dismiss();

				adapter = new JobTitleListAdapter(JobTitleActivity.this, FacilicomNetworkParser.parseJobTitle(responseBody));
				listView.setAdapter(adapter);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				dialog.dismiss();

				AlertDialog.Builder builder = new AlertDialog.Builder(JobTitleActivity.this);

				builder.setTitle(R.string.message_label);
				builder.setMessage(R.string.server_error);
				builder.setNegativeButton(R.string.next_button, null);

				builder.create().show();
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		JobTitle JobTitle = (JobTitle) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("UID", JobTitle.getUID());
		intent.putExtra("Name", JobTitle.getName());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class JobTitleListAdapter
			extends BaseAdapter {

		LayoutInflater inflater;
		List<JobTitle> objects;

		JobTitleListAdapter(Context context, List<JobTitle> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public JobTitle getItem(int i) {
			return objects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = inflater.inflate(R.layout.list_item, parent, false);
			}

			((TextView) view.findViewById(R.id.name)).setText(objects.get(position).getName());

			return view;
		}
	}
}
