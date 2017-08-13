package ru.facilicom24.manager.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.DictionaryActivity;
import ru.facilicom24.manager.cache.CheckBlanksRepository;
import ru.facilicom24.manager.cache.CheckObjectsRepository;
import ru.facilicom24.manager.cache.CheckTypesRepository;
import ru.facilicom24.manager.cache.ChecksRepository;
import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.model.CheckBlank;
import ru.facilicom24.manager.model.CheckObject;
import ru.facilicom24.manager.model.CheckType;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.SessionManager;

public class CreateCheckFragment
		extends BaseFragment
		implements
		View.OnClickListener,
		ListView.OnItemClickListener {

	static public final int CREATECHECKFRAGMENT_CATEGORY = 2000;
	static public final int CREATECHECKFRAGMENT_ACCOUNT = 2001;
	static public final int CREATECHECKFRAGMENT_BLANK = 2002;

	static final int CATEGORY = 2;
	static final int ACCOUNT = 3;
	static final int BLANK = 4;

	Check check;
	String defaultNo;
	NFAdapter adapter;
	ArrayList<NFItem> form;
	ICreateCheckFragmentListener createCheckFragmentListener;

	public CreateCheckFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (getTargetFragment() instanceof ICreateCheckFragmentListener) {
			createCheckFragmentListener = (ICreateCheckFragmentListener) getTargetFragment();
		} else if (context instanceof ICreateCheckFragmentListener) {
			createCheckFragmentListener = (ICreateCheckFragmentListener) context;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_check, container, false);

		defaultNo = getString(R.string.default_no);

		//

		check = new Check();

		if (FacilicomApplication.getAccount(getActivity()) > 0) {
			CheckObject checkObject = new CheckObjectsRepository(getActivity()).getObjectById(FacilicomApplication.getAccount(getActivity()));

			if (checkObject != null) {
				CheckType checkType = new CheckTypesRepository(getActivity()).getTypeById(checkObject.getCheckTypeId());

				if (checkType != null) {
					check.setCheckType(checkType);
					check.setCheckObject(checkObject);
				}
			}
		}

		check.setCheckId(UUID.randomUUID().toString());
		check.setDate(new Date());
		check.setComment("");

		//

		form = new ArrayList<>();

		form.add(new NFItem(NFItem.Type.Title, "Проверка качества"));
		form.add(new NFItem(NFItem.Type.Value, "Дата", FacilicomApplication.dateTimeFormat4.format(check.getDate())));
		form.add(new NFItem(NFItem.Type.Choose, "Вид деятельности", check.getCheckType() != null ? check.getCheckType().getCheckName() : "Выбрать"));

		form.add(new NFItem(NFItem.Type.Choose, "Объект", check.getCheckObject() != null
				? TextUtils.concat(check.getCheckObject().getCheckObjectName(), ". ", check.getCheckObject().getCheckObjectAddress()).toString()
				: "Выбрать"
		));

		form.add(new NFItem(NFItem.Type.Choose, "Бланк", "Выбрать"));

		view.findViewById(R.id.createFontButton).setOnClickListener(this);

		ListView formListView = (ListView) view.findViewById(R.id.formListView);

		adapter = new NFAdapter(getActivity(), form);

		formListView.setAdapter(adapter);
		formListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		if (check.getCheckType() != null
				&& check.getCheckObject() != null
				&& check.getCheckBlank() != null) {

			new ChecksRepository(getActivity()).create(check);

			SessionManager.getInstance().setLastEditedZone(-1);
			SessionManager.getInstance().setLastEditedElement(-1);

			if (createCheckFragmentListener != null) {
				createCheckFragmentListener.onCheckFormCreated();
			}
		} else {
			showAlertDialog(R.id.alert_dialog, R.string.error, R.string.create_check_error);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case CATEGORY: {
				startActivityForResult(new Intent(getActivity(), DictionaryActivity.class)
								.putExtra(DictionaryActivity.REQUEST_CODE, CREATECHECKFRAGMENT_CATEGORY),
						CATEGORY);
			}
			break;

			case ACCOUNT: {
				if (check.getCheckType() != null) {
					startActivityForResult(new Intent(getActivity(), DictionaryActivity.class)
									.putExtra(DictionaryActivity.REQUEST_CODE, CREATECHECKFRAGMENT_ACCOUNT)
									.putExtra(DictionaryActivity.PARAMETER_CATEGORY_UID, check.getCheckType().getCheckId()),
							ACCOUNT);
				} else {
					Toast.makeText(getActivity(), R.string.check_blank_check_object_empty, Toast.LENGTH_LONG).show();
				}
			}
			break;

			case BLANK: {
				if (check.getCheckType() != null) {
					startActivityForResult(new Intent(getActivity(), DictionaryActivity.class)
									.putExtra(DictionaryActivity.REQUEST_CODE, CREATECHECKFRAGMENT_BLANK)
									.putExtra(DictionaryActivity.PARAMETER_CATEGORY_UID, check.getCheckType().getCheckId()),
							BLANK);
				} else {
					Toast.makeText(getActivity(), R.string.check_blank_check_object_empty, Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case CATEGORY: {
						CheckType checkType = new CheckTypesRepository(getActivity()).getTypeById(data.getStringExtra(DictionaryActivity.KEY));

						if (checkType != null) {
							check.setCheckType(checkType);
							form.get(CATEGORY).setText(data.getStringExtra(DictionaryActivity.VALUE));

							check.setCheckObject(null);
							form.get(ACCOUNT).setText(defaultNo);

							check.setCheckBlank(null);
							form.get(BLANK).setText(defaultNo);

							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getActivity(), R.string.check_type_empty, Toast.LENGTH_LONG).show();
						}
					}
					break;

					case ACCOUNT: {
						int accountId = data.getIntExtra(DictionaryActivity.KEY, 0);

						if (FacilicomApplication.checkInRule(accountId, 2)) {
							CheckObject checkObject = new CheckObjectsRepository(getActivity()).getObjectById(accountId);

							if (checkObject != null) {
								check.setCheckObject(checkObject);
								form.get(ACCOUNT).setText(data.getStringExtra(DictionaryActivity.VALUE));

								check.setCheckBlank(null);
								form.get(BLANK).setText(defaultNo);

								adapter.notifyDataSetChanged();
							} else {
								Toast.makeText(getActivity(), R.string.check_object_empty, Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(getActivity(), R.string.confirmation_location_message, Toast.LENGTH_LONG).show();
						}
					}
					break;

					case BLANK: {
						CheckBlank checkBlank = new CheckBlanksRepository(getActivity()).getByCheckBlankId(data.getIntExtra(DictionaryActivity.KEY, 0));

						if (checkBlank != null) {
							check.setCheckBlank(checkBlank);
							form.get(BLANK).setText(data.getStringExtra(DictionaryActivity.VALUE));

							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getActivity(), R.string.check_blank_empty, Toast.LENGTH_LONG).show();
						}
					}
					break;
				}
			}
		}
	}

	public interface ICreateCheckFragmentListener {
		void onCheckFormCreated();


	}
}
