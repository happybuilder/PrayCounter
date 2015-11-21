package edu.praycounter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	
	private int count = 0;
	private int MIN_CLICK_INTERVAL = 1;	// 3 秒鐘之內不能再按.
	private Date lastClickTime = new Date();
	
	private TextView txtCounter;
	ComponentName headphoneButtonReceiverComponent;
	AudioManager audioManager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Init counter
		txtCounter = (TextView)this.findViewById(R.id.txtCounter);
		txtCounter.setText(Integer.toString(count));
		
		IntentFilter ifHeadphoneButtonClick = new IntentFilter();
		ifHeadphoneButtonClick.addAction("KEYCODE_HEADSETHOOK");
		ifHeadphoneButtonClick.addAction("EXTRA_VOLUME_STREAM_VALUE");
		this.registerReceiver(headphoneButtonClickReceiver, ifHeadphoneButtonClick);
		
		Drawable backImg = getResources().getDrawable(R.drawable.lotus1);
		backImg.setAlpha(70);

		headphoneButtonReceiverComponent = new ComponentName(this, EarButtonReceiver.class);
		AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		audioManager.registerMediaButtonEventReceiver(headphoneButtonReceiverComponent);			
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

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
//		audioManager.unregisterMediaButtonEventReceiver(headphoneButtonReceiverComponent);	
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

    protected BroadcastReceiver headphoneButtonClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Date current = new Date();
			long clickInterval = TimeUnit.MILLISECONDS.toSeconds(
					current.getTime() - lastClickTime.getTime());
			if (clickInterval > MIN_CLICK_INTERVAL) {
				lastClickTime = new Date();
				txtCounter.setText(Integer.toString(++count));
			}
		}
    };
    
}
