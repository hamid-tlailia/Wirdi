package com.wirdi.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

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

    // ———— تحديد الموقع لأوقات الصلاة ————
    private static final int REQ_LOCATION = 7;
    private LocationListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private void startLocate() {
        if (Build.VERSION.SDK_INT >= 23
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_LOCATION);
            return;
        }
        fetchLocation();
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code != REQ_LOCATION) return;
        if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) fetchLocation();
        else locationError("لم يُسمح بالوصول إلى الموقع");
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm == null) { locationError("خدمة الموقع غير متاحة"); return; }
        Location best = null;
        for (String p : lm.getProviders(true)) {
            try {
                Location l = lm.getLastKnownLocation(p);
                if (l != null && (best == null || l.getTime() > best.getTime())) best = l;
            } catch (Exception ignored) {
            }
        }
        if (best != null && System.currentTimeMillis() - best.getTime() < 6 * 3600 * 1000L) {
            deliver(best);
            return;
        }
        String provider = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? LocationManager.NETWORK_PROVIDER
                : lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER : null;
        if (provider == null) {
            if (best != null) deliver(best);
            else locationError("فعّل خدمة الموقع (GPS) ثم أعد المحاولة");
            return;
        }
        final Location fallback = best;
        listener = new LocationListener() {
            @Override public void onLocationChanged(Location l) { stopLocate(lm); deliver(l); }
            @Override public void onStatusChanged(String p, int s, Bundle b) {}
            @Override public void onProviderEnabled(String p) {}
            @Override public void onProviderDisabled(String p) {}
        };
        lm.requestLocationUpdates(provider, 0, 0, listener, Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (listener == null) return;
            stopLocate(lm);
            if (fallback != null) deliver(fallback);
            else locationError("تعذّر تحديد الموقع، جرّب الإدخال اليدوي");
        }, 25000);
    }

    private void stopLocate(LocationManager lm) {
        if (listener != null) lm.removeUpdates(listener);
        listener = null;
    }

    private void deliver(Location l) {
        final double lat = l.getLatitude(), lng = l.getLongitude();
        new Thread(() -> {
            String city = "";
            try {
                List<Address> a = new Geocoder(this, new Locale("ar")).getFromLocation(lat, lng, 1);
                if (a != null && !a.isEmpty()) {
                    Address ad = a.get(0);
                    city = ad.getLocality() != null ? ad.getLocality()
                            : ad.getSubAdminArea() != null ? ad.getSubAdminArea()
                            : ad.getAdminArea() != null ? ad.getAdminArea() : "";
                }
            } catch (Exception ignored) {
            }
            final String js = "onNativeLocation(" + lat + "," + lng + "," + JSONObject.quote(city) + ")";
            runOnUiThread(() -> web.evaluateJavascript(js, null));
        }).start();
    }

    private void locationError(String msg) {
        web.evaluateJavascript("onNativeLocationError(" + JSONObject.quote(msg) + ")", null);
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
        public void requestLocation() {
            runOnUiThread(MainActivity.this::startLocate);
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
