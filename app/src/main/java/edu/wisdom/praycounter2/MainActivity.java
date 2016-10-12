package edu.wisdom.praycounter2;

import java.io.IOException;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApTja3WgL/ydPiS+rwf1rrwxynhYqMOtN4t9MFGQe7ymLvOE6v/Nb7pHDuMcJftEEK4IK4fJGL8+KS4T21R0nWoPqtEhsuMogecR/4bLf/WAvE4KJDXKo8jwSlLY01IdgF7Lb5pnB7g6AmEM9J7Sn5NWYIxB95bbn2PQoJpLG7wPOjfWGL1kLkcc3EzmCfVZztZJYW4zAtZlykxVEtlgjeH1Gg8aVx8/BPAh0RNVpUdK1j7QjligJv0heJERVfc1kKdLLDuKBA6DU6qVThMpPnXtdxWILXmeGOHjO7VWzFpzNUx7beyWBWkMeSDiixILO1bxj2izYxLQF2hAdjqgodQIDAQAB";

	private final int PRAY_SETTING = 1;
	private final int PRAY_LIST = 2;

	private boolean debug = true;

	private int MIN_CLICK_INTERVAL = 500;    // 半秒鐘之內不能再按.
	private Date lastClickTime = new Date();

	private PrayCounterDb dbCounter;
	private CounterBean counter;

	private TextView txtCounter;
	private TextView txtCompleted;
	private Button btnPraySetting;
	private Button btnResetCounter;
	private Button btnSoundTest;
	ComponentName headphoneButtonReceiverComponent;
	AudioManager audioManager;

	private MusicIntentReceiver myReceiver;
	boolean roundFull;

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.idMainToolbar);
		myToolbar.setTitle("誦經計數機 " + BuildConfig.VERSION_NAME);
		myToolbar.inflateMenu(R.menu.menu_main);

//	    setSupportActionBar(myToolbar);

		counter = new CounterBean();

		btnPraySetting = (Button) findViewById(R.id.btnPraySetting);
		btnResetCounter = (Button) findViewById(R.id.btnResetCounter);
		Button btnAddOne = (Button) findViewById(R.id.btnAddOneTest);

		headphoneButtonReceiverComponent = new ComponentName(this, EarButtonReceiver.class);

		if (!debug) {
			btnAddOne.setVisibility(View.GONE);
		}

		dbCounter = new PrayCounterDb(this, counter);

		// Init counter control.
		txtCounter = (TextView) this.findViewById(R.id.txtCounter);
		txtCounter.setText(Integer.toString(counter.getCurrent()));

		myReceiver = new MusicIntentReceiver();

		IntentFilter ifHeadphoneButtonClick = new IntentFilter();
		ifHeadphoneButtonClick.addAction("KEYCODE_HEADSETHOOK");
//		ifHeadphoneButtonClick.addAction("EXTRA_VOLUME_STREAM_VALUE");	// 大細聲不加一 (這句指令保留).
		this.registerReceiver(headphoneButtonClickReceiver, ifHeadphoneButtonClick);

		Drawable backImg = getResources().getDrawable(R.drawable.lotus1);
		backImg.setAlpha(70);

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

		// Play sound on device.
		btnSoundTest = (Button) this.findViewById(R.id.btnSoundTest);
		player = new MediaPlayer();
		setOnPreparedListener();
		setOnCompletionListener();

		readSoundOnDevice();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(myReceiver, filter);

		dbCounter.checkoutCurrentCounter();
		roundFull = counter.roundSize == counter.current;

		updateScreen(roundFull);
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(myReceiver);
//		audioManager.unregisterMediaButtonEventReceiver(headphoneButtonReceiverComponent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Menu UI setting on /res/menu/menu_main//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		} else if (id == R.id.itemPrayList) {
//			Intent intent = new Intent(this, PrayListActivity.class);
//			startActivityForResult(intent, PRAY_LIST);
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == PRAY_SETTING) {
//			if (resultCode == RESULT_OK) {
//
//			}
//		}
//	}

	public void btnPraySetting_onClick(View view) {
		navToPraySettingActivity();
	}

	public void btnAddOneTest_onClick(View view) {
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
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
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

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://edu.wisdom.praycounter2/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://edu.wisdom.praycounter2/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	private class MusicIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
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
		roundFull = dbCounter.addOne();
		updateScreen(roundFull);

		if (roundFull) {
			// Play sound for round completed.
			player.start();

			// Get instance of Vibrator from current Context
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(2000);
		}
	}

	protected void resetCounter() {
		dbCounter.resetAllCounter();
		updateScreen(false);
	}

	DialogInterface.OnClickListener confirmResetDialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					resetCounter();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// Do nothing.
					break;
			}
		}
	};

	private void updateScreen(boolean roundFull) {
		int count = counter.getCurrent();
		String prayName = counter.getName().equals(PrayCounterDbHelper.BLANK_PRAY_NAME) ? "輸入經文名稱" : counter.getName();
		btnPraySetting.setText(prayName);

		if (count >= 1000) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) txtCounter.getLayoutParams();
			p.setMargins(0, 160, 0, 0);
			txtCounter.setLayoutParams(p);
			txtCounter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
		}
		else {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) txtCounter.getLayoutParams();
//			p.setMargins(0, 30, 0, 0);
			txtCounter.setLayoutParams(p);
			txtCounter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 130);
		}
		txtCounter.setText(Integer.toString(count));

		if (roundFull) {
			btnResetCounter.setText(getResources().getString(R.string.round_full));
			btnResetCounter.setTextColor(Color.parseColor("#DB0A5B"));
		} else {
			btnResetCounter.setText(getResources().getString(R.string.reset_counter));
			btnResetCounter.setTextColor(getResources().getColor(R.color.color_main_text));
		}
	}

	public void showActionMenuItem(MenuItem menu) {
		View menuItemView = findViewById(R.id.menuMain);
		PopupMenu popup = new PopupMenu(this, menuItemView);
		MenuInflater inflate = popup.getMenuInflater();
		inflate.inflate(R.menu.items_main, popup.getMenu());
		popup.show();
	}

	public void itemPrayList_onClick(MenuItem item) {
		Intent intent = new Intent(this, PrayListActivity.class);
//		startActivityForResult(intent, PRAY_LIST);
		startActivity(intent);
	}

	public void itemPraySetting_onClick(MenuItem item) {
		navToPraySettingActivity();
	}

	public void setOnPreparedListener() {
		player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.v("Debug", "Player is prepared.");
				btnSoundTest.setEnabled(true);
			}
		});
	}

	public void setOnCompletionListener() {
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.v("Debug", "Sound completed");
			}
		});
	}

	public void readSoundOnDevice() {
		Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.completed);

		player.reset();
		player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);

		try {
			player.setDataSource(this, uri);
			player.prepareAsync();
			Log.v("Debug", "prepareAsync");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnSoundTest_onClick(View view) {
		player.start();
		Log.v("Debug", "Play sound");
	}

	// 聲音很快播完, 所以不需要用這個.
	public void btnSoundStop_onClick(View view) {
		player.stop();
		Log.v("Debug", "Stop sound");
	}

	public void navToPraySettingActivity() {
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
}
