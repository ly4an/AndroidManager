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

import java.util.ArrayList;
import java.util.List;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.Photo;

public class PhotoAlbumActivity
		extends BaseActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	public static final String PHOTOS_EXTRA = "Photos";
	public static final String FORM_EXTRA = "Form";
	public static final String OBJECT_EXTRA = "Object";

	List<Photo> mPhotos;
	ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_photo_album);

		mPhotos = (ArrayList<Photo>) getIntent().getSerializableExtra(PHOTOS_EXTRA);

		GridView photoAlbumView = (GridView) findViewById(R.id.photo_album);

		photoAlbumView.setAdapter(new PhotosAdapter());
		photoAlbumView.setOnItemClickListener(this);

		((TextView) findViewById(R.id.form)).setText(getIntent().getStringExtra(FORM_EXTRA));
		((TextView) findViewById(R.id.object)).setText(getIntent().getStringExtra(OBJECT_EXTRA));

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
			return mPhotos.size();
		}

		@Override
		public Photo getItem(int i) {
			return mPhotos.get(i);
		}

		@Override
		public long getItemId(int i) {
			return getItem(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.photo_album_item, viewGroup, false);
			}

			String uri = getItem(i).getUri();
			imageLoader.displayImage(uri, (ImageView) view, options);
			view.setTag(uri);

			return view;
		}
	}
}
