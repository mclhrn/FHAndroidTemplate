package com.feedhenry.android.utilities;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardToggle {

    public static void hideTheKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void showTheKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
