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
    static final int DAY_START_HOUR = 3;
    static final int EVENING_HOUR = 15;
    static final int TOTAL_PAGES = 604;

    private static final String PREFS = "wirdi";
    private static final String K_STATE = "state";
    private static final String K_META = "meta";
    private static final String K_FOCUS = "widget_focus";

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

    String focus() {
        return prefs.getString(K_FOCUS, null);
    }

    void setFocus(String id) {
        prefs.edit().putString(K_FOCUS, id).apply();
    }

    // ———— الوقت ————
    static String dayKey() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, -DAY_START_HOUR);
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    static String currentPeriod() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return h >= DAY_START_HOUR && h < EVENING_HOUR ? "morning" : "evening";
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

    boolean isStarted(JSONObject w) {
        JSONObject p = peek(w.optString("id"));
        return p != null && !p.optBoolean("done") && (p.optInt("s") > 0 || p.optInt("c") > 0);
    }

    /** نفس منطق nextWird في app.js */
    JSONObject nextWird() {
        String per = currentPeriod();
        for (JSONObject w : wirds) {
            String wp = w.optString("period");
            if (isStarted(w) && (wp.equals(per) || wp.equals("day"))) return w;
        }
        for (JSONObject w : wirds) if (!isDone(w) && w.optString("period").equals(per)) return w;
        for (JSONObject w : wirds) if (!isDone(w) && w.optString("period").equals("day")) return w;
        return null;
    }

    /** الورد المعروض في الويدجت: الذي اختاره المستخدم بزر التبديل إن لم يكتمل، وإلا التالي */
    JSONObject widgetWird() {
        String f = focus();
        if (f != null) {
            JSONObject w = wird(f);
            if (w != null && !isDone(w)) return w;
        }
        return nextWird();
    }

    /** الأوراد غير المكتملة المناسبة للوقت (للتبديل بينها) */
    List<JSONObject> openWirds() {
        String per = currentPeriod();
        List<JSONObject> out = new ArrayList<>();
        for (JSONObject w : wirds) {
            String wp = w.optString("period");
            if (!isDone(w) && (wp.equals(per) || wp.equals("day"))) out.add(w);
        }
        if (out.isEmpty()) for (JSONObject w : wirds) if (!isDone(w)) out.add(w);
        return out;
    }

    /** عدّة واحدة. يُرجع: 0 عادي، 1 اكتمل ذكر، 2 اكتمل الورد */
    int count(JSONObject w) throws JSONException {
        JSONObject p = prog(w.optString("id"));
        if (p.optBoolean("done")) return 2;
        if (isQuran(w)) {
            p.put("done", true);
            int pages = w.optInt("pages", 2);
            state.put("quranPage", Math.min(TOTAL_PAGES, state.optInt("quranPage") + pages));
            save();
            return 2;
        }
        JSONArray steps = w.getJSONArray("steps");
        int s = Math.min(p.optInt("s"), steps.length() - 1);
        int target = steps.getJSONObject(s).optInt("count");
        int c = p.optInt("c") + 1;
        int result = 0;
        if (c >= target) {
            if (s < steps.length() - 1) {
                p.put("s", s + 1);
                p.put("c", 0);
                result = 1;
            } else {
                p.put("c", target);
                p.put("done", true);
                result = 2;
            }
        } else {
            p.put("c", c);
        }
        save();
        return result;
    }

    /** بداية يوم جديد: يحفظ نسبة الأمس ويصفّر التقدّم */
    boolean rollover() {
        String today = dayKey();
        String day = state.optString("day", "");
        if (today.equals(day)) return false;
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
        setFocus(null);
        return true;
    }

    int quranPage() {
        return state.optInt("quranPage");
    }

    boolean vibrateEnabled() {
        JSONObject s = state.optJSONObject("settings");
        return s == null || s.optBoolean("vibrate", true);
    }

    // ———— أرقام عربية ————
    static String ar(int n) {
        String s = String.valueOf(n);
        StringBuilder b = new StringBuilder();
        for (char ch : s.toCharArray()) b.append(ch >= '0' && ch <= '9' ? (char) ('٠' + (ch - '0')) : ch);
        return b.toString();
    }
}
