package ru.facilicom24.manager.activities;

import android.app.Activity;
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
import database.TSTask;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoTSTaskRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.network.FacilicomNetworkParser;
import ru.facilicom24.manager.utils.NetworkHelper;

public class TaskListActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public int TS_TASK_DO = 1;

	final static public String TS_TASK_ID = "TS_TASK_ID";
	final static public String TS_TASKS_REFRESH = "TS_TASKS_REFRESH";
	final static public String TS_TASKS_MODE = "TS_TASKS_MODE";
	final static public String TS_TASKS_RUNNING = "Running";
	final static public String TS_TASKS_CONTROL = "Control";
	final static public String TS_TASKS_CLOSED = "Closed";

	Mode mode;
	TasksAdapter tasksAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_task_list);

		mode = (Mode) getIntent().getSerializableExtra(TS_TASKS_MODE);

		((TextView) findViewById(R.id.titleFontTextView)).setText(mode == Mode.Running
				? R.string.task_list_caption_running
				: R.string.task_list_caption_control
		);

		ListView tasksListView = (ListView) findViewById(R.id.tasksListView);

		tasksAdapter = new TasksAdapter(mode);

		tasksListView.setAdapter(tasksAdapter);
		tasksListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		startActivityForResult(new Intent(this, TaskDoActivity.class)
						.putExtra(TS_TASKS_MODE, mode)
						.putExtra(TS_TASK_ID, adapterView.getAdapter().getItemId(i)),
				TS_TASK_DO
		);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null) {
			switch (requestCode) {
				case TS_TASK_DO: {
					long id = data.getLongExtra(TS_TASKS_REFRESH, 0);

					if (id > 0) {
						for (int index = 0; index < tasksAdapter.tasks.size(); index++) {
							if (tasksAdapter.tasks.get(index).getId() == id) {
								tasksAdapter.tasks.remove(index);
								tasksAdapter.notifyDataSetChanged();
								break;
							}
						}
					}
				}
				break;
			}
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	public enum Mode {
		Running,
		Control
	}

	class TasksAdapter extends BaseAdapter {
		List<TSTask> tasks;
		LayoutInflater layoutInflater;

		TasksAdapter(Mode mode) {
			layoutInflater = (LayoutInflater) TaskListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			tasks = DaoTSTaskRepository.loadByStatus(TaskListActivity.this, mode == Mode.Running
					? TS_TASKS_RUNNING
					: TS_TASKS_CONTROL
			);

			if (NetworkHelper.isConnected(TaskListActivity.this)) {
				final ProgressDialog progressDialog = new ProgressDialog(TaskListActivity.this);

				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setMessage(getString(mode == Mode.Running
						? R.string.task_list_activity_load_running
						: R.string.task_list_activity_load_control
				));

				final Mode finalMode = mode;

				progressDialog.show();
				FacilicomNetworkClient.getTaskToDo(new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						DaoTSTaskRepository.deleteAll(TaskListActivity.this);
						FacilicomNetworkParser.parseTSTasks(TaskListActivity.this, responseBody);

						tasks = DaoTSTaskRepository.loadByStatus(TaskListActivity.this, finalMode == Mode.Running
								? TS_TASKS_RUNNING
								: TS_TASKS_CONTROL
						);

						notifyDataSetChanged(progressDialog);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						notifyDataSetChanged(progressDialog);
					}
				});
			}
		}

		@Override
		public int getCount() {
			return tasks.size();
		}

		@Override
		public TSTask getItem(int i) {
			return tasks.get(i);
		}

		@Override
		public long getItemId(int i) {
			return tasks.get(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = layoutInflater.inflate(R.layout.activity_task_list_item, viewGroup, false);
			}

			TSTask tsTask = tasks.get(i);

			((TextView) view.findViewById(R.id.dateTextView)).setText(tsTask.getDueDate().substring(0, 5));
			((TextView) view.findViewById(R.id.accountTextView)).setText(tsTask.getAccountName());
			((TextView) view.findViewById(R.id.addressTextView)).setText(tsTask.getAccountAddress());
			((TextView) view.findViewById(R.id.noteTextView)).setText(tsTask.getTitle());

			return view;
		}

		void notifyDataSetChanged(ProgressDialog progressDialog) {
			progressDialog.dismiss();
			notifyDataSetChanged();
		}
	}
}
