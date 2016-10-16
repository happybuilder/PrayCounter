package edu.wisdom.praycounter2;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PrayCounterDb {
	
	// Define column index.
//	private final static int ID = 0;
//	private final static int CURRENT = 1;
//	private final static int ROUND = 2;
//	private final static int ROUNDSIZE = 3;
//	private final static int NAME = 4;
//	private final static int NOTES = 5;
//	private final static int LASTUPDATE = 6;
	
	private Context context;
	public CounterBean counter;
	
	public final static String TABLE_COUNTER = "counter";	
	public final static String COL_CURRENT = "current";
	public final static String COL_ROUND = "round";
	public final static String COL_ROUNDSIZE = "roundsize";
	public final static String COL_NAME = "name";
	public final static String COL_NOTES = "notes";
	public final static String COL_LASTUPDATE = "lastupdate";
	
	public final static String TABLE_STATUS = "status";
	public final static String COL_CURRENT_ID = "currentid";
	
	public PrayCounterDb(Context context, CounterBean counter) {
		this.context = context;
		this.counter = counter;
	}
	
	// 替目前經文加一.
	// 傳回: 是否已 round full.
	public boolean addOne() {
        boolean roundFull = false;

		if (checkoutCurrentCounter()) 
		// 將現有的誦經紀錄加一.
		{
			roundFull = addOneByUpdate();
		}
		else
		// 啟始一筆誦經紀錄.
		{
			addOneByInsert();
		}
		
        counter.getCurrent();
        
		return roundFull;
	}

    // return: false: 未滿 round size.
	private boolean addOneByUpdate() {
        boolean roundFull = false;

		// 如果計數器尚未到 roundSize, 或根本沒有設定 roundSize
		if (counter.current < counter.roundSize || counter.roundSize == 0) {
            counter.current++;

            // 每輪所誦次數, 至少誦 2 次, 否則代表不使用「輪數」.
            if (counter.roundSize > 1) {
                if (counter.current >= counter.roundSize) {        // 如果已誦滿一輪 ...
                    counter.round++;                    // 輪數加一.
                    roundFull = true;
                }
            }

			// Save it to database.
            PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put("_id", counter.id);
                values.put(COL_CURRENT, counter.current);
                values.put(COL_ROUND, counter.round);
                values.put(COL_LASTUPDATE, PrayCounterDbHelper.dateToString(counter.lastUpdate));
                db.update(TABLE_COUNTER, values, "_id = ?", new String[]{Long.toString(counter.id)});

                // Set to current.
                values.clear();
                values.put(COL_CURRENT_ID, counter.id);
                values.put(COL_LASTUPDATE, PrayCounterDbHelper.dateToString(counter.lastUpdate));
                db.update(TABLE_STATUS, values, null, null);        // "status" table 只有一個 record, 無需設定 where clause.
            } finally {
                db.close();
            }
        }
        else {
            roundFull = true;
        }
        
        return roundFull;
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
        values.put(COL_CURRENT, counter.current);
        values.put(COL_ROUND, counter.round);
        values.put(COL_ROUNDSIZE, counter.roundSize);
        values.put(COL_NAME, counter.name);
        values.put(COL_NOTES, counter.notes);
        values.put(COL_LASTUPDATE, sLastUpdate);

		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
	        counter.id = db.insert(TABLE_COUNTER, null, values);
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
        values.put(COL_CURRENT, counter.current);
        values.put(COL_ROUND, counter.round);
        values.put(COL_ROUNDSIZE, counter.roundSize);
        values.put(COL_NAME, counter.name);
        values.put(COL_NOTES, counter.notes);
        values.put(COL_LASTUPDATE, sLastUpdate);

		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
	        counter.id = db.update(TABLE_COUNTER, values, "_id=" + counter.id, null);
	        counter.lastUpdate = lastUpdate;
        }
        finally {
        	db.close();
        }
	}
	
	public long getCounterId(String prayName) {
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
        	if (prayName.equals("")) {
        		prayName = PrayCounterDbHelper.BLANK_PRAY_NAME;
        	}
			Cursor c = db.rawQuery("select _id from counter where name = ?", 
					new String[]{prayName} );
			c.moveToFirst();
			try {
				return c.getInt(0);
			}
			finally {
				c.close();
			}
        }
        finally {
        	db.close();
        }
	}
	
	// 取得目前的經文 ID.
	public long getCurrentId() {
		String[] columns = {COL_CURRENT_ID};
		
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
        	Cursor cursor = db.query(TABLE_STATUS, columns, null, null, null, null, null);
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
		String[] columns = {"_id", COL_CURRENT, COL_ROUND, COL_ROUNDSIZE, COL_NAME, COL_NOTES, COL_LASTUPDATE};
        
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
			Cursor cursor = db.query(TABLE_COUNTER, columns, "_id = " + getCurrentId(), 
					null, null, null, null);
			try {
				if (cursor.moveToFirst()) {								// 如果已有此經文的誦經紀錄
					counter.id = cursor.getInt(cursor.getColumnIndex("_id"));
					counter.current = cursor.getInt(cursor.getColumnIndex(COL_CURRENT));
					counter.round = cursor.getInt(cursor.getColumnIndex(COL_ROUND));
					counter.roundSize = cursor.getInt(cursor.getColumnIndex(COL_ROUNDSIZE));
					counter.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
					counter.notes = cursor.getString(cursor.getColumnIndex(COL_NOTES));
					counter.lastUpdate = PrayCounterDbHelper.stringToDate(cursor.getString(cursor.getColumnIndex(COL_LASTUPDATE)));
					
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
	public boolean isPrayNameOnlyNoName() {
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
			Cursor c = db.rawQuery("select count(*) from counter where name = ?", 
					new String[]{PrayCounterDbHelper.BLANK_PRAY_NAME} );
			c.moveToFirst();		
			try {
				return c.getInt(0) == 1;	
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
	// 如果唸誦另一經文, 把 counter 設回零, 系統不鼓勵跳來跳去唸誦.
	public void setCurrent(long currentId) {
		if (getCurrentId() != currentId) {	// 如果切換另一經文			
			PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        try {
				ContentValues values = new ContentValues();
				values.put(COL_CURRENT_ID, currentId);			
				db.update(TABLE_STATUS, values, null, null);
				resetAllCounter();
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

	public Cursor queryPrayInfo() {
		PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
		String[] columns = {"_id", COL_NAME, COL_ROUND, COL_ROUNDSIZE, COL_NOTES, COL_LASTUPDATE};
    	Cursor cursor = db.query(TABLE_COUNTER, columns, null, null, null, null, null);
		return cursor;
	}
}
