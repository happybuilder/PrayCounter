package edu.wisdom.praycounter2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PrayListAdapter extends BaseAdapter {

	private ArrayList<PrayItem> listData;
	private LayoutInflater layoutInflater;

	public PrayListAdapter(Context aContext, ArrayList<PrayItem> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(aContext);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.pray_list_item, null);
			holder = new ViewHolder();
			holder.prayNameView = (TextView) convertView.findViewById(R.id.txtvPrayName);
			holder.roundSizeView = (TextView) convertView.findViewById(R.id.txtvRoundSize);
			holder.prayDateView = (TextView) convertView.findViewById(R.id.txtvPrayDate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.prayNameView.setText(listData.get(position).getPrayName());
		holder.roundSizeView.setText(listData.get(position).getRoundSize());
		holder.prayDateView.setText(listData.get(position).getPrayDate());
		return convertView;
	}

	static class ViewHolder {
		TextView prayNameView;
		TextView roundSizeView;
		TextView prayDateView;
	}
}
