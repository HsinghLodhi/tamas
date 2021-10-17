package com.video.tamas.Utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by chetan on 12/19/2017.
 */

public class StringUtils {
    private static DeviceResourceManager resourceManager = null;
    public static String getString(EditText et) {
        if (et != null) {
            return et.getText().toString().trim();
        } else {
            return "";
        }
    }


    public static String getString(Context context, RadioGroup rg) {
        if (rg == null) {
            return "";
        }
        int selectedBtnId = rg.getCheckedRadioButtonId();
        RadioButton selectedBtn = ((AppCompatActivity) context).findViewById(selectedBtnId);
        if (selectedBtn == null) {
            return "";
        }
        return selectedBtn.getText().toString().trim();
    }
    public static String getString(Spinner spn) {
        if (spn != null) {
            return spn.getSelectedItem().toString().trim();
        } else {
            return "";
        }
    }
    public static String getString(CheckBox chkbx) {
        if (chkbx == null) {
            return "";
        }
        return chkbx.isChecked() ? "true" : "";
    }

    public static String getString(JSONObject jsonObject, String key) {
        String str = "";
        if (jsonObject != null && key != null) {
            try {
                str = jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
                str = "";
            }
        }
        return str;
    }

    public static boolean isValidForm(String... args) {
        boolean isValid = true;
        for (String str : args) {
            if (TextUtils.isEmpty(str)) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }
    public static boolean isValidEmail(String email) {
        Pattern emailPattern =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.find();
    }
    public static boolean isValidPassword(String password) {
        Pattern emailPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(password);
        return matcher.find();
    }


}
