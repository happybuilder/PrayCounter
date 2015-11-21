package edu.praycounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class EarButtonReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("TestApp", "EAR BUTTON PRESS RECEIVED");
        abortBroadcast();
        KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (key != null) {
	        if(key.getAction() == KeyEvent.ACTION_UP) {
	            int keycode = key.getKeyCode();
	            if(keycode == KeyEvent.KEYCODE_MEDIA_NEXT) {
	                Log.d("TestApp", "NEXT PRESSED");
	            } else if(keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
	                Log.d("TestApp", "PREVIOUS PRESSED");
	            } else if(keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
//	            	((IEarButtonHandler)context).onEarButtonPress(keycode);
	                Log.d("TestApp", "EAR SET HOOT PRESSED");
	            }
	        }
        }
        else {
        	int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
        	Log.d("TestApp", "volume: " + Integer.toString(volume));
        }
    }
}
