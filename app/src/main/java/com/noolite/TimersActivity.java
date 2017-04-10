package com.noolite;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.noolite.adapters.ChannelListAdapter;
import com.noolite.adapters.TimerListAdapter;
import com.noolite.dbchannels.DBManagerChannel;
import com.noolite.dbtimer.DBManagerTimer;
import com.noolite.timers.TimerElement;

import java.text.ParseException;
import java.util.ArrayList;


//отображение таймеров
public class TimersActivity extends Activity {

    private ActionBar actionBar;
    private View view;
    private TextView title;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timers);

        LayoutInflater vi = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(R.layout.action_bar_channel_activity, null);

        actionBar = getActionBar();

        actionBar.setCustomView(view, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        title = (TextView) view.findViewById(R.id.actionBarGroupTitle);
        title.setText("Таймеры");

        //переход на главное Activity по нажатию на кнопку "назад"
        backBtn = (ImageButton) view.findViewById(R.id.backBtnChannels);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //получение таймеров из базы данных
        ArrayList<TimerElement> allTimers = new ArrayList<TimerElement>();
        DBManagerTimer dbManager = DBManagerTimer
                .getInstance(getApplicationContext());
        dbManager.connect(getApplicationContext());

        try {
            allTimers = DBManagerTimer.getAll();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //создания адаптера для UI таймеров и присваивание адаптера списку
        ListView timersListView = (ListView) findViewById(R.id.timersListView);
        TimerListAdapter customAdapter = new TimerListAdapter(this,
                allTimers);
        timersListView.setAdapter(customAdapter);

    }
}
