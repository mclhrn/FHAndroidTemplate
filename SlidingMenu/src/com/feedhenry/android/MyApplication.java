package com.feedhenry.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.feedhenry.android.server.FHAgent;
import com.feedhenry.android.utilities.MyToast;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
//import com.feedhenry.android.notification.GCMManager;

public class MyApplication extends Application {


	private static Context context;
    private static boolean isInitialised = false;

    
    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    
    public static boolean initApp(final Activity activity){
		Log.i("FEEDHENRY", "In initApp");
       if (FHAgent.isOnline()){
            if (activity==null){
                initFH(activity);
            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFH(activity);
                    }
                });
            }
            isInitialised = true;
            return true;
        }
        return false;
    }

    
    protected static void initFH(final Activity activity){
        FHAgent.init(new FHActCallback() {
            @Override
            public void success(FHResponse fhResponse) {
            	activity.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                    	 MyToast.showToast("Connected to FH server");
                     }
                 });
            }
            @Override
            public void fail(FHResponse fhResponse) {
            	activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	MyToast.showToast("Server connection failed");
                    }
                });
            }
        });
    }

    
    public static boolean initApp(){
        return initApp();
    }
    
    
    public static boolean isInitialised() {
        return isInitialised;
    }
    
    
    public static Context getAppContext() {
        return MyApplication.context;
    }
}