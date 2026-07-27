// ===== SHULEKA ADMIN — Supabase Config =====
const SUPABASE_URL = 'https://uiwgbviucbbqcxdkpdwa.supabase.co';
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg';

const supabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);
console.log('Supabase client initialized');
