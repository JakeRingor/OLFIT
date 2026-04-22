package com.example.olfit

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.FlowType

object Supabase {
    private const val SUPABASE_URL = "https://gelbjpuqwagtzmrbcsyo.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdlbGJqcHVxd2FndHptcmJjc3lvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3NDc2ODcsImV4cCI6MjA5MjMyMzY4N30.RJ73dVqFAeoU5nrJSIFg6BTAR9rI3O5FKzWfnAS2sYE"

    val client = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
        install(Auth) {
            flowType = FlowType.PKCE
            // Supabase-kt 3.x with compose-auth should handle persistence automatically.
            // If it's not working, we might need to manually handle it or check if the session is loaded.
        }
        install(Postgrest)
    }
}
