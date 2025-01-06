package org.incept5.api.core.config

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger

@ApplicationScoped
class StartupLogger {
    
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    lateinit var dbUrl: String
    
    private val log: Logger = Logger.getLogger(StartupLogger::class.java)
    
    fun onStart(@Observes event: StartupEvent) {
        log.info("Starting application with database URL: $dbUrl")
    }
} 