package edu.wisdom.praycounter2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static edu.wisdom.praycounter2.PrayCounterDb.COL_LASTUPDATE;
import static edu.wisdom.praycounter2.PrayCounterDb.COL_NAME;
import static edu.wisdom.praycounter2.PrayCounterDb.COL_ROUNDSIZE;

public class PrayListAdapter extends CursorAdapter {

	private static String datetimeFormat = "yyyy年MM月dd日, hh時mm分";

	public PrayListAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.pray_list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Find fields to populate in inflated template
		TextView prayNameView = (TextView) view.findViewById(R.id.txtvPrayName);
		TextView roundSizeView = (TextView) view.findViewById(R.id.txtvRoundSize);
		TextView prayDateView = (TextView) view.findViewById(R.id.txtvPrayDate);

		// Extract properties from cursor
		String prayName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
		int roundSize = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROUNDSIZE));
		Date prayDate = PrayCounterDbHelper.stringToDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTUPDATE)));

		// Populate fields with extracted properties
		prayNameView.setText(prayName);
		roundSizeView.setText(Integer.toString(roundSize));
		prayDateView.setText(formatDate(prayDate));
	}

	private static String formatDate(java.util.Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat ( datetimeFormat );
		return sdf.format(date);
	}


	static class ViewHolder {
		TextView prayNameView;
		TextView roundSizeView;
		TextView prayDateView;
	}
}
