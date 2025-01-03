package org.incept5.api.core.security

import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.AuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.LoggerFactory
import java.util.*

@ApplicationScoped
class SupabaseJwtAuthMechanism(
    private val jwtValidator: SupabaseJwtValidator,
    private val config: SupabaseJwtConfig
) : HttpAuthenticationMechanism {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val prefix = "Bearer"

    override fun authenticate(
        context: RoutingContext,
        identityProviderManager: IdentityProviderManager
    ): Uni<SecurityIdentity> {
        val token = extractToken(context)
        if (token == null) {
            logger.debug("No JWT token found in the request")
            return Uni.createFrom().optional(Optional.empty())
        }

        return try {
            val claims = jwtValidator.validateToken(token)
            val builder = QuarkusSecurityIdentity.builder()
            builder.setPrincipal { claims.subject }
            Uni.createFrom().item(builder.build())
        } catch (e: Exception) {
            logger.error("JWT validation failed", e)
            Uni.createFrom().optional(Optional.empty())
        }
    }

    override fun getChallenge(context: RoutingContext): Uni<ChallengeData> {
        return Uni.createFrom().item(
            ChallengeData(
                401,
                "WWW-Authenticate",
                "Bearer realm=\"${config.realm()}\", charset=\"UTF-8\""
            )
        )
    }

    private fun extractToken(context: RoutingContext): String? {
        val auth = context.request().getHeader("Authorization") ?: return null
        if (!auth.startsWith("$prefix ", ignoreCase = true)) return null
        return auth.substring(prefix.length + 1)
    }

    override fun getCredentialTypes(): Set<Class<out AuthenticationRequest>> = emptySet()
} 