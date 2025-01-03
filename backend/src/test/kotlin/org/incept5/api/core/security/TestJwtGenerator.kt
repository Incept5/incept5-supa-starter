package org.incept5.api.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Instant
import java.util.*

@ApplicationScoped
class TestJwtGenerator {
    @ConfigProperty(name = "supabase.jwt.secret")
    lateinit var jwtSecret: String

    @ConfigProperty(name = "supabase.jwt.issuer")
    lateinit var issuer: String

    fun generateToken(userId: UUID = TEST_USER_ID, expirationMinutes: Long = 60): String {
        val algorithm = Algorithm.HMAC256(Base64.getDecoder().decode(jwtSecret))
        val now = Instant.now()

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .withClaim("role", "authenticated")
            .withClaim("aud", "authenticated")
            .sign(algorithm)
    }

    companion object {
        val TEST_USER_ID: UUID = UUID.fromString("018df487-1234-4567-8901-234567890123")
        val TEST_USER_ID_2: UUID = UUID.fromString("018df487-2345-6789-0123-456789012345")
        
        fun randomTestUserId(): UUID = UUID.randomUUID()
    }
} 