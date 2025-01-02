package org.incept5.api.test

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.TestInstance

@QuarkusTest
@QuarkusTestResource(DatabaseTestResource::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTest 