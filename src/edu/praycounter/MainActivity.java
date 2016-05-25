package edu.praycounter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	
	private final int PRAY_SETTING = 1;
	private final int PRAY_LIST = 2;
	
	private boolean debug = true;
	
	private int MIN_CLICK_INTERVAL = 500;	// 半秒鐘之內不能再按.
	private Date lastClickTime = new Date();
	
	private PrayCounterDb dbCounter;
	private CounterBean counter;
	
	private TextView txtCounter;
	private Button btnPraySetting;
	ComponentName headphoneButtonReceiverComponent;
	AudioManager audioManager;
	
	private MusicIntentReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
	    setSupportActionBar(myToolbar);
		
		counter = new CounterBean();
		
		btnPraySetting = (Button)findViewById(R.id.btnPraySetting);
		Button btnAddOne = (Button)findViewById(R.id.btnAddOne);
		
		headphoneButtonReceiverComponent = new ComponentName(this, EarButtonReceiver.class);
		
		if (!debug) {
			btnAddOne.setVisibility(View.GONE);
		}
		
		dbCounter = new PrayCounterDb(this, counter);
		
		// Init counter
		txtCounter = (TextView)this.findViewById(R.id.txtCounter);
		txtCounter.setText(Integer.toString(counter.getCurrent()));
		
		myReceiver = new MusicIntentReceiver();
		
		IntentFilter ifHeadphoneButtonClick = new IntentFilter();
		ifHeadphoneButtonClick.addAction("KEYCODE_HEADSETHOOK");
//		ifHeadphoneButtonClick.addAction("EXTRA_VOLUME_STREAM_VALUE");	// 大細聲不加一 (這句指令保留).
		this.registerReceiver(headphoneButtonClickReceiver, ifHeadphoneButtonClick);
		
		Drawable backImg = getResources().getDrawable(R.drawable.lotus1);
		backImg.setAlpha(70);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	    IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
	    registerReceiver(myReceiver, filter);
	    
		dbCounter.checkoutCurrentCounter();
		updateScreen();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(myReceiver);
//		audioManager.unregisterMediaButtonEventReceiver(headphoneButtonReceiverComponent);	
	}

	// Menu UI setting on /res/menu/main.xml
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.menu_title_activity_pray_list) {
			Intent intent = new Intent(this, PrayListActivity.class);
			startActivityForResult(intent, PRAY_LIST);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == PRAY_SETTING) {
//			if (resultCode == RESULT_OK) {
//
//			}
//		}
	}

	public void btnPraySetting_onClick(View view) {
		Intent intent = new Intent(this, PraySettingActivity.class);
		intent.putExtra("id", counter.id);
		intent.putExtra("current", counter.current);
		intent.putExtra("round", counter.round);
		intent.putExtra("roundSize", counter.roundSize);
		intent.putExtra("name", counter.name);
		intent.putExtra("notes", counter.notes);
		intent.putExtra("isCurrent", counter.isCurrent);
		intent.putExtra("lastUpdate", PrayCounterDbHelper.dateToString(counter.lastUpdate));
		
		startActivityForResult(intent, PRAY_SETTING);
	}
	
	public void btnAddOne_onClick(View view) {
		addOne();
	}
	
	public void btnResetCounter_onClick(View view) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    TextView title = new TextView(this);
	    title.setText("   [ 重置數值 ]");
	    title.setTextSize(40);
	    builder.setCustomTitle(title);
	    builder.setMessage("確定要重置回零嗎?").setPositiveButton("確定", confirmResetDialogClickListener)
	        .setNegativeButton("取消", confirmResetDialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(40);
        // Positive
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(40);
        // Negative
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(40);
    }
	
	protected void registerEarButtonReceiver() {
		audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		Log.d("DEBUG", "Reg receiver.");
		audioManager.registerMediaButtonEventReceiver(headphoneButtonReceiverComponent);					
	}

    protected BroadcastReceiver headphoneButtonClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Date current = new Date();
			long clickInterval = current.getTime() - lastClickTime.getTime();
			if (clickInterval > MIN_CLICK_INTERVAL) {
				lastClickTime = new Date();
				addOne();
			}
		}
    };
    
    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                case 0:
                	if (audioManager != null) {
	                    Log.d("DEBUG", "Headset is unplugged, unReg Receiver.");
	                    audioManager.unregisterMediaButtonEventReceiver(headphoneButtonReceiverComponent);
                	}
                    
                    break;
                case 1:
                    Log.d("DEBUG", "Headset is plugged");
            		registerEarButtonReceiver();
            		
                    break;
                default:
                    Log.d("DEBUG", "I have no idea what the headset state is");
                }
            }
        }
    }
    
    protected void addOne() {    	
		dbCounter.addOne();
		updateScreen();	    	
    }
    
    protected void resetCounter() {
    	dbCounter.resetAllCounter();
		updateScreen();	    	
    }
    
    DialogInterface.OnClickListener confirmResetDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
        		resetCounter();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                // Do nothing.
                break;
            }
        }
    };

    private void updateScreen() {
    	int count = counter.getCurrent();
    	String prayName = counter.getName().equals(PrayCounterDbHelper.BLANK_PRAY_NAME) ? "輸入經文名稱" : counter.getName();
    	btnPraySetting.setText(prayName);
    	txtCounter.setText(Integer.toString(count));
    }
}
