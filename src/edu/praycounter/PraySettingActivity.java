package edu.praycounter;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PraySettingActivity extends AppCompatActivity {

	public enum Mode {
		ADD, SELECT, CLEAR
	}

	public Mode mode = Mode.CLEAR;

	private DbCounter dbCounter;
	private EditText edtPrayName;
	private Button btnAddOrSelectPray;
	private LinearLayout layoutRoundSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pray_setting);

		dbCounter = new DbCounter(this);
		edtPrayName = (EditText) findViewById(R.id.edtPrayName);
		btnAddOrSelectPray = (Button) findViewById(R.id.btnAddOrSelectPray);

		edtPrayName.addTextChangedListener(new TextWatcher() {

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				// you can call or do what you want with your EditText here
				mode = anaMode(edtPrayName.getText().toString());
				updateBtnAddOrSelectPrayState();
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pray_setting, menu);
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
		return super.onOptionsItemSelected(item);
	}

	public void btnBack_onClick(View view) {
		finish();
	}

	// 根據經文名稱輸入欄位, 判斷欄位後的按鈕是「新增」或「選擇」
	public Mode anaMode(String prayName) {
		if (prayName == null || prayName.trim().equals("")) {
			if (dbCounter.isPrayNameDbEmpty()) {
				return Mode.ADD;
			} else {
				return Mode.SELECT;
			}
		} else {
			if (dbCounter.isPrayNameExists(prayName)) {
				return Mode.CLEAR;
			} else {
				return Mode.ADD;
			}
		}
	}

	// 因應經文名稱內容, 更新按扭文字.
	private void updateBtnAddOrSelectPrayState() {
		String btnAddText = "";

		switch (mode) {
		case ADD:
			btnAddText = getResources().getString(R.string.btn_add);
			break;
		case SELECT:
			btnAddText = getResources().getString(R.string.btn_select);
			break;
		case CLEAR:
			btnAddText = getResources().getString(R.string.btn_clear);
			break;
		}

		btnAddOrSelectPray.setText(btnAddText);
	}

	public void btnAddOrSelectPray_onClick(View view) {
		switch (mode) {
		case ADD:
			addPray(edtPrayName.getText().toString());
			break;
		case SELECT:
			break;
		case CLEAR:
			clearPrayName();	// 清空「經文名稱」輸入欄.
			break;
		}
	}
	
	// 清空「經文名稱」輸入欄.
	private void clearPrayName() {
		String btnAddText = getResources().getString(R.string.btn_add);
		edtPrayName.setText("");
		btnAddOrSelectPray.setText(btnAddText);
		layoutRoundSetting.setVisibility(View.INVISIBLE);					
	}
	
	// 新增經文紀錄.
	private void addPray(final String prayName) {
		String title = getResources().getString(R.string.title_add_new_pray);
		String msg = String.format("經文名稱為「%s」, 是嗎?", prayName);
		String confirm = getResources().getString(R.string.btn_confirm);
		String cancel = getResources().getString(R.string.btn_cancel);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(title)
	    	   .setMessage(msg)	     	    
	           .setCancelable(false)
	           .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                     addPrayConfirm(prayName);
	                }
	           })
	           .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                     dialog.cancel();
	                }
	           });
	     AlertDialog alert = builder.create();
	     alert.show();
	}
	
	private void addPrayConfirm(String prayName) {
		
	}
}
