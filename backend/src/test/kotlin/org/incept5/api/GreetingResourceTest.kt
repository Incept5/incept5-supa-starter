package org.incept5.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.incept5.api.test.BaseTest
import org.junit.jupiter.api.Test

@QuarkusTest
class GreetingResourceTest : BaseTest() {

    @Test
    fun testHelloEndpoint() {
        given()
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("Hello from RESTEasy Reactive"))
    }
}