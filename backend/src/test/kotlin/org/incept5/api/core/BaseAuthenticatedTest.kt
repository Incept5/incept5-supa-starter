package org.incept5.api.core

import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.parsing.Parser
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
        // debug logging for restassured
        //RestAssured.filters(RequestLoggingFilter(), ResponseLoggingFilter())
        // Set default parser to JSON for all responses
        RestAssured.defaultParser = Parser.JSON
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
} 