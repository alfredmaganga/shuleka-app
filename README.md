# 📚 Shuleka App

Shuleka ni app ya kusomeshea inayowezesha mwalimu kuweka taarifa na wanafunzi kuangalia.

## ✨ Features
- 📊 Matokeo ya mitihani
- 📢 Taarifa za shule
- 📝 Vidokezo / Notes
- 📋 Vipimo
- 📄 Upload PDF
- 🎨 Muonekano mzuri na smooth animations

## 🏗️ Structure

```
shuleka/
├── admin-website/          # Admin panel (website)
│   ├── index.html          # Login page
│   ├── dashboard.html      # Manage posts
│   ├── style.css           # Styles
│   └── config.js           # Supabase config
├── android-app/            # Student Android app
│   └── app/src/main/java/com/shuleka/app/
│       ├── MainActivity.kt
│       ├── data/           # Post model, Supabase client
│       └── ui/screens/     # Home, Detail screens
├── supabase/
│   └── migration.sql       # Database setup
└── .github/workflows/      # CI/CD
    ├── build-apk.yml       # Build APK
    └── deploy-admin.yml    # Deploy website
```

## 🚀 Setup Instructions

### 1. Supabase (Database)
1. Go to [supabase.com](https://supabase.com) → New Project
2. Open SQL Editor → paste contents of `supabase/migration.sql` → Run
3. Go to **Authentication** → create admin account (email + password)
4. Go back to SQL Editor → run:
   ```sql
   INSERT INTO profiles (id, full_name, role)
   VALUES ('<admin-user-id>', 'Mwalimu', 'admin');
   ```
5. Copy your **Project URL** and **anon key** from Settings → API

### 2. Admin Website
1. Open `admin-website/config.js`
2. Replace `YOUR_PROJECT` and `YOUR_ANON_KEY` with your Supabase values
3. Deploy to GitHub Pages (auto-deploys on push)

### 3. Android App
1. Open `android-app/app/src/main/java/com/shuleka/app/data/SupabaseClient.kt`
2. Replace `YOUR_PROJECT` and `YOUR_ANON_KEY`
3. Push to GitHub → APK builds automatically via GitHub Actions
4. Download APK from Actions → Artifacts

### 4. Keystore (for APK signing)
Ask Codex to generate a keystore, then add to GitHub Secrets:
- `KEYSTORE_BASE64` — base64 encoded keystore
- `KEYSTORE_PASSWORD` — keystore password
- `KEY_ALIAS` — key alias
- `KEY_PASSWORD` — key password

## 🔒 Security
- ✅ Only anon key in app code
- ✅ RLS enabled on all tables
- ✅ Service role key NEVER in app
- ✅ OneSignal REST key in Edge Function only
- ✅ Keystore in GitHub Secrets only

## 🧪 RLS Test Steps
1. Create 2 accounts: **User A** (admin) and **User B** (student)
2. As User A, try to read User B's data → should work (public read)
3. As User B (or anon), try to INSERT a post → should FAIL
4. As User B, try to DELETE a post → should FAIL
5. If any write succeeds → policy is broken, fix it

## 📱 Testing the APK
1. Download APK from GitHub Actions → Artifacts
2. Install on phone (enable "Unknown Sources")
3. Test all screens
4. Turn off internet → test offline behavior
5. Close app → reopen → check state
