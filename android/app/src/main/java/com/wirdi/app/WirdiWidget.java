package com.wirdi.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/** ويدجت الشاشة الرئيسية: يعرض الورد الحالي وأين وصلت، ويعدّ بلمسة واحدة. */
public class WirdiWidget extends AppWidgetProvider {
    static final String ACTION_COUNT = "com.wirdi.app.COUNT";
    static final String ACTION_SWITCH = "com.wirdi.app.SWITCH";
    static final String EXTRA_WIRD = "wird";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        render(ctx, mgr, ids);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        String action = intent.getAction();
        if (ACTION_COUNT.equals(action)) {
            WirdiStore st = new WirdiStore(ctx);
            st.rollover();
            String id = intent.getStringExtra(EXTRA_WIRD);
            JSONObject w = id != null ? st.wird(id) : null;
            if (w != null && !st.isDone(w)) {
                try {
                    int r = st.count(w);
                    if (st.vibrateEnabled()) buzz(ctx, r == 0 ? 15 : 60);
                    if (r == 2) st.setFocus(null);
                } catch (Exception ignored) {
                }
            }
            updateAll(ctx);
        } else if (ACTION_SWITCH.equals(action)) {
            WirdiStore st = new WirdiStore(ctx);
            st.rollover();
            List<JSONObject> open = st.openWirds();
            if (!open.isEmpty()) {
                JSONObject cur = st.widgetWird();
                int i = 0;
                for (int k = 0; k < open.size(); k++)
                    if (cur != null && open.get(k).optString("id").equals(cur.optString("id"))) i = k + 1;
                st.setFocus(open.get(i % open.size()).optString("id"));
            }
            updateAll(ctx);
        }
    }

    static void updateAll(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, WirdiWidget.class));
        if (ids.length > 0) render(ctx, mgr, ids);
    }

    private static void render(Context ctx, AppWidgetManager mgr, int[] ids) {
        WirdiStore st = new WirdiStore(ctx);
        st.rollover();
        RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget);

        v.setOnClickPendingIntent(R.id.w_root, openApp(ctx, null, 0));
        v.setOnClickPendingIntent(R.id.w_switch, broadcast(ctx, ACTION_SWITCH, null, 1));

        if (!st.hasMeta()) {
            v.setTextViewText(R.id.w_pct, "");
            v.setProgressBar(R.id.w_progress, 100, 0, false);
            v.setTextViewText(R.id.w_title, "افتح التطبيق للبدء");
            v.setTextViewText(R.id.w_step, "اضغط هنا مرة واحدة");
            v.setTextViewText(R.id.w_count, "");
            v.setTextViewText(R.id.w_btn, "☾");
            v.setOnClickPendingIntent(R.id.w_btn, openApp(ctx, null, 0));
            mgr.updateAppWidget(ids, v);
            return;
        }

        int pct = (int) Math.round(st.dayPercent() * 100);
        v.setTextViewText(R.id.w_pct, WirdiStore.ar(pct) + "٪ من ورد اليوم");
        v.setProgressBar(R.id.w_progress, 100, pct, false);

        JSONObject w = st.widgetWird();
        if (w == null) {
            v.setTextViewText(R.id.w_title, "أتممت أورادك اليوم");
            v.setTextViewText(R.id.w_step, "تقبّل الله منك");
            v.setTextViewText(R.id.w_count, "");
            v.setTextViewText(R.id.w_btn, "✓");
            v.setTextColor(R.id.w_btn, 0xFF3CCF97);
            v.setInt(R.id.w_btn, "setBackgroundResource", R.drawable.count_btn_done);
            v.setOnClickPendingIntent(R.id.w_btn, openApp(ctx, null, 0));
            v.setOnClickPendingIntent(R.id.w_info, openApp(ctx, null, 0));
            mgr.updateAppWidget(ids, v);
            return;
        }

        String id = w.optString("id");
        v.setTextColor(R.id.w_btn, 0xFF1D140A);
        v.setInt(R.id.w_btn, "setBackgroundResource", R.drawable.count_btn);
        v.setTextViewText(R.id.w_title, w.optString("title"));
        v.setOnClickPendingIntent(R.id.w_info, openApp(ctx, id, 2));
        v.setOnClickPendingIntent(R.id.w_btn, broadcast(ctx, ACTION_COUNT, id, 3));

        if (WirdiStore.isQuran(w)) {
            int last = st.quranPage();
            int a = last % WirdiStore.TOTAL_PAGES + 1;
            int b = a % WirdiStore.TOTAL_PAGES + 1;
            v.setTextViewText(R.id.w_step, "اضغط ✓ بعد القراءة");
            v.setTextViewText(R.id.w_count, "صفحة " + WirdiStore.ar(a) + " و" + WirdiStore.ar(b));
            v.setTextViewText(R.id.w_btn, "✓");
        } else {
            JSONObject p = st.peek(id);
            int s = p == null ? 0 : p.optInt("s");
            int c = p == null ? 0 : p.optInt("c");
            JSONArray steps = w.optJSONArray("steps");
            JSONObject step = steps.optJSONObject(Math.min(s, steps.length() - 1));
            String stepLabel = steps.length() > 1
                    ? step.optString("title") + " · " + WirdiStore.ar(s + 1) + "/" + WirdiStore.ar(steps.length())
                    : (c == 0 ? "اضغط الزر الذهبي للعدّ" : "وصلت هنا — تابع");
            v.setTextViewText(R.id.w_step, stepLabel);
            v.setTextViewText(R.id.w_count, WirdiStore.ar(c) + " / " + WirdiStore.ar(step.optInt("count")));
            v.setTextViewText(R.id.w_btn, "+١");
        }
        mgr.updateAppWidget(ids, v);
    }

    private static PendingIntent broadcast(Context ctx, String action, String wird, int req) {
        Intent i = new Intent(ctx, WirdiWidget.class).setAction(action);
        if (wird != null) i.putExtra(EXTRA_WIRD, wird);
        return PendingIntent.getBroadcast(ctx, req, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static PendingIntent openApp(Context ctx, String wird, int req) {
        Intent i = new Intent(ctx, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (wird != null) i.putExtra(EXTRA_WIRD, wird);
        return PendingIntent.getActivity(ctx, req, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static void buzz(Context ctx, long ms) {
        Vibrator vib = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (vib == null || !vib.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= 26) vib.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        else vib.vibrate(ms);
    }
}
