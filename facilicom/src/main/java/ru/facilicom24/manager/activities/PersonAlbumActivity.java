package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.List;

import database.PersonPhoto;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoPersonPhotoRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class PersonAlbumActivity
		extends BaseActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String NAME_PARAM = "Name";
	final static public String TYPE_PARAM = "Type";

	ImageLoader imageLoader;
	List<PersonPhoto> photos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_person_album);

		DaoPersonPhotoRepository repository = new DaoPersonPhotoRepository();

		if (getIntent().getStringExtra(TYPE_PARAM).equals(PersonActivity.PERSON_TYPE_CREATE)) {
			photos = repository.getAllPersonPhotoCreate(this);
		} else if (getIntent().getStringExtra(TYPE_PARAM).equals(PersonActivity.PERSON_TYPE_BIND)) {
			photos = repository.getAllPersonPhotoBind(this);
		}

		GridView photoAlbum = (GridView) findViewById(R.id.photo_album);

		photoAlbum.setAdapter(new PhotosAdapter());
		photoAlbum.setOnItemClickListener(this);

		((TextView) findViewById(R.id.name)).setText(getIntent().getStringExtra(NAME_PARAM));

		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(PhotoActivity.IMG_URI_EXTRA, (String) view.getTag());
		startActivity(intent);
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class PhotosAdapter extends BaseAdapter {
		DisplayImageOptions options;

		PhotosAdapter() {
			options = new DisplayImageOptions.Builder()
					.resetViewBeforeLoading(false)
					.cacheInMemory(true)
					.cacheOnDisk(false)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.ARGB_8888)
					.displayer(new SimpleBitmapDisplayer())
					.postProcessor(new BitmapProcessor() {
						@Override
						public Bitmap process(Bitmap bitmap) {
							Matrix matrix = new Matrix();
							matrix.postRotate(0);

							int height = bitmap.getHeight();
							int width = bitmap.getWidth();

							if ((height < 300) || (width < 400)) {
								return bitmap;
							} else {
								return Bitmap.createBitmap(bitmap, (width - 400) / 2, (height - 300) / 2, 400, 300, matrix, false);
							}
						}
					})
					.build();
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		@Override
		public PersonPhoto getItem(int i) {
			return photos.get(i);
		}

		@Override
		public long getItemId(int i) {
			return photos.get(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.photo_album_item, viewGroup, false);
			}

			String uri = getItem(i).getPersonPhotoUri();
			imageLoader.displayImage(uri, (ImageView) view, options);
			view.setTag(uri);

			return view;
		}
	}
}
