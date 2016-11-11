package edu.wisdom.praycounter2;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chan Chu Man on 11/11/2016.
 */

public class DateUtils {

    public final static String DATETIMEFORMAT = "yyMMddHHmmss";

    public static String dateToString(Date datetime) {
        if (datetime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIMEFORMAT);
            return sdf.format(datetime);
        }

        return "";
    }

    public static Date stringToDate(String datetime) {
        if (!datetime.trim().equals("")) {
            DateFormat df = new SimpleDateFormat(DATETIMEFORMAT);
            try {
                return df.parse(datetime);
            } catch (ParseException e) {
                Log.d("Debug", "stringToDate failed: " + datetime);
            }
        }

        return null;
    }

}
