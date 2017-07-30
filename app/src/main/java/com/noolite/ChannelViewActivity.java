package com.noolite;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.noolite.adapters.ChannelListAdapter;
import com.noolite.domain.ChannelElement;
import com.noolite.db.ds.BasicDataSource;
import com.noolite.db.ds.ChannelsDataSource;
import com.noolite.db.ds.DataSourceManager;
import com.noolite.db.ds.GroupDataSource;
import com.noolite.domain.GroupElement;
import com.noolite.domain.SensorElement;

/**
 * Activity для отображения списка каналов выбранной группы.
 */
public class ChannelViewActivity extends Activity implements OnClickListener {

	private static ArrayList<ChannelElement> allChannels = new ArrayList<ChannelElement>(); //список всех каналов
	private static ListView channelListView;
	private static ChannelListAdapter customAdapter;  //адаптер для списка каналов
	private ImageButton back;
	private static String groupTitle = new String();
	private ActionBar actionBar;
	private View view;
    //Intent, который вызывал открытие этого Activity и в котором передавались данные
	private static Intent intent;
//	private UpdateReceiver updateReceiver;

    private GroupElement currentGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channels);

		//получение вызывавшего Intent
        intent = getIntent();
        int groupId = (int) intent.getLongExtra(BasicDataSource.GROUP_ID, 0);

        GroupDataSource groupDS = DataSourceManager.getInstance().getGroupDS(getApplicationContext());
        try {
            currentGroup  = groupDS.get(groupId);
            groupTitle = currentGroup.getName();

            //получение всех каналов шлюза
            ChannelsDataSource chds = DataSourceManager.getInstance().getChannelsDS(getApplicationContext());
            allChannels = chds.getAll();

            //выбор каналов, отображаемых в данной группе
            List<ChannelElement> channelsToView = new ArrayList<ChannelElement>();
			channelsToView.addAll(currentGroup.getChannelElements());
            //добавление каналов, которые надо отображать
            for (SensorElement sensorElement : currentGroup.getSensorElements()) {
                channelsToView.add(
                        new ChannelElement(sensorElement.getId(), sensorElement.getName(), NooLiteDefs.CHANNEL_TYPE_SENSOR, 0, 0));
            }

            //инициализация списка каналов и его адаптера
            channelListView = (ListView) findViewById(R.id.lvChannels);
            Log.d("channelsToView", channelsToView.toString());
            customAdapter = new ChannelListAdapter(this, channelsToView);
            channelListView.setAdapter(customAdapter);

            actionBar = getActionBar();
            LayoutInflater vi = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
            view = vi.inflate(R.layout.action_bar_channel_activity, null);
            TextView actionBarTitle = (TextView) view
                    .findViewById(R.id.actionBarGroupTitle);
            actionBarTitle.setText(groupTitle);

            actionBar.setCustomView(view, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            back = (ImageButton) view.findViewById(R.id.backBtnChannels);
            back.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	//возвращение на главное Activity
	@Override
	public void onClick(View v) {
		SharedPreferences.Editor edit = MainActivity
				.getSharedPref().edit();
		edit.putBoolean("dialogShow", false);
		edit.commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		SharedPreferences.Editor edit = MainActivity
				.getSharedPref().edit();
		edit.putBoolean("dialogShow", false);
		edit.commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataSourceManager.getInstance().close();

	}
}

