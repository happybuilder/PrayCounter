package edu.praycounter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrayCounterDbHelper extends SQLiteOpenHelper {

	public final static String DATETIMEFORMAT = "yyMMddHHmmss";
	private final static String DB_NAME = "praycounter.db";
	private final static int COUNTER_VERSION = 1;
	
	protected SQLiteDatabase db;

	private String[] counter_sql = {
			// Version 1
			"create table counter(" + " _id integer primary key autoincrement" + ", current integer not null default 0" // 已誦了多少遍經文.
					+ ", round integer not null default 0" // 已誦了多少輪經文.
					+ ", roundsize integer not null default 0" // 一輪經文為多少遍.
					+ ", name nvarchar(30)" // 經文名稱.
					+ ", notes text" // 筆記.
					+ ", islast boolean" // 目前是否誦此經文.
					+ ", lastupdate varchar(12))" // 最後更新日期時間.
	};

	public PrayCounterDbHelper(Context context) {
		super(context, DB_NAME, null, COUNTER_VERSION);
		
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String sql = counter_sql[COUNTER_VERSION - 1];

		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public SQLiteDatabase getDb() {
		return this.db;
	}

}
