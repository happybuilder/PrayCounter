package edu.praycounter;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PrayListFragment extends ListFragment {
	
    private PrayCounterDb dbCounter;
	private SimpleCursorAdapter prayListAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// In frag_pray_list.xml, must has listview id = "@id/android:list"
		return inflater.inflate(R.layout.frag_pray_list, container, false);	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		dbCounter = new PrayCounterDb(this.getActivity(), null);

		Cursor cursor = dbCounter.queryPrayInfo();
		
		String[] columns = new String[] {
				PrayCounterDb.COL_NAME,
				PrayCounterDb.COL_ROUNDSIZE
		};
		
		int[] views = new int[] {
			R.id.pray_name,
			R.id.round_size
		};		

		this.prayListAdapter = new SimpleCursorAdapter(this.getActivity(), 
				R.layout.pray_list_item, cursor, columns, views,
				SimpleCursorAdapter.FLAG_AUTO_REQUERY // FLAG_REGISTER_CONTENT_OBSERVER
		);

		this.setListAdapter(prayListAdapter);
	}
}
