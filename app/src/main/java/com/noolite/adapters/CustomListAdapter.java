package com.noolite.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noolite.R;
import com.noolite.groups.GroupElement;

//адаптер для UI списка групп
public class CustomListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<GroupElement> groups; //список отображаемых групп
	private LayoutInflater inflater;

	public CustomListAdapter(Context context, ArrayList<GroupElement> groups) {
		this.context = context;
		this.groups = groups;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return groups.size();
	}

	@Override
	public Object getItem(int position) {
		return groups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return groups.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView;

		if (convertView == null) {
			customView = inflater.inflate(R.layout.group_item, null);
		} else {
			customView = convertView;
		}
		//отображение названия элемента
		TextView title = (TextView) customView.findViewById(R.id.groupTitle);
		title.setText(groups.get(position).getName());

		return customView;
	}

}
