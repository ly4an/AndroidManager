package ru.facilicom24.manager.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.DismissReason;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.network.FacilicomNetworkParser;

public class DismissReasonActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;
	EditText searchField;

	DismissReasonListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_dismiss_reason);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
				DismissReasonActivity.this.adapter.getFilter().filter(charSequence);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		final ProgressDialog dialog = new ProgressDialog(this);

		dialog.setMessage(getString(R.string.dismiss_reason_load));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();

		FacilicomNetworkClient.getDismissReasons(new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				dialog.dismiss();

				adapter = new DismissReasonListAdapter(DismissReasonActivity.this, FacilicomNetworkParser.parseDismissReasons(responseBody));
				listView.setAdapter(adapter);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				dialog.dismiss();

				AlertDialog.Builder builder = new AlertDialog.Builder(DismissReasonActivity.this);

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
		DismissReason dismissReason = (DismissReason) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("UID", dismissReason.getUID());
		intent.putExtra("Name", dismissReason.getName());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class DismissReasonListAdapter
			extends BaseAdapter
			implements Filterable {

		LayoutInflater inflater;

		List<DismissReason> inObjects;
		List<DismissReason> outObjects;

		DismissReasonListAdapter(Context context, List<DismissReason> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			this.inObjects = objects;
			outObjects = objects;
		}

		@Override
		public int getCount() {
			return outObjects.size();
		}

		@Override
		public DismissReason getItem(int i) {
			return outObjects.get(i);
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

			((TextView) view.findViewById(R.id.name)).setText(outObjects.get(position).getName());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					ArrayList<DismissReason> objects = new ArrayList<>();

					String pattern = constraint.toString().toLowerCase();

					for (int i = 0; i < inObjects.size(); i++) {
						String dataNames = String.valueOf(inObjects.get(i).getName());

						if (dataNames.toLowerCase().contains(pattern)) {
							objects.add(inObjects.get(i));
						}
					}

					//

					FilterResults results = new FilterResults();

					results.values = objects;
					results.count = objects.size();

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					outObjects = (List<DismissReason>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
