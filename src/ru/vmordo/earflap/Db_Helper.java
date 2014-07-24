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
		// ����������� �����������
		super(context, "myDB", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("test", "--- onCreate database ---");
		// ������� ������� � ������
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
		// ������������ � ��
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d(LOG_TAG, "--- Insert in mytable: ---");
		// ���������� ������ ��� ������� � ���� ���: ������������ ������� -
		// ��������
		cv.put("file_name", fln);
		cv.put("max_ampl", max_ampl);
		cv.put("avg_ampl", avg_ampl);
		cv.put("rlen", rlen);
		// ��������� ������ � �������� �� ID
		long rowID = db.insert("rec_log", null, cv);
		Log.d(LOG_TAG, "row inserted, ID = " + rowID);

		// ��������� ����������� � ��
		this.close();
	}

	public void update_status(String fln) {
		ContentValues cv = new ContentValues();
		// ������������ � ��
		SQLiteDatabase db = this.getWritableDatabase();

		Log.v(LOG_TAG, "--- update_status in mytable: ---");
		// ���������� ������ ��� ������� � ���� ���: ������������ ������� -
		// ��������
		cv.put("fstatus", "1");
		String[] w = {fln};
		// ��������� ������ � �������� �� ID
		long rowID = db.update("rec_log", cv, "file_name = ?", w );
		Log.v(LOG_TAG, "row updated  = " + rowID);

		// ��������� ����������� � ��
		this.close();
	}

	public Cursor get_log_cur() {
		// ������������ � ��
		SQLiteDatabase db = this.getWritableDatabase();

		Log.v(LOG_TAG, "--- rec_log   get_log_cur ---");
		// ������ ������ ���� ������ �� ������� mytable, �������� Cursor
		return db.query("rec_log", null, " fstatus is null ", null, null, null, " max_ampl DESC ");
	}

	public void print_log() {
		// ������������ � ��
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d(LOG_TAG, "--- Rows in mytable: ---");
		// ������ ������ ���� ������ �� ������� mytable, �������� Cursor
		Cursor c = db.query("rec_log", null, null, null, null, null, null);

		// ������ ������� ������� �� ������ ������ �������
		// ���� � ������� ��� �����, �������� false
		if (c.moveToFirst()) {

			// ���������� ������ �������� �� ����� � �������
			int idColIndex = c.getColumnIndex("_id");
			int nameColIndex = c.getColumnIndex("file_name");
			int max_amplColIndex = c.getColumnIndex("max_ampl");
			int avg_amplColIndex = c.getColumnIndex("avg_ampl");
			int rlenColIndex = c.getColumnIndex("rlen");
			int fstatusColIndex = c.getColumnIndex("fstatus");
			int tsColIndex = c.getColumnIndex("date_time");

			do {
				// �������� �������� �� ������� �������� � ����� ��� � ���
				Log.d(LOG_TAG,
						"ID = " + c.getInt(idColIndex) + ", file = "
								+ c.getString(nameColIndex) + ", max aml = "
								+ c.getString(max_amplColIndex)
								+ ", avg aml = "
								+ c.getString(avg_amplColIndex) + ", len = "
								+ c.getString(rlenColIndex) + ", fstatus = "
								+ c.getString(fstatusColIndex) +

								", time = " + c.getString(tsColIndex));
				// ������� �� ��������� ������
				// � ���� ��������� ��� (������� - ���������), �� false -
				// ������� �� �����
			} while (c.moveToNext());
		} else
			Log.d(LOG_TAG, "0 rows");
		c.close();
		// ��������� ����������� � ��
		this.close();
	}

	public List<String> get_list(int cnt) {
		List<String> list = new ArrayList<String>();
		int n = 0;
		// ������������ � ��
		SQLiteDatabase db = this.getWritableDatabase();

		// ������ ������ ���� ������ �� ������� mytable, �������� Cursor
		Cursor c = db.query("rec_log", null, " fstatus is null ", null, null, null, " max_ampl DESC ");

		// ������ ������� ������� �� ������ ������ �������
		// ���� � ������� ��� �����, �������� false
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
 * // ������� ������ ��� �������� � ���������� �������� �� dbHelper = new
 * db_helper(this);
 * 
 * public void onClick(View v) {
 * 
 * // ������� ������ ��� ������ ContentValues cv = new ContentValues();
 * 
 * // �������� ������ �� ����� ����� String name = etName.getText().toString();
 * String email = etEmail.getText().toString();
 * 
 * // ������������ � �� SQLiteDatabase db = dbHelper.getWritableDatabase();
 * 
 * 
 * switch (v.getId()) { case R.id.btnAdd: Log.d(LOG_TAG,
 * "--- Insert in mytable: ---"); // ���������� ������ ��� ������� � ���� ���:
 * ������������ ������� - ��������
 * 
 * cv.put("file_name", name); cv.put("ampl", email); cv.put("date_time",
 * name+email); // ��������� ������ � �������� �� ID long rowID =
 * db.insert("rec_log", null, cv); Log.d(LOG_TAG, "row inserted, ID = " +
 * rowID); break; case R.id.btnRead: Log.d(LOG_TAG, "--- Rows in mytable: ---");
 * // ������ ������ ���� ������ �� ������� mytable, �������� Cursor Cursor c =
 * db.query("rec_log", null, null, null, null, null, null);
 * 
 * // ������ ������� ������� �� ������ ������ ������� // ���� � ������� ���
 * �����, �������� false if (c.moveToFirst()) {
 * 
 * // ���������� ������ �������� �� ����� � ������� int idColIndex =
 * c.getColumnIndex("id"); int nameColIndex = c.getColumnIndex("file_name"); int
 * emailColIndex = c.getColumnIndex("ampl"); int tsColIndex =
 * c.getColumnIndex(""date_time");
 * 
 * do { // �������� �������� �� ������� �������� � ����� ��� � ���
 * Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) + ", name = " +
 * c.getString(nameColIndex) + ", email = " + c.getString(emailColIndex));
 * ", time = " + c.getString(tsColIndex )); // ������� �� ��������� ������ // �
 * ���� ��������� ��� (������� - ���������), �� false - ������� �� ����� } while
 * (c.moveToNext()); } else Log.d(LOG_TAG, "0 rows"); c.close(); break; case
 * R.id.btnClear: Log.d(LOG_TAG, "--- Clear mytable: ---"); // ������� ���
 * ������ int clearCount = db.delete("rec_log", null, null); Log.d(LOG_TAG,
 * "deleted rows count = " + clearCount); break; } // ��������� ����������� � ��
 * dbHelper.close(); } }
 */
