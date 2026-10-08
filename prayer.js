// حساب أوقات الصلاة محليًا (خوارزمية PrayTimes.org المبسّطة) — دون إنترنت.
// نفس المنطق موجود في android/.../PrayerTimes.java للويدجت.

const PRAYER_METHODS = {
  mwl: { name: "رابطة العالم الإسلامي", fajr: 18, isha: 17 },
  tunisia: { name: "تونس", fajr: 18, isha: 18 },
  algeria: { name: "الجزائر", fajr: 18, isha: 17 },
  morocco: { name: "المغرب", fajr: 19, isha: 17 },
  egypt: { name: "الهيئة المصرية", fajr: 19.5, isha: 17.5 },
  qatar: { name: "قطر (الأوقاف)", fajr: 18, ishaMin: 90, dhuhrMin: 0 },
  makkah: { name: "أم القرى (السعودية)", fajr: 18.5, ishaMin: 90 },
  uae: { name: "الإمارات", fajr: 18.2, isha: 18.2 },
  kuwait: { name: "الكويت", fajr: 18, isha: 17.5 },
  gulf: { name: "الخليج", fajr: 19.5, ishaMin: 90 },
  karachi: { name: "كراتشي", fajr: 18, isha: 18 },
  isna: { name: "أمريكا الشمالية", fajr: 15, isha: 15 },
  france: { name: "فرنسا (12°)", fajr: 12, isha: 12 },
};

// الطريقة المعتمدة حسب المنطقة الزمنية للجهاز (تُختار تلقائيًا ما لم يغيّرها المستخدم)
const METHOD_BY_ZONE = {
  "Asia/Qatar": "qatar", "Asia/Riyadh": "makkah", "Asia/Aden": "makkah", "Asia/Dubai": "uae", "Asia/Kuwait": "kuwait",
  "Asia/Bahrain": "gulf", "Asia/Muscat": "gulf", "Africa/Tunis": "tunisia", "Africa/Algiers": "algeria",
  "Africa/Casablanca": "morocco", "Africa/El_Aaiun": "morocco", "Africa/Cairo": "egypt", "Africa/Khartoum": "egypt",
  "Africa/Tripoli": "egypt", "Asia/Karachi": "karachi", "Asia/Kolkata": "karachi", "Asia/Dhaka": "karachi",
  "Europe/Paris": "france",
};
function methodForZone(tz = Intl.DateTimeFormat().resolvedOptions().timeZone) {
  if (METHOD_BY_ZONE[tz]) return METHOD_BY_ZONE[tz];
  if (/^America\//.test(tz)) return "isna";
  return "mwl";
}

const PRAYER_KEYS = ["fajr", "dhuhr", "asr", "maghrib", "isha"];
// دقائق الانتظار بين الأذان والإقامة (قابلة للتعديل من الإعدادات)
const IQAMA_DEFAULT = { fajr: 25, dhuhr: 20, asr: 25, maghrib: 10, isha: 20 };
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
  // adj: تعديل يدوي بالدقائق لكل صلاة لمطابقة التقويم المحلي
  function forDate(date, lat, lng, methodKey, adj = {}) {
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
    t.dhuhr += (m.dhuhrMin ?? 1) / 60; // احتياط بعد الزوال (دقيقة افتراضيًا)
    for (const k of PRAYER_KEYS) t[k] += (Number(adj[k]) || 0) / 60;
    return t;
  }

  const toDate = (base, h) => new Date(base.getFullYear(), base.getMonth(), base.getDate(), 0, Math.round(h * 60));
  const nameOf = (k, at) => (k === "dhuhr" && at.getDay() === 5 ? "الجمعة" : PRAYER_NAMES[k]);

  /** الحالة الآن (p = إعدادات الصلاة):
   *  - بين الأذان والإقامة: { phase: "iqama", key, name, adhan, at: وقت الإقامة }
   *  - غير ذلك: { phase: "adhan", key, name, at: وقت الأذان القادم } */
  function status(p, now = new Date()) {
    const iq = { ...IQAMA_DEFAULT, ...(p.iqama || {}) };
    for (let add = -1; add < 2; add++) {
      const day = new Date(now.getFullYear(), now.getMonth(), now.getDate() + add);
      const t = forDate(day, p.lat, p.lng, p.method, p.adj);
      for (const k of PRAYER_KEYS) {
        const adhan = toDate(day, t[k]);
        const iqama = new Date(adhan.getTime() + (Number(iq[k]) || 0) * 60000);
        if (now < adhan) return { phase: "adhan", key: k, name: nameOf(k, adhan), at: adhan };
        if (now < iqama) return { phase: "iqama", key: k, name: nameOf(k, adhan), adhan, at: iqama };
      }
    }
    return null;
  }

  return { forDate, status, toDate };
})();
