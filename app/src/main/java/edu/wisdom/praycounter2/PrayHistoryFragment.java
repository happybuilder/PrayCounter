package edu.wisdom.praycounter2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Chan Chu Man on 10/11/2016.
 */

public class PrayHistoryFragment extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pray_history_frag, container, false);

//        activity = this.getActivity();

//        PrayCounterDb db = new PrayCounterDb(activity, null);
//        Cursor prayCursor = db.queryPrayInfo();
//        final ListView lv1 = (ListView) view.findViewById(R.id.lstvPrayList);
//        lv1.setAdapter(new PrayListAdapter(activity, prayCursor));
//        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//                Object o = lv1.getItemAtPosition(position);
//                PrayItem prayItem = (PrayItem) o;
//                Toast.makeText(activity, "Selected :" + " " + prayItem, Toast.LENGTH_LONG).show();
//            }
//        });


        return view;
    }



}
