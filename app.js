"use strict";

/* ========= أيقونات ========= */
const I = {
  sunrise: '<path d="M12 3v3M5.2 7.2l1.6 1.6M18.8 7.2l-1.6 1.6M3 15h2M19 15h2M7 15a5 5 0 0 1 10 0"/><path d="M3 19h18"/>',
  moon: '<path d="M20 14.5A8 8 0 0 1 9.5 4a8 8 0 1 0 10.5 10.5z"/><path d="M16 4.5l.5 1.3 1.3.5-1.3.5-.5 1.3-.5-1.3-1.3-.5 1.3-.5z"/>',
  heart: '<path d="M12 20s-7-4.4-9-8.6C1.6 8.3 3.4 5 6.6 5c2 0 3.4 1.2 4.4 2.6l1 1.4 1-1.4C14 6.2 15.4 5 17.4 5c3.2 0 5 3.3 3.6 6.4C19 15.6 12 20 12 20z"/>',
  star: '<path d="M12 2.5l2.4 4.2 4.3-1.3-1.3 4.3 4.1 2.3-4.1 2.3 1.3 4.3-4.3-1.3L12 21.5l-2.4-4.2-4.3 1.3 1.3-4.3L2.5 12l4.1-2.3-1.3-4.3 4.3 1.3z"/>',
  beads: '<circle cx="12" cy="5" r="2"/><circle cx="17.5" cy="8" r="2"/><circle cx="17.5" cy="14" r="2"/><circle cx="12" cy="17" r="2"/><circle cx="6.5" cy="14" r="2"/><circle cx="6.5" cy="8" r="2"/><path d="M12 19v3"/>',
  drop: '<path d="M12 3s6 6.4 6 11a6 6 0 0 1-12 0c0-4.6 6-11 6-11z"/><path d="M9.5 14.5a2.6 2.6 0 0 0 2.5 2.5"/>',
  book: '<path d="M12 6.5C10 5 7 4.5 3.5 5v13c3.5-.5 6.5 0 8.5 1.5 2-1.5 5-2 8.5-1.5V5C17 4.5 14 5 12 6.5z"/><path d="M12 6.5v13"/>',
  check: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  checkCircle: '<circle cx="12" cy="12" r="9.5"/><path d="M7.5 12.3l3 3 6-6"/>',
  arrow: '<path d="M15 5l-7 7 7 7"/>',
  chev: '<path d="M9 5l7 7-7 7"/>',
  undo: '<path d="M9 14L4 9l5-5"/><path d="M4 9h10.5a5.5 5.5 0 0 1 0 11H11"/>',
  reset: '<path d="M3 12a9 9 0 1 0 3-6.7L3 8"/><path d="M3 3v5h5"/>',
  skip: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  settings: '<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1.1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/>',
  dots: '<circle cx="12" cy="5" r="1.4"/><circle cx="12" cy="12" r="1.4"/><circle cx="12" cy="19" r="1.4"/>',
  flame: '<path d="M12 22c4 0 7-2.7 7-6.6 0-3.4-2.3-5.8-4.2-8.1-.5 1.8-1.4 3-2.8 3.6.4-3.1-.9-6.2-3.6-8.4C8.5 6.8 5 9.6 5 15.4 5 19.3 8 22 12 22z"/>',
  mosque: '<path d="M12 3c2.5 2 4.5 3.6 4.5 6.2V11h-9V9.2C7.5 6.6 9.5 5 12 3z"/><path d="M4 21V12h16v9"/><path d="M10 21v-3.5a2 2 0 0 1 4 0V21"/><path d="M3 21h18"/><path d="M12 1.5v1.5"/>',
  pin: '<path d="M12 21s-7-6.2-7-11.5a7 7 0 0 1 14 0C19 14.8 12 21 12 21z"/><circle cx="12" cy="9.5" r="2.5"/>',
  sparkle: '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/><path d="M19 16l.7 1.8 1.8.7-1.8.7L19 21l-.7-1.8-1.8-.7 1.8-.7z"/>',
};
const icon = (n, sw = 1.8) =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="${sw}" stroke-linecap="round" stroke-linejoin="round">${I[n]}</svg>`;

/* ========= أدوات ========= */
const $ = (s) => document.querySelector(s);
const $$ = (s) => document.querySelectorAll(s);
const AR = new Intl.NumberFormat("en-US", { useGrouping: false }); // أرقام عادية 123
const n = (x) => AR.format(x);
const TOTAL_PAGES = 604;

/* ———— الفترات حسب أوقات الصلاة الحقيقية ————
   الصباح: من الفجر إلى العصر · المساء: من العصر إلى العشاء · الليل: من العشاء إلى الفجر (قيام الليل)
   يبدأ يوم الأوراد الجديد مع الفجر. */
const FALLBACK_TIMES = { fajr: 4, asr: 15, maghrib: 18.5, isha: 20 }; // قبل تحديد الموقع
const ymd = (d) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
const addDays = (d, k) => new Date(d.getFullYear(), d.getMonth(), d.getDate() + k);
const atHour = (d, h) => new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, Math.round(h * 60));

function dayTimes(date) {
  const p = S.settings.prayer;
  const t = p && p.lat != null ? PrayerTimes.forDate(date, p.lat, p.lng, p.method, p.adj) : FALLBACK_TIMES;
  return { fajr: atHour(date, t.fajr), asr: atHour(date, t.asr), maghrib: atHour(date, t.maghrib), isha: atHour(date, t.isha) };
}
function timeInfo(now = new Date()) {
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const t = dayTimes(today);
  let period = "night", wirdDay = today, lastThird = null;
  if (now < t.fajr) wirdDay = addDays(today, -1);
  else if (now < t.asr) period = "morning";
  else if (now < t.isha) period = "evening";
  if (period === "night") {
    const start = now < t.fajr ? addDays(today, -1) : today;
    const mg = dayTimes(start).maghrib, fj = dayTimes(addDays(start, 1)).fajr;
    lastThird = new Date(fj - (fj - mg) / 3);
  }
  return { period, wirdDay, key: ymd(wirdDay), lastThird };
}
const dayKey = () => timeInfo().key;
const currentPeriod = () => timeInfo().period;
const tabFor = (per) => (per === "night" ? "day" : per);
const wirdById = (id) => WIRDS.find((w) => w.id === id);
const totalOf = (w) => (w.type === "quran" ? 1 : w.steps.reduce((a, s) => a + s.count, 0));

/* ========= الحالة ========= */
// داخل تطبيق أندرويد: الحالة تُحفظ عند التطبيق الأصلي لتشاركها مع الويدجت
const NATIVE = window.WirdiNative || null;
const KEY = "wirdi:v1";
const DEFAULTS = {
  day: "",
  prog: {},
  quranPage: 0,
  history: {},
  settings: { theme: "auto", vibrate: true, auto: true, wake: true, scale: 1 },
};
let S = load();

function load() {
  try {
    const nat = NATIVE && NATIVE.getState();
    const raw = JSON.parse(nat || localStorage.getItem(KEY));
    if (raw) return { ...DEFAULTS, ...raw, settings: { ...DEFAULTS.settings, ...raw.settings } };
  } catch (e) {}
  return structuredClone(DEFAULTS);
}
function save() {
  const json = JSON.stringify(S);
  try { localStorage.setItem(KEY, json); } catch (e) {}
  if (NATIVE) try { NATIVE.saveState(json); } catch (e) {}
}
// وصف مختصر للأوراد يحتاجه الويدجت
function pushMeta() {
  if (!NATIVE) return;
  const meta = WIRDS.map((w) => ({
    id: w.id, title: w.title, short: w.short || w.title, period: w.period, type: w.type || "dhikr", pages: w.pages || 0,
    steps: (w.steps || []).map((st) => ({ title: st.title, count: st.count })),
  }));
  try { NATIVE.setMeta(JSON.stringify(meta)); } catch (e) {}
}
function rollover() {
  const today = dayKey();
  if (S.day === today) return false;
  if (!S.day || today < S.day) { S.day = S.day || today; save(); return false; } // لا نرجع للخلف
  S.history[S.day] = Math.round(dayPercent() * 100);
  // الاحتفاظ بآخر 60 يومًا فقط
  const keys = Object.keys(S.history).sort();
  while (keys.length > 60) delete S.history[keys.shift()];
  S.day = today;
  S.prog = {};
  save();
  return true;
}
const P = (id) => (S.prog[id] ||= { s: 0, c: 0, done: false });

function fraction(w) {
  const p = S.prog[w.id];
  if (!p) return 0;
  if (p.done) return 1;
  if (w.type === "quran") return 0;
  let acc = 0;
  for (let i = 0; i < p.s; i++) acc += w.steps[i].count;
  return (acc + p.c) / totalOf(w);
}
function dayPercent() {
  return WIRDS.reduce((a, w) => a + fraction(w), 0) / WIRDS.length;
}
function streak() {
  let k = 0;
  const base = timeInfo().wirdDay;
  if (dayPercent() >= 1) k++;
  for (let i = 1; ; i++) {
    const key = ymd(addDays(base, -i));
    if ((S.history[key] ?? 0) >= 100) k++;
    else break;
  }
  return k;
}

/* ========= حلقة تقدّم ========= */
function ringSVG(size, stroke, frac) {
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  return `<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
    <circle class="track" cx="${size / 2}" cy="${size / 2}" r="${r}"/>
    <circle class="bar" cx="${size / 2}" cy="${size / 2}" r="${r}" stroke-dasharray="${c}" stroke-dashoffset="${c * (1 - frac)}" ${frac <= 0 ? 'opacity="0"' : ""}/>
  </svg>`;
}

/* ========= الشاشة الرئيسية ========= */
function isMissed(w) {
  if (S.prog[w.id]?.done) return false;
  const per = currentPeriod();
  return (w.period === "morning" && per !== "morning") || (w.period === "evening" && per === "night");
}
let tab = tabFor(currentPeriod());

function whereText(w) {
  const p = S.prog[w.id];
  if (w.type === "quran") {
    if (p?.done) return { t: "أتممت ورد اليوم", done: true };
    return { t: `التالي: صفحة ${n(nextPage(1))} و${n(nextPage(2))}` };
  }
  if (p?.done) return { t: "اكتمل — تقبّل الله", done: true };
  if (isMissed(w)) return { t: "فات وقته — يمكنك قضاؤه", missed: true };
  if (!p || (p.s === 0 && p.c === 0)) {
    return { t: w.steps.length > 1 ? `${n(w.steps.length)} أذكار · لم تبدأ بعد` : `${n(w.steps[0].count)} مرة · لم تبدأ بعد` };
  }
  const st = w.steps[p.s];
  if (w.steps.length > 1) return { t: `وصلت: ${st.title} · ${n(p.c)}/${n(st.count)}` };
  return { t: `وصلت إلى ${n(p.c)} من ${n(st.count)}` };
}

function renderHome() {
  const now = new Date();
  const hijri = new Intl.DateTimeFormat("ar-SA-u-ca-islamic-umalqura-nu-latn", { day: "numeric", month: "long", year: "numeric" }).format(now);
  const greg = new Intl.DateTimeFormat("ar-u-nu-latn", { weekday: "long", day: "numeric", month: "long" }).format(now);
  $("#date-line").textContent = `${greg} · ${hijri}`;

  const ti = timeInfo();
  const per = ti.period;
  $("#greeting").textContent = per === "morning" ? "صباح الذكر والنور" : per === "evening" ? "مساء الطمأنينة" : "ليلة مباركة";
  renderNight(ti);
  const pct = dayPercent();
  const doneCount = WIRDS.filter((w) => S.prog[w.id]?.done).length;
  $("#hero-sub").textContent =
    pct >= 1 ? "أتممت أورادك اليوم، ما شاء الله" : `أتممت ${n(doneCount)} من ${n(WIRDS.length)} أوراد`;

  const sk = streak();
  $("#streak").innerHTML = sk > 0 ? `${icon("flame")} ${n(sk)} ${sk === 1 ? "يوم" : sk === 2 ? "يومان" : sk <= 10 ? "أيام" : "يومًا"} متتالية` : `${icon("sparkle")} ابدأ سلسلتك اليوم`;

  // آخر 7 أيام
  const week = [];
  for (let i = 6; i >= 0; i--) {
    const d = addDays(timeInfo().wirdDay, -i);
    const key = ymd(d);
    const v = i === 0 ? Math.round(pct * 100) : S.history[key] ?? 0;
    const lbl = new Intl.DateTimeFormat("ar", { weekday: "narrow" }).format(d);
    week.push(`<div class="d ${i === 0 ? "today" : ""}"><span class="dot ${v >= 100 ? "full" : ""}" style="--p:${v}"></span>${lbl}</div>`);
  }
  $("#week").innerHTML = week.join("");

  $("#day-ring").innerHTML =
    ringSVG(112, 9, pct) +
    `<div class="ring-label"><div><div class="pct">${n(Math.round(pct * 100))}<small>%</small></div><span class="cap">ورد اليوم</span></div></div>`;

  // تابع من حيث توقفت
  const next = nextWird();
  const cont = $("#continue");
  if (next) {
    const p = S.prog[next.id];
    const started = p && (p.s > 0 || p.c > 0);
    cont.hidden = false;
    cont.onclick = () => openWird(next.id);
    cont.innerHTML = `
      <span class="c-ico">${icon(next.icon)}</span>
      <span class="c-txt">
        <div class="c-kicker">${started ? "تابع من حيث توقفت" : "وردك التالي"}</div>
        <div class="c-name">${next.title}</div>
        <div class="c-where">${whereText(next).t}</div>
      </span>
      <span class="c-go">${icon("arrow", 2.2)}</span>`;
  } else cont.hidden = true;

  renderPrayer();
  renderTabs();
  renderCards();
}

function nextWird() {
  const per = currentPeriod();
  const open = (w) => !S.prog[w.id]?.done;
  const started = (w) => S.prog[w.id] && !S.prog[w.id].done && (S.prog[w.id].s > 0 || S.prog[w.id].c > 0);
  // أولًا: ورد بدأته ولم تكمله، ثم أوراد الوقت الحالي، ثم أوراد اليوم
  return (
    WIRDS.find((w) => started(w) && (w.period === per || w.period === "day")) ||
    WIRDS.find((w) => open(w) && w.period === per) ||
    WIRDS.find((w) => open(w) && w.period === "day") ||
    null
  );
}

function renderTabs() {
  const order = ["morning", "evening", "day"];
  $$("#tabs button").forEach((b) => b.setAttribute("aria-selected", String(b.dataset.period === tab)));
  $(".tab-glider").style.transform = `translateX(${-100 * order.indexOf(tab)}%)`;
}

function renderCards() {
  const list = WIRDS.filter((w) => w.period === tab);
  $("#cards").innerHTML = list
    .map((w, i) => {
      const f = fraction(w);
      const wt = whereText(w);
      return `<button class="card ${wt.done ? "done" : ""} ${wt.missed ? "missed" : ""}" data-id="${w.id}" style="animation-delay:${i * 50}ms">
        <div class="ring mini ${wt.done ? "done" : ""}">${ringSVG(54, 7, f)}<div class="ring-label"><span class="ico">${icon(wt.done ? "check" : w.icon, wt.done ? 2.4 : 1.8)}</span></div></div>
        <div class="info">
          <h3>${w.title}</h3>
          <p class="sub">${w.subtitle}</p>
          <div class="where">${wt.done ? icon("checkCircle") : ""}${wt.t}</div>
        </div>
        <span class="chev">${icon("chev")}</span>
      </button>`;
    })
    .join("");
}

/* ========= قيام الليل ========= */
const QIYAM = [
  { t: "«ينزل ربنا تبارك وتعالى كل ليلة إلى السماء الدنيا حين يبقى ثلث الليل الآخر، يقول: من يدعوني فأستجيب له، من يسألني فأعطيه، من يستغفرني فأغفر له»", s: "متفق عليه" },
  { t: "«أفضل الصلاة بعد الفريضة صلاة الليل»", s: "رواه مسلم" },
  { t: "«عليكم بقيام الليل، فإنه دأب الصالحين قبلكم، وهو قربة إلى ربكم، ومكفرة للسيئات، ومنهاة للإثم»", s: "رواه الترمذي" },
  { t: "«من قام بعشر آيات لم يُكتب من الغافلين، ومن قام بمئة آية كُتب من القانتين»", s: "رواه أبو داود" },
  { t: "﴿تَتَجَافَىٰ جُنُوبُهُمْ عَنِ الْمَضَاجِعِ يَدْعُونَ رَبَّهُمْ خَوْفًا وَطَمَعًا﴾", s: "السجدة: 16" },
  { t: "﴿وَبِالْأَسْحَارِ هُمْ يَسْتَغْفِرُونَ﴾", s: "الذاريات: 18" },
  { t: "«أقرب ما يكون الرب من العبد في جوف الليل الآخر، فإن استطعت أن تكون ممن يذكر الله في تلك الساعة فكن»", s: "رواه الترمذي" },
];
function renderNight(ti) {
  const card = $("#night-card");
  card.hidden = ti.period !== "night";
  if (card.hidden) return;
  const q = QIYAM[ti.wirdDay.getDate() % QIYAM.length];
  const now = new Date();
  const when = now >= ti.lastThird ? "أنت الآن في الثلث الأخير من الليل" : `يبدأ الثلث الأخير من الليل ${hm(ti.lastThird)}`;
  card.innerHTML = `
    <div class="n-head"><span class="n-ico">${icon("moon")}</span>
      <div><div class="n-title">وقت قيام الليل</div><div class="n-when">${when}</div></div></div>
    <p class="n-quote">${q.t}</p>
    <div class="n-foot"><span>${q.s}</span><span>ولو بركعتين</span></div>`;
}

/* ========= أوقات الصلاة ========= */
const pr = () => S.settings.prayer || null;
const hm = (d) => n(d.getHours()).padStart(2, "0") + ":" + n(d.getMinutes()).padStart(2, "0");
function inText(ms) {
  const mins = Math.max(1, Math.round(ms / 60000));
  const h = Math.floor(mins / 60), m = mins % 60;
  if (!h) return `بعد ${n(m)} د`;
  return m ? `بعد ${n(h)} س ${n(m)} د` : `بعد ${n(h)} س`;
}
function renderPrayer() {
  const card = $("#prayer-card"), p = pr();
  const nx = p && p.lat != null && PrayerTimes.status(p);
  card.classList.toggle("empty", !nx);
  card.classList.toggle("iqama", nx?.phase === "iqama");
  card.innerHTML = nx
    ? nx.phase === "iqama"
      ? `<span class="p-ico">${icon("mosque")}</span>
       <span class="p-txt"><div class="p-kicker">حان وقت ${nx.name} · أُذّن ${hm(nx.adhan)}</div><div class="p-name">الإقامة<b>${hm(nx.at)}</b></div></span>
       <span class="p-left">${inText(nx.at - Date.now())}</span>`
      : `<span class="p-ico">${icon("mosque")}</span>
       <span class="p-txt"><div class="p-kicker">الصلاة القادمة${p.city ? " · " + p.city : ""}</div><div class="p-name">${nx.name}<b>${hm(nx.at)}</b></div></span>
       <span class="p-left">${inText(nx.at - Date.now())}</span>`
    : `<span class="p-ico">${icon("pin")}</span><span class="p-txt"><div class="p-kicker">أوقات الصلاة</div><div class="p-name">حدّد موقعك لعرض الصلاة القادمة</div></span>`;
}
function setLocation(lat, lng, city) {
  S.settings.prayer = { ...(pr() || { method: methodForZone() }), lat: +lat, lng: +lng, city: city || "" };
  prayerChanged();
  askNotifyPermission();
  toast("تم حفظ الموقع");
}
function renderPrayerSettings() {
  const p = pr();
  const sel = $("#method");
  if (!sel.options.length) {
    sel.innerHTML = Object.entries(PRAYER_METHODS).map(([k, m]) => `<option value="${k}">${m.name}</option>`).join("");
  }
  sel.value = p?.method || methodForZone();
  $("#method-hint").textContent = p?.methodManual ? "" : "اختيرت تلقائيًا حسب منطقتك — يمكنك تغييرها";
  const has = p && p.lat != null;
  $("#ptable-wrap").hidden = !has;
  $("#set-iqama-notify").checked = p?.notify !== false;
  if (has) {
    const today = new Date();
    const t = PrayerTimes.forDate(today, p.lat, p.lng, p.method, p.adj);
    const iq = { ...IQAMA_DEFAULT, ...(p.iqama || {}) };
    $("#loc-status").innerHTML = `${p.city ? `<b>${p.city}</b> · ` : ""}<bdi dir="ltr">${p.lat.toFixed(3)}, ${p.lng.toFixed(3)}</bdi>`;
    $("#ptable").innerHTML = `<div class="pt-h"><span>الصلاة</span><span>الأذان</span><span>تعديل (د)</span><span>الإقامة بعد (د)</span></div>` +
      PRAYER_KEYS.map((k) => {
        const adhan = PrayerTimes.toDate(today, t[k]);
        return `<div class="pt-r"><span>${PRAYER_NAMES[k]}</span><b>${hm(adhan)}</b>
          <input type="number" inputmode="numeric" data-k="${k}" data-f="adj" value="${Number(p.adj?.[k]) || 0}" min="-30" max="30">
          <input type="number" inputmode="numeric" data-k="${k}" data-f="iqama" value="${Number(iq[k]) || 0}" min="0" max="90"></div>`;
      }).join("");
    $("#lat").value = p.lat; $("#lng").value = p.lng;
  } else {
    $("#loc-status").textContent = "لم يُحدَّد الموقع بعد. تُحسب الأوقات على جهازك دون إنترنت.";
  }
}
// بعد أي تغيير في إعدادات الصلاة: إعادة جدولة إشعار الإقامة وتحديث الويدجت
function prayerChanged() {
  save();
  if (NATIVE && NATIVE.scheduleAlarms) try { NATIVE.scheduleAlarms(); } catch (e) {}
  renderPrayer();
  renderPrayerSettings();
}
function askNotifyPermission() {
  const p = pr();
  if (NATIVE && NATIVE.requestNotifications && p?.lat != null && p.notify !== false) {
    try { NATIVE.requestNotifications(); } catch (e) {}
  }
}
function locate() {
  $("#loc-btn").disabled = true;
  $("#loc-btn").textContent = "جارٍ تحديد الموقع…";
  const done = () => { $("#loc-btn").disabled = false; $("#loc-btn").textContent = "تحديد موقعي تلقائيًا"; };
  window.onNativeLocation = (lat, lng, city) => { done(); setLocation(lat, lng, city); };
  window.onNativeLocationError = (msg) => { done(); toast(msg || "تعذّر تحديد الموقع"); };
  if (NATIVE && NATIVE.requestLocation) { NATIVE.requestLocation(); return; }
  if (!navigator.geolocation) return window.onNativeLocationError();
  navigator.geolocation.getCurrentPosition(
    (pos) => window.onNativeLocation(pos.coords.latitude, pos.coords.longitude, ""),
    () => window.onNativeLocationError("لم يُسمح بالوصول للموقع"),
    { enableHighAccuracy: false, timeout: 15000, maximumAge: 86400000 }
  );
}

/* ========= العدّاد ========= */
let cur = null; // الورد المفتوح
let wakeLock = null;

function openWird(id) {
  const w = wirdById(id);
  if (!w) return;
  if (w.type === "quran") return openQuran();
  cur = w;
  const p = P(w.id);
  if (p.done) { p.s = 0; p.c = 0; p.done = false; save(); } // إعادة الورد إن كان مكتملًا
  $("#c-done").hidden = true;
  showSheet("#counter");
  renderCounter(true);
  requestWake();
}

function renderCounter(fresh) {
  const w = cur, p = P(w.id), st = w.steps[p.s];
  $("#c-title").textContent = w.title;
  $("#c-step-label").textContent = w.steps.length > 1 ? `الذكر ${n(p.s + 1)} من ${n(w.steps.length)}` : w.subtitle;

  $("#c-steps").hidden = w.steps.length < 2;
  $("#c-steps").innerHTML = w.steps
    .map((s, i) => {
      const f = i < p.s ? 1 : i === p.s ? p.c / s.count : 0;
      return `<button class="st ${i < p.s ? "done" : ""} ${i === p.s ? "cur" : ""}" data-i="${i}" aria-label="${s.title}" style="--f:${f}"><i></i></button>`;
    })
    .join("");

  if (fresh || $("#c-text").dataset.step !== String(p.s)) {
    $("#c-dhikr-title").textContent = w.steps.length > 1 ? st.title : "";
    const t = $("#c-text");
    t.textContent = st.text;
    t.dataset.step = p.s;
    t.classList.toggle("long", st.text.length > 160);
    t.scrollTop = 0;
    $("#c-benefit").textContent = st.benefit || "";
    $("#c-source").textContent = st.source ? `— ${st.source}` : "";
    $("#c-benefit-wrap").hidden = !st.benefit;
    $("#c-benefit-wrap").open = false;
  }
  updateTap();
}

function updateTap() {
  const p = P(cur.id), st = cur.steps[p.s];
  $("#tap-count").textContent = n(p.c);
  $("#tap-target").textContent = `من ${n(st.count)}`;
  const c = 2 * Math.PI * 90;
  const bar = $("#tap-bar");
  bar.setAttribute("stroke-dasharray", c);
  bar.setAttribute("stroke-dashoffset", c * (1 - p.c / st.count));
  bar.style.opacity = p.c ? 1 : 0;
  $("#tap").classList.toggle("complete", p.c >= st.count);
  const segs = $$("#c-steps .st");
  if (segs[p.s]) segs[p.s].style.setProperty("--f", p.c / st.count);
}

let advancing = null;
function count() {
  if (!cur || advancing) return;
  const p = P(cur.id), st = cur.steps[p.s];
  if (p.c >= st.count) return;
  p.c++;
  save();
  const tap = $("#tap");
  tap.classList.add("press");
  setTimeout(() => tap.classList.remove("press"), 90);
  const r = document.createElement("span");
  r.className = "ripple";
  tap.appendChild(r);
  setTimeout(() => r.remove(), 600);

  if (p.c >= st.count) {
    vibrate([30, 60, 30]);
    updateTap();
    if (S.settings.auto) advancing = setTimeout(nextStep, 650);
  } else {
    vibrate(12);
    updateTap();
  }
}

function nextStep() {
  advancing = null;
  const p = P(cur.id);
  if (p.s < cur.steps.length - 1) {
    p.s++;
    p.c = 0;
    save();
    renderCounter();
    toast(`التالي: ${cur.steps[p.s].title}`);
  } else {
    p.done = true;
    p.c = cur.steps[p.s].count;
    save();
    showDone();
  }
}

function showDone() {
  vibrate([40, 80, 40, 80, 80]);
  $(".done-burst").innerHTML = icon("checkCircle", 1.6);
  const nx = nextWird();
  $("#c-done-sub").textContent = `أتممت «${cur.title}»`;
  $("#c-next-wird").hidden = !nx;
  if (nx) {
    $("#c-next-wird").textContent = `التالي: ${nx.title}`;
    $("#c-next-wird").onclick = () => openWird(nx.id);
  }
  $("#c-done").hidden = false;
}

function vibrate(pat) {
  if (!S.settings.vibrate) return;
  if (NATIVE) { try { NATIVE.vibrate(JSON.stringify([].concat(pat))); } catch (e) {} return; }
  if (navigator.vibrate) navigator.vibrate(pat);
}

async function requestWake() {
  if (NATIVE) { if (S.settings.wake) try { NATIVE.keepScreenOn(true); } catch (e) {} return; }
  if (!S.settings.wake || !("wakeLock" in navigator)) return;
  try { wakeLock = await navigator.wakeLock.request("screen"); } catch (e) {}
}
function releaseWake() {
  if (NATIVE) try { NATIVE.keepScreenOn(false); } catch (e) {}
  wakeLock?.release?.();
  wakeLock = null;
}

/* ========= ورد القرآن ========= */
const quranW = () => wirdById("quran");
const wrap = (x) => ((x - 1) % TOTAL_PAGES + TOTAL_PAGES) % TOTAL_PAGES + 1;
function nextPage(k) {
  const p = S.prog.quran;
  const base = p?.done ? S.quranPage - quranW().pages : S.quranPage;
  return wrap(base + k);
}
const juzOf = (pg) => (pg < 22 ? 1 : Math.min(30, Math.floor((pg - 2) / 20) + 1));

function openQuran() {
  showSheet("#quran");
  renderQuran();
}
function renderQuran() {
  const w = quranW(), done = S.prog.quran?.done;
  const a = nextPage(1), b = nextPage(2);
  $("#q-p1").textContent = n(a);
  $("#q-p2").textContent = n(b);
  $("#q-label").textContent = done ? "أتممت ورد اليوم — بارك الله فيك" : `ورد اليوم: الصفحة ${n(a)} و${n(b)}`;
  $("#q-juz").textContent = `الجزء ${n(juzOf(a))}`;
  const read = S.quranPage % TOTAL_PAGES || (S.quranPage ? TOTAL_PAGES : 0);
  $("#q-bar").style.width = `${(read / TOTAL_PAGES) * 100}%`;
  const left = Math.ceil((TOTAL_PAGES - read) / w.pages);
  $("#q-khatma").textContent = read ? `قرأت ${n(read)} من ${n(TOTAL_PAGES)} صفحة · تبقّى ${n(left)} يومًا للختمة` : "ابدأ ختمتك: صفحتان كل يوم = ختمة كل 10 أشهر تقريبًا";
  const btn = $("#q-done");
  btn.textContent = done ? "تراجع عن الإتمام" : `قرأت الصفحتين ${n(a)}–${n(b)}`;
  btn.className = done ? "btn ghost wide" : "btn gold wide";
  $("#q-input").value = S.quranPage;
  $("#q-benefit").textContent = w.benefit;
  $("#q-source").textContent = `— ${w.source}`;
}
function toggleQuranDone() {
  const p = P("quran"), w = quranW();
  if (p.done) {
    p.done = false;
    S.quranPage = Math.max(0, S.quranPage - w.pages);
  } else {
    p.done = true;
    S.quranPage += w.pages;
    if (S.quranPage >= TOTAL_PAGES) { S.quranPage = TOTAL_PAGES; toast("ختمت القرآن! تقبّل الله منك"); }
    vibrate([40, 80, 40]);
  }
  save();
  renderQuran();
}
function setQuranPage(v) {
  const p = P("quran");
  v = Math.max(0, Math.min(TOTAL_PAGES, Math.round(Number(v) || 0)));
  // بعد الختم يبدأ من جديد
  S.quranPage = v >= TOTAL_PAGES && !p.done ? 0 : v;
  save();
  renderQuran();
}

/* ========= الصفحات المنبثقة ========= */
let openSheet = null;
function showSheet(sel) {
  if (openSheet && openSheet !== sel) $(openSheet).hidden = true;
  openSheet = sel;
  $(sel).hidden = false;
  document.body.style.overflow = "hidden";
  if (history.state?.sheet !== sel) history.pushState({ sheet: sel }, "");
}
function closeSheet(fromPop) {
  if (!openSheet) return;
  if (advancing) { clearTimeout(advancing); advancing = null; }
  $(openSheet).hidden = true;
  openSheet = null;
  cur = null;
  document.body.style.overflow = "";
  releaseWake();
  renderHome();
  if (!fromPop && history.state?.sheet) history.back();
}
window.addEventListener("popstate", () => closeSheet(true));

/* ========= الإعدادات ========= */
function applySettings() {
  document.documentElement.dataset.theme = S.settings.theme;
  document.documentElement.style.setProperty("--scale", S.settings.scale);
  const dark =
    S.settings.theme === "dark" || (S.settings.theme === "auto" && !matchMedia("(prefers-color-scheme: light)").matches);
  $('meta[name="theme-color"]').content = dark ? "#06140f" : "#f6f0e2";
}
function renderSettings() {
  $$("#set-theme button").forEach((b) => b.setAttribute("aria-pressed", String(b.dataset.v === S.settings.theme)));
  $$("#set-scale button").forEach((b) => b.setAttribute("aria-pressed", String(Number(b.dataset.v) === S.settings.scale)));
  $("#set-vibrate").checked = S.settings.vibrate;
  $("#set-auto").checked = S.settings.auto;
  $("#set-wake").checked = S.settings.wake;
  $("#install-group").hidden = !!NATIVE;
  renderPrayerSettings();
  renderInstallHelp();
}

/* ========= التثبيت ========= */
let deferredPrompt = null;
const isStandalone = () => !!NATIVE || matchMedia("(display-mode: standalone)").matches || navigator.standalone;
const isIOS = () => /iphone|ipad|ipod/i.test(navigator.userAgent) || (navigator.platform === "MacIntel" && navigator.maxTouchPoints > 1);

function renderInstallHelp() {
  const h = $("#install-help");
  if (isStandalone()) {
    h.innerHTML = "التطبيق مثبّت على جهازك ✓ — يمكنك سحب أيقونته إلى الشاشة الرئيسية الأولى ليكون أمام عينيك دائمًا.";
  } else if (isIOS()) {
    h.innerHTML = `<ol><li>افتح الرابط في <b>Safari</b></li><li>اضغط زر <b>المشاركة</b> (المربع والسهم)</li><li>اختر <b>«إضافة إلى الشاشة الرئيسية»</b> ثم <b>إضافة</b></li></ol>`;
  } else {
    h.innerHTML = `<ol><li>افتح الرابط في <b>Chrome</b></li><li>اضغط زر التثبيت أدناه، أو من القائمة ⋮ اختر <b>«تثبيت التطبيق»</b></li><li>اضغط مطولًا على الأيقونة لاختصارات سريعة (تسبيح، استغفار…)</li></ol>`;
  }
  $("#install-btn-2").hidden = !deferredPrompt;
}
function maybeShowInstallBanner() {
  let dismissed = false;
  try { dismissed = localStorage.getItem("wirdi:install-dismissed") === "1"; } catch (e) {}
  $("#install-banner").hidden = isStandalone() || dismissed || !(deferredPrompt || isIOS());
  $("#install-btn").textContent = deferredPrompt ? "تثبيت" : "كيف؟";
}
async function doInstall() {
  if (!deferredPrompt) { showSheet("#settings"); renderSettings(); return; }
  deferredPrompt.prompt();
  await deferredPrompt.userChoice.catch(() => {});
  deferredPrompt = null;
  maybeShowInstallBanner();
  renderInstallHelp();
}
window.addEventListener("beforeinstallprompt", (e) => {
  e.preventDefault();
  deferredPrompt = e;
  maybeShowInstallBanner();
  renderInstallHelp();
});
window.addEventListener("appinstalled", () => { deferredPrompt = null; maybeShowInstallBanner(); toast("تم التثبيت — بارك الله فيك"); });

/* ========= نافذة تأكيد (بديل confirm الذي لا يعمل في WebView) ========= */
function ask(msg, okLabel = "نعم") {
  return new Promise((resolve) => {
    const m = $("#modal");
    $("#modal-msg").textContent = msg;
    $("#modal-ok").textContent = okLabel;
    m.hidden = false;
    const close = (v) => { m.hidden = true; m.onclick = null; resolve(v); };
    $("#modal-ok").onclick = (e) => { e.stopPropagation(); close(true); };
    $("#modal-cancel").onclick = (e) => { e.stopPropagation(); close(false); };
    m.onclick = (e) => { if (e.target === m) close(false); };
  });
}

/* ========= Toast ========= */
let toastT;
function toast(msg) {
  const t = $("#toast");
  t.textContent = msg;
  t.classList.add("show");
  clearTimeout(toastT);
  toastT = setTimeout(() => t.classList.remove("show"), 1900);
}

/* ========= الأحداث ========= */
function bind() {
  // تدرّج ذهبي مشترك للحلقات
  document.body.insertAdjacentHTML(
    "afterbegin",
    `<svg width="0" height="0" style="position:absolute" aria-hidden="true"><defs><linearGradient id="goldGrad" x1="0" y1="0" x2="1" y2="1"><stop offset="0" style="stop-color:var(--gold-hi)"/><stop offset="1" style="stop-color:var(--gold)"/></linearGradient></defs></svg>`
  );
  $("#open-settings").innerHTML = icon("settings");
  $$("[data-close]").forEach((b) => {
    if (b.classList.contains("icon-btn")) b.innerHTML = icon("arrow", 2);
    b.addEventListener("click", () => closeSheet());
  });
  $("#c-menu").innerHTML = icon("dots", 2.4);
  $("#c-undo span").innerHTML = icon("undo");
  $("#c-reset span").innerHTML = icon("reset");
  $("#c-skip span").innerHTML = icon("skip", 2.2);
  $$(".benefit-ico").forEach((e) => (e.innerHTML = icon("sparkle")));

  $("#tabs").addEventListener("click", (e) => {
    const b = e.target.closest("button");
    if (!b) return;
    tab = b.dataset.period;
    renderTabs();
    renderCards();
  });
  $("#cards").addEventListener("click", (e) => {
    const c = e.target.closest(".card");
    if (c) openWird(c.dataset.id);
  });

  // العدّ: المنطقة كلها حول الزر قابلة للضغط
  const tapZone = $("#tap-zone");
  tapZone.addEventListener("pointerdown", (e) => {
    if (e.target.closest(".tap-controls")) return;
    e.preventDefault();
    count();
  });
  tapZone.addEventListener("keydown", (e) => { if (e.key === " " || e.key === "Enter") { e.preventDefault(); count(); } });
  document.addEventListener("keydown", (e) => {
    if (openSheet === "#counter" && (e.key === "ArrowDown" || e.key === "+")) count();
  });

  $("#c-undo").onclick = () => {
    if (advancing) { clearTimeout(advancing); advancing = null; }
    const p = P(cur.id);
    if (p.c > 0) p.c--;
    else if (p.s > 0) { p.s--; p.c = cur.steps[p.s].count - 1; renderCounter(); }
    save();
    updateTap();
  };
  $("#c-reset").onclick = () => {
    if (advancing) { clearTimeout(advancing); advancing = null; }
    P(cur.id).c = 0;
    save();
    updateTap();
    toast("أُعيد العدّ لهذا الذكر");
  };
  $("#c-skip").onclick = () => {
    if (advancing) { clearTimeout(advancing); advancing = null; }
    const p = P(cur.id);
    p.c = cur.steps[p.s].count;
    save();
    updateTap();
    vibrate([30, 60, 30]);
    setTimeout(nextStep, 250);
  };
  $("#c-menu").onclick = async () => {
    if (!(await ask("إعادة هذا الورد من البداية؟", "إعادة"))) return;
    Object.assign(P(cur.id), { s: 0, c: 0, done: false });
    save();
    renderCounter(true);
  };
  $("#c-steps").addEventListener("click", (e) => {
    const b = e.target.closest(".st");
    if (!b) return;
    const i = Number(b.dataset.i), p = P(cur.id);
    if (i === p.s) return;
    if (advancing) { clearTimeout(advancing); advancing = null; }
    p.s = i;
    p.c = 0;
    save();
    renderCounter();
  });

  // القرآن
  $("#q-done").onclick = toggleQuranDone;
  $("#q-minus").onclick = () => setQuranPage(S.quranPage - 1);
  $("#q-plus").onclick = () => setQuranPage(S.quranPage + 1);
  $("#q-input").onchange = (e) => setQuranPage(e.target.value);

  // الإعدادات
  $("#open-settings").onclick = () => { showSheet("#settings"); renderSettings(); };
  $("#prayer-card").onclick = () => { showSheet("#settings"); renderSettings(); if (pr()?.lat == null) setTimeout(() => $("#loc-btn").scrollIntoView({ block: "center" }), 50); };
  $("#loc-btn").onclick = locate;
  $("#method").onchange = (e) => {
    S.settings.prayer = { ...(pr() || {}), method: e.target.value, methodManual: true };
    prayerChanged();
  };
  $("#ptable").addEventListener("change", (e) => {
    const inp = e.target.closest("input");
    if (!inp) return;
    const p = pr(), f = inp.dataset.f, k = inp.dataset.k;
    const lim = f === "adj" ? [-30, 30] : [0, 90];
    const v = Math.max(lim[0], Math.min(lim[1], Math.round(Number(inp.value) || 0)));
    p[f] = { ...(f === "iqama" ? IQAMA_DEFAULT : {}), ...(p[f] || {}), [k]: v };
    prayerChanged();
  });
  $("#set-iqama-notify").onchange = (e) => {
    S.settings.prayer = { ...(pr() || {}), notify: e.target.checked };
    prayerChanged();
    if (e.target.checked) askNotifyPermission();
  };
  $("#loc-save").onclick = () => {
    const lat = parseFloat($("#lat").value), lng = parseFloat($("#lng").value);
    if (!(Math.abs(lat) <= 90 && Math.abs(lng) <= 180)) return toast("إحداثيات غير صحيحة");
    setLocation(lat, lng, "");
  };
  setInterval(() => { if (!openSheet) renderPrayer(); }, 30000);
  $("#set-theme").onclick = (e) => { const b = e.target.closest("button"); if (!b) return; S.settings.theme = b.dataset.v; save(); applySettings(); renderSettings(); };
  $("#set-scale").onclick = (e) => { const b = e.target.closest("button"); if (!b) return; S.settings.scale = Number(b.dataset.v); save(); applySettings(); renderSettings(); };
  $("#set-vibrate").onchange = (e) => { S.settings.vibrate = e.target.checked; save(); if (e.target.checked) vibrate(20); };
  $("#set-auto").onchange = (e) => { S.settings.auto = e.target.checked; save(); };
  $("#set-wake").onchange = (e) => { S.settings.wake = e.target.checked; save(); };
  $("#reset-today").onclick = async () => {
    if (!(await ask("تصفير تقدّم أوراد اليوم؟ لن يتغيّر موضعك في القرآن.", "تصفير"))) return;
    S.prog = {};
    save();
    toast("تم التصفير");
  };
  $("#install-btn").onclick = doInstall;
  $("#install-btn-2").onclick = doInstall;
  $("#install-dismiss").onclick = () => {
    try { localStorage.setItem("wirdi:install-dismissed", "1"); } catch (e) {}
    $("#install-banner").hidden = true;
  };

  // تجديد اليوم عند العودة للتطبيق
  document.addEventListener("visibilitychange", () => {
    if (document.visibilityState !== "visible") return;
    if (NATIVE) S = load(); // ربما عدّ المستخدم من الويدجت
    if (rollover()) { tab = tabFor(currentPeriod()); closeSheet(); }
    if (!openSheet) renderHome();
    if (openSheet === "#counter" && cur) { renderCounter(true); requestWake(); }
    if (openSheet === "#quran") renderQuran();
  });
  matchMedia("(prefers-color-scheme: light)").addEventListener?.("change", applySettings);
}

/* ========= تشغيل ========= */
applySettings();
(function autoMethod() {
  const p = S.settings.prayer;
  if (p && !p.methodManual && p.method !== methodForZone()) { p.method = methodForZone(); save(); }
})();
pushMeta();
askNotifyPermission();
rollover();
save();
bind();
renderHome();
maybeShowInstallBanner();

// اختصارات الأيقونة: ?w=tasbih
const qp = new URLSearchParams(location.search).get("w");
if (qp && wirdById(qp)) openWird(qp);

if (!NATIVE && "serviceWorker" in navigator) {
  window.addEventListener("load", () => navigator.serviceWorker.register("sw.js").catch(() => {}));
}
