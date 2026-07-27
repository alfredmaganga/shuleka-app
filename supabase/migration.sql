-- ===== SHULEKA APP — Database Setup =====
-- Run this in Supabase SQL Editor

-- 1. Profiles table
CREATE TABLE IF NOT EXISTS profiles (
  id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  full_name TEXT NOT NULL DEFAULT '',
  role TEXT NOT NULL DEFAULT 'student' CHECK (role IN ('admin', 'student'))
);

-- 2. Posts table
CREATE TABLE IF NOT EXISTS posts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title TEXT NOT NULL,
  body TEXT DEFAULT '',
  category TEXT NOT NULL DEFAULT 'taarifa'
    CHECK (category IN ('matokeo', 'taarifa', 'notes', 'vipimo', 'mengineyo')),
  pdf_url TEXT,
  created_by UUID REFERENCES profiles(id) ON DELETE SET NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- 3. Enable RLS
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE posts ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies — PROFILES
-- Everyone can read profiles
CREATE POLICY "Anyone can read profiles"
  ON profiles FOR SELECT
  USING (true);

-- Only the user can insert their own profile
CREATE POLICY "Users can insert own profile"
  ON profiles FOR INSERT
  WITH CHECK (auth.uid() = id);

-- Only the user can update their own profile
CREATE POLICY "Users can update own profile"
  ON profiles FOR UPDATE
  USING (auth.uid() = id);

-- 5. RLS Policies — POSTS
-- Everyone can read posts (students need to see content)
CREATE POLICY "Anyone can read posts"
  ON posts FOR SELECT
  USING (true);

-- Only authenticated users can insert posts
CREATE POLICY "Authenticated users can insert posts"
  ON posts FOR INSERT
  WITH CHECK (auth.role() = 'authenticated');

-- Only the author can update their posts
CREATE POLICY "Authors can update own posts"
  ON posts FOR UPDATE
  USING (auth.uid() = created_by);

-- Only the author can delete their posts
CREATE POLICY "Authors can delete own posts"
  ON posts FOR DELETE
  USING (auth.uid() = created_by);

-- 6. Create storage bucket for PDFs
INSERT INTO storage.buckets (id, name, public) VALUES ('post-files', 'post-files', true)
ON CONFLICT (id) DO NOTHING;

-- Storage policies
CREATE POLICY "Anyone can read storage objects"
  ON storage.objects FOR SELECT
  USING (bucket_id = 'post-files');

CREATE POLICY "Authenticated users can upload"
  ON storage.objects FOR INSERT
  WITH CHECK (bucket_id = 'post-files' AND auth.role() = 'authenticated');

CREATE POLICY "Authenticated users can delete"
  ON storage.objects FOR DELETE
  USING (bucket_id = 'post-files' AND auth.role() = 'authenticated');

-- 7. Create admin account function (run once)
-- First create the admin user in Supabase Auth, then run:
-- INSERT INTO profiles (id, full_name, role) VALUES ('<admin-user-id>', 'Mwalimu', 'admin');
