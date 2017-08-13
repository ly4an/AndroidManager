package ru.facilicom24.manager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import database.Person;
import database.PersonPhoto;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.PersonActivity;
import ru.facilicom24.manager.cache.DaoPersonPhotoRepository;
import ru.facilicom24.manager.dialogs.ImageDialog;
import ru.facilicom24.manager.views.FontButton;

import static ru.facilicom24.manager.fragments.PersonStage2Fragment.MOSCOW_REGION_UID;

public class PersonDocumentFragment
		extends Fragment
		implements View.OnClickListener {

	static final public String TAG = "PersonDocumentFragment";

	static final String IMAGE_DIALOG_TAG = "PersonDocumentFragment_ImageDialog";
	final static String TAKE_IMAGE_MODE = "TakeImageMode";
	final static String TEMP_IMAGE_FILE_NAME = "TempImageFileName";

	final static String ID = "ID";
	final static String PERSON_LOCAL_UID = "PERSON_LOCAL_UID";
	final static String NEED_PATENT_OR_PERMISSION = "NEED_PATENT_OR_PERMISSION";
	final static String BANK = "BANK";

	int photoRequestCode;

	public PersonDocumentFragment() {
	}

	static public PersonDocumentFragment newInstance(Person person) {
		Bundle bundle = new Bundle();

		bundle.putLong(ID, person.getId());
		bundle.putString(PERSON_LOCAL_UID, person.getPersonLocalUID());
		bundle.putInt(NEED_PATENT_OR_PERMISSION, person.getNeedPatentOrPermission());
		bundle.putBoolean(BANK, person.getRegionUID() != null && person.getRegionUID().equals(MOSCOW_REGION_UID));

		PersonDocumentFragment personDocumentFragment = new PersonDocumentFragment();
		personDocumentFragment.setArguments(bundle);
		return personDocumentFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_person_document, container, false);

		view.findViewById(R.id.photoFontButton1).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton2).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton3).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton4).setOnClickListener(this);

		view.findViewById(R.id.photoFontButton6).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton7).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton8).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton9).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton10).setOnClickListener(this);
		view.findViewById(R.id.photoFontButton11).setOnClickListener(this);

		bind(view);

		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.photoFontButton1:
				photoRequestCode = PersonActivity.PHOTO_1;
				break;

			case R.id.photoFontButton2:
				photoRequestCode = PersonActivity.PHOTO_2;
				break;

			case R.id.photoFontButton3:
				photoRequestCode = PersonActivity.PHOTO_3;
				break;

			case R.id.photoFontButton4:
				photoRequestCode = PersonActivity.PHOTO_4;
				break;

			case R.id.photoFontButton6:
				photoRequestCode = PersonActivity.PHOTO_6;
				break;

			case R.id.photoFontButton7:
				photoRequestCode = PersonActivity.PHOTO_7;
				break;

			case R.id.photoFontButton8:
				photoRequestCode = PersonActivity.PHOTO_8;
				break;

			case R.id.photoFontButton9:
				photoRequestCode = PersonActivity.PHOTO_9;
				break;

			case R.id.photoFontButton10:
				photoRequestCode = PersonActivity.PHOTO_10;
				break;

			case R.id.photoFontButton11:
				photoRequestCode = PersonActivity.PHOTO_11;
				break;

			default:
				photoRequestCode = 0;
				break;
		}

		if (photoRequestCode > 0) {
			ImageDialog.newInstance().show(getActivity().getSupportFragmentManager(), IMAGE_DIALOG_TAG);
		}
	}

	public void takePhoto() {
		setTakeImageMode(getActivity(), TakeImageMode.Photo);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			try {
				File photoFile = FacilicomApplication.photoFileGenerate();

				if (photoFile != null) {
					Uri photoFileUri = Uri.fromFile(photoFile);

					setTempImageFileName(getActivity(), photoFileUri);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);

					System.gc();
					startActivityForResult(intent, photoRequestCode);
				} else {
					Toast.makeText(getActivity(), R.string.create_image_file_error, Toast.LENGTH_LONG).show();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			Toast.makeText(getActivity(), R.string.error_no_camera, Toast.LENGTH_LONG).show();
		}
	}

	public void takeGallery() {
		setTakeImageMode(getActivity(), TakeImageMode.Gallery);
		startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), photoRequestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode >= PersonActivity.PHOTO_1
				&& requestCode != PersonActivity.PHOTO_5
				&& requestCode <= PersonActivity.PHOTO_11) {

			personPhotoApply(requestCode, data);
		}
	}

	void personPhotoApply(int personPhotoType, Intent data) {
		switch (getTakeImageMode(getActivity())) {
			case Photo: {
				personPhotoSave(personPhotoType);
			}
			break;

			case Gallery: {
				if (data != null) {
					Uri selectedImage = data.getData();
					String[] filePathColumn = {MediaStore.Images.Media.DATA};

					Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

					if (cursor != null && cursor.moveToFirst()) {
						String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
						cursor.close();

						setTempImageFileName(getActivity(), Uri.fromFile(new File(path)));

						personPhotoSave(personPhotoType);
					}
				}
			}
			break;
		}
	}

	void personPhotoSave(int personPhotoType) {
		Uri tempImageFileName = getTempImageFileName(getActivity());

		if (tempImageFileName != null && new File(tempImageFileName.getPath()).length() > 0) {
			PersonPhoto personPhoto = new PersonPhoto();

			personPhoto.setPersonId(getArguments().getLong(ID));
			personPhoto.setPersonLocalUID(getArguments().getString(PERSON_LOCAL_UID));

			personPhoto.setImageLocalUID(UUID.randomUUID().toString());

			personPhoto.setPersonPhotoType(personPhotoType);
			personPhoto.setPersonPhotoUri(tempImageFileName.toString());

			DaoPersonPhotoRepository.insertOrUpdate(getActivity(), personPhoto);
		} else {
			Toast.makeText(getActivity(), R.string.error_photo, Toast.LENGTH_LONG).show();
		}

		setTempImageFileName(getActivity(), null);

		bind(getView());
	}

	void setTakeImageMode(Activity activity, TakeImageMode takeImageMode) {
		activity.getIntent().putExtra(TAKE_IMAGE_MODE, takeImageMode);
	}

	TakeImageMode getTakeImageMode(Activity activity) {
		return (TakeImageMode) activity.getIntent().getSerializableExtra(TAKE_IMAGE_MODE);
	}

	void setTempImageFileName(Activity activity, Uri tempImageFileName) {
		activity.getIntent().putExtra(TEMP_IMAGE_FILE_NAME, tempImageFileName);
	}

	Uri getTempImageFileName(Activity activity) {
		return (Uri) activity.getIntent().getParcelableExtra(TEMP_IMAGE_FILE_NAME);
	}

	void bind(View view) {
		((FontButton) view.findViewById(R.id.photoFontButton1)).setText(getFontButtonTitle(
				R.string.personal_stage_photo1,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_1)
		));

		((FontButton) view.findViewById(R.id.photoFontButton2)).setText(getFontButtonTitle(
				R.string.personal_stage_photo2,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_2)
		));

		((FontButton) view.findViewById(R.id.photoFontButton3)).setText(getFontButtonTitle(
				R.string.personal_stage_photo3,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_3)
		));

		((FontButton) view.findViewById(R.id.photoFontButton4)).setText(getFontButtonTitle(
				R.string.personal_stage_photo4,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_4)
		));

		((FontButton) view.findViewById(R.id.photoFontButton6)).setText(getFontButtonTitle(
				R.string.personal_stage_photo6,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_6)
		));

		((FontButton) view.findViewById(R.id.photoFontButton7)).setText(getFontButtonTitle(
				R.string.personal_stage_photo7,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_7)
		));

		((FontButton) view.findViewById(R.id.photoFontButton8)).setText(getFontButtonTitle(
				R.string.personal_stage_photo8,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_8)
		));

		((FontButton) view.findViewById(R.id.photoFontButton9)).setText(getFontButtonTitle(
				R.string.personal_stage_photo9,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_9)
		));

		((FontButton) view.findViewById(R.id.photoFontButton10)).setText(getFontButtonTitle(
				R.string.personal_stage_photo10,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_10)
		));

		((FontButton) view.findViewById(R.id.photoFontButton11)).setText(getFontButtonTitle(
				R.string.personal_stage_photo11,
				DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_11)
		));
	}

	String getFontButtonTitle(int resId, long quantity) {
		return quantity > 0
				? TextUtils.concat("(", String.valueOf(quantity), ") ", getString(resId)).toString()
				: getString(resId);
	}

	private enum TakeImageMode {
		Photo,
		Gallery
	}
}
