package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.dialogs.DatePickerDialogFragment;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

import static ru.facilicom24.manager.fragments.BaseFragment.DATE_PICKER_DIALOG;

public class TaskActivity
		extends BaseActivity
		implements
		DatePickerDialogFragment.IDatePickerDialogListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	static final int TASK_TYPE_ACTIVITY = 1;
	static final int VID_TASK_ACTIVITY = 2;
	static final int EMPLOYEE_ACTIVITY = 3;
	static final int DATE_ACTIVITY = 4;
	static final int TASK_TARGET_TEXT_ACTIVITY = 5;

	static final int TASK_TARGET_ACTIVITY = 6;
	static final int TASK_TEXT_ACTIVITY = 7;

	ListView listView;
	JSONObject parameters;
	JSONObject dataContext;
	InteractionAdapter adapter;
	List<InteractionItem> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_task);

		parameters = new JSONObject();

		try {
			parameters.put("Type", 0);
			parameters.put("Kind", 0);

			parameters.put("Employee", "");
			parameters.put("Date", "");
			parameters.put("Target", "");
			parameters.put("Text", "");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		items = new ArrayList<>();

		items.add(new InteractionItem("fragment_task_item"));

		items.add(new InteractionItem("Тип", "Выбрать"));
		items.add(new InteractionItem("Вид", "Выбрать"));
		items.add(new InteractionItem("Исполнитель", "Выбрать"));
		items.add(new InteractionItem("Срок", "Выбрать"));

		items.add(new InteractionItem("fragment_task_text"));

		listView = (ListView) findViewById(R.id.items);

		adapter = new InteractionAdapter();
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i) {
					case TASK_TYPE_ACTIVITY: {
						startActivityForResult(new Intent(TaskActivity.this, TaskTypeActivity.class), TASK_TYPE_ACTIVITY);
					}
					break;

					case VID_TASK_ACTIVITY: {
						startActivityForResult(new Intent(TaskActivity.this, VidTaskActivity.class), VID_TASK_ACTIVITY);
					}
					break;

					case EMPLOYEE_ACTIVITY: {
						startActivityForResult(new Intent(TaskActivity.this, EmployeeActivity.class), EMPLOYEE_ACTIVITY);
					}
					break;

					case DATE_ACTIVITY: {
						try {
							showDialog(DatePickerDialogFragment.newInstance(MobileActivity.stringToDate(parameters.getString("Date"))), DATE_PICKER_DIALOG);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
					break;
				}
			}
		});

		findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (validateAndBind()) {
					setResult(Activity.RESULT_OK, new Intent().putExtra("Task", dataContext.toString()));
					finish();
				}
			}
		});
	}

	boolean validateAndBind() {
		try {
			int type = parameters.getInt("Type");
			int kind = parameters.getInt("Kind");

			String employee = parameters.getString("Employee");
			String date = parameters.getString("Date");
			String target = parameters.getString("Target");
			String text = parameters.getString("Text");

			if (type == 0) {
				String message = getString(R.string.task_validate_type);
				showAlertDialog(R.id.alert_dialog, getString(R.string.error), message);
				return false;
			}

			if (kind == 0) {
				String message = getString(R.string.task_validate_kind);
				showAlertDialog(R.id.alert_dialog, getString(R.string.error), message);
				return false;
			}

			if (employee.length() == 0) {
				String message = getString(R.string.task_validate_employee);
				showAlertDialog(R.id.alert_dialog, getString(R.string.error), message);
				return false;
			}

			if (date.length() == 0) {
				String message = getString(R.string.task_validate_date);
				showAlertDialog(R.id.alert_dialog, getString(R.string.error), message);
				return false;
			}

			if (target.length() == 0) {
				String message = getString(R.string.task_validate_target);
				showAlertDialog(R.id.alert_dialog, getString(R.string.error), message);
				return false;
			}

			if (text.length() == 0) {
				String message = getString(R.string.task_validate_text);
				showAlertDialog(R.id.alert_dialog, getString(R.string.error), message);
				return false;
			}

			Calendar dueDate = MobileActivity.stringToDate(date);
			String _dueDate = FacilicomApplication.dateTimeFormat5.format(dueDate.getTime());

			String taskUID = UUID.randomUUID().toString();

			Calendar createdOn = Calendar.getInstance();
			String _createdOn = FacilicomApplication.dateTimeFormat7.format(createdOn.getTime());

			dataContext = new JSONObject();

			dataContext.put("ActUID", "");

			dataContext.put("ClientID", "");
			dataContext.put("AccountID", "");
			dataContext.put("ServiceTypeUID", "");
			dataContext.put("ContactID", "");

			dataContext.put("TaskUID", taskUID);

			dataContext.put("TypeID", type);
			dataContext.put("VidID", kind);
			dataContext.put("ExecutorLogin", employee);
			dataContext.put("DueDate", _dueDate);

			dataContext.put("Subject", target);
			dataContext.put("Text", text);

			dataContext.put("Result", "");
			dataContext.put("Status", "Running");
			dataContext.put("CreatedOn", _createdOn);

			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case TASK_TYPE_ACTIVITY: {
					items.get(TASK_TYPE_ACTIVITY).value = data.getStringExtra("Name");
					adapter.notifyDataSetChanged();

					try {
						parameters.put("Type", data.getIntExtra("Type", 0));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case VID_TASK_ACTIVITY: {
					items.get(VID_TASK_ACTIVITY).value = data.getStringExtra("Name");
					adapter.notifyDataSetChanged();

					try {
						parameters.put("Kind", data.getIntExtra("Kind", 0));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case EMPLOYEE_ACTIVITY: {
					items.get(EMPLOYEE_ACTIVITY).value = data.getStringExtra("Name");
					adapter.notifyDataSetChanged();

					try {
						parameters.put("Employee", data.getStringExtra("Employee"));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case TASK_TARGET_ACTIVITY: {
					String target = data.getStringExtra("Text");

					items.get(TASK_TARGET_TEXT_ACTIVITY).target = target;
					adapter.notifyDataSetChanged();

					try {
						parameters.put("Target", target);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case TASK_TEXT_ACTIVITY: {
					String text = data.getStringExtra("Text");

					items.get(TASK_TARGET_TEXT_ACTIVITY).text = text;
					adapter.notifyDataSetChanged();

					try {
						parameters.put("Text", text);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;
			}
		}
	}

	@Override
	public void onDateSet(Calendar calendar) {
		String date = MobileActivity.dateToString(calendar.getTime());

		items.get(DATE_ACTIVITY).value = date;
		adapter.notifyDataSetChanged();

		try {
			parameters.put("Date", date);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class InteractionItem {
		String name;
		String value;

		String target;
		String text;

		InteractionItem(String name) {
			this.name = name;
		}

		InteractionItem(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private class InteractionAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public InteractionItem getItem(int i) {
			return items.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			InteractionItem item = getItem(i);

			switch (item.name) {
				case "fragment_task_item": {
					view = getLayoutInflater().inflate(R.layout.fragment_task_item, viewGroup, false);
				}
				break;

				case "fragment_task_text": {
					view = getLayoutInflater().inflate(R.layout.fragment_task_text, viewGroup, false);

					TextView targetTextView = (TextView) view.findViewById(R.id.target);
					TextView textTextView = (TextView) view.findViewById(R.id.text);

					targetTextView.setText(item.target);
					textTextView.setText(item.text);

					targetTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							try {
								String target = parameters.getString("Target");

								Intent intent = new Intent(TaskActivity.this, TextActivity.class);

								intent.putExtra("Caption", getString(R.string.task_target));
								intent.putExtra("Text", target);

								startActivityForResult(intent, TASK_TARGET_ACTIVITY);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					});

					textTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							try {
								String text = parameters.getString("Text");

								Intent intent = new Intent(TaskActivity.this, TextActivity.class);

								intent.putExtra("Caption", getString(R.string.task_text));
								intent.putExtra("Text", text);

								startActivityForResult(intent, TASK_TEXT_ACTIVITY);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					});
				}
				break;

				default: {
					view = getLayoutInflater().inflate(R.layout.interaction_item, viewGroup, false);

					TextView title = (TextView) view.findViewById(R.id.interaction_title);
					TextView option = (TextView) view.findViewById(R.id.interaction_option);

					title.setText(item.name);
					option.setText(item.value);
				}
				break;
			}

			return view;
		}
	}
}
