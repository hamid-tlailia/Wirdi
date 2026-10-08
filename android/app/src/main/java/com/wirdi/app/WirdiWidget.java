package com.wirdi.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/** ويدجت للعرض فقط: إحصائيات أوراد اليوم (ما اكتمل وما ينقص) مع الصلاة القادمة. العدّ داخل التطبيق. */
public class WirdiWidget extends AppWidgetProvider {
    static final String ACTION_REFRESH = "com.wirdi.app.REFRESH";
    static final String EXTRA_WIRD = "wird";

    private static final int[] CHIPS = {R.id.c0, R.id.c1, R.id.c2, R.id.c3, R.id.c4, R.id.c5, R.id.c6, R.id.c7};

    private static final int C_TEXT = 0xFFEEF0FF, C_MUTED = 0xFFA9B0D6, C_DONE = 0xFF6EE7B7,
            C_PARTIAL = 0xFFFFC46B, C_MISSED = 0xFFFF8A80;

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        render(ctx, mgr, ids);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        String a = intent.getAction();
        if (ACTION_REFRESH.equals(a) || Intent.ACTION_TIME_CHANGED.equals(a)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(a) || Intent.ACTION_DATE_CHANGED.equals(a)) {
            updateAll(ctx);
        }
    }

    @Override
    public void onDisabled(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(refreshIntent(ctx));
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
        v.setOnClickPendingIntent(R.id.w_root, openApp(ctx));

        WirdiStore.TimeInfo ti = st.timeInfo(System.currentTimeMillis());
        long nextRefresh = renderPrayer(v, st, ti);
        renderStats(v, st, ti);

        mgr.updateAppWidget(ids, v);
        scheduleRefresh(ctx, st, nextRefresh);
    }

    // ———— الصلاة القادمة ————
    private static long renderPrayer(RemoteViews v, WirdiStore st, WirdiStore.TimeInfo ti) {
        JSONObject p = st.prayerSettings();
        long now = System.currentTimeMillis();
        if (p == null || !p.has("lat")) {
            v.setTextViewText(R.id.w_p_label, "أوقات الصلاة · من إعدادات التطبيق");
            v.setTextViewText(R.id.w_p_name, "حدّد موقعك");
            v.setTextViewText(R.id.w_p_time, "");
            v.setViewVisibility(R.id.w_p_box, View.GONE);
            return Long.MAX_VALUE;
        }
        PrayerTimes.Status nx = PrayerTimes.status(p, now);
        if (nx == null) {
            v.setViewVisibility(R.id.w_p_box, View.GONE);
            return Long.MAX_VALUE;
        }
        String city = p.optString("city", "");
        if (nx.iqama) {
            // بين الأذان والإقامة: عدّ تنازلي حتى الإقامة
            v.setTextViewText(R.id.w_p_label, "حان وقت " + nx.name + " · أُذّن " + hhmm(nx.adhan));
            v.setTextColor(R.id.w_p_label, C_PARTIAL);
        } else if (ti.period.equals("night")) {
            // الفراغ بين العشاء والفجر: ترغيب في قيام الليل
            v.setTextViewText(R.id.w_p_label, now >= ti.lastThird
                    ? "☾ أنت في الثلث الأخير — قم ولو بركعتين"
                    : "☾ قيام الليل · الثلث الأخير " + hhmm(ti.lastThird));
            v.setTextColor(R.id.w_p_label, C_PARTIAL);
        } else {
            v.setTextViewText(R.id.w_p_label, city.isEmpty() ? "الصلاة القادمة" : "الصلاة القادمة · " + city);
            v.setTextColor(R.id.w_p_label, C_MUTED);
        }
        v.setTextViewText(R.id.w_p_name, nx.iqama ? "الإقامة" : nx.name);
        v.setTextViewText(R.id.w_p_time, hhmm(nx.at));
        v.setViewVisibility(R.id.w_p_box, View.VISIBLE);
        v.setTextViewText(R.id.w_p_cap, nx.iqama ? "حتى الإقامة" : "حتى الأذان");
        v.setChronometer(R.id.w_p_left, SystemClock.elapsedRealtime() + (nx.at - now), "%s", true);
        v.setChronometerCountDown(R.id.w_p_left, true);
        long t = nx.at + 5000;
        if (ti.period.equals("night") && ti.lastThird > now) t = Math.min(t, ti.lastThird + 5000);
        return t;
    }

    static String hhmm(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        return String.format(Locale.US, "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    // ———— إحصائيات الأوراد ————
    private static void renderStats(RemoteViews v, WirdiStore st, WirdiStore.TimeInfo ti) {
        if (!st.hasMeta()) {
            v.setProgressBar(R.id.w_ring, 100, 0, false);
            v.setTextViewText(R.id.w_pct, "");
            for (int id : CHIPS) v.setViewVisibility(id, View.INVISIBLE);
            return;
        }
        int pct = (int) Math.round(st.dayPercent() * 100);
        v.setProgressBar(R.id.w_ring, 100, pct, false);
        v.setTextViewText(R.id.w_pct, pct + "%");

        String per = ti.period;
        for (int i = 0; i < CHIPS.length; i++) {
            if (i >= st.wirds.size()) {
                v.setViewVisibility(CHIPS[i], View.INVISIBLE);
                continue;
            }
            JSONObject w = st.wirds.get(i);
            String name = w.optString("short", w.optString("title"));
            String wp = w.optString("period");
            boolean done = st.isDone(w);
            boolean started = st.fraction(w) > 0;
            // فات وقته: الصباح بعد العصر، والمساء بعد العشاء
            boolean missed = !done && ((wp.equals("morning") && !per.equals("morning"))
                    || (wp.equals("evening") && per.equals("night")));
            boolean due = wp.equals("day") || wp.equals(per) || missed;

            int bg, color;
            String label;
            if (done) { bg = R.drawable.chip_done; color = C_DONE; label = name; }
            else if (missed) { bg = R.drawable.chip_missed; color = C_MISSED; label = name; }
            else if (started) { bg = R.drawable.chip_partial; color = C_PARTIAL; label = name; }
            else { bg = R.drawable.chip_todo; color = due ? C_TEXT : C_MUTED; label = name; }


            v.setViewVisibility(CHIPS[i], View.VISIBLE);
            v.setTextViewText(CHIPS[i], label);
            v.setTextColor(CHIPS[i], color);
            v.setInt(CHIPS[i], "setBackgroundResource", bg);
        }
    }

    // ———— التحديث التلقائي: عند الصلاة القادمة، وبداية المساء (١٥:٠٠)، واليوم الجديد (٣:٠٠) ————
    // ———— التحديث التلقائي عند حدود الفترات (الفجر، العصر، العشاء) وعند كل صلاة ————
    private static void scheduleRefresh(Context ctx, WirdiStore st, long prayerAt) {
        long now = System.currentTimeMillis();
        long t = prayerAt;
        for (int add = 0; add < 2; add++) {
            for (long b : st.dayTimes(WirdiStore.startOfDay(now, add))) {
                if (b > now) t = Math.min(t, b + 5000);
            }
        }
        if (t != Long.MAX_VALUE) PrayerAlarms.setExact(ctx, t, refreshIntent(ctx));
    }

    private static PendingIntent refreshIntent(Context ctx) {
        Intent i = new Intent(ctx, WirdiWidget.class).setAction(ACTION_REFRESH);
        return PendingIntent.getBroadcast(ctx, 10, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static PendingIntent openApp(Context ctx) {
        Intent i = new Intent(ctx, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
