package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import database.Person;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoPersonPhotoRepository;
import ru.facilicom24.manager.cache.DaoPersonRepository;
import ru.facilicom24.manager.dialogs.CheckDialog;
import ru.facilicom24.manager.dialogs.ImageDialog;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.fragments.PersonDocumentFragment;
import ru.facilicom24.manager.fragments.PersonStage1Fragment;
import ru.facilicom24.manager.fragments.PersonStage2Fragment;

public class PersonActivity
		extends FragmentActivity
		implements
		CheckDialog.ICheckDialogListener,
		ImageDialog.IImageDialogListener,
		CaptionFragment.OnFragmentInteractionListener {

	final static public String PERSON_TYPE_CREATE = "Create";
	final static public String PERSON_TYPE_BIND = "Bind";
	final static public String PERSON_TYPE_UNBIND = "UnBind";

	final static public int PHOTO_1 = 15;
	final static public int PHOTO_2 = 16;
	final static public int PHOTO_3 = 17;
	final static public int PHOTO_4 = 18;
	final static public int PHOTO_5 = 19;
	final static public int PHOTO_6 = 20;
	final static public int PHOTO_7 = 21;
	final static public int PHOTO_8 = 22;
	final static public int PHOTO_9 = 23;
	final static public int PHOTO_10 = 24;
	final static public int PHOTO_11 = 25;

	final static String CHECK_DIALOG_TAG = "PersonActivity_CheckDialog";

	ModeType mode;

	public ModeType getMode() {
		return mode;
	}

	public void setMode(ModeType mode) {
		this.mode = mode;

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.captionFragment, new CaptionSimpleFragment())
				.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_person);

		setMode(ModeType.Stage1);
	}

	@Override
	public void onBackPressed() {
		switch (getMode()) {
			case Stage1: {
				CheckDialog dialog = CheckDialog.newInstance(false);
				dialog.show(getSupportFragmentManager(), CHECK_DIALOG_TAG);
			}
			break;

			case Stage2: {
				CheckDialog dialog = CheckDialog.newInstance();
				dialog.show(getSupportFragmentManager(), CHECK_DIALOG_TAG);
			}
			break;

			case Document: {
				getSupportFragmentManager()
						.beginTransaction()
						.remove(getSupportFragmentManager().findFragmentByTag(PersonDocumentFragment.TAG))
						.commit();

				setMode(ModeType.Stage2);
			}
			break;
		}
	}

	@Override
	public void onSaveBtnClicked() {
		save();
	}

	@Override
	public void onSendBtnClicked() {
		send();
	}

	@Override
	public void onDeleteBtnClicked() {
		delete();
	}

	void save() {
		switch (getMode()) {
			case Stage1: {
				((PersonStage1Fragment) getSupportFragmentManager().findFragmentById(R.id.person_stage_fragment_1)).save();
			}
			break;

			case Stage2: {
				((PersonStage2Fragment) getSupportFragmentManager().findFragmentByTag(PersonStage2Fragment.TAG)).save();
			}
			break;
		}
	}

	void send() {
		((PersonStage2Fragment) getSupportFragmentManager().findFragmentByTag(PersonStage2Fragment.TAG)).send();
	}

	public void delete() {
		DaoPersonPhotoRepository.clearPersonPhotoCreate(this);
		DaoPersonRepository.clearPersonCreate(this);

		finish();
	}

	@Override
	public void onCamera() {
		((PersonDocumentFragment) getSupportFragmentManager().findFragmentByTag(PersonDocumentFragment.TAG)).takePhoto();
	}

	@Override
	public void onGallery() {
		((PersonDocumentFragment) getSupportFragmentManager().findFragmentByTag(PersonDocumentFragment.TAG)).takeGallery();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		send();
	}

	@Override
	public void captionFragmentOnSavePressed() {
		save();
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
		PersonStage2Fragment fragment = (PersonStage2Fragment) getSupportFragmentManager().findFragmentByTag(PersonStage2Fragment.TAG);

		Person person = fragment.getPerson();
		String name = TextUtils.concat(person.getLastName(), " ", person.getFirstName(), " ", person.getFatherName()).toString();

		Intent intent = new Intent(this, PersonAlbumActivity.class);

		intent.putExtra(PersonAlbumActivity.NAME_PARAM, name);
		intent.putExtra(PersonAlbumActivity.TYPE_PARAM, PersonActivity.PERSON_TYPE_CREATE);

		startActivity(intent);
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return getMode() == ModeType.Stage2;
	}

	@Override
	public boolean saveIcon() {
		return getMode() == ModeType.Stage2;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return getMode() == ModeType.Stage2;
	}

	public enum ModeType {
		Stage1,
		Stage2,
		Document
	}
}
