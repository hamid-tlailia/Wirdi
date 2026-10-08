package com.wirdi.app;

import java.util.Calendar;
import java.util.TimeZone;

/** حساب أوقات الصلاة محليًا — نفس منطق prayer.js. */
final class PrayerTimes {
    static final String[] KEYS = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
    static final String[] NAMES = {"الفجر", "الظهر", "العصر", "المغرب", "العشاء"};

    /** {fajr, isha, ishaMinutes} — ishaMinutes > 0 يعني العشاء بعد المغرب بدقائق ثابتة */
    static double[] method(String key) {
        switch (key == null ? "" : key) {
            case "tunisia": return new double[]{18, 18, 0};
            case "algeria": return new double[]{18, 17, 0};
            case "morocco": return new double[]{19, 17, 0};
            case "egypt": return new double[]{19.5, 17.5, 0};
            case "makkah": return new double[]{18.5, 0, 90};
            case "gulf": return new double[]{19.5, 0, 90};
            case "karachi": return new double[]{18, 18, 0};
            case "isna": return new double[]{15, 15, 0};
            case "france": return new double[]{12, 12, 0};
            default: return new double[]{18, 17, 0};
        }
    }

    static final class Next {
        final String name;
        final long at;
        Next(String name, long at) { this.name = name; this.at = at; }
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

    /** أوقات الصلوات الخمس بالساعات العشرية (توقيت الجهاز) */
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
        return new double[]{fajr + adj, dhuhr + adj + 1 / 60.0, asr + adj, maghrib + adj, isha + adj};
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

    static Next next(double lat, double lng, String methodKey, long now) {
        for (int add = 0; add < 2; add++) {
            Calendar day = Calendar.getInstance();
            day.setTimeInMillis(now);
            day.set(Calendar.HOUR_OF_DAY, 0);
            day.set(Calendar.MINUTE, 0);
            day.set(Calendar.SECOND, 0);
            day.set(Calendar.MILLISECOND, 0);
            day.add(Calendar.DAY_OF_MONTH, add);
            double[] t = forDay(day, lat, lng, methodKey);
            for (int i = 0; i < t.length; i++) {
                Calendar at = (Calendar) day.clone();
                at.add(Calendar.MINUTE, (int) Math.round(t[i] * 60));
                if (at.getTimeInMillis() > now) {
                    String name = i == 1 && at.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? "الجمعة" : NAMES[i];
                    return new Next(name, at.getTimeInMillis());
                }
            }
        }
        return null;
    }
}
