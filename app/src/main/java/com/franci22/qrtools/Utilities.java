package com.franci22.qrtools;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

public class Utilities {

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void copyTextToClipboard(String text, Context ctx) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Material QR Tools", text);
            clipboard.setPrimaryClip(clip);
        }
    }

}