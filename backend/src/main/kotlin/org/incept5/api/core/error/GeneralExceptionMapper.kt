package org.incept5.api.core.error

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.slf4j.LoggerFactory

/**
 * Global exception handler that converts all unhandled exceptions into appropriate HTTP responses.
 * Provides consistent error response format across the API.
 *
 * Response format:
 * ```json
 * {
 *   "error": "ExceptionClassName",
 *   "message": "Human readable error message",
 *   "status": 500
 * }
 * ```
 *
 * Handles two main cases:
 * 1. WebApplicationException - uses its status code and message
 * 2. All other exceptions - converts to 500 Internal Server Error
 */
@Provider
class GeneralExceptionMapper : ExceptionMapper<Throwable> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun toResponse(exception: Throwable): Response {
        // If it's already a WebApplicationException, use its status code
        if (exception is WebApplicationException) {
            val status = exception.response.status
            return createErrorResponse(
                status = status,
                message = exception.message ?: Response.Status.fromStatusCode(status)?.reasonPhrase ?: "Unknown error",
                error = exception
            )
        }

        // For all other exceptions, return 500 Internal Server Error
        logger.error("Unhandled exception occurred", exception)
        return createErrorResponse(
            status = Response.Status.INTERNAL_SERVER_ERROR.statusCode,
            message = "An unexpected error occurred",
            error = exception
        )
    }

    /**
     * Creates a standardized error response with consistent format.
     *
     * @param status HTTP status code
     * @param message Human readable error message
     * @param error The exception that caused the error
     * @return Response object with JSON error details
     */
    private fun createErrorResponse(status: Int, message: String, error: Throwable): Response {
        val response = mapOf(
            "error" to error.javaClass.simpleName,
            "message" to message,
            "status" to status
        )

        return Response.status(status)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(response)
            .build()
    }
} 