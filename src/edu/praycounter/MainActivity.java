package edu.praycounter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	
	private boolean debug = false;
	
	private int MIN_CLICK_INTERVAL = 1;	// 1秒鐘之內不能再按.
	private Date lastClickTime = new Date();
	
	private DbCounter dbCounter;
	
	private TextView txtCounter;
	ComponentName headphoneButtonReceiverComponent;
	AudioManager audioManager;
	
	private MusicIntentReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnAddOne = (Button)findViewById(R.id.btnAddOne);
		
		headphoneButtonReceiverComponent = new ComponentName(this, EarButtonReceiver.class);
		
		if (!debug) {
			btnAddOne.setVisibility(View.GONE);
		}
		
		dbCounter = new DbCounter(this);
		
		// Init counter
		txtCounter = (TextView)this.findViewById(R.id.txtCounter);
		txtCounter.setText(Integer.toString(dbCounter.getCurrent()));
		
		myReceiver = new MusicIntentReceiver();
		
		IntentFilter ifHeadphoneButtonClick = new IntentFilter();
		ifHeadphoneButtonClick.addAction("KEYCODE_HEADSETHOOK");
//		ifHeadphoneButtonClick.addAction("EXTRA_VOLUME_STREAM_VALUE");	// 大細聲不加一, 這句指令保留.
		this.registerReceiver(headphoneButtonClickReceiver, ifHeadphoneButtonClick);
		
		Drawable backImg = getResources().getDrawable(R.drawable.lotus1);
		backImg.setAlpha(70);
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

	    IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
	    registerReceiver(myReceiver, filter);
	    
		dbCounter.getCounter();
		txtCounter.setText(Integer.toString(dbCounter.getCurrent()));
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
		audioManager.registerMediaButtonEventReceiver(headphoneButtonReceiverComponent);					
	}

    protected BroadcastReceiver headphoneButtonClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Date current = new Date();
			long clickInterval = TimeUnit.MILLISECONDS.toSeconds(
					current.getTime() - lastClickTime.getTime());
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
                    Log.d("DEBUG", "Headset is unplugged");
                    audioManager.unregisterMediaButtonEventReceiver(headphoneButtonReceiverComponent);
                    
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
		int count = dbCounter.addOne();
		txtCounter.setText(Integer.toString(count));	    	
    }
    
    protected void resetCounter() {
    	dbCounter.resetCounter();
		txtCounter.setText(Integer.toString(0));	    	
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

}
