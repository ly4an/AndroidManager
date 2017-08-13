package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
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
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import database.DaoSession;
import database.ServiceRequest;
import database.ServiceRequestDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.network.FacilicomNetworkParser;
import ru.facilicom24.manager.utils.NetworkHelper;

public class RequestListActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String MODE_EXTRA = "MODE_EXTRA";

	final static public String TYPE_RECEIVED = "Получена";
	final static public String TYPE_INPROGRESS = "В работе";
	final static public String TYPE_SERVICED = "Выполнена";
	final static public String TYPE_POSTPONED = "Отложена";
	final static public String TYPE_REWORK = "На доработке";
	final static public String TYPE_CANCELED = "Снята";
	final static public String TYPE_ASSIGNED = "Назначена";

	final static int REQUEST_EDIT_CODE = 1;

	Mode mode;
	View emptyTextView;
	ListView requestListView;
	RequestsAdapter requestsAdapter;

	static List<ServiceRequest> load(Mode mode) {
		switch (mode) {
			case Execute: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.CanExecute.eq(1))
						.list();
			}

			case Mine: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Mine.eq(1))
						.list();
			}

			case Assigned: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.in(TYPE_RECEIVED, TYPE_ASSIGNED))
						.list();
			}

			case InProgress: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.eq(TYPE_INPROGRESS))
						.list();
			}

			case Postponed: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.eq(TYPE_POSTPONED))
						.list();
			}

			case Serviced: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.eq(TYPE_SERVICED))
						.list();
			}

			case Canceled: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.eq(TYPE_CANCELED))
						.list();
			}

			case Rework: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.eq(TYPE_REWORK))
						.list();
			}

			case DeadLineOut: {
				List<ServiceRequest> serviceRequests = new ArrayList<>();

				List<ServiceRequest> mineServiceRequests = FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.Status.in(TYPE_RECEIVED, TYPE_ASSIGNED))
						.list();

				if (mineServiceRequests != null && mineServiceRequests.size() > 0) {
					Date dateTimeNow = new Date();
					for (ServiceRequest serviceRequest : mineServiceRequests) {
						try {
							if (FacilicomApplication.dateTimeFormat9.parse(serviceRequest.getDueDate()).getTime() < dateTimeNow.getTime()) {
								serviceRequests.add(serviceRequest);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				}

				return serviceRequests;
			}

			case Mark: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().queryBuilder()
						.where(ServiceRequestDao.Properties.NeedEvaluate.eq(1))
						.list();
			}

			case All: {
				return FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().loadAll();
			}
		}

		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_request_list);

		mode = (Mode) getIntent().getSerializableExtra(MODE_EXTRA);

		int[] titles = {
				R.string.request_menu_activity_1,
				R.string.request_menu_activity_2,
				R.string.request_menu_activity_4,

				R.string.request_control_menu_1,
				R.string.request_control_menu_2,
				R.string.request_control_menu_3,
				R.string.request_control_menu_4,
				R.string.request_control_menu_5,
				R.string.request_control_menu_6,
				R.string.request_control_menu_7,
				R.string.request_control_menu_8
		};

		((TextView) findViewById(R.id.titleFontTextView)).setText(titles[mode.ordinal()]);

		requestsAdapter = new RequestsAdapter();

		emptyTextView = findViewById(R.id.emptyTextView);
		requestListView = (ListView) findViewById(R.id.requestListView);

		requestsAdapter.emptyCheck();

		requestListView.setAdapter(requestsAdapter);
		requestListView.setOnItemClickListener(this);

		((EditText) findViewById(R.id.searchEditText)).addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				requestsAdapter.getFilter().filter(charSequence);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}

	@Override
	public void onBackPressed() {
		back();
	}

	void back() {
		setResult(Activity.RESULT_OK, new Intent().putExtra(MODE_EXTRA, mode));
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		startActivityForResult(new Intent(this, RequestEditActivity.class)
						.putExtra(RequestEditActivity.REQUEST_ID, l)
						.putExtra(RequestEditActivity.REQUEST_MODE, mode),
				REQUEST_EDIT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case REQUEST_EDIT_CODE: {
						long id = data.getLongExtra(RequestEditActivity.REQUEST_ID, 0);

						if (id > 0) {
							for (int index = 0; index < requestsAdapter.filterableServiceRequests.size(); index++) {
								if (requestsAdapter.filterableServiceRequests.get(index).getId() == id) {
									requestsAdapter.filterableServiceRequests.remove(index);
									break;
								}
							}

							requestsAdapter.emptyCheck();
							requestsAdapter.notifyDataSetChanged();
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		back();
	}

	enum Mode {
		// RequestMenuActivity

		Execute,
		Mark,
		All,

		// RequestControlMenuActivity

		Mine,
		Assigned,
		InProgress,
		Postponed,
		Serviced,
		Canceled,
		Rework,
		DeadLineOut
	}

	private class RequestsAdapter
			extends BaseAdapter
			implements Filterable {

		LayoutInflater layoutInflater;
		List<ServiceRequest> serviceRequests;
		List<ServiceRequest> filterableServiceRequests;

		RequestsAdapter() {
			layoutInflater = (LayoutInflater) RequestListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			serviceRequests = filterableServiceRequests = load(mode);

			if (NetworkHelper.isConnected(RequestListActivity.this)
					&& (mode == Mode.Execute || mode == Mode.Mark)) {

				final ProgressDialog progressDialog = new ProgressDialog(RequestListActivity.this);

				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setMessage(getString(R.string.request_list_progress));

				progressDialog.show();

				FacilicomNetworkClient.getServiceRequestsCount(new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						DaoSession daoSession = FacilicomApplication.getInstance().getDaoSession();

						daoSession.getServiceRequestDao().deleteAll();
						daoSession.getServiceRequestServantDao().deleteAll();
						daoSession.getServiceRequestLogDao().deleteAll();
						daoSession.getServiceRequestFileDao().deleteAll();

						getServiceRequests(0, FacilicomNetworkParser.parseServiceRequestCount(responseBody), progressDialog);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						notifyDataSetChanged(progressDialog);
					}
				});
			}
		}

		void getServiceRequests(final int offset, final int count, final ProgressDialog progressDialog) {
			if (count > 0) {
				FacilicomNetworkClient.getServiceRequests(offset, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						int length = FacilicomNetworkParser.parseServiceRequests(responseBody);

						if (length > 0) {
							getServiceRequests(offset + length, count - length, progressDialog);
						} else {
							serviceRequests = filterableServiceRequests = load(mode);
							notifyDataSetChanged(progressDialog);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						serviceRequests = filterableServiceRequests = load(mode);
						notifyDataSetChanged(progressDialog);
					}
				});
			} else {
				serviceRequests = filterableServiceRequests = load(mode);
				notifyDataSetChanged(progressDialog);
			}
		}

		@Override
		public int getCount() {
			return filterableServiceRequests.size();
		}

		@Override
		public Object getItem(int i) {
			return filterableServiceRequests.get(i);
		}

		@Override
		public long getItemId(int i) {
			return filterableServiceRequests.get(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = layoutInflater.inflate(R.layout.activity_request_list_item, viewGroup, false);
			}

			ServiceRequest serviceRequest = filterableServiceRequests.get(i);

			((TextView) view.findViewById(R.id.numberTextView)).setText(String.valueOf(serviceRequest.getID()));
			((TextView) view.findViewById(R.id.dateTextView)).setText(serviceRequest.getDueDate());
			((TextView) view.findViewById(R.id.statusTextView)).setText(serviceRequest.getStatus());
			((TextView) view.findViewById(R.id.contentTextView)).setText(serviceRequest.getContent());

			((TextView) view.findViewById(R.id.facilityNameTextView)).setText(serviceRequest.getFacilityName());
			((TextView) view.findViewById(R.id.facilityAddressTextView)).setText(serviceRequest.getFacilityAddress());

			return view;
		}

		public void notifyDataSetChanged(ProgressDialog progressDialog) {
			progressDialog.dismiss();
			notifyDataSetChanged();
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence charSequence) {
					String pattern = charSequence.toString().toLowerCase();
					ArrayList<ServiceRequest> filterableServiceRequests = new ArrayList<>();

					for (int i = 0; i < serviceRequests.size(); i++) {
						ServiceRequest serviceRequest = serviceRequests.get(i);

						String text = TextUtils.concat(
								serviceRequest.getID().toString(),
								serviceRequest.getFacilityName(),
								serviceRequest.getFacilityAddress(),
								serviceRequest.getContent()
						).toString();

						if (text.toLowerCase().contains(pattern)) {
							filterableServiceRequests.add(serviceRequest);
						}
					}

					FilterResults filterResults = new FilterResults();

					filterResults.values = filterableServiceRequests;
					filterResults.count = filterableServiceRequests.size();

					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
					filterableServiceRequests = (List<ServiceRequest>) filterResults.values;

					emptyCheck();
					notifyDataSetChanged();
				}
			};
		}

		void emptyCheck() {
			if (requestsAdapter.filterableServiceRequests != null && requestsAdapter.filterableServiceRequests.size() > 0) {
				requestListView.setVisibility(View.VISIBLE);
				emptyTextView.setVisibility(View.GONE);
			} else {
				requestListView.setVisibility(View.GONE);
				emptyTextView.setVisibility(View.VISIBLE);
			}
		}
	}
}
