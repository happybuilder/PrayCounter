package edu.wisdom.praycounter2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Chan Chu Man on 10/11/2016.
 */

public class PrayListFragment extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pray_list_frag, container, false);

        activity = this.getActivity();

        PrayCounterDb db = new PrayCounterDb(activity, null);
        Cursor prayCursor = db.queryPrayInfo();
        final ListView lv1 = (ListView) view.findViewById(R.id.lstvPrayList);
        lv1.setAdapter(new PrayListAdapter(activity, prayCursor));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv1.getItemAtPosition(position);
                PrayItem prayItem = (PrayItem) o;
                Toast.makeText(activity, "Selected :" + " " + prayItem, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

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

    // just for testing.
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
