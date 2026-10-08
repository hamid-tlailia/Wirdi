package com.wirdi.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * الحالة المشتركة بين تطبيق الويب (داخل WebView) والويدجت.
 * نفس صيغة JSON التي يستخدمها app.js:
 * { day, prog: {id: {s, c, done}}, quranPage, history: {day: pct}, settings }
 */
final class WirdiStore {
    /** قبل تحديد الموقع: الفجر، العصر، المغرب، العشاء (بالساعات) */
    static final double[] FALLBACK = {4, 15, 18.5, 20};
    static final int TOTAL_PAGES = 604;

    private static final String PREFS = "wirdi";
    private static final String K_STATE = "state";
    private static final String K_META = "meta";

    private final SharedPreferences prefs;
    JSONObject state;
    final List<JSONObject> wirds = new ArrayList<>();

    WirdiStore(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        try {
            String s = prefs.getString(K_STATE, null);
            state = s != null ? new JSONObject(s) : new JSONObject();
        } catch (JSONException e) {
            state = new JSONObject();
        }
        try {
            JSONArray m = new JSONArray(prefs.getString(K_META, "[]"));
            for (int i = 0; i < m.length(); i++) wirds.add(m.getJSONObject(i));
        } catch (JSONException ignored) {
        }
    }

    // ———— تخزين خام (للجسر مع JavaScript) ————
    static String rawState(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(K_STATE, "");
    }

    static void saveRawState(Context ctx, String json) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(K_STATE, json).apply();
    }

    static void saveMeta(Context ctx, String json) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(K_META, json).apply();
    }

    void save() {
        prefs.edit().putString(K_STATE, state.toString()).apply();
    }

    boolean hasMeta() {
        return !wirds.isEmpty();
    }

    // ———— الوقت: الصباح من الفجر إلى العصر، المساء من العصر إلى العشاء، الليل من العشاء إلى الفجر ————
    static final class TimeInfo {
        String period;   // morning | evening | night
        String key;      // يوم الأوراد (يبدأ مع الفجر)
        long lastThird;  // بداية الثلث الأخير من الليل (في فترة الليل فقط)
    }

    /** {fajr, asr, maghrib, isha} بالمللي ثانية ليوم معيّن */
    long[] dayTimes(Calendar day) {
        JSONObject p = prayerSettings();
        double[] h;
        if (p != null && p.has("lat")) {
            double[] t = PrayerTimes.forDay(day, p);
            h = new double[]{t[0], t[2], t[3], t[4]};
        } else {
            h = FALLBACK;
        }
        long[] out = new long[4];
        for (int i = 0; i < 4; i++) {
            Calendar c = (Calendar) day.clone();
            c.add(Calendar.MINUTE, (int) Math.round(h[i] * 60));
            out[i] = c.getTimeInMillis();
        }
        return out;
    }

    static Calendar startOfDay(long ms, int addDays) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, addDays);
        return c;
    }

    TimeInfo timeInfo(long now) {
        TimeInfo ti = new TimeInfo();
        Calendar today = startOfDay(now, 0);
        long[] t = dayTimes(today);
        Calendar wirdDay = today;
        ti.period = "night";
        if (now < t[0]) wirdDay = startOfDay(now, -1);
        else if (now < t[1]) ti.period = "morning";
        else if (now < t[3]) ti.period = "evening";
        if (ti.period.equals("night")) {
            Calendar start = now < t[0] ? startOfDay(now, -1) : today;
            long maghrib = dayTimes(start)[2];
            Calendar next = (Calendar) start.clone();
            next.add(Calendar.DAY_OF_MONTH, 1);
            long fajr = dayTimes(next)[0];
            ti.lastThird = fajr - (fajr - maghrib) / 3;
        }
        ti.key = String.format(Locale.US, "%04d-%02d-%02d",
                wirdDay.get(Calendar.YEAR), wirdDay.get(Calendar.MONTH) + 1, wirdDay.get(Calendar.DAY_OF_MONTH));
        return ti;
    }

    // ———— التقدّم ————
    JSONObject prog(String id) {
        try {
            JSONObject all = state.optJSONObject("prog");
            if (all == null) {
                all = new JSONObject();
                state.put("prog", all);
            }
            JSONObject p = all.optJSONObject(id);
            if (p == null) {
                p = new JSONObject().put("s", 0).put("c", 0).put("done", false);
                all.put(id, p);
            }
            return p;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    JSONObject peek(String id) {
        JSONObject all = state.optJSONObject("prog");
        return all == null ? null : all.optJSONObject(id);
    }

    JSONObject wird(String id) {
        for (JSONObject w : wirds) if (w.optString("id").equals(id)) return w;
        return null;
    }

    static boolean isQuran(JSONObject w) {
        return "quran".equals(w.optString("type"));
    }

    static int total(JSONObject w) {
        if (isQuran(w)) return 1;
        JSONArray st = w.optJSONArray("steps");
        int t = 0;
        for (int i = 0; st != null && i < st.length(); i++) t += st.optJSONObject(i).optInt("count");
        return Math.max(t, 1);
    }

    double fraction(JSONObject w) {
        JSONObject p = peek(w.optString("id"));
        if (p == null) return 0;
        if (p.optBoolean("done")) return 1;
        if (isQuran(w)) return 0;
        JSONArray st = w.optJSONArray("steps");
        int acc = 0, s = p.optInt("s");
        for (int i = 0; i < s && i < st.length(); i++) acc += st.optJSONObject(i).optInt("count");
        return (acc + p.optInt("c")) / (double) total(w);
    }

    double dayPercent() {
        if (wirds.isEmpty()) return 0;
        double a = 0;
        for (JSONObject w : wirds) a += fraction(w);
        return a / wirds.size();
    }

    boolean isDone(JSONObject w) {
        JSONObject p = peek(w.optString("id"));
        return p != null && p.optBoolean("done");
    }

    /** بداية يوم جديد: يحفظ نسبة الأمس ويصفّر التقدّم */
    boolean rollover() {
        String today = timeInfo(System.currentTimeMillis()).key;
        String day = state.optString("day", "");
        if (today.equals(day)) return false;
        if (!day.isEmpty() && today.compareTo(day) < 0) return false; // لا نرجع للخلف
        try {
            if (!day.isEmpty() && hasMeta()) {
                JSONObject h = state.optJSONObject("history");
                if (h == null) {
                    h = new JSONObject();
                    state.put("history", h);
                }
                h.put(day, (int) Math.round(dayPercent() * 100));
                TreeSet<String> keys = new TreeSet<>();
                for (Iterator<String> it = h.keys(); it.hasNext(); ) keys.add(it.next());
                while (keys.size() > 60) h.remove(keys.pollFirst());
            }
            state.put("day", today);
            state.put("prog", new JSONObject());
        } catch (JSONException ignored) {
        }
        save();
        return true;
    }

    int quranPage() {
        return state.optInt("quranPage");
    }

    JSONObject prayerSettings() {
        JSONObject s = state.optJSONObject("settings");
        return s == null ? null : s.optJSONObject("prayer");
    }

    boolean vibrateEnabled() {
        JSONObject s = state.optJSONObject("settings");
        return s == null || s.optBoolean("vibrate", true);
    }
}
