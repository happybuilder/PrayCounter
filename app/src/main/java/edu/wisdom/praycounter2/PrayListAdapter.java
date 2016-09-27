package edu.wisdom.praycounter2;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class PrayListAdapter extends SimpleCursorAdapter {

	public PrayListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
	}

}
