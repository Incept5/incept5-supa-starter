interface ImportMetaEnv {
  VITE_API_URL: string
  VITE_SUPABASE_URL: string
  VITE_SUPABASE_ANON_KEY: string
}

export const env = {
  VITE_API_URL: import.meta.env.VITE_API_URL,
  VITE_SUPABASE_URL: import.meta.env.VITE_SUPABASE_URL,
  VITE_SUPABASE_ANON_KEY: import.meta.env.VITE_SUPABASE_ANON_KEY,
}
