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

public class MainActivity extends AppCompatActivity {
	
	private int count = 0;
	
	private TextView txtCounter;
	public static BroadcastReceiver earButtonReceiver;
	public BroadcastReceiver headButtonReceiver;
	ComponentName earButtonReceiverComponent;
	ComponentName headButtonReceiverComponent;
	AudioManager audioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Init counter
		txtCounter = (TextView)this.findViewById(R.id.txtCounter);
		txtCounter.setText(Integer.toString(count));
		
		IntentFilter ifEarStopClick = new IntentFilter();
		ifEarStopClick.addAction("earStopClick");
		this.registerReceiver(earStopClickReceiver, ifEarStopClick);
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

		headButtonReceiverComponent = new ComponentName(this, EarButtonReceiver.class);
		AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		audioManager.registerMediaButtonEventReceiver(headButtonReceiverComponent);			
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		audioManager.unregisterMediaButtonEventReceiver(earButtonReceiverComponent);	
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

    private BroadcastReceiver earStopClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			txtCounter.setText(Integer.toString(++count));
		}
    };
    
//	@Override
//	public void onEarButtonPress(int keycode) {
//		txtCounter.setText(Integer.toString(++count));		
//	}

}
