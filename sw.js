// عامل الخدمة: يجعل التطبيق يعمل دون إنترنت
const CACHE = "wirdi-v8";
const ASSETS = [
  "./",
  "index.html",
  "styles.css",
  "app.js",
  "data.js",
  "prayer.js",
  "manifest.webmanifest",
  "icons/icon.svg",
  "icons/icon-192.png",
  "icons/icon-512.png",
  "icons/apple-touch-icon.png",
  "fonts/Amiri-Regular.ttf",
  "fonts/Amiri-Bold.ttf",
  "fonts/IBMPlexSansArabic-Regular.ttf",
  "fonts/IBMPlexSansArabic-Medium.ttf",
  "fonts/IBMPlexSansArabic-SemiBold.ttf",
  "fonts/IBMPlexSansArabic-Bold.ttf",
];

self.addEventListener("install", (e) => {
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(ASSETS)).then(() => self.skipWaiting()));
});

self.addEventListener("activate", (e) => {
  e.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (e) => {
  const req = e.request;
  if (req.method !== "GET") return;
  const url = new URL(req.url);

  if (url.origin !== location.origin) return;

  // ملفات التطبيق: الشبكة أولًا (للتحديثات) مع الرجوع للذاكرة دون إنترنت
  e.respondWith(
    fetch(req)
      .then((res) => {
        const copy = res.clone();
        caches.open(CACHE).then((c) => c.put(req, copy));
        return res;
      })
      .catch(() => caches.match(req, { ignoreSearch: true }).then((hit) => hit || caches.match("index.html")))
  );
});
