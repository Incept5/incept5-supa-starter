package org.incept5.api.core

import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class ApiDocumentationTest : BaseAuthenticatedTest() {

    @Test
    fun `swagger ui should be accessible at api-docs endpoint`() {
        given()
            .When {
                get("/api/docs")
            }
            .Then {
                statusCode(Response.Status.OK.statusCode)
                contentType("text/html")
            }
    }

    @Test
    fun `openapi json spec should be accessible with bearer auth scheme`() {
        given()
            .When {
                get("/api/openapi.json")
            }
            .Then {
                statusCode(Response.Status.OK.statusCode)
                contentType(MediaType.APPLICATION_JSON)
                body("components.securitySchemes.bearer.type", equalTo("http"))
                body("components.securitySchemes.bearer.scheme", equalTo("bearer"))
                body("components.securitySchemes.bearer.bearerFormat", equalTo("JWT"))
            }
    }

    @Test
    fun `openapi yaml spec should be accessible with bearer auth scheme`() {
        given()
            .When {
                get("/api/openapi.yaml")
            }
            .Then {
                statusCode(Response.Status.OK.statusCode)
                contentType("application/yaml")
            }
    }
} 