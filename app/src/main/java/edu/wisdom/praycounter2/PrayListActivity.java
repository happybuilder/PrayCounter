package edu.wisdom.praycounter2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

// 用 AppCompatActivity 比單用 Activity 多了 Tool Bar (App Bar) 的功能.
//   Ref: https://developer.android.com/training/appbar/setting-up.html
public class PrayListActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pray_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_praylist);
//        setSupportActionBar(toolbar);

		ArrayList prayListData = getListData();
		final ListView lv1 = (ListView) findViewById(R.id.lstvPrayList);
		lv1.setAdapter(new PrayListAdapter(this, prayListData));
		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Object o = lv1.getItemAtPosition(position);
				PrayItem prayItem = (PrayItem) o;
				Toast.makeText(PrayListActivity.this, "Selected :" + " " + prayItem, Toast.LENGTH_LONG).show();
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.pray_list_menu, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.itemPraySetting) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ArrayList getListData() {
		ArrayList<PrayItem> results = new ArrayList<PrayItem>();
		PrayItem prayItem = new PrayItem();

		prayItem.setPrayName("般若心經");
		prayItem.setRoundSize("唸誦次數：1080");
		prayItem.setPrayDate("2016年10月30日, 13時35分");
		results.add(prayItem);

		prayItem = new PrayItem();
		prayItem.setPrayName("大悲咒");
		prayItem.setRoundSize("唸誦次數：1080");
		prayItem.setPrayDate("2016年10月30日, 13時35分");
		results.add(prayItem);

		return results;
	}	
}
