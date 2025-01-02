package org.incept5.api.test

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class DatabaseTestResource : QuarkusTestResourceLifecycleManager {
    companion object {
        private val postgres: PostgreSQLContainer<*> by lazy {
            PostgreSQLContainer(DockerImageName.parse("postgres:15"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withCommand(
                    "postgres",
                    "-c", "fsync=off",
                    "-c", "synchronous_commit=off",
                    "-c", "full_page_writes=off"
                )
                .apply { start() }
        }

        private val migrationsDir: Path by lazy {
            val tempDir = Path.of(System.getProperty("java.io.tmpdir"))
            val dir = tempDir.resolve("test-migrations")
            if (!Files.exists(dir)) {
                Files.createDirectories(dir)
                copyMigrations(dir)
            }
            dir
        }

        private fun copyMigrations(targetDir: Path) {
            val projectDir = System.getProperty("user.dir")
            val sourceDir = Path.of(projectDir).parent.resolve("docker/migrations")

            Files.walk(sourceDir)
                .filter { it.toString().endsWith(".sql") }
                .forEach { source ->
                    val targetFile = targetDir.resolve(source.fileName)
                    if (!Files.exists(targetFile)) {
                        Files.copy(source, targetFile)
                    }
                }
        }
    }

    override fun start(): Map<String, String> {
        return mapOf(
            "quarkus.datasource.jdbc.url" to postgres.jdbcUrl,
            "quarkus.datasource.username" to postgres.username,
            "quarkus.datasource.password" to postgres.password,
            "quarkus.flyway.locations" to "filesystem:${migrationsDir}"
        )
    }

    override fun stop() {
        // Container will be stopped by JVM shutdown hook
    }
} 