package org.incept5.api.modules.widget.controller

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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Test
import java.util.UUID

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WidgetControllerTest : BaseAuthenticatedTest() {

    @BeforeEach
    fun initializeTestData() {
        ensureTestUsers()
    }

    @Test
    fun `should create widget when authenticated`() {
        val request = CreateWidgetRequest(
            description = "Test Widget",
            category = WidgetCategory.BASIC,
            level = 1
        )

        givenAuth()
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
    fun `should list only authenticated user widgets`() {
        // Create a widget for our test user
        val widget1Id = createWidget("Widget 1")
        
        // Create a widget for another user
        createWidget("Widget 2", TestJwtGenerator.TEST_USER_ID_2)

        // List widgets for our test user
        givenAuth()
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