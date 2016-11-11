package edu.wisdom.praycounter2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by Chan Chu Man on 11/11/2016.
 */

public class PrayHistoryDb {

    private Context context;
    private PrayHistoryBean history;

    public final static String TABLE_PRAYHISTORY = "prayhistory";
    public final static String COL_ROUNDSIZE = "roundsize";
    public final static String COL_NAME = "name";
    public final static String COL_NOTES = "notes";
    public final static String COL_CREATEDATETIME = "createdttm";

    public PrayHistoryDb(Context context, PrayHistoryBean history) {
        this.context = context;
        this.history = history;
    }

    public long insertPrayHistory() {
        Date createDttm = new Date();
        String sCreateDateTime = DateUtils.dateToString(createDttm);

        ContentValues values = new ContentValues();
        values.put(COL_ROUNDSIZE, history.roundSize);
        values.put(COL_NAME, history.name);
        values.put(COL_NOTES, history.notes);
        values.put(COL_CREATEDATETIME, sCreateDateTime);

        PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            history.id = db.insert(TABLE_PRAYHISTORY, null, values);
            history.createDttm = createDttm;
        }
        finally {
            db.close();
        }

        return history.id;
    }

    public Cursor queryPrayHistory() {
        PrayCounterDbHelper dbHelper = new PrayCounterDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {"_id", COL_NAME, COL_ROUNDSIZE, COL_NOTES, COL_CREATEDATETIME};
        Cursor cursor = db.query(TABLE_PRAYHISTORY, columns, null, null, null, null, null);
        return cursor;
    }

}
