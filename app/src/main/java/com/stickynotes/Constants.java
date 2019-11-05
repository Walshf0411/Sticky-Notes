package com.stickynotes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Constants {
    public static final String FIRESTORE_NOTE_TITLE_KEY = "title";
    public static final String FIRESTORE_NOTE_KEY = "note";
    public static final String FIRESTORE_NOTE_DATETIME_KEY = "datetime";
    public static final String TAG = "sticky_notes";

    public static Intent getClearHistoryIntent(Context source, Class destination) {
        Intent intent = new Intent(source, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent getSimpleIntent(Context source, Class destination) {
        return new Intent(source, destination);
    }

    public static void showToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void log(String message) {
        Log.i(TAG, message);
    }

    public Intent getNotesHead(Context ctx) {
        return new Intent(ctx, FloatingViewService.class);
    }
}
