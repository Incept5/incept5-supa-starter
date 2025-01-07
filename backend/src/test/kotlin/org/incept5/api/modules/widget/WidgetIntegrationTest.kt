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
        val testUser = createTestUser()
        val request = CreateWidgetRequest(
            description = "Test Widget",
            category = WidgetCategory.BASIC,
            level = 1
        )

        givenAuth(testUser)
            .When {
                body(request)
                post("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.CREATED.statusCode)
                body("id", notNullValue())
                body("userId", equalTo(testUser.toString()))
                body("description", equalTo(request.description))
                body("category", equalTo(request.category.name))
                body("level", equalTo(request.level))
                body("createdAt", notNullValue())
                body("updatedAt", notNullValue())
            }
    }

    @Test
    fun `should reject invalid category query parameter`() {
        val testUser = createTestUser()
        // Create a widget for our test user
        createWidget(testUser, "Widget 1")
        
        // Try to filter by invalid category
        givenAuth(testUser)
            .When {
                get("/api/widgets?category=invalid_category")
            }
            .Then {
                statusCode(Response.Status.BAD_REQUEST.statusCode)
            }
    }

    @Test
    fun `should list only authenticated user widgets`() {
        val testUser1 = createTestUser()
        val testUser2 = createTestUser()

        // Create a widget for our test user
        val widget1Id = createWidget(testUser1, "Widget 1")
        
        // Create a widget for another user
        createWidget(testUser2, "Widget 2")

        // List widgets for our test user
        givenAuth(testUser1)
            .When {
                get("/api/widgets")
            }
            .Then {
                statusCode(Response.Status.OK.statusCode)
                body("content.size()", equalTo(1))
                body("content[0].id", equalTo(widget1Id))
                body("content[0].userId", equalTo(testUser1.toString()))
            }
    }

    private fun createWidget(userId: UUID, description: String): String {
        val request = CreateWidgetRequest(
            description = description,
            category = WidgetCategory.BASIC,
            level = 1
        )

        return givenAuth(userId)
            .When {
                body(request)
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