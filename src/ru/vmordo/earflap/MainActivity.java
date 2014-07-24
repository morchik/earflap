package ru.vmordo.earflap;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import ru.vmordo.earflap.R;
import ru.vmordo.util.Log;
import ru.vmordo.util.Net;
import ru.vmordo.util.upLoader;


public class MainActivity extends Activity {
	final static String LOG_TAG = "MainActivity";

	static Db_Helper dbHelper;
	static SharedPreferences sp = null;
	static Context bCnt = null;

	TextView tvOut;
	static public EditText editTextLog = null;
	Button btnOk;
	static int cnt = 0;
	public static boolean allowRec = true;
	public static String uid = null;

	static int AvgDL = 6500; // среднее значение амплитуды сигнала после
								// которого файлам меняется расширение по
								// формуле // unused now
	// эти параметры нужны чтобы не хранить файлы на которых ничего не слышно
	static int DelMin = 3000; // максимальный порог амплитуды после которого
								// файлы не удаляются
	static int DelAvg = 2000; // средний порог с которого файлы остаются

	String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
			"Костя", "Игорь", "Анна", "Денис", "Андрей" };

	static String fstr(int inum) {
		String cRes = inum + "";
		if (inum <= 9) {
			cRes = "0" + inum;
		}
		return cRes;
	}

	  @Override
	  public void onStart() {
	    super.onStart();
	    // The rest of your onStart() code.
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  } 
	  
	  @Override
	  public void onStop() {
	    super.onStop();
	    // The rest of your onStop() code.
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }

	public static File getFileName() {
		// получаем текущее время
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		String mMonth = fstr(c.get(Calendar.MONTH) + 1);
		String mDay = fstr(c.get(Calendar.DAY_OF_MONTH));
		String mHour = fstr(c.get(Calendar.HOUR_OF_DAY));
		String mMinute = fstr(c.get(Calendar.MINUTE));
		String mSec = fstr(c.get(Calendar.SECOND));

		String str_file_name = +mYear + "/" + mMonth + "/" + mDay + "/a"
				+ mYear + "." + mMonth + "." + mDay + "-" + mHour + "."
				+ mMinute + "." + mSec + ".tmp";
		File fileName = null;
		// Log.d(LOG_TAG, "getFileName 1 3 2");
		File sdDir;
		if (sp != null)
			sdDir = new File(sp.getString("ldir_address",
					"/storage/external_SD/DCIM"));
		else
			sdDir = new File("/storage/external_SD/DCIM");

		// Log.d(LOG_TAG, "getFileName 1 3 3 " + sdDir.getPath());
		if (!sdDir.isDirectory()) {
			String sdState = android.os.Environment.getExternalStorageState();
			if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
				// Log.d(LOG_TAG, " MEDIA_MOUNTED file_name  is:" +
				// str_file_name);
				sdDir = android.os.Environment.getExternalStorageDirectory();

			} else {
				// Log.d(LOG_TAG, " MEDIA NOY MOUNTED file_name  is:" +
				// str_file_name);
				sdDir = android.os.Environment.getDataDirectory();
			}
			// sp.setString("ldir_address", sdDir.getPath()); //???
		}
		// Log.d(LOG_TAG, "getFileName 1 3 3 " + sdDir.getPath());
		fileName = new File(sdDir, str_file_name);
		if (!fileName.getParentFile().exists()) {
			fileName.getParentFile().mkdirs();
			// Log.d(LOG_TAG, " mkdirs:" + fileName.getParent());
		}
		// Log.d(LOG_TAG, " file_name  is:" + fileName.getPath());
		return fileName;
	}

	public static void startRecord() {
		if (MainActivity.allowRec)
			for (int k = 0; k < 6; k++) {
				MediaRecorder recorder = new MediaRecorder();
				try {
					// Log.d(LOG_TAG, k + " 0 ");
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					// Log.d(LOG_TAG, k + " 1 ");
					recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					// Log.d(LOG_TAG, k + " 1 2");
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
					// Log.d(LOG_TAG, k + " 1 3");
					File fileName = getFileName();
					// Log.d(LOG_TAG, k + " 1 4");
					recorder.setOutputFile(fileName.getPath());

					// Log.d(LOG_TAG, k + " 2 ");
					recorder.prepare();
					// Log.d(LOG_TAG, k + " 3 ");
					recorder.start();
					// Log.d(LOG_TAG, k + " 4 ");
					int maxam = 0;
					int avgam = 0;
					int nstep = 20;
					for (int i = 0; i < nstep; i++) {
						Thread.sleep(500);
						int amp = recorder.getMaxAmplitude();
						// Log.d(LOG_TAG, k + " " + i
						// + " current amplitude is:" + amp);
						if (amp > maxam)
							maxam = amp;
						avgam = avgam + amp;
					}
					avgam = avgam / nstep;
					// Log.d(LOG_TAG, k + " current max amplitude is: " + maxam
					// + " avg: " + avgam);

					recorder.stop();
					recorder.reset();
					recorder.release();
					// удалить ли тихий файл
					DelMin = Integer.valueOf(sp
							.getString("DelMin", "" + DelMin));
					DelAvg = Integer.valueOf(sp
							.getString("DelAvg", "" + DelAvg));
					if ((sp.getBoolean("chb_del_q", false)) && (maxam < DelMin)
							&& (avgam < DelAvg)) {
						// удалить пустой файл
						//Log.d(LOG_TAG,k + " удалить тихий файл:" + fileName.getPath());
						if (!fileName.delete()) {
							// Log.d(LOG_TAG, k
							// + " не удалось удалить тихий файл:"
							// + fileName.getName());
						}
					} else {
						dbHelper.insert_log(fileName.getPath(), maxam, avgam,
								nstep * 500); // записываем db
						try {
							if (sp.getBoolean("chb_toast", false))
								Toast.makeText(bCnt,
										"Rec Max " + maxam + " Avg " + avgam,
										Toast.LENGTH_SHORT).show(); // do not
																	// work ???
						} catch (Exception e) {
							// tvOut.setText(" error " + e.getMessage());
							// Log.d(LOG_TAG, k + "Toast error is: " +
							// e.getMessage());
						}

						/*
						 * Log.d(LOG_TAG, k + " переименовываваем громкий файл:"
						 * + fileName.getName()); File newName = new
						 * File(fileName.getPath() + Math.round((avgam - AvgDL)
						 * / 1000)); if (!fileName.renameTo(newName))
						 * Log.d(LOG_TAG, k + " не удалось переименовать файл:"
						 * + fileName.getName());
						 */
					}
				} catch (Exception e) {
					// tvOut.setText(" error " + e.getMessage());
					// Log.v(LOG_TAG, k + " error is: " + e.getMessage());
				}
			}
		/*
		if (bCnt != null) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(bCnt);
			boolean valb = prefs.getBoolean("chb_autostart", false);
			if (valb) {
				List<String> list = dbHelper.get_list(5);
				(new upLoader()).listTaskUpLoad(list, bCnt);
				Log.d(LOG_TAG, "listTaskUpLoad on ");
			}
		}
		*/
	}

	/*
	 * private Cursor getContacts() { // Выполняем запрос Uri uri =
	 * ContactsContract.Contacts.CONTENT_URI; String[] projection = new String[]
	 * { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME
	 * }; String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
	 * + ("1") + "'"; String[] selectionArgs = null; String sortOrder =
	 * ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	 * 
	 * return managedQuery(uri, projection, selection, selectionArgs,
	 * sortOrder); }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bCnt = getBaseContext();
		// найдем View-элементы
		tvOut = (TextView) findViewById(R.id.tvOut);
		editTextLog = (EditText) findViewById(R.id.editTextLog);
		btnOk = (Button) findViewById(R.id.btnOk);
		ru.vmordo.util.Log.context = bCnt;
		ru.vmordo.util.Log.lView = editTextLog;

		if (uid == null) {
			final TelephonyManager tm = (TelephonyManager) getBaseContext()
					.getSystemService(Context.TELEPHONY_SERVICE);

			String tmDevice, tmSerial, androidId;
			tmDevice = "" + tm.getDeviceId();
			editTextLog.append(tmDevice + "\n");

			if (tmDevice.length() < 3)
				tmDevice = "bad";
			tmSerial = "" + tm.getSimSerialNumber();
			editTextLog.append(tmSerial + "\n");
			androidId = ""
					+ android.provider.Settings.Secure.getString(
							getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(),
					((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			uid = deviceUuid.toString();
			upLoader.u_dir = "/http/a/" + tmDevice + "/" + uid;
		}
		editTextLog.append(uid + "\n"+ upLoader.u_dir + "\n");

		Log.v("TST", Net.getInetType(bCnt));
		
		// получаем SharedPreferences, которое работает с файлом настроек
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		// создаем объект для создания и управления версиями БД
		dbHelper = new Db_Helper(this);

		tvOut.setText("DelMin=" + DelMin + " DelAvg=" + DelAvg + " cnt = "
				+ cnt);
		
		/// каждую минуту
		if (BootBroadReceiv.odin == null) {
			BootBroadReceiv.odin = new BootBroadReceiv();
			bCnt.registerReceiver(BootBroadReceiv.odin, new IntentFilter(
					"android.intent.action.TIME_TICK"));
			Log.d(LOG_TAG, "registerReceiver ");
			
		}

		// находим список
		ListView lvMain = (ListView) findViewById(R.id.lvMain);
		// устанавливаем режим выбора пунктов списка
		lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// создаем адаптер
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_single_choice//R.layout.my_list_item
		// , names);
		Cursor mCursor = dbHelper.get_log_cur(); // getContacts();
		// startManagingCursor(mCursor);
		// присваиваем адаптер списку
		lvMain.setAdapter(new android.widget.SimpleCursorAdapter(
				this,
				R.layout.list_item // android.R.layout.simple_list_item_1
				, mCursor, new String[] { "file_name" },
				new int[] { android.R.id.text1 },
				android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		/*
		 * lvMain.setAdapter(new android.widget.SimpleCursorAdapter(this ,
		 * R.layout.my_list_item //android.R.layout.two_line_list_item , mCursor
		 * , new String[] { "date_time", "file_name",
		 * "_id"//ContactsContract.Contacts._ID,
		 * //ContactsContract.Contacts.DISPLAY_NAME } , new int[] {
		 * android.R.id.text1,android.R.id.text2, R.id.text3 } ,
		 * android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER ));
		 */
		lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				String fstr = (String) ((TextView) itemClicked).getText();
				// Toast.makeText(getApplicationContext(), fstr,
				// Toast.LENGTH_SHORT).show();
				(new upLoader()).oneTaskUpLoad(fstr, bCnt);
				Uri uri = Uri.parse("file://" + fstr);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				it.setDataAndType(uri, "audio/mp3");
				startActivity(it);
				editTextLog.append(uri.toString() + "\n");
			}
		});
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		// инициализация
		tabHost.setup();
		TabHost.TabSpec tabSpec;
		// создаем вкладку и указываем тег
		tabSpec = tabHost.newTabSpec("tag1");
		// название вкладки
		tabSpec.setIndicator("Вкладка 1");
		// указываем id компонента из FrameLayout, он и станет содержимым
		tabSpec.setContent(R.id.tab1);
		// добавляем в корневой элемент
		tabHost.addTab(tabSpec);
		tabSpec = tabHost.newTabSpec("tag2");
		// указываем название и картинку
		// в нашем случае вместо картинки идет xml-файл,
		// который определяет картинку по состоянию вкладки
		// tabSpec.setIndicator("Вкладка 2",
		// getResources().getDrawable(R.drawable.tab_icon_selector));
		tabSpec.setIndicator("Вкладка 2");
		tabSpec.setContent(R.id.tab2);
		tabHost.addTab(tabSpec);
		tabSpec = tabHost.newTabSpec("tag3");
		// создаем View из layout-файла с,View v =
		// getLayoutInflater().inflate(R.layout.tab_header, null);
		// и устанавливаем его, как заголовок tabSpec.setIndicator(v);
		tabSpec.setIndicator("Вкладка 3");
		tabSpec.setContent(R.id.tab3);
		tabHost.addTab(tabSpec);
		// вторая вкладка будет выбрана по умолчанию
		// tabHost.setCurrentTabByTag("tag2");
		// обработчик переключения вкладок
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				// Toast.makeText(getBaseContext(), "tabId = " + tabId,
				// Toast.LENGTH_SHORT).show();
			}
		});
		// Toast.makeText(getBaseContext(), "end create @", Toast.LENGTH_SHORT)
		// .show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, R.string.pref);
		mi.setIntent(new Intent(this, PrefActivity.class));
		return super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
	}

	public void onClickStart(View v) {
		startService(new Intent(this, TrackingService.class));
	}

	public void onClickStop(View v) {
		//List<String> list = dbHelper.get_list(20);
		//(new upLoader()).listTaskUpLoad(list, bCnt);
		stopService(new Intent(this, TrackingService.class));
		/// каждую минуту
		if (BootBroadReceiv.odin == null) {
			BootBroadReceiv.odin = new BootBroadReceiv();
			bCnt.registerReceiver(BootBroadReceiv.odin, new IntentFilter(
					"android.intent.action.TIME_TICK"));
			Log.d(LOG_TAG, "registerReceiver TIME_TICK");
			
		}
	}

	public void onClick(View v) {
		cnt = cnt + 1;
		if (allowRec) {
			allowRec = false;
			tvOut.setText("Нажата кнопка OFF " + cnt);
		} else {
			allowRec = true;
			tvOut.setText("Нажата кнопка ON " + cnt);
		}
	}

}
