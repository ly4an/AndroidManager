package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import database.ActAccount;
import database.ActAccountDao;
import database.Client;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;

public class ServiceRequestActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		ListView.OnItemClickListener,
		CaptionFragment.OnFragmentInteractionListener {

	final static public int DICTIONARY_ORIO_TYPE = 1000;
	final static public int DICTIONARY_ORIO_ARTICLE = 1001;
	final static public int DICTIONARY_ORIO_INVENTORY = 1002;

	final static int CLIENT = 0;
	final static int ACCOUNT = 1;
	final static int GROUP = 2;
	final static int ORIO_TYPE = 3;
	final static int ORIO_ARTICLE = 4;
	final static int ORIO_INVENTORY = 5;
	final static int PHONE = 6;
	final static int RESPONSIBLE = 7;
	final static int DESCRIPTION = 8;

	// ОРиО
	final static String ORIO = "778F4D20-EF5B-4392-B3FE-69B5FADCFA4E";

	NFAdapter nfAdapter;
	ArrayList<NFItem> nfItems;
	DataContract dataContract;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_service_request);

		dataContract = new DataContract();

		nfItems = new ArrayList<>();
		String choose = getString(R.string.choose);

		nfItems.add(new NFItem(NFItem.Type.Choose, getString(R.string.client), dataContract.clientName != null ? dataContract.clientName : choose));
		nfItems.add(new NFItem(NFItem.Type.Choose, getString(R.string.account), dataContract.accountName != null ? dataContract.accountName : choose));
		nfItems.add(new NFItem(NFItem.Type.Choose, getString(R.string.group), dataContract.groupName != null ? dataContract.groupName : choose));
		nfItems.add(new NFItem(NFItem.Type.Choose, getString(R.string.orioType), dataContract.orioTypeName != null ? dataContract.orioTypeName : choose));
		nfItems.add(new NFItem(NFItem.Type.Choose, getString(R.string.orioArticle), dataContract.orioArticleName != null ? dataContract.orioArticleName : choose));
		nfItems.add(new NFItem(NFItem.Type.Choose, getString(R.string.orioInventory), dataContract.orioInventoryName != null ? dataContract.orioInventoryName : choose));
		nfItems.add(new NFItem(NFItem.Type.Phone, getString(R.string.phone), 50));
		nfItems.add(new NFItem(NFItem.Type.Text, getString(R.string.responsible), 50));

		nfItems.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_description, new NFItem.ILayoutBase() {
			@Override
			public void OnCreate(View view) {
				TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);

				descriptionTextView.setText(dataContract.description);
				descriptionTextView.setOnClickListener(ServiceRequestActivity.this);
			}
		}));

		ListView formListView = (ListView) findViewById(R.id.formListView);

		formListView.setOnItemClickListener(this);
		formListView.setAdapter(nfAdapter = new NFAdapter(this, nfItems));
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		dataContract.phone = nfItems.get(PHONE).getText();
		dataContract.responsible = nfItems.get(RESPONSIBLE).getText();

		ArrayList<String> messages = new ArrayList<>();

		if (dataContract.clientID == 0) {
			messages.add(nfItems.get(CLIENT).getName());
		}

		if (dataContract.accountID == 0) {
			messages.add(nfItems.get(ACCOUNT).getName());
		}

		if (dataContract.groupUID == null) {
			messages.add(nfItems.get(GROUP).getName());
		}

		if (dataContract.orioTypeUID == null) {
			messages.add(nfItems.get(ORIO_TYPE).getName());
		}

		if (dataContract.orioArticleUID == null) {
			messages.add(nfItems.get(ORIO_ARTICLE).getName());
		}

		if (dataContract.orioInventoryUID == null) {
			messages.add(nfItems.get(ORIO_INVENTORY).getName());
		}

		if (dataContract.phone == null || dataContract.phone.isEmpty()) {
			messages.add(nfItems.get(PHONE).getName());
		}

		if (dataContract.responsible == null || dataContract.responsible.isEmpty()) {
			messages.add(nfItems.get(RESPONSIBLE).getName());
		}

		if (dataContract.description == null || dataContract.description.isEmpty()) {
			messages.add(getString(R.string.description));
		}

		if (messages.isEmpty()) {
			RFService.createRequest(new OrioRequest(
					dataContract.accountID,
					dataContract.groupUID,
					dataContract.orioTypeUID,
					dataContract.orioArticleUID,
					dataContract.orioInventoryUID,
					dataContract.orioInventoryName,
					dataContract.responsible,
					dataContract.phone,
					dataContract.description,
					null,
					ORIO,
					0
			), new Callback<Void>() {
				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					if (response != null && response.body() != null) {
						Toast.makeText(ServiceRequestActivity.this, R.string.request_create_done, Toast.LENGTH_LONG).show();
						finish();
					} else {
						errorShow();
					}
				}

				@Override
				public void onFailure(Call<Void> call, Throwable t) {
					errorShow();
				}
			});
		} else {
			new AlertDialog.Builder(this)
					.setTitle(R.string.message)
					.setMessage(TextUtils.concat(getString(R.string.required), ": ", TextUtils.join(", ", messages)))
					.setPositiveButton(R.string.btn_ok, null)
					.show();
		}
	}

	@Override
	public void captionFragmentOnSavePressed() {
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
		return true;
	}

	@Override
	public boolean saveIcon() {
		return false;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}

	void errorShow() {
		Toast.makeText(ServiceRequestActivity.this, R.string.request_create_error, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case CLIENT: {
				startActivityForResult(new Intent(this, CheckClientActivity.class), CLIENT);
			}
			break;

			case ACCOUNT: {
				startActivityForResult(new Intent(this, CheckActAccountActivity.class)
								.putExtra("ClientId", dataContract.clientID),
						ACCOUNT);
			}
			break;

			case GROUP: {
				startActivityForResult(new Intent(this, NomenclatureGroupActivity.class), GROUP);
			}
			break;

			case ORIO_TYPE: {
				startActivityForResult(new Intent(this, DictionaryActivity.class)
								.putExtra(DictionaryActivity.REQUEST_CODE, DICTIONARY_ORIO_TYPE),
						ORIO_TYPE);
			}
			break;

			case ORIO_ARTICLE: {
				startActivityForResult(new Intent(this, DictionaryActivity.class)
								.putExtra(DictionaryActivity.REQUEST_CODE, DICTIONARY_ORIO_ARTICLE),
						ORIO_ARTICLE);
			}
			break;

			case ORIO_INVENTORY: {
				ArrayList<String> messages = new ArrayList<>();

				if (dataContract.groupUID == null) {
					messages.add(getString(R.string.group));
				}

				if (dataContract.accountID == 0) {
					messages.add(getString(R.string.account));
				}

				if (messages.size() == 0) {
					startActivityForResult(new Intent(this, DictionaryActivity.class)
									.putExtra(DictionaryActivity.REQUEST_CODE, DICTIONARY_ORIO_INVENTORY)
									// .putExtra(DictionaryActivity.PARAMETER_DIRECTUM_ID, 3)
									// .putExtra(DictionaryActivity.PARAMETER_GROUP_UID, "000000001"),
									.putExtra(DictionaryActivity.PARAMETER_DIRECTUM_ID, dataContract.accountID)
									.putExtra(DictionaryActivity.PARAMETER_GROUP_UID, dataContract.groupUID),
							ORIO_INVENTORY);
				} else {
					new AlertDialog.Builder(this)
							.setTitle(R.string.message)
							.setMessage(TextUtils.concat(getString(R.string.required), ": ", TextUtils.join(", ", messages)))
							.setPositiveButton(R.string.btn_ok, null)
							.show();
				}
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.descriptionTextView: {
				startActivityForResult(new Intent(this, TextActivity.class)
								.putExtra(TextActivity.CAPTION, getString(R.string.description))
								.putExtra(TextActivity.TEXT, dataContract.description),
						DESCRIPTION);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case CLIENT: {
						dataContract.clientID = data.getIntExtra("ClientId", 0);
						dataContract.clientName = data.getStringExtra("Title");

						dataContract.accountID = 0;
						dataContract.accountName = null;
						dataContract.accountAddress = null;

						nfItems.get(CLIENT).setText(dataContract.clientName);
						nfItems.get(ACCOUNT).setText(getString(R.string.choose));

						nfAdapter.notifyDataSetChanged();
					}
					break;

					case ACCOUNT: {
						dataContract.accountID = data.getIntExtra("AccountId", 0);
						dataContract.accountName = data.getStringExtra("Title");
						dataContract.accountAddress = data.getStringExtra("Address");

						nfItems.get(ACCOUNT).setText(getAccountName());

						if (dataContract.clientID == 0) {
							List<ActAccount> actAccounts = FacilicomApplication.getInstance().getDaoSession().getActAccountDao().queryBuilder()
									.where(ActAccountDao.Properties.DirectumID.eq(dataContract.accountID))
									.list();

							if (actAccounts != null && !actAccounts.isEmpty()) {
								Client client = actAccounts.get(0).getClient();

								if (client != null) {
									dataContract.clientID = client.getClientID();
									dataContract.clientName = client.getName();

									nfItems.get(CLIENT).setText(dataContract.clientName);
								}
							}
						}

						nfAdapter.notifyDataSetChanged();
					}
					break;

					case GROUP: {
						dataContract.groupUID = data.getStringExtra("NomenclatureGroupCode");
						dataContract.groupName = data.getStringExtra("Title");

						nfItems.get(GROUP).setText(dataContract.groupName);
						nfAdapter.notifyDataSetChanged();
					}
					break;

					case ORIO_TYPE: {
						dataContract.orioTypeUID = data.getStringExtra(DictionaryActivity.KEY);
						dataContract.orioTypeName = data.getStringExtra(DictionaryActivity.VALUE);

						nfItems.get(ORIO_TYPE).setText(dataContract.orioTypeName);
						nfAdapter.notifyDataSetChanged();
					}
					break;

					case ORIO_ARTICLE: {
						dataContract.orioArticleUID = data.getStringExtra(DictionaryActivity.KEY);
						dataContract.orioArticleName = data.getStringExtra(DictionaryActivity.VALUE);

						nfItems.get(ORIO_ARTICLE).setText(dataContract.orioArticleName);
						nfAdapter.notifyDataSetChanged();
					}
					break;

					case ORIO_INVENTORY: {
						dataContract.orioInventoryUID = data.getStringExtra(DictionaryActivity.KEY);
						dataContract.orioInventoryName = data.getStringExtra(DictionaryActivity.VALUE);

						nfItems.get(ORIO_INVENTORY).setText(dataContract.orioInventoryName);
						nfAdapter.notifyDataSetChanged();
					}
					break;

					case DESCRIPTION: {
						dataContract.description = data.getStringExtra(TextActivity.TEXT);
						((TextView) findViewById(R.id.descriptionTextView)).setText(dataContract.description);
					}
					break;
				}
			}
		}
	}

	String getAccountName() {
		return TextUtils.concat(dataContract.accountName, ". ", dataContract.accountAddress).toString();
	}

	private class DataContract {
		int clientID;
		String clientName;

		int accountID;
		String accountName;
		String accountAddress;

		String groupUID;
		String groupName;

		String orioTypeUID;
		String orioTypeName;

		String orioArticleUID;
		String orioArticleName;

		String orioInventoryUID;
		String orioInventoryName;

		String phone;
		String responsible;
		String description;
	}

	public class OrioRequest {
		int DirectumID;
		String NomGroup;
		String Type;
		String Article;
		String DeviceCode;
		String DeviceName;
		String ResponsibleUser;
		String Phone;
		String Comment;
		String Description;
		String ServiceRequestGUID;
		int InternalServiceRequestID;

		OrioRequest(
				int DirectumID,
				String NomGroup,
				String Type,
				String Article,
				String DeviceCode,
				String DeviceName,
				String ResponsibleUser,
				String Phone,
				String Comment,
				String Description,
				String ServiceRequestGUID,
				int InternalServiceRequestID
		) {
			this.DirectumID = DirectumID;
			this.NomGroup = NomGroup;
			this.Type = Type;
			this.Article = Article;
			this.DeviceCode = DeviceCode;
			this.DeviceName = DeviceName;
			this.ResponsibleUser = ResponsibleUser;
			this.Phone = Phone;
			this.Comment = Comment;
			this.Description = Description;
			this.ServiceRequestGUID = ServiceRequestGUID;
			this.InternalServiceRequestID = InternalServiceRequestID;
		}
	}
}
