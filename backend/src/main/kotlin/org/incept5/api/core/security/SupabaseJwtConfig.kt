package org.incept5.api.core.security

import io.quarkus.runtime.annotations.StaticInitSafe
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import io.smallrye.config.WithName

@ConfigMapping(prefix = "supabase.jwt")
@StaticInitSafe
interface SupabaseJwtConfig {
    @WithName("issuer")
    @WithDefault("supabase")
    fun issuer(): String

    @WithName("secret")
    fun secret(): String

    @WithName("realm")
    @WithDefault("Incept5 API")
    fun realm(): String
} 