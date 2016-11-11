package edu.wisdom.praycounter2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chan Chu Man on 17/10/2016.
 * 每唸滿一輪, 寫一筆紀錄.
 */

public class PrayHistoryDbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "prayhistory.db";
    private final static int COUNTER_VERSION = 1;

    private String[] sql = {
            /* Version 1 */
            "create table prayhistory(" + " _id integer primary key autoincrement"
                    + ", name nvarchar(30)" // 經文名稱.
                    + ", roundsize integer not null default 0" // 唸了多少遍經文.
                    + ", notes text" // 筆記.
                    + ", createdttm varchar(12) not null);" // 建立的日期時間.
    };

    public PrayHistoryDbHelper(Context context) {
        super(context, DB_NAME, null, COUNTER_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQLs = sql[COUNTER_VERSION - 1];

        /* Create "prayhistory" tables. */
        db.execSQL(SQLs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }



}
