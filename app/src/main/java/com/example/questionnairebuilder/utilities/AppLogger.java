package com.example.questionnairebuilder.utilities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class AppLogger {

    private static final String TAG = "AppLogger";

    private static FirebaseAnalytics analytics;
    private static FirebaseCrashlytics crashlytics;

    public static void init(Context context) {
        analytics = FirebaseAnalytics.getInstance(context);
        crashlytics = FirebaseCrashlytics.getInstance();
    }

    // Log general events to Firebase Analytics
    public static void logEvent(String eventName, Bundle params) {
        if (analytics != null) {
            analytics.logEvent(eventName, params);
        }
        Log.d(TAG, "Event: " + eventName + " | " + (params != null ? params.toString() : "No Params"));
    }

    // Log breadcrumbs to Crashlytics
    public static void logDebug(String message) {
        if (crashlytics != null) {
            crashlytics.log(message);
        }
        Log.d(TAG, "Debug: " + message);
    }

    // Log caught exceptions to Crashlytics
    public static void logError(String contextMessage, Exception e) {
        if (crashlytics != null) {
            crashlytics.log(contextMessage);
            crashlytics.recordException(new Exception(contextMessage, e));
        }
        Log.e(TAG, "Error: ", e);
    }
}
