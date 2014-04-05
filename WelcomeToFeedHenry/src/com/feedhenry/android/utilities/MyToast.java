package com.feedhenry.android.utilities;

import android.widget.Toast;

import com.feedhenry.android.MyApplication;

public class MyToast {
    public static void showToast(String text){
        Toast toast=Toast.makeText(MyApplication.getAppContext(),text,Toast.LENGTH_SHORT);
        toast.show();
    }
}
