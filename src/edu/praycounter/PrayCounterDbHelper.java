package edu.praycounter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PrayCounterDbHelper extends SQLiteOpenHelper {

	public final static String DATETIMEFORMAT = "yyMMddHHmmss";
	private final static String DB_NAME = "praycounter.db";
	private final static int COUNTER_VERSION = 1;
	public final static String BLANK_PRAY_NAME = "NONAME";

	private String[] counter_sql = {
			// Version 1
			"create table counter(" + " _id integer primary key autoincrement" + ", current integer not null default 0" // 已誦了多少遍經文.
					+ ", round integer not null default 0" // 已誦了多少輪經文.
					+ ", roundsize integer not null default 0" // 一輪經文為多少遍.
					+ ", name nvarchar(30)" // 經文名稱.
					+ ", notes text" // 筆記.
					+ ", lastupdate varchar(12));" // 最後更新日期時間.
	};

	// "status" table 總是只有一個 record.
	private String[] status_sql = {
			// Version 1
			"create table status(" + " _id integer primary key autoincrement"
					+ ", currentid integer references counter(_id)" // 目前正在誦唸哪一個經文.
					+ ", lastupdate varchar(12));" // 最後更新日期時間.
	};

	public PrayCounterDbHelper(Context context) {
		super(context, DB_NAME, null, COUNTER_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// final String SQLs = counter_sql[COUNTER_VERSION - 1]
		// + status_sql[COUNTER_VERSION - 1];
		String SQLs = counter_sql[COUNTER_VERSION - 1];

		// Create "counter" and "status" tables.
		db.execSQL(SQLs);

		SQLs = status_sql[COUNTER_VERSION - 1];

		db.execSQL(SQLs);

		// Init "counter" table.
		long id = initCounterTable(db);

		// Init "status" table.
		initStatusTable(db, id);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public static String dateToString(Date datetime) {
		if (datetime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(PrayCounterDbHelper.DATETIMEFORMAT);
			return sdf.format(datetime);
		}

		return "";
	}

	public static Date stringToDate(String datetime) {
		if (!datetime.trim().equals("")) {
			DateFormat df = new SimpleDateFormat(PrayCounterDbHelper.DATETIMEFORMAT);
			try {
				return df.parse(datetime);
			} catch (ParseException e) {
				Log.d("Debug", "stringToDate failed: " + datetime);
			}
		}

		return null;
	}

	// 以一個叫 "NONAME" 為名的經文名稱作 Init record.
	private long initCounterTable(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put("current", 0);
		values.put("round", 0);
		values.put("roundsize", 0);
		values.put("name", BLANK_PRAY_NAME);
		values.put("notes", "");
		values.put("lastupdate", dateToString(new Date()));

		return db.insert("counter", null, values);
	}

	// 設定一個叫 "NONAME" 為名的經文名稱作為 Current.
	public void initStatusTable(SQLiteDatabase db, long counter_id) {
		ContentValues values = new ContentValues();
		values.put("currentid", counter_id);
		values.put("lastupdate", dateToString(new Date()));

		db.insert("status", null, values);
	}
}
