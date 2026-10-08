package com.wirdi.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONObject;

/** إشعار وقت الإقامة: منبّه دقيق للإقامة القادمة، يُعاد ضبطه بعد كل إشعار وبعد إعادة تشغيل الهاتف. */
public class PrayerAlarms extends BroadcastReceiver {
    static final String ACTION_IQAMA = "com.wirdi.app.IQAMA";
    private static final String CHANNEL = "iqama";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_AT = "at";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (ACTION_IQAMA.equals(intent.getAction())) {
            long at = intent.getLongExtra(EXTRA_AT, 0);
            // لا نُشعر بإقامة قديمة (مثلًا إن كان الهاتف مطفأً)
            if (Math.abs(System.currentTimeMillis() - at) < 10 * 60000L) notify(ctx, intent.getStringExtra(EXTRA_NAME));
        }
        // ACTION_IQAMA / BOOT_COMPLETED / MY_PACKAGE_REPLACED: جدولة الإقامة التالية وتحديث الويدجت
        schedule(ctx);
        WirdiWidget.updateAll(ctx);
    }

    /** يضبط منبّهًا واحدًا لأقرب إقامة قادمة (أو يلغيه إن كان الإشعار معطّلًا). */
    static void schedule(Context ctx) {
        PendingIntent cancel = PendingIntent.getBroadcast(ctx, 20, new Intent(ctx, PrayerAlarms.class).setAction(ACTION_IQAMA),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        WirdiStore st = new WirdiStore(ctx);
        JSONObject p = st.prayerSettings();
        if (p == null || !p.has("lat") || !p.optBoolean("notify", true)) {
            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            if (am != null) am.cancel(cancel);
            return;
        }
        long now = System.currentTimeMillis();
        PrayerTimes.Status s = PrayerTimes.status(p, now);
        // إن كنا قبل الأذان فالإقامة القادمة هي إقامة هذه الصلاة نفسها
        if (s != null && !s.iqama) s = PrayerTimes.status(p, s.at + 1000);
        if (s == null) return;
        Intent i = new Intent(ctx, PrayerAlarms.class).setAction(ACTION_IQAMA)
                .putExtra(EXTRA_NAME, s.name).putExtra(EXTRA_AT, s.at);
        setExact(ctx, s.at, PendingIntent.getBroadcast(ctx, 20, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
    }

    /** منبّه دقيق حتى في وضع توفير الطاقة، مع بديل غير دقيق إن لم يُسمح بالمنبّهات الدقيقة. */
    static void setExact(Context ctx, long at, PendingIntent pi) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
        }
    }

    private static void notify(Context ctx, String name) {
        if (Build.VERSION.SDK_INT >= 33
                && ctx.checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) return;
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "وقت الإقامة", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("تنبيه عند حلول وقت إقامة الصلاة");
            nm.createNotificationChannel(ch);
            b = new Notification.Builder(ctx, CHANNEL);
        } else {
            b = new Notification.Builder(ctx).setPriority(Notification.PRIORITY_HIGH).setDefaults(Notification.DEFAULT_ALL);
        }
        PendingIntent open = PendingIntent.getActivity(ctx, 21, new Intent(ctx, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        b.setSmallIcon(R.drawable.ic_notify)
                .setColor(0xFFD4A94F)
                .setContentTitle("حان وقت إقامة صلاة " + (name == null ? "" : name))
                .setContentText("﴿إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا﴾")
                .setCategory(Notification.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(open);
        nm.notify(30, b.build());
    }
}
