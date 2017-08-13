package ru.facilicom24.manager.activities;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class PhotoActivity
		extends BaseActivity
		implements CaptionSimpleFragment.OnFragmentInteractionListener {

	public static final String IMG_URI_EXTRA = "Image";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_photo);

		ImageView imageView = (ImageView) findViewById(R.id.photo);
		ImageLoader.getInstance().displayImage(getIntent().getStringExtra(IMG_URI_EXTRA), imageView, new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(false)
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.displayer(new SimpleBitmapDisplayer())
				.postProcessor(new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap bitmap) {
						Matrix matrix = new Matrix();
						matrix.postRotate(0);
						return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
					}
				})
				.build()
		);
	}

	@Override
	public void captionFragmentOnBackPressed() {
		finish();
	}
}
