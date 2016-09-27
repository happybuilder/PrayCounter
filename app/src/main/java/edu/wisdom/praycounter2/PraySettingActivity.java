package edu.wisdom.praycounter2;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class PraySettingActivity extends AppCompatActivity {

	public enum Mode {
		ADD, CHANGE_CURRENT, NONE
	}

//	public Mode mode = Mode.CLEAR;

	private PrayCounterDb prayCounterDb;
	private CounterBean counter;
	
	private EditText edtPrayName;
	private Button btnAddOrSelectPray;
	private EditText edtRoundSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pray_setting);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        myToolbar.setTitle("誦經計數機 " + BuildConfig.VERSION_NAME);

		// Components.
		edtPrayName = (EditText) findViewById(R.id.edtPrayName);
		btnAddOrSelectPray = (Button) findViewById(R.id.btnSelectPray);
		edtRoundSize = (EditText) findViewById(R.id.edtRoundSize);
		
		// Get counter from bundle.
		counter = new CounterBean();
		counter.id = getIntent().getExtras().getLong("id");
		counter.current = getIntent().getExtras().getInt("current");
		counter.round = getIntent().getExtras().getInt("round");
		counter.roundSize = getIntent().getExtras().getInt("roundSize");
		counter.name = getIntent().getExtras().getString("name");
		counter.notes = getIntent().getExtras().getString("notes");
		counter.lastUpdate = PrayCounterDbHelper.stringToDate(getIntent().getExtras().getString("lastUpdate"));

		// Init screen value from bundle.
		edtPrayName.setText(getDisplayPrayName(counter.name));
		edtRoundSize.setText(getDisplayRoundSize(counter.roundSize));

		// DB Module.
		prayCounterDb = new PrayCounterDb(this, counter);
		
		// Event
		edtPrayName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {	// on lost focus.
				if (!hasFocus) {
					addOrUpdatePray();
				}
			}
		});
		
		edtRoundSize.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {	// on lost focus.
				if (!hasFocus) {
					addOrUpdatePray();
				}
			}
		});
		 
//		edtPrayName.addTextChangedListener(new TextWatcher() {
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			public void afterTextChanged(Editable s) {
//				if (edtPrayName.getText().toString().trim().equals("")) {
//					layoutRoundSetting.setVisibility(View.INVISIBLE);			
//				} 
//				else {
//					layoutRoundSetting.setVisibility(View.VISIBLE);
//				}
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//		});

	}	// end onCreate(...)

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pray_setting, menu);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		return true;
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
		else if (id == android.R.id.home) {
			navigateBack();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		super.onPause();		
	}
	
	public void btnBack_onClick(View view) {
		navigateBack();
	}
	
	private void navigateBack() {		
		addOrUpdatePray();
		
//		Intent intent = new Intent();
//		intent.putExtra("id", counter.id);
//		intent.putExtra("current", counter.current);
//		intent.putExtra("round", counter.round);
//		intent.putExtra("roundSize", counter.roundSize);
//		intent.putExtra("name", counter.name);
//		intent.putExtra("notes", counter.notes);
//		intent.putExtra("isCurrent", counter.isCurrent);
//		intent.putExtra("lastUpdate", PrayCounterDbHelper.dateToString(counter.lastUpdate));		
//		setResult(Activity.RESULT_OK, intent);
		
		setResult(Activity.RESULT_OK, null);
		
		finish();		
	}
	
	private String getDisplayPrayName(String prayName) {
		if (prayName.trim().equals(PrayCounterDbHelper.BLANK_PRAY_NAME)) {
			return "";
		}
		else {
			return prayName.trim();
		}
	}
	
	private String getDisplayRoundSize(int roundSize) {
		if (roundSize == 0) {
			return "";
		}
		else {
			return Integer.toString(roundSize);
		}
	}
	
	private void gatherSetting() {
		String prayName = edtPrayName.getText().toString().trim();
		if (prayName.equals("")) {
			prayName = PrayCounterDbHelper.BLANK_PRAY_NAME;
		}
		
		counter.isCurrent = true;
		counter.name = prayName;
		counter.roundSize = StringUtils.parseInt(edtRoundSize);
		counter.notes = "";		// TODO
	}
	
	private void addOrUpdatePray() {		
		gatherSetting();
		switch (anaMode(counter.name)) {
			case ADD:
				if (prayCounterDb.isPrayNameOnlyNoName()) {
					prayCounterDb.updatePray();	
				}
				else {
					Log.d("debug", "addOrUpdatePray(), addPray");
					addPray();
				}
				break;
			case CHANGE_CURRENT:
				counter.id = prayCounterDb.getCounterId(edtPrayName.getText().toString());
				Log.d("debug", "addOrUpdatePray(), CHANGE_CURRENT, id:" + counter.id);
				prayCounterDb.setCurrent(counter.id);
				gatherSetting();
				prayCounterDb.updatePray();
				break;
			default: // NONE
				break;
		}
	}

	// 根據經文名稱輸入欄位, 判斷欄位後的按鈕是「新增」或「選擇」
	public Mode anaMode(String prayName) {
		if (prayName == null || prayName.trim().equals("")) {
			return Mode.NONE;
		} else {
			if (prayCounterDb.isPrayNameExists(prayName)) {
				return Mode.CHANGE_CURRENT;
			} else {
				return Mode.ADD;
			}
		}
	}

//	// 因應經文名稱內容, 更新按扭文字.
//	private void updateBtnAddOrSelectPrayState() {
//		String btnAddText = "";
//
//		switch (mode) {
//		case ADD:
//			btnAddText = getResources().getString(R.string.btn_add);
//			break;
//		case SELECT:
//			btnAddText = getResources().getString(R.string.btn_select);
//			break;
//		case CLEAR:
//			btnAddText = getResources().getString(R.string.btn_clear);
//			break;
//		}
//
//		btnAddOrSelectPray.setText(btnAddText);
//	}

	public void btnSelectPray_onClick(View view) {
	}
	
	// 清空「經文名稱」輸入欄.
	private void clearPrayName() {
		String btnAddText = getResources().getString(R.string.btn_add);
		edtPrayName.setText("");
		btnAddOrSelectPray.setText(btnAddText);
	}
	
	// 新增經文紀錄.
	private void addPray() {
		// initial counter
		long id = prayCounterDb.insertPray();
		prayCounterDb.setCurrent(id);
	}
	
}
