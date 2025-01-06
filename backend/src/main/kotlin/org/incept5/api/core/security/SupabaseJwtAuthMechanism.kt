package org.incept5.api.core.security

import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.AuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism
import io.smallrye.mutiny.Uni
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType
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
        logger.debug("Starting authentication process for request: ${context.request().uri()}")
        val token = extractToken(context)
        if (token == null) {
            logger.debug("No JWT token found in the request headers")
            return Uni.createFrom().optional(Optional.empty())
        }
        logger.debug("JWT token extracted successfully")

        return try {
            logger.debug("Attempting to validate JWT token")
            val claims = jwtValidator.validateToken(token)
            logger.debug("JWT claims validated successfully for subject: ${claims.subject}")
            val builder = QuarkusSecurityIdentity.builder()
            builder.setPrincipal { claims.subject }
            val identity: SecurityIdentity = builder.build()
            Uni.createFrom().item(identity)
                .onItem().invoke { _ -> 
                    logger.debug("Security identity built successfully for user: ${claims.subject}")
                }
        } catch (e: Exception) {
            logger.error("JWT validation failed: ${e.message}", e)
            Uni.createFrom().optional(Optional.empty())
        }
    }

    override fun getChallenge(context: RoutingContext): Uni<ChallengeData> {
        logger.debug("Generating authentication challenge for request: ${context.request().uri()}")
        
        val errorResponse = mapOf(
            "error" to "Unauthorized",
            "message" to "Authentication is required to access this resource",
            "status" to 401
        )
        
        // Set the response body and content type
        context.response()
            .putHeader("Content-Type", MediaType.APPLICATION_JSON)
            .setStatusCode(401)
            .end(Json.encode(errorResponse))
        
        // Return the WWW-Authenticate challenge header
        return Uni.createFrom().item(
            ChallengeData(
                401,
                "WWW-Authenticate",
                "Bearer realm=\"${config.realm()}\", charset=\"UTF-8\""
            )
        ).onItem().invoke { _ -> 
            logger.debug("Authentication challenge generated successfully")
        }
    }

    private fun extractToken(context: RoutingContext): String? {
        val auth = context.request().getHeader("Authorization")
        if (auth == null) {
            logger.debug("No Authorization header found")
            return null
        }
        if (!auth.startsWith("$prefix ", ignoreCase = true)) {
            logger.debug("Authorization header does not start with '$prefix'")
            return null
        }
        logger.debug("Authorization header found and properly formatted")
        return auth.substring(prefix.length + 1)
    }

    override fun getCredentialTypes(): Set<Class<out AuthenticationRequest>> = emptySet()
} 