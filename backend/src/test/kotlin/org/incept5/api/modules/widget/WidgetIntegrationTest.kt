package org.incept5.api.modules.widget

import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import jakarta.ws.rs.core.Response
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.incept5.api.core.BaseAuthenticatedTest
import org.incept5.api.core.security.TestJwtGenerator
import org.incept5.api.modules.widget.domain.WidgetCategory
import org.incept5.api.modules.widget.dto.CreateWidgetRequest
import org.junit.jupiter.api.Test
import java.util.UUID

/**
 * Integration test for the Widget module demonstrating end-to-end testing without mocks.
 * 
 * This test class serves as an example of how to properly write integration tests in the project:
 * - Uses real database interactions instead of mocks
 * - Tests complete request/response cycles
 * - Demonstrates proper authentication setup
 * - Shows validation testing
 * - Includes proper test data setup and cleanup
 * - Tests both success and error scenarios
 */
@QuarkusTest
class WidgetIntegrationTest : BaseAuthenticatedTest() {

    @Test
    fun `should create widget when authenticated`() {
        val request = CreateWidgetRequest(
            description = "Test Widget",
            category = WidgetCategory.BASIC,
            level = 1
        )

        givenAuth(TestJwtGenerator.TEST_USER_ID)
            .body(request)
            .When {
                post("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.CREATED.statusCode)
                body("id", notNullValue())
                body("description", equalTo(request.description))
                body("category", equalTo(request.category.name))
                body("level", equalTo(request.level))
                body("userId", equalTo(TestJwtGenerator.TEST_USER_ID.toString()))
            }
    }

    @Test
    fun `should return 401 when not authenticated`() {
        val request = CreateWidgetRequest(
            description = "Test Widget",
            category = WidgetCategory.BASIC,
            level = 1
        )

        given()
            .body(request)
            .When {
                post("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.UNAUTHORIZED.statusCode)
            }
    }

    @Test
    fun `should return 400 with validation errors for invalid widget request`() {
        val request = CreateWidgetRequest(
            description = "", // Invalid: blank description
            category = WidgetCategory.BASIC,
            level = 0 // Invalid: below minimum level
        )

        givenAuth(TestJwtGenerator.TEST_USER_ID)
            .body(request)
            .When {
                post("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.BAD_REQUEST.statusCode)
                body("error", equalTo("Validation Error"))
                body("status", equalTo(400))
                body("violations.size()", equalTo(3))
                body("violations.find { it.field == 'createWidget.request.description' && it.message == 'Description must be between 3 and 1000 characters' }", notNullValue())
                body("violations.find { it.field == 'createWidget.request.description' && it.message == 'Description is required' }", notNullValue())
                body("violations.find { it.field == 'createWidget.request.level' && it.message == 'Level must be at least 1' }", notNullValue())
            }
    }

    @Test
    fun `should list only authenticated user widgets`() {
        // Create a widget for our test user
        val widget1Id = createWidget("Widget 1")
        
        // Create a widget for another user
        createWidget("Widget 2", TestJwtGenerator.TEST_USER_ID_2)

        // List widgets for our test user
        givenAuth(TestJwtGenerator.TEST_USER_ID)
            .When {
                get("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.OK.statusCode)
                body("content.size()", equalTo(1))
                body("content[0].id", equalTo(widget1Id))
                body("content[0].userId", equalTo(TestJwtGenerator.TEST_USER_ID.toString()))
            }
    }

    private fun createWidget(description: String, userId: UUID = TestJwtGenerator.TEST_USER_ID): String {
        val request = CreateWidgetRequest(
            description = description,
            category = WidgetCategory.BASIC,
            level = 1
        )

        return givenAuth(userId)
            .body(request)
            .When {
                post("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.CREATED.statusCode)
            }
            .Extract {
                path("id")
            }
    }
} 