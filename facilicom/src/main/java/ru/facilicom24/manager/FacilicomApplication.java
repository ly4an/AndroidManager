package ru.facilicom24.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import database.ActAccountDao;
import database.CheckInLog;
import database.CheckInLogDao;
import database.DaoMaster;
import database.DaoSession;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.facilicom24.manager.activities.LoginActivity;
import ru.facilicom24.manager.model.CheckObject;
import ru.facilicom24.manager.model.MapAccount;
import ru.facilicom24.manager.retrofit.IRFService;
import ru.facilicom24.manager.services.MockCheck;
import ru.facilicom24.manager.utils.SessionManager;

@ReportsCrashes(
		mailTo = "vorontsov@sotkon.ru;lych@sotkon.ru",
		mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.crash_toast_text
)

public class FacilicomApplication
		extends Application {

	final static public int TICK = 500;

	final static public SimpleDateFormat dateTimeFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ", Locale.US);
	final static public SimpleDateFormat dateTimeFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	final static public SimpleDateFormat dateTimeFormat3 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	final static public SimpleDateFormat dateTimeFormat4 = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
	final static public SimpleDateFormat dateTimeFormat5 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	final static public SimpleDateFormat dateTimeFormat6 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
	final static public SimpleDateFormat dateTimeFormat7 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.US);
	final static public SimpleDateFormat dateTimeFormat8 = new SimpleDateFormat("HH:mm", Locale.US);
	final static public SimpleDateFormat dateTimeFormat9 = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
	final static public SimpleDateFormat dateTimeFormat10 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	static final public int ClientsAndAccountsAllowShowAllReasons[] = {
			// Кризисный Клиент/объект
			5,

			// Работа по возврату объекта/клиента
			19,

			// Работа с дебиторской задолженностью
			67
	};

	final static int STORE_CHECKIN_DAYS = 7;

	final static String DOMAIN_1 = "facilicom.ru";
	final static String DOMAIN_2 = "ritekom.ru";
	final static String DOMAIN_3 = "cs-service.by";

	final static String DATABASE_NAME = "facilicom-db";
	final static String DIRECTUM_NAME = "DirectumID";

	final static String DATA_DIRECTORY_FACILICOM = "Facilicom";
	final static String DATA_DIRECTORY_MANAGER = "Manager";

	static final int RETROFIT_READ_TIMEOUT = 30;
	static final String RETROFIT_GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	static public Gson rfGson;
	static public IRFService rfService;

	static FacilicomApplication facilicomApplication;

	DaoSession daoSession;
	List<MapAccount> mapAccounts;
	List<CheckObject> checkObjects;

	public static String getAPI() {
		// return "http://192.168.29.14/Server/";
		// return "http://192.168.29.167:1616/";
		return "http://manager7.facilicom.info/";
	}

	public static FacilicomApplication getInstance() {
		return facilicomApplication;
	}

	static public int getAccount(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(DIRECTUM_NAME, 0);
	}

	static public void setAccount(Context context, int accountId) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putInt(DIRECTUM_NAME, accountId);
		editor.apply();

		if (accountId > 0) {
			CheckInLog checkInLog = new CheckInLog();

			checkInLog.setDateTime(new Date());
			checkInLog.setDirectumID(accountId);

			FacilicomApplication.getInstance().getDaoSession().getCheckInLogDao().insert(checkInLog);
		}
	}

	static public boolean checkInRule(int accountId, int days) {
		Calendar today = Calendar.getInstance();

		today.set(Calendar.HOUR_OF_DAY, 0);

		today.clear(Calendar.MINUTE);
		today.clear(Calendar.SECOND);
		today.clear(Calendar.MILLISECOND);

		Calendar date = Calendar.getInstance();

		date.setTime(today.getTime());
		date.add(Calendar.DAY_OF_MONTH, -STORE_CHECKIN_DAYS);

		FacilicomApplication.getInstance().getDaoSession().getCheckInLogDao().queryBuilder()
				.where(CheckInLogDao.Properties.DateTime.lt(date.getTime()))
				.buildDelete()
				.executeDeleteWithoutDetachingEntities();

		date.setTime(today.getTime());
		date.add(Calendar.DAY_OF_MONTH, -days);

		return FacilicomApplication.getInstance().getDaoSession().getActAccountDao().queryBuilder()
				.where(ActAccountDao.Properties.DirectumID.eq(accountId),
						ActAccountDao.Properties.Status.eq(0)).count() > 0

				||

				FacilicomApplication.getInstance().getDaoSession().getCheckInLogDao().queryBuilder()
						.where(CheckInLogDao.Properties.DirectumID.eq(accountId),
								CheckInLogDao.Properties.DateTime.gt(date.getTime()))
						.count() > 0;
	}

	static public File photoFileGenerate() {
		File directory = new File(TextUtils.concat(Environment.getExternalStorageDirectory().getPath(), File.separator, DATA_DIRECTORY_FACILICOM, File.separator, DATA_DIRECTORY_MANAGER).toString());
		{
			directory.mkdirs();
		}

		try {
			return File.createTempFile(TextUtils.concat("MX", dateTimeFormat3.format(new Date())).toString(), ".jpg", directory);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	// Domain

	static boolean domainCheck(Activity activity, String... domains) {
		String userName = PreferenceManager.getDefaultSharedPreferences(activity).getString(LoginActivity.USERNAME, null);

		if (userName != null) {
			String[] userNameParts = userName.split("@");

			if (userNameParts.length == 2) {
				String pattern = userNameParts[1].trim().toLowerCase();

				for (String domain : domains) {
					if (domain.equals(pattern)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	static public int getLogoResId(Activity activity) {
		return domainCheck(activity, DOMAIN_2) ? R.drawable.ritek_logo : R.drawable.facilicom_header_logo;
	}

	static public int getBigLogoResId(Activity activity) {
		return domainCheck(activity, DOMAIN_2) ? R.drawable.ritek_logo : R.drawable.facilicom_big_logo;
	}

	static public int getThemeResId(Activity activity) {
		return domainCheck(activity, DOMAIN_2) ? R.style.AppTheme2 : R.style.AppTheme1;
	}

	public static boolean isMinsk(Activity activity) {
		return domainCheck(activity, DOMAIN_3);
	}

	public static int getThemeIcon(Activity activity, int resId) {
		if (domainCheck(activity, DOMAIN_2)) {
			switch (resId) {
				case R.drawable.mark_zero_btn:
					return R.drawable.mark_zero_btn_dark;
				case R.drawable.mark_one_btn:
					return R.drawable.mark_one_btn_dark;
				case R.drawable.mark_two_btn:
					return R.drawable.mark_two_btn_dark;
				case R.drawable.photo_btn:
					return R.drawable.photo_btn_dark;
			}
		}

		return resId;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		facilicomApplication = this;

		// Init

		ACRA.init(this);
		SessionManager.init(this);

		// ImageLoader

		ImageLoaderConfiguration.Builder Builder = new ImageLoaderConfiguration.Builder(this);

		Builder.threadPoolSize(3);
		Builder.threadPriority(Thread.MIN_PRIORITY);

		Builder.memoryCacheSize(2000000);
		Builder.memoryCacheSizePercentage(13);

		ImageLoader.getInstance().init(Builder.build());

		// DAO

		daoSession = new DaoMaster(
				new DaoMaster.DevOpenHelper(
						this, DATABASE_NAME, null
				).getWritableDatabase()
		).newSession();

		// Retrofit

		rfGson = new GsonBuilder()
				.setDateFormat(RETROFIT_GSON_DATE_FORMAT)
				.create();

		rfService = new Retrofit.Builder()
				.baseUrl(TextUtils.concat(getAPI(), "api/").toString())
				.addConverterFactory(GsonConverterFactory.create(rfGson))
				.client(new OkHttpClient().newBuilder().readTimeout(RETROFIT_READ_TIMEOUT, TimeUnit.SECONDS).build())
				.build()
				.create(IRFService.class);

		// Mock

		MockCheck.initializeApplications(this);
	}

	public DaoSession getDaoSession() {
		return daoSession;
	}

	public List<MapAccount> getMapAccounts() {
		return mapAccounts != null ? mapAccounts : new ArrayList<MapAccount>();
	}

	public void setMapAccounts(List<MapAccount> mapAccounts) {
		this.mapAccounts = mapAccounts;
	}
}
