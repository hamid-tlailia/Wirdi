// حساب أوقات الصلاة محليًا (خوارزمية PrayTimes.org المبسّطة) — دون إنترنت.
// نفس المنطق موجود في android/.../PrayerTimes.java للويدجت.

const PRAYER_METHODS = {
  mwl: { name: "رابطة العالم الإسلامي", fajr: 18, isha: 17 },
  tunisia: { name: "تونس", fajr: 18, isha: 18 },
  algeria: { name: "الجزائر", fajr: 18, isha: 17 },
  morocco: { name: "المغرب", fajr: 19, isha: 17 },
  egypt: { name: "الهيئة المصرية", fajr: 19.5, isha: 17.5 },
  makkah: { name: "أم القرى (السعودية)", fajr: 18.5, ishaMin: 90 },
  gulf: { name: "الخليج", fajr: 19.5, ishaMin: 90 },
  karachi: { name: "كراتشي", fajr: 18, isha: 18 },
  isna: { name: "أمريكا الشمالية", fajr: 15, isha: 15 },
  france: { name: "فرنسا (12°)", fajr: 12, isha: 12 },
};

const PRAYER_NAMES = { fajr: "الفجر", dhuhr: "الظهر", asr: "العصر", maghrib: "المغرب", isha: "العشاء" };

const PrayerTimes = (() => {
  const rad = (d) => (d * Math.PI) / 180;
  const deg = (r) => (r * 180) / Math.PI;
  const fix = (a, b) => ((a % b) + b) % b;

  function julian(y, m, d) {
    if (m <= 2) { y -= 1; m += 12; }
    const A = Math.floor(y / 100), B = 2 - A + Math.floor(A / 4);
    return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + B - 1524.5;
  }
  function sun(jd) {
    const D = jd - 2451545.0;
    const g = fix(357.529 + 0.98560028 * D, 360);
    const q = fix(280.459 + 0.98564736 * D, 360);
    const L = fix(q + 1.915 * Math.sin(rad(g)) + 0.02 * Math.sin(rad(2 * g)), 360);
    const e = 23.439 - 0.00000036 * D;
    const RA = fix(deg(Math.atan2(Math.cos(rad(e)) * Math.sin(rad(L)), Math.cos(rad(L)))) / 15, 24);
    return { decl: deg(Math.asin(Math.sin(rad(e)) * Math.sin(rad(L)))), eqt: q / 15 - RA };
  }

  /** أوقات يوم واحد بالساعات العشرية حسب التوقيت المحلي للجهاز */
  function forDate(date, lat, lng, methodKey) {
    const m = PRAYER_METHODS[methodKey] || PRAYER_METHODS.mwl;
    const y = date.getFullYear(), mo = date.getMonth() + 1, d = date.getDate();
    const tz = -new Date(y, mo - 1, d, 12).getTimezoneOffset() / 60;
    const jd = julian(y, mo, d) - lng / (15 * 24);

    const mid = (t) => fix(12 - sun(jd + t / 24).eqt, 24);
    const angleTime = (angle, t, ccw) => {
      const decl = sun(jd + t / 24).decl;
      const x = (-Math.sin(rad(angle)) - Math.sin(rad(decl)) * Math.sin(rad(lat))) / (Math.cos(rad(decl)) * Math.cos(rad(lat)));
      const T = deg(Math.acos(Math.max(-1, Math.min(1, x)))) / 15;
      return mid(t) + (ccw ? -T : T);
    };
    const asrTime = (t) => {
      const decl = sun(jd + t / 24).decl;
      const angle = -deg(Math.atan(1 / (1 + Math.tan(rad(Math.abs(lat - decl))))));
      return angleTime(angle, t);
    };

    const t = {
      fajr: angleTime(m.fajr, 5, true),
      sunrise: angleTime(0.833, 6, true),
      dhuhr: mid(12),
      asr: asrTime(13),
      maghrib: angleTime(0.833, 18),
    };
    t.isha = m.ishaMin ? t.maghrib + m.ishaMin / 60 : angleTime(m.isha, 18);
    for (const k in t) t[k] += tz - lng / 15;
    t.dhuhr += 1 / 60; // احتياط دقيقة بعد الزوال
    return t;
  }

  const toDate = (base, h) => {
    const dt = new Date(base.getFullYear(), base.getMonth(), base.getDate());
    return new Date(dt.getTime() + Math.round(h * 60) * 60000);
  };

  /** الصلاة القادمة: { key, name, at: Date } */
  function next(lat, lng, methodKey, now = new Date()) {
    for (let add = 0; add < 2; add++) {
      const day = new Date(now.getFullYear(), now.getMonth(), now.getDate() + add);
      const t = forDate(day, lat, lng, methodKey);
      for (const k of ["fajr", "dhuhr", "asr", "maghrib", "isha"]) {
        const at = toDate(day, t[k]);
        if (at > now) {
          const name = k === "dhuhr" && at.getDay() === 5 ? "الجمعة" : PRAYER_NAMES[k];
          return { key: k, name, at };
        }
      }
    }
    return null;
  }

  return { forDate, next };
})();
