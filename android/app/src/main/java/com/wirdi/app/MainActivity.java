package com.wirdi.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

/** يعرض تطبيق الويب المضمّن ويشارك حالته مع الويدجت. */
public class MainActivity extends Activity {
    private WebView web;
    private String pendingWird;
    private boolean loaded;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        web.setBackgroundColor(0xFF06140F);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setTextZoom(100);
        web.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        web.addJavascriptInterface(new Bridge(), "WirdiNative");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loaded = true;
                openPending();
            }
        });
        setContentView(web);
        pendingWird = getIntent().getStringExtra(WirdiWidget.EXTRA_WIRD);
        web.loadUrl("file:///android_asset/www/index.html");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String w = intent.getStringExtra(WirdiWidget.EXTRA_WIRD);
        if (w != null) {
            pendingWird = w;
            if (loaded) openPending();
        }
    }

    private void openPending() {
        if (pendingWird == null || !pendingWird.matches("[a-z_]+")) return;
        // ننتظر قليلًا حتى يعيد التطبيق قراءة الحالة عند العودة للواجهة
        final String id = pendingWird;
        pendingWird = null;
        web.postDelayed(() -> web.evaluateJavascript("openWird('" + id + "')", null), 150);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WirdiWidget.updateAll(this);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    private class Bridge {
        @JavascriptInterface
        public String getState() {
            return WirdiStore.rawState(MainActivity.this);
        }

        @JavascriptInterface
        public void saveState(String json) {
            WirdiStore.saveRawState(MainActivity.this, json);
        }

        @JavascriptInterface
        public void setMeta(String json) {
            WirdiStore.saveMeta(MainActivity.this, json);
            runOnUiThread(() -> WirdiWidget.updateAll(MainActivity.this));
        }

        @JavascriptInterface
        public void vibrate(String patternJson) {
            try {
                JSONArray a = new JSONArray(patternJson);
                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vib == null || !vib.hasVibrator()) return;
                if (a.length() == 1) {
                    long ms = a.getLong(0);
                    if (Build.VERSION.SDK_INT >= 26) vib.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vib.vibrate(ms);
                } else {
                    long[] p = new long[a.length() + 1];
                    for (int i = 0; i < a.length(); i++) p[i + 1] = a.getLong(i);
                    if (Build.VERSION.SDK_INT >= 26) vib.vibrate(VibrationEffect.createWaveform(p, -1));
                    else vib.vibrate(p, -1);
                }
            } catch (Exception ignored) {
            }
        }

        @JavascriptInterface
        public void keepScreenOn(boolean on) {
            runOnUiThread(() -> {
                if (on) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            });
        }
    }
}
