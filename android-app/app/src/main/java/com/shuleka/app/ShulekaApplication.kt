package com.shuleka.app

import android.app.Application
import android.util.Log
import com.onesignal.OneSignal

class ShulekaApplication : Application() {

    companion object {
        private const val TAG = "ShulekaApp"
        private const val ONESIGNAL_APP_ID = "4e418ddf-7c60-4054-a3ad-c7c473c5b8ac"
    }

    override fun onCreate() {
        super.onCreate()
        
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        
        OneSignal.promptForPushNotifications()
        
        Log.d(TAG, "OneSignal initialized with App ID: $ONESIGNAL_APP_ID")
    }
}
