package receiver;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.noolite.pebble.PebbleManager;
import com.noolite.settings.SettingsValues;


public class DataReceiver extends PebbleDataReceiver{
	
	private final static UUID APP_UUID = UUID
			.fromString("1151b807-682b-46c2-a945-1707516fce6f");
	
	public DataReceiver() {
		super(APP_UUID);
	}

	//действия при получении сингала с pebble
	@Override
	public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
		
		PebbleManager pm = new PebbleManager(context);
		//если в данный момент в настройках активирована работа с pebble,
		//происходит ее обработка
		if(SettingsValues.isWatchesEnabled())
		try {
			pm.sendMessage(dict.getString(33));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//ответное сообщение на pebble, сигнализирующее, что сигнал на телефоне получен
		PebbleKit.sendAckToPebble(context, transactionId);
		//обновление списка с каналами после получения сигналов с pebble
		try{
			Intent updateIntent = new Intent("update");
			context.sendBroadcast(updateIntent);
		}catch (Exception e) {

		}
	}

}
