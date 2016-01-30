package edu.praycounter;

import android.widget.EditText;

public class StringUtils {

	public static int parseInt(String s) {
		if (s.trim().equals("")) {
			return 0;
		}
		else {
			return Integer.parseInt(s);
		}
	}
	
	public static int parseInt(EditText txt) {
		String s = txt.getText().toString();
		
		if (s.trim().equals("")) {
			return 0;
		}
		else {
			return Integer.parseInt(s);
		}
	}

}
