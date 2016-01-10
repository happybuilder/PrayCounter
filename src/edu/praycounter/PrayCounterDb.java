package edu.praycounter;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PrayCounterDb {
	
	// Define column index.
	private final static int ID = 0;
	private final static int CURRENT = 1;
	private final static int ROUND = 2;
	private final static int ROUNDSIZE = 3;
	private final static int NAME = 4;
	private final static int NOTES = 5;
	private final static int LASTUPDATE = 6;
	
	private Context context;
	public CounterBean counter;
	
	public PrayCounterDb(Context context, CounterBean counter) {
		this.context = context;
		this.counter = counter;
	}
	
	// 替目前經文加一.
	// 傳回最新 counter 數值.
	public int addOne() {
		if (checkoutCurrentCounter()) 
		// 將現有的誦經紀錄加一.
		{
			addOneByUpdate();
		}
		else
		// 啟始一筆誦經紀錄.
		{
			addOneByInsert();	// TODO.
		}
		
        counter.getCurrent();
        
		return counter.current;
	}
	
	private int addOneByUpdate() {
		counter.current++;
		counter.roundSize = 0;		// TODO: 稍後從畫面設定.

		// 每輪所誦次數, 至少誦 2 次, 否則代表不使用「輪數」.
		if (counter.roundSize > 1) {
			if (counter.current >= counter.roundSize) {		// 如果已誦滿一輪 ...
				counter.current = 0;				// 重置計數器.
				counter.round++;					// 輪數加一.
			}
		}
		
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("_id", counter.id);
            values.put("current", counter.current);
            values.put("round", counter.round);
            values.put("lastupdate", PrayCounterDbHelper.dateToString(counter.lastUpdate));
            db.update("counter", values, "_id = ?", new String[]{Long.toString(counter.id)});

            // Set to current.
            values.clear();
            values.put("currentid", counter.id);
            values.put("lastupdate", PrayCounterDbHelper.dateToString(counter.lastUpdate));        
            db.update("status", values, null, null);		// "status" table 只有一個 record, 無需設定 where clause.            
        }
        finally {
        	db.close();
        }
        
        return counter.current;		
	}
	
	// 新增一個經文紀錄, set current = 1
	public void addOneByInsert() {
		counter.current = 1;
		
		// 每一輪誦經數, 至少兩遍或以上, 如果設定為 1, 則設回 0, 即是不使用.
		if (counter.roundSize <= 1) {
			counter.roundSize = 0;
		}

		insertPray();		
	}
	
	public long insertPray() {
		Date lastUpdate = new Date();
		String sLastUpdate = PrayCounterDbHelper.dateToString(lastUpdate);

		ContentValues values = new ContentValues();
        values.put("current", counter.current);
        values.put("round", counter.round);
        values.put("roundsize", counter.roundSize);
        values.put("name", counter.name);
        values.put("notes", counter.notes);
        values.put("lastupdate", sLastUpdate);

		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
	        counter.id = db.insert("counter", null, values);
	        counter.lastUpdate = lastUpdate;
        }
        finally {
        	db.close();
        }
        
        return counter.id;
	}
	
	public void updatePray() {
		Date lastUpdate = new Date();
		String sLastUpdate = PrayCounterDbHelper.dateToString(lastUpdate);

		ContentValues values = new ContentValues();
        values.put("current", counter.current);
        values.put("round", counter.round);
        values.put("roundsize", counter.roundSize);
        values.put("name", counter.name);
        values.put("notes", counter.notes);
        values.put("lastupdate", sLastUpdate);

		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
	        counter.id = db.update("counter", values, "_id=" + counter.id, null);
	        counter.lastUpdate = lastUpdate;
        }
        finally {
        	db.close();
        }
	}
	
	// 取得目前的經文 ID.
	public long getCurrentId() {
		String[] columns = {"currentId"};
		
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
        	Cursor cursor = db.query("status", columns, null, null, null, null, null);
			try {
				if (cursor.moveToFirst()) {
					return cursor.getLong(0);
				}
				else {
					return -1;
				}
			}
			finally {
				cursor.close();
			}
        }
        finally {
        	db.close();
        }
	}

	// 取得目前的 Counter value
	// Return:
	//   false = 不存在指定經文名稱的誦經紀錄
	//   true  = 找到指定經文名稱的誦經紀錄
	public boolean checkoutCurrentCounter() {
		String[] columns = {"_id", "current", "round", "roundsize", "name", "notes", "lastupdate"};
        
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
			Cursor cursor = db.query("counter", columns, "_id = " + getCurrentId(), 
					null, null, null, null);
			try {
				if (cursor.moveToFirst()) {								// 如果已有此經文的誦經紀錄
					counter.id = cursor.getInt(ID);
					counter.current = cursor.getInt(CURRENT);
					counter.round = cursor.getInt(ROUND);
					counter.roundSize = cursor.getInt(ROUNDSIZE);
					counter.name = cursor.getString(NAME);
					counter.notes = cursor.getString(NOTES);
					counter.lastUpdate = PrayCounterDbHelper.stringToDate(cursor.getString(LASTUPDATE));
					
					return true;
				}
			}
			finally {
				cursor.close();
			}
        }
        finally {
        	db.close();
        }
		
		return false;
	}
	
	// 傳回: DB 有沒有指定經文的誦經紀錄.
	public boolean isPrayNameExists(String prayName) {
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
			Cursor c = db.rawQuery("select count(*) from counter where name = ?", 
					new String[]{prayName} );
			c.moveToFirst();
			try {
				return c.getInt(0) > 0;
			}
			finally {
				c.close();
			}
        }
        finally {
        	db.close();
        }
	}
	
	// 傳回: DB 是否沒有任何具名誦經紀錄 (NONAME 是不具名紀錄).
	public boolean isPrayNameDbEmpty() {
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
			Cursor c = db.rawQuery("select count(*) from counter where name <> ?", 
					new String[]{PrayCounterDbHelper.BLANK_PRAY_NAME} );
			c.moveToFirst();		
			try {
				return c.getInt(0) == 0;
			}
			finally {
				c.close();
			}
        }
        finally {
        	db.close();
        }
	}
	
	// 設定目前唸誦的經文
	// name: 經文名稱
	// 如果唸誦另一經文, 把 counter 設回零, 系統不容許跳來跳去唸誦.
	public void setCurrent(long currentId) {
		if (getCurrentId() != currentId) {	// 如果切換另一經文
			resetAllCounter();
			
			PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        try {
				ContentValues values = new ContentValues();
				values.put("currentid", currentId);			
				db.update("status", values, null, null);
	        }
	        finally {
	        	db.close();
	        }
		}		
	}
	
	public void resetAllCounter() {
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
			db.execSQL("UPDATE counter set current = 0");
			checkoutCurrentCounter();
        }
        finally {
        	db.close();
        }
	}

}
