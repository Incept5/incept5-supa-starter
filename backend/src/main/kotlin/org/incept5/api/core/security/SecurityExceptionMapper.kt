package org.incept5.api.core.security

import io.quarkus.security.UnauthorizedException
import jakarta.annotation.Priority
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.slf4j.LoggerFactory

@Provider
@Priority(Priorities.AUTHENTICATION)
class SecurityExceptionMapper : ExceptionMapper<SecurityException> {
    
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun toResponse(exception: SecurityException): Response {
        logger.error("Security exception occurred", exception)
        
        val status = when (exception) {
            is UnauthorizedException -> Response.Status.UNAUTHORIZED
            else -> Response.Status.FORBIDDEN
        }

        val error = mapOf(
            "error" to status.reasonPhrase,
            "message" to (exception.message ?: "Access denied"),
            "status" to status.statusCode
        )

        return Response.status(status)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(error)
            .build()
    }
} 