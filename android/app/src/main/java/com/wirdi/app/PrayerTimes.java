package com.wirdi.app;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

/** حساب أوقات الصلاة محليًا — نفس منطق prayer.js. */
final class PrayerTimes {
    static final String[] KEYS = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
    static final String[] NAMES = {"الفجر", "الظهر", "العصر", "المغرب", "العشاء"};
    /** دقائق الانتظار بين الأذان والإقامة (الافتراضي) */
    static final int[] IQAMA_DEFAULT = {25, 20, 25, 10, 20};

    /** {fajr, isha, ishaMinutes[, dhuhrMinutes]} — ishaMinutes > 0 يعني العشاء بعد المغرب بدقائق ثابتة؛
     *  dhuhrMinutes احتياط بعد الزوال (دقيقة افتراضيًا) */
    static double[] method(String key) {
        switch (key == null ? "" : key) {
            case "tunisia": return new double[]{18, 18, 0};
            case "algeria": return new double[]{18, 17, 0};
            case "morocco": return new double[]{19, 17, 0};
            case "egypt": return new double[]{19.5, 17.5, 0};
            case "qatar": return new double[]{18, 0, 90, 0};
            case "makkah": return new double[]{18.5, 0, 90};
            case "uae": return new double[]{18.2, 18.2, 0};
            case "kuwait": return new double[]{18, 17.5, 0};
            case "gulf": return new double[]{19.5, 0, 90};
            case "karachi": return new double[]{18, 18, 0};
            case "isna": return new double[]{15, 15, 0};
            case "france": return new double[]{12, 12, 0};
            default: return new double[]{18, 17, 0};
        }
    }

    /** الحالة الآن: iqama = بين الأذان والإقامة (at = وقت الإقامة)، وإلا at = الأذان القادم */
    static final class Status {
        boolean iqama;
        int index;
        String name;
        long adhan, at;
    }

    private static double rad(double d) { return Math.toRadians(d); }
    private static double deg(double r) { return Math.toDegrees(r); }
    private static double fix(double a, double b) { return ((a % b) + b) % b; }

    private static double julian(int y, int m, int d) {
        if (m <= 2) { y -= 1; m += 12; }
        int A = y / 100, B = 2 - A + A / 4;
        return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + B - 1524.5;
    }

    /** {decl, eqt} */
    private static double[] sun(double jd) {
        double D = jd - 2451545.0;
        double g = fix(357.529 + 0.98560028 * D, 360);
        double q = fix(280.459 + 0.98564736 * D, 360);
        double L = fix(q + 1.915 * Math.sin(rad(g)) + 0.02 * Math.sin(rad(2 * g)), 360);
        double e = 23.439 - 0.00000036 * D;
        double RA = fix(deg(Math.atan2(Math.cos(rad(e)) * Math.sin(rad(L)), Math.cos(rad(L)))) / 15, 24);
        return new double[]{deg(Math.asin(Math.sin(rad(e)) * Math.sin(rad(L)))), q / 15 - RA};
    }

    /** أوقات الصلوات الخمس بالساعات العشرية (توقيت الجهاز)، مع التعديل اليدوي بالدقائق من الإعدادات */
    static double[] forDay(Calendar day, JSONObject p) {
        double[] t = forDay(day, p.optDouble("lat"), p.optDouble("lng"), p.optString("method", "mwl"));
        JSONObject adj = p.optJSONObject("adj");
        if (adj != null) for (int i = 0; i < KEYS.length; i++) t[i] += adj.optDouble(KEYS[i], 0) / 60.0;
        return t;
    }

    static double[] forDay(Calendar day, double lat, double lng, String methodKey) {
        double[] m = method(methodKey);
        int y = day.get(Calendar.YEAR), mo = day.get(Calendar.MONTH) + 1, d = day.get(Calendar.DAY_OF_MONTH);
        Calendar noon = (Calendar) day.clone();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        double tz = TimeZone.getDefault().getOffset(noon.getTimeInMillis()) / 3600000.0;
        final double jd = julian(y, mo, d) - lng / (15 * 24);

        double fajr = angleTime(jd, lat, m[0], 5, true);
        double dhuhr = mid(jd, 12);
        double asr = asrTime(jd, lat, 13);
        double maghrib = angleTime(jd, lat, 0.833, 18, false);
        double isha = m[2] > 0 ? maghrib + m[2] / 60 : angleTime(jd, lat, m[1], 18, false);
        double adj = tz - lng / 15;
        return new double[]{fajr + adj, dhuhr + adj + (m.length > 3 ? m[3] : 1) / 60.0, asr + adj, maghrib + adj, isha + adj};
    }

    private static double mid(double jd, double t) {
        return fix(12 - sun(jd + t / 24)[1], 24);
    }

    private static double angleTime(double jd, double lat, double angle, double t, boolean ccw) {
        double decl = sun(jd + t / 24)[0];
        double x = (-Math.sin(rad(angle)) - Math.sin(rad(decl)) * Math.sin(rad(lat)))
                / (Math.cos(rad(decl)) * Math.cos(rad(lat)));
        double T = deg(Math.acos(Math.max(-1, Math.min(1, x)))) / 15;
        return mid(jd, t) + (ccw ? -T : T);
    }

    private static double asrTime(double jd, double lat, double t) {
        double decl = sun(jd + t / 24)[0];
        double angle = -deg(Math.atan(1 / (1 + Math.tan(rad(Math.abs(lat - decl))))));
        return angleTime(jd, lat, angle, t, false);
    }

    static Status status(JSONObject p, long now) {
        JSONObject iq = p.optJSONObject("iqama");
        for (int add = -1; add < 2; add++) {
            Calendar day = WirdiStore.startOfDay(now, add);
            double[] t = forDay(day, p);
            for (int i = 0; i < t.length; i++) {
                Calendar at = (Calendar) day.clone();
                at.add(Calendar.MINUTE, (int) Math.round(t[i] * 60));
                long adhan = at.getTimeInMillis();
                int wait = iq == null ? IQAMA_DEFAULT[i] : iq.optInt(KEYS[i], IQAMA_DEFAULT[i]);
                long iqama = adhan + wait * 60000L;
                if (now < adhan || now < iqama) {
                    Status s = new Status();
                    s.iqama = now >= adhan;
                    s.index = i;
                    s.name = i == 1 && at.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? "الجمعة" : NAMES[i];
                    s.adhan = adhan;
                    s.at = s.iqama ? iqama : adhan;
                    return s;
                }
            }
        }
        return null;
    }
}
