package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import database.TSTask;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoTSTaskRepository;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class TaskDoActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		CaptionFragment.OnFragmentInteractionListener {

	final int DETAILED_RESULT = 1;
	final int CONTROL_RESULT = 2;

	TSTask tsTask;
	NFAdapter adapter;
	ArrayList<NFItem> items;
	TaskListActivity.Mode mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_task_do);

		mode = (TaskListActivity.Mode) getIntent().getSerializableExtra(TaskListActivity.TS_TASKS_MODE);

		((TextView) findViewById(R.id.titleFontTextView)).setText(mode == TaskListActivity.Mode.Running
				? R.string.task_do_caption_running
				: R.string.task_do_caption_control
		);

		tsTask = DaoTSTaskRepository.get(this, getIntent().getLongExtra(TaskListActivity.TS_TASK_ID, 0));

		if (tsTask != null) {
			items = new ArrayList<>();

			items.add(new NFItem(NFItem.Type.Value, "Клиент", tsTask.getClientName()));
			items.add(new NFItem(NFItem.Type.Value, "Объект", TextUtils.concat(tsTask.getAccountName(), ". ", tsTask.getAccountAddress()).toString()));
			items.add(new NFItem(NFItem.Type.Value, "Контакт", tsTask.getContactName()));
			items.add(new NFItem(NFItem.Type.Value, "Задача", tsTask.getTitle(), NFItem.SHOW_ALL));
			items.add(new NFItem(NFItem.Type.Value, "Автор", tsTask.getAuthor()));
			items.add(new NFItem(NFItem.Type.Value, "Исполнитель", tsTask.getExecutor()));

			switch (mode) {
				case Running: {
					if (tsTask.getControlComment() != null && tsTask.getControlComment().length() > 0) {
						items.add(new NFItem(NFItem.Type.Value, "Комментарий к доработке", tsTask.getControlComment()));
					}

					items.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_task_do_text, new NFItem.ILayoutBase() {

						@Override
						public void OnCreate(View view) {
							TextView resultTextView = (TextView) view.findViewById(R.id.resultTextView);

							resultTextView.setText(tsTask.getDetailedResult());
							resultTextView.setOnClickListener(TaskDoActivity.this);
						}
					}));
				}
				break;

				case Control: {
					items.add(new NFItem(NFItem.Type.Value, "Результат", tsTask.getDetailedResult()));

					items.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_task_control_text, new NFItem.ILayoutBase() {

						@Override
						public void OnCreate(View view) {
							TextView commentTextView = (TextView) view.findViewById(R.id.commentTextView);

							commentTextView.setText(tsTask.getControlComment());
							commentTextView.setOnClickListener(TaskDoActivity.this);

							view.findViewById(R.id.doFontButton).setOnClickListener(TaskDoActivity.this);
							view.findViewById(R.id.postponedFontButton).setOnClickListener(TaskDoActivity.this);
						}
					}));
				}
				break;
			}

			adapter = new NFAdapter(this, items);
			((ListView) findViewById(R.id.taskListView)).setAdapter(adapter);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.doFontButton: {
				applyControl(TaskListActivity.TS_TASKS_CLOSED);
			}
			break;

			case R.id.postponedFontButton: {
				applyControl(TaskListActivity.TS_TASKS_RUNNING);
			}
			break;

			case R.id.resultTextView: {
				Intent intent = new Intent(this, TextActivity.class);

				intent.putExtra("Caption", getString(R.string.task_do_text));
				intent.putExtra("Text", tsTask.getDetailedResult());

				startActivityForResult(intent, DETAILED_RESULT);
			}
			break;

			case R.id.commentTextView: {
				Intent intent = new Intent(this, TextActivity.class);

				intent.putExtra("Caption", getString(R.string.task_control_text));
				intent.putExtra("Text", tsTask.getControlComment());

				startActivityForResult(intent, CONTROL_RESULT);
			}
			break;
		}
	}

	void applyRunning() {
		if (tsTask.getDetailedResult() != null && tsTask.getDetailedResult().length() > 0) {
			if (NetworkHelper.isConnected(this)) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.message)
						.setMessage(R.string.task_do_confirm)
						.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								JSONObject taskJSON = getTaskRunningJson();

								if (taskJSON != null) {
									final ProgressDialog progressDialog = new ProgressDialog(TaskDoActivity.this);

									progressDialog.setMessage(getString(R.string.task_main_task_post_result_send));
									progressDialog.setCancelable(false);
									progressDialog.setCanceledOnTouchOutside(false);

									progressDialog.show();

									FacilicomNetworkClient.postTask(TaskDoActivity.this, taskJSON, new AsyncHttpResponseHandler() {

										@Override
										public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
											progressDialog.dismiss();
											Toast.makeText(TaskDoActivity.this, R.string.task_main_task_post_result_done, Toast.LENGTH_LONG).show();

											DaoTSTaskRepository.delete(TaskDoActivity.this, tsTask);

											setResult(Activity.RESULT_OK, new Intent().putExtra(TaskListActivity.TS_TASKS_REFRESH, tsTask.getId()));
											finish();
										}

										@Override
										public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
											progressDialog.dismiss();
											Toast.makeText(TaskDoActivity.this, R.string.task_main_task_post_data_result_2, Toast.LENGTH_LONG).show();
										}
									});
								} else {
									Toast.makeText(TaskDoActivity.this, R.string.task_main_task_post_data_result_1, Toast.LENGTH_LONG).show();
								}
							}
						})
						.setNegativeButton(R.string.btn_no, null)
						.show();
			} else {
				Toast.makeText(this, R.string.errorConnection, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, R.string.task_do_caption_running_required, Toast.LENGTH_LONG).show();
		}
	}

	void applyControl(final String status) {
		if ((tsTask.getControlComment() != null && tsTask.getControlComment().length() > 0)
				|| status.equals(TaskListActivity.TS_TASKS_CLOSED)) {

			if (NetworkHelper.isConnected(this)) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.message)
						.setMessage(status.equals(TaskListActivity.TS_TASKS_CLOSED)
								? R.string.task_do_confirm
								: R.string.task_do_confirm_postponed
						)
						.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								JSONObject taskJSON = getTaskControlJson(status);

								if (taskJSON != null) {
									final ProgressDialog progressDialog = new ProgressDialog(TaskDoActivity.this);

									progressDialog.setMessage(getString(status.equals(TaskListActivity.TS_TASKS_CLOSED)
											? R.string.task_main_task_post_result_send
											: R.string.task_main_task_post_result_postponed_send)
									);

									progressDialog.setCancelable(false);
									progressDialog.setCanceledOnTouchOutside(false);

									progressDialog.show();

									FacilicomNetworkClient.postTask(TaskDoActivity.this, taskJSON, new AsyncHttpResponseHandler() {

										@Override
										public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
											progressDialog.dismiss();
											Toast.makeText(TaskDoActivity.this, status.equals(TaskListActivity.TS_TASKS_CLOSED)
															? R.string.task_main_task_post_result_done
															: R.string.task_main_task_post_result_postponed_done,
													Toast.LENGTH_LONG).show();

											DaoTSTaskRepository.delete(TaskDoActivity.this, tsTask);

											setResult(Activity.RESULT_OK, new Intent().putExtra(TaskListActivity.TS_TASKS_REFRESH, tsTask.getId()));
											finish();
										}

										@Override
										public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
											progressDialog.dismiss();
											Toast.makeText(TaskDoActivity.this, status.equals(TaskListActivity.TS_TASKS_CLOSED)
															? R.string.task_main_task_post_data_result_2
															: R.string.task_main_task_post_data_result_postponed_2,
													Toast.LENGTH_LONG).show();
										}
									});
								} else {
									Toast.makeText(TaskDoActivity.this, status.equals(TaskListActivity.TS_TASKS_CLOSED)
													? R.string.task_main_task_post_data_result_1
													: R.string.task_main_task_post_data_result_postponed_1,
											Toast.LENGTH_LONG).show();
								}
							}
						})
						.setNegativeButton(R.string.btn_no, null)
						.show();
			} else {
				Toast.makeText(this, R.string.errorConnection, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, R.string.task_do_caption_control_required, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case DETAILED_RESULT: {
					tsTask.setDetailedResult(data.getStringExtra("Text"));
					adapter.notifyDataSetChanged();
				}
				break;

				case CONTROL_RESULT: {
					tsTask.setControlComment(data.getStringExtra("Text"));
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	JSONObject getTaskRunningJson() {
		JSONObject result = new JSONObject();

		try {
			result.put("TaskUID", tsTask.getID());
			result.put("Result", tsTask.getDetailedResult());
			result.put("Status", TaskListActivity.TS_TASKS_CLOSED);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return result;
	}

	JSONObject getTaskControlJson(String status) {
		JSONObject result = new JSONObject();

		try {
			result.put("TaskUID", tsTask.getID());
			result.put("ControlComment", tsTask.getControlComment());
			result.put("Status", status);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return result;
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
	}

	@Override
	public void captionFragmentOnSavePressed() {
		applyRunning();
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return false;
	}

	@Override
	public boolean saveIcon() {
		return getIntent().getSerializableExtra(TaskListActivity.TS_TASKS_MODE) == TaskListActivity.Mode.Running;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}
}
