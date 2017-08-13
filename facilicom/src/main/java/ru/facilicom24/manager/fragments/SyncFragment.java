package ru.facilicom24.manager.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.ChecksRepository;
import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.model.CheckRequest;
import ru.facilicom24.manager.network.ApiRequestHelper;
import ru.facilicom24.manager.retrofit.CheckContract;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.services.ImageUploadService;
import ru.facilicom24.manager.utils.NetworkHelper;

public class SyncFragment extends BaseFragment {

	ISyncFragmentListener mListener;
	ChecksRepository checksRepository;
	BroadcastReceiver broadcastReceiver;

	public SyncFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		Fragment targetFragment = getTargetFragment();

		if (targetFragment instanceof ISyncFragmentListener) {
			mListener = (ISyncFragmentListener) targetFragment;
		} else if (context instanceof ISyncFragmentListener) {
			mListener = (ISyncFragmentListener) context;
		}

		checksRepository = new ChecksRepository(context);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (ImageUploadService.IMAGE_UPLOAD_ONE_ACTION.equals(intent.getAction())) {
					Toast.makeText(context, R.string.uploading_photo, Toast.LENGTH_LONG).show();
				} else if (ImageUploadService.IMAGE_UPLOAD_STARTED_ACTION.equals(intent.getAction())) {
					Toast.makeText(context, R.string.uploading_photos, Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(ImageUploadService.IMAGE_UPLOAD_ONE_ACTION));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(ImageUploadService.IMAGE_UPLOAD_STARTED_ACTION));
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	public void syncCheck(final Check check) {
		if (NetworkHelper.isConnected(getActivity())) {
			CheckRequest checkRequest = ApiRequestHelper.buildCheckRequest(getActivity(), check);

			RFService.checkUpdate(check.getCheckId(), new CheckContract(
					checkRequest.getAccountId(),
					checkRequest.getFormId(),
					checkRequest.getCreatedOn(),
					checkRequest.getLongitude(),
					checkRequest.getLatitude(),
					checkRequest.getComments(),
					checkRequest.getMarks()
			), new Callback<Void>() {

				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					if (response != null && response.errorBody() == null) {
						if (check.getPhotos() == null || check.getPhotos().isEmpty()) {
							RFService.checkEnd(check.getCheckId(), new Callback<JsonObject>() {

								@Override
								public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
									if (response != null && response.body() != null) {
										checksRepository.delete(check);
										syncDone(false);
									} else {
										syncLater(check);
									}
								}

								@Override
								public void onFailure(Call<JsonObject> call, Throwable t) {
									syncLater(check);
								}
							});
						} else {
							if (getActivity() != null) {
								ImageUploadService.upload(getActivity(), ImageUploadService.NEW_CHECKS_PHOTO);
							}

							syncDone(false);
						}
					} else {
						syncLater(check);
					}
				}

				@Override
				public void onFailure(Call<Void> call, Throwable t) {
					syncLater(check);
				}
			});
		} else {
			syncLater(check);
		}
	}

	void syncLater(Check check) {
		check.setState(Check.READY);
		checksRepository.update(check);

		syncDone(true);
	}

	void syncDone(boolean syncLater) {
		if (getActivity() != null) {
			Toast.makeText(getActivity(), syncLater
							? R.string.sync_not_possible_network
							: R.string.actSendOk,
					Toast.LENGTH_LONG).show();
		}

		if (mListener != null) {
			mListener.onSyncDone();
		}
	}

	interface ISyncFragmentListener {
		void onSyncDone();
	}
}
