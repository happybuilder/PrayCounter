package edu.praycounter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbCounter {

	// Define column index.
	private final static int ID = 0;
	private final static int CURRENT = 1;
	private final static int ROUND = 2;
	private final static int ROUNDSIZE = 3;
	private final static int NAME = 4;
	private final static int NOTES = 5;
	private final static int ISCURRENT = 6;
	private final static int LASTUPDATE = 7;
	
	// Current counter record.
	private int id;				// Record ID.
	private int current;		// 已誦了多少遍經文.
	private int round;			// 已誦了多少輪經文.
	private int roundSize;		// 一輪經文為多少遍.
	private String name;		// 經文名稱.
	private String notes;		// 筆記.
	private Date lastUpdate;	// 最後更新日期時間.
	
	private PrayCounterDbHelper dbHelper;
	
	public DbCounter(Context context) {
		this.dbHelper = new PrayCounterDbHelper(context);
	}
	
//	public DbCounter(PrayCounterDbHelper dbHelper) {
//		this.dbHelper = dbHelper;
//	}
	
	public int getCurrent() {
		return this.current;
	}
	
	// 替目前經文加一.
	// 傳回最新 counter 數值.
	public int addOne() {
		if (getCounter()) 
		// 將現有的誦經紀錄加一.
		{
			addOneByUpdate();
		}
		else
		// 啟始一筆誦經紀錄.
		{
			addOneByInsert(null, 0, null);	// TODO.
		}
		
        getCurrent();
        
		return current;
	}
	
	private int addOneByUpdate() {
		current++;
		roundSize = 0;		// TODO: 稍後從畫面設定.

		// 每輪所誦次數, 至少誦 2 次, 否則代表不使用「輪數」.
		if (roundSize > 1) {
			if (current >= roundSize) {	// 如果已誦滿一輪 ...
				current = 0;				// 重置計數器.
				round++;					// 輪數加一.
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(PrayCounterDbHelper.DATETIMEFORMAT);
		lastUpdate = new Date();
		String sDateTime = sdf.format(lastUpdate);
		
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("current", current);
        values.put("round", round);
        values.put("iscurrent", true);
        values.put("lastupdate", sDateTime);
        
        dbHelper.getDb().update("counter", values, "_id = ?", new String[]{Integer.toString(id)});
        
        return current;		
	}
	
	public void addOneByInsert(String name, int roundSize, String notes) {
		// 清除全部紀錄的 is last 旗號.
		dbHelper.getDb().execSQL("UPDATE counter set iscurrent = 0");

		SimpleDateFormat sdf = new SimpleDateFormat(PrayCounterDbHelper.DATETIMEFORMAT);
		lastUpdate = new Date();
		String sDateTime = sdf.format(lastUpdate);
		
		if (name == null || name.trim().equals("")) {
			name = "NONAME";
		}
		
		current = 1;
		
		// 每一輪誦經數, 至少兩遍或以上, 如果設定為 1, 則設回 0, 即是不使用.
		if (roundSize <= 1) {
			roundSize = 0;
		}
		
        ContentValues values = new ContentValues();
        values.put("current", current);
        values.put("round", 0);
        values.put("roundsize", roundSize);
        values.put("name", name);
        values.put("notes", notes);
        values.put("iscurrent", true);
        values.put("lastupdate", sDateTime);

        dbHelper.getDb().insert("counter", null, values);
	}

	// 取得目前的 Counter value
	// Return:
	//   false = 不存在指定經文名稱的誦經紀錄
	//   true  = 找到指定經文名稱的誦經紀錄
	public boolean getCounter() {		
		String[] columns = {"_id", "current", "round", "roundsize", "name", "notes", "iscurrent", "lastupdate"};
		Cursor cursor = dbHelper.getDb().query("counter", columns, "iscurrent = 1", null, null, null, null);
		try {
			if (cursor.moveToFirst()) {								// 如果已有此經文的誦經紀錄
				this.id = cursor.getInt(ID);
				this.current = cursor.getInt(CURRENT);
				this.round = cursor.getInt(ROUND);
				this.roundSize = cursor.getInt(ROUNDSIZE);
				this.name = cursor.getString(NAME);
				this.notes = cursor.getString(NOTES);
				
				try {
					DateFormat df = new SimpleDateFormat(PrayCounterDbHelper.DATETIMEFORMAT);
					this.lastUpdate = df.parse(cursor.getString(LASTUPDATE));
				} catch (ParseException e) {
					Log.d("Debug", "lastUpdate problem");
					this.lastUpdate = new Date();
				}
				
				return true;
			}
		}
		finally {
			cursor.close();
		}
		
		return false;
	}
	
	// 設定目前唸誦的經文
	// name: 經文名稱
	// 如果唸誦另一經文, 把 counter 設回零, 系統不容許跳來跳去唸誦.
	public void setCurrent(String name) {
		// 取得目前唸誦哪一經文
		getCounter();
		
		if (!this.name.equals(name)) {	// 如果切換另一經文
			resetCounter();
		}
		
		dbHelper.getDb().execSQL("UPDATE counter set iscurrent = 1 where name = '" + name + "'");
		getCounter();
	}
	
	public void resetCounter() {
		dbHelper.getDb().execSQL("UPDATE counter set current = 0 where iscurrent = 1");
		getCounter();
	}
}
