package ru.facilicom24.manager.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import database.ServiceRequest;
import database.ServiceRequestLog;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class RequestHistoryActivity
		extends FragmentActivity
		implements CaptionSimpleFragment.OnFragmentInteractionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_request_history);

		ServiceRequest serviceRequest = FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().load(getIntent().getLongExtra(RequestEditActivity.REQUEST_ID, 0));

		if (serviceRequest != null) {
			((TextView) findViewById(R.id.titleFontTextView)).setText(String.format(getString(R.string.request_history_caption), serviceRequest.getID()));
			((ListView) findViewById(R.id.historyListView)).setAdapter(new RequestHistoryAdapter(serviceRequest.getLog()));
		} else {
			findViewById(R.id.titleFontTextView).setVisibility(View.GONE);
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class RequestHistoryAdapter extends BaseAdapter {

		List<ServiceRequestLog> log;
		LayoutInflater layoutInflater;

		RequestHistoryAdapter(List<ServiceRequestLog> log) {
			this.log = log;
			layoutInflater = (LayoutInflater) RequestHistoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return log.size();
		}

		@Override
		public Object getItem(int i) {
			return log.get(i);
		}

		@Override
		public long getItemId(int i) {
			return log.get(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = layoutInflater.inflate(R.layout.activity_request_history_item, viewGroup, false);
			}

			ServiceRequestLog serviceRequestLog = log.get(i);

			((TextView) view.findViewById(R.id.dateTextView)).setText(serviceRequestLog.getStatusSetOn());
			((TextView) view.findViewById(R.id.statusTextView)).setText(serviceRequestLog.getStatus());
			((TextView) view.findViewById(R.id.nameTextView)).setText(serviceRequestLog.getStatusSetByFullName());
			((TextView) view.findViewById(R.id.commentTextView)).setText(serviceRequestLog.getComment());

			return view;
		}
	}
}
