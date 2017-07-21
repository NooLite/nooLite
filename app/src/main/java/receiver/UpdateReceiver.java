package receiver;

import com.noolite.ChannelViewActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//BroadcastReceiver для приема сигнала о необходимости обновить UI,
// когда были отосланы команды с pebble
public class UpdateReceiver extends BroadcastReceiver{

	//при получении надо обновить список
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			ChannelViewActivity.updateList();
		} catch (Exception e) {

		}
	}

}
