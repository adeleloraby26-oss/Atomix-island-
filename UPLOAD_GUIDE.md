# 📱 خطوات رفع المشروع على GitHub وتنزيل APK

## الخطوة 1 — إنشاء Repository على GitHub

1. افتح [github.com](https://github.com) وسجل دخول
2. اضغط **"New"** أو **"+"** في الأعلى
3. اكتب اسم المشروع: `AtomixIsland`
4. اختر **Private** (خاص) أو **Public**
5. اضغط **"Create repository"**

---

## الخطوة 2 — رفع الملفات

### من المتصفح (أسهل طريقة):
1. افتح الـ Repository اللي أنشأته
2. اضغط **"uploading an existing file"**
3. اسحب كل ملفات المشروع وارفعها
4. اضغط **"Commit changes"**

### من الكمبيوتر (لو عندك):
```bash
git init
git add .
git commit -m "first commit"
git remote add origin https://github.com/USERNAME/AtomixIsland.git
git push -u origin main
```

---

## الخطوة 3 — تنزيل APK

بعد الرفع بـ 5-10 دقايق:

1. افتح الـ Repository على GitHub
2. اضغط على **"Actions"** في القائمة العلوية
3. هتلاقي Build يشتغل تلقائي ✅
4. بعد ما يخلص — اضغط عليه
5. في الأسفل هتلاقي **"Artifacts"**
6. اضغط **"AtomixIsland-Debug"** وحمله 📥

---

## ملاحظات مهمة

- ✅ كل مرة تعدل الكود وترفعه — بيبني APK جديد تلقائي
- ✅ Debug APK للتجربة على تليفونك مباشرة
- ⚠️ لا ترفع ملفات `.jks` أو `.keystore` أبداً (مفاتيح التوقيع)
