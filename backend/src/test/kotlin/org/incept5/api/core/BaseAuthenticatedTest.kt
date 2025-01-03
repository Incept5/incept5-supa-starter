package org.incept5.api.core

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.incept5.api.core.security.TestJwtGenerator
import org.junit.jupiter.api.BeforeEach
import java.util.UUID

abstract class BaseAuthenticatedTest {
    
    @Inject
    protected lateinit var jwtGenerator: TestJwtGenerator

    @Inject
    private lateinit var em: EntityManager

    @BeforeEach
    fun setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    protected fun givenAuth(userId: UUID = TestJwtGenerator.TEST_USER_ID): RequestSpecification {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer ${jwtGenerator.generateToken(userId)}")
    }

    protected fun given(): RequestSpecification {
        return RestAssured.given()
            .contentType(ContentType.JSON)
    }

    @Transactional
    protected fun ensureUser(userId: UUID, email: String = "${userId}@example.com") {
        em.createNativeQuery("""
            INSERT INTO auth.users (id, email, encrypted_password, email_confirmed_at)
            VALUES (:id, :email, 'dummy', CURRENT_TIMESTAMP)
            ON CONFLICT (id) DO NOTHING
        """)
        .setParameter("id", userId)
        .setParameter("email", email)
        .executeUpdate()
    }

    @Transactional
    protected fun ensureTestUsers() {
        ensureUser(TestJwtGenerator.TEST_USER_ID, "test1@example.com")
        ensureUser(TestJwtGenerator.TEST_USER_ID_2, "test2@example.com")
    }
} 