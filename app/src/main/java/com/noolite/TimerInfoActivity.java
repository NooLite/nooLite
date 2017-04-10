package com.noolite;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


//Отображение информации о таймере
public class TimerInfoActivity extends Activity {

    private ActionBar actionBar;
    private View view;
    private TextView title;
    private ImageButton backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_timer);

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
        backBtn = (ImageButton) view.findViewById(R.id.backBtnChannels);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimersActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}