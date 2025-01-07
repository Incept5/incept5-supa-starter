package org.incept5.api.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.LoggerFactory
import java.util.Base64

@ApplicationScoped
class SupabaseJwtValidator(private val config: SupabaseJwtConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val algorithm: Algorithm by lazy {
        Algorithm.HMAC256(
            // supabase.jwt.secret is expected to already be Base64 URL–encoded
            Base64.getDecoder().decode(config.secret().trim())
        )
    }

    fun validateToken(token: String): DecodedJWT {
        logger.debug("Starting JWT token validation")
        logger.debug("Using issuer: ${config.issuer()}")
        
        return JWT.require(algorithm)
            .build()
            .verify(token)
            .also { jwt ->
                logger.debug(
                    """
                    JWT token validated successfully:
                    - Subject: ${jwt.subject}
                    - Issuer: ${jwt.issuer}
                    - Issued At: ${jwt.issuedAt}
                    - Expires At: ${jwt.expiresAt}
                    """.trimIndent()
                )
            }
    }
}