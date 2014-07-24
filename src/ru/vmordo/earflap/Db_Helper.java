package ru.vmordo.earflap;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.vmordo.util.Log;

public class Db_Helper extends SQLiteOpenHelper {

	final String LOG_TAG = "db_Logs";

	public Db_Helper(Context context) {
		// конструктор суперкласса
		super(context, "myDB", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("test", "--- onCreate database ---");
		// создаем таблицу с полями
		db.execSQL("create table rec_log ("
				+ "_id integer primary key autoincrement," + "file_name text,"
				+ "max_ampl text," + "avg_ampl text," + "rlen text,"
				+ "fstatus text, "
				+ "date_time TIMESTAMP default CURRENT_TIMESTAMP );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void insert_log(String fln, int max_ampl, int avg_ampl, int rlen) {
		ContentValues cv = new ContentValues();
		// подключаемся к БД
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d(LOG_TAG, "--- Insert in mytable: ---");
		// подготовим данные для вставки в виде пар: наименование столбца -
		// значение
		cv.put("file_name", fln);
		cv.put("max_ampl", max_ampl);
		cv.put("avg_ampl", avg_ampl);
		cv.put("rlen", rlen);
		// вставляем запись и получаем ее ID
		long rowID = db.insert("rec_log", null, cv);
		Log.d(LOG_TAG, "row inserted, ID = " + rowID);

		// закрываем подключение к БД
		this.close();
	}

	public void update_status(String fln) {
		ContentValues cv = new ContentValues();
		// подключаемся к БД
		SQLiteDatabase db = this.getWritableDatabase();

		Log.v(LOG_TAG, "--- update_status in mytable: ---");
		// подготовим данные для вставки в виде пар: наименование столбца -
		// значение
		cv.put("fstatus", "1");
		String[] w = {fln};
		// вставляем запись и получаем ее ID
		long rowID = db.update("rec_log", cv, "file_name = ?", w );
		Log.v(LOG_TAG, "row updated  = " + rowID);

		// закрываем подключение к БД
		this.close();
	}

	public Cursor get_log_cur() {
		// подключаемся к БД
		SQLiteDatabase db = this.getWritableDatabase();

		Log.v(LOG_TAG, "--- rec_log   get_log_cur ---");
		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		return db.query("rec_log", null, " fstatus is null ", null, null, null, " max_ampl DESC ");
	}

	public void print_log() {
		// подключаемся к БД
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d(LOG_TAG, "--- Rows in mytable: ---");
		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		Cursor c = db.query("rec_log", null, null, null, null, null, null);

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {

			// определяем номера столбцов по имени в выборке
			int idColIndex = c.getColumnIndex("_id");
			int nameColIndex = c.getColumnIndex("file_name");
			int max_amplColIndex = c.getColumnIndex("max_ampl");
			int avg_amplColIndex = c.getColumnIndex("avg_ampl");
			int rlenColIndex = c.getColumnIndex("rlen");
			int fstatusColIndex = c.getColumnIndex("fstatus");
			int tsColIndex = c.getColumnIndex("date_time");

			do {
				// получаем значения по номерам столбцов и пишем все в лог
				Log.d(LOG_TAG,
						"ID = " + c.getInt(idColIndex) + ", file = "
								+ c.getString(nameColIndex) + ", max aml = "
								+ c.getString(max_amplColIndex)
								+ ", avg aml = "
								+ c.getString(avg_amplColIndex) + ", len = "
								+ c.getString(rlenColIndex) + ", fstatus = "
								+ c.getString(fstatusColIndex) +

								", time = " + c.getString(tsColIndex));
				// переход на следующую строку
				// а если следующей нет (текущая - последняя), то false -
				// выходим из цикла
			} while (c.moveToNext());
		} else
			Log.d(LOG_TAG, "0 rows");
		c.close();
		// закрываем подключение к БД
		this.close();
	}

	public List<String> get_list(int cnt) {
		List<String> list = new ArrayList<String>();
		int n = 0;
		// подключаемся к БД
		SQLiteDatabase db = this.getWritableDatabase();

		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		Cursor c = db.query("rec_log", null, " fstatus is null ", null, null, null, " max_ampl DESC ");

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {
			int nameColIndex = c.getColumnIndex("file_name");
			do {
				n = n + 1;
				Log.v(LOG_TAG, "  "+c.getString(nameColIndex));
				list.add(c.getString(nameColIndex));
			} while ((c.moveToNext())&&(n < cnt));
		} else
			Log.v(LOG_TAG, "0 rows selected");
		c.close();
		this.close();
		return list;
	}
}

/*
 * 
 * // создаем объект для создания и управления версиями БД dbHelper = new
 * db_helper(this);
 * 
 * public void onClick(View v) {
 * 
 * // создаем объект для данных ContentValues cv = new ContentValues();
 * 
 * // получаем данные из полей ввода String name = etName.getText().toString();
 * String email = etEmail.getText().toString();
 * 
 * // подключаемся к БД SQLiteDatabase db = dbHelper.getWritableDatabase();
 * 
 * 
 * switch (v.getId()) { case R.id.btnAdd: Log.d(LOG_TAG,
 * "--- Insert in mytable: ---"); // подготовим данные для вставки в виде пар:
 * наименование столбца - значение
 * 
 * cv.put("file_name", name); cv.put("ampl", email); cv.put("date_time",
 * name+email); // вставляем запись и получаем ее ID long rowID =
 * db.insert("rec_log", null, cv); Log.d(LOG_TAG, "row inserted, ID = " +
 * rowID); break; case R.id.btnRead: Log.d(LOG_TAG, "--- Rows in mytable: ---");
 * // делаем запрос всех данных из таблицы mytable, получаем Cursor Cursor c =
 * db.query("rec_log", null, null, null, null, null, null);
 * 
 * // ставим позицию курсора на первую строку выборки // если в выборке нет
 * строк, вернется false if (c.moveToFirst()) {
 * 
 * // определяем номера столбцов по имени в выборке int idColIndex =
 * c.getColumnIndex("id"); int nameColIndex = c.getColumnIndex("file_name"); int
 * emailColIndex = c.getColumnIndex("ampl"); int tsColIndex =
 * c.getColumnIndex(""date_time");
 * 
 * do { // получаем значения по номерам столбцов и пишем все в лог
 * Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) + ", name = " +
 * c.getString(nameColIndex) + ", email = " + c.getString(emailColIndex));
 * ", time = " + c.getString(tsColIndex )); // переход на следующую строку // а
 * если следующей нет (текущая - последняя), то false - выходим из цикла } while
 * (c.moveToNext()); } else Log.d(LOG_TAG, "0 rows"); c.close(); break; case
 * R.id.btnClear: Log.d(LOG_TAG, "--- Clear mytable: ---"); // удаляем все
 * записи int clearCount = db.delete("rec_log", null, null); Log.d(LOG_TAG,
 * "deleted rows count = " + clearCount); break; } // закрываем подключение к БД
 * dbHelper.close(); } }
 */
