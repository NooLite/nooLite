package com.noolite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.noolite.R;
import com.noolite.domain.TimerElement;

import java.util.ArrayList;

//адаптер для UI таймеров
public class TimerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TimerElement> list;
    private LayoutInflater inflater;
    private static boolean[] checked;

    public TimerListAdapter(Context context, ArrayList<TimerElement> timers) {
        this.context = context;
        this.list = timers;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checked = new boolean[timers.size()];
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView;

        if (convertView == null) {
            customView = inflater.inflate(R.layout.timer_item, null);
        } else {
            customView = convertView;
        }

        TimerElement newItem = this.list.get(position);

        //инициализация элементов UI
        TextView timerItem = (TextView) customView.findViewById(R.id.timerTitle);
        TextView timeView = (TextView) customView.findViewById(R.id.timeView);
        ImageButton isOn = (ImageButton) customView.findViewById(R.id.timerSwitch);
        if(newItem.isOn()){
            isOn.setImageResource(R.drawable.selected);
            checked[position] = true;
        }else{
            isOn.setImageResource(R.drawable.unselected);
            checked[position] = false;
        }

        //список дней недели
        ArrayList<TextView> daysOfWeekDisplay = new ArrayList<TextView>();

        TextView monday = (TextView) customView.findViewById(R.id.monday);
        daysOfWeekDisplay.add(monday);
        TextView tuesday = (TextView) customView.findViewById(R.id.tuesday);
        daysOfWeekDisplay.add(tuesday);
        TextView wednesday = (TextView) customView.findViewById(R.id.wednesday);
        daysOfWeekDisplay.add(wednesday);
        TextView thursday = (TextView) customView.findViewById(R.id.thurstday);
        daysOfWeekDisplay.add(thursday);
        TextView friday = (TextView) customView.findViewById(R.id.friday);
        daysOfWeekDisplay.add(friday);
        TextView saturday = (TextView) customView.findViewById(R.id.saturday);
        daysOfWeekDisplay.add(saturday);
        TextView sunday = (TextView) customView.findViewById(R.id.sunday);
        daysOfWeekDisplay.add(sunday);

        //изменение цвета активных дней недели
        boolean[] daysActive = newItem.getDaysArray();
        for(int i=0; i<7; i++){
            if(daysActive[i]){
                daysOfWeekDisplay.get(i).setTextColor(0x00B89C);
            }else{
                daysOfWeekDisplay.get(i).setTextColor(0x555555);
            }
        }

        TextView channelNameView = (TextView) customView.findViewById(R.id.channelNameView);
        channelNameView.setText(String.valueOf(newItem.getId()));
        TextView commandNameView = (TextView) customView.findViewById(R.id.actionNameView);
        commandNameView.setText(String.valueOf(newItem.getCommand()));

        return customView;
    }
}

