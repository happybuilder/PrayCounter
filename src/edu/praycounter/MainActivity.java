package edu.praycounter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IEarButtonHandler {
	
	private int count = 0;
	
	private TextView txtCounter;
	public static BroadcastReceiver earButtonReceiver;
	public BroadcastReceiver headButtonReceiver;
	ComponentName earButtonReceiverComponent;
	ComponentName headButtonReceiverComponent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Init counter
		txtCounter = (TextView)this.findViewById(R.id.txtCounter);
		txtCounter.setText(Integer.toString(count));
		
		earButtonReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        Log.v("TestApp", "BUTTON PRESS RECEIVED");
		        abortBroadcast();
		        KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		        if (key != null) {
			        if(key.getAction() == KeyEvent.ACTION_UP) {
			            int keycode = key.getKeyCode();
			            if(keycode == KeyEvent.KEYCODE_MEDIA_NEXT) {
			                Log.d("TestApp", "next pressed");
			            } else if(keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
			                Log.d("TestApp", "previous pressed");
			            } else if(keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
//			            	((IEarButtonHandler)context).onEarButtonPress(keycode);
			                Log.d("TestApp", "stop pressed");
			            }
			        }
		        }
		        else {
		        	int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
		        	Log.d("TestApp", "volume: " + Integer.toString(volume));
		        }
		    }
		};
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// Way 1 (OK)
//		regByAudioManager();
		
		// Way 2 (Not work)
		//regByReceiverInstance();		
		
		// Way 3
		regInternalReceiver();
	}
	
	// Register Component (with Receiver Class)
	private void regByAudioManager()
	{
		earButtonReceiverComponent = new ComponentName(this, EarButtonReceiver.class);
		AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		am.registerMediaButtonEventReceiver(earButtonReceiverComponent);		
	}
	
	private void unregByAudioManager()
	{
		AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		am.unregisterMediaButtonEventReceiver(earButtonReceiverComponent);	
	}
	
	private void regByReceiverInstance()
	{
		IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_BUTTON");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		this.registerReceiver(earButtonReceiver, intentFilter);
	}
	
	private void regInternalReceiver()
	{
//		headButtonReceiverComponent = new ComponentName(this, HeadButtonReceiver.class);
//		AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
//		am.registerMediaButtonEventReceiver(headButtonReceiverComponent);				
		
		headButtonReceiver = new HeadButtonReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction("android.intent.action.MEDIA_BUTTON");
		this.registerReceiver(headButtonReceiver, filter);
	}
	
	private void unregInternalReceiver()
	{
//		AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
//		am.unregisterMediaButtonEventReceiver(headButtonReceiverComponent);
		
		this.unregisterReceiver(headButtonReceiver);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Way 1 (OK)
//		unregByAudioManager();
		
		// Way 2
		//this.unregisterReceiver(earButtonReceiver);

		// Way 3
		unregInternalReceiver();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void btnAddOne_onClick(View view) {
		txtCounter.setText(Integer.toString(++count));	
	}

	@Override
	public void onEarButtonPress(int keycode) {
		txtCounter.setText(Integer.toString(++count));		
	}

	public static class HeadButtonReceiver extends BroadcastReceiver {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Log.v("TestApp", "HEAD BUTTON PRESS RECEIVED");
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
//		            	((IEarButtonHandler)context).onEarButtonPress(keycode);
		                Log.d("TestApp", "HEAD SET HOOT PRESSED");
		            }
		        }
	        }
	        else {
	        	int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
	        	Log.d("TestApp", "volume: " + Integer.toString(volume));
	        }
	    }
	}

}
