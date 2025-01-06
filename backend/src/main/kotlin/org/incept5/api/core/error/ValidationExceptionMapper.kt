package org.incept5.api.core.error

import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.slf4j.LoggerFactory

/**
 * Exception mapper that handles validation failures and converts them into user-friendly error responses.
 * Processes Jakarta Validation constraint violations and returns detailed error information.
 *
 * Response format:
 * ```json
 * {
 *   "error": "Validation Error",
 *   "status": 400,
 *   "violations": [
 *     {
 *       "field": "propertyPath",
 *       "message": "validation message",
 *       "invalidValue": "actual invalid value"
 *     }
 *   ]
 * }
 * ```
 */
@Provider
class ValidationExceptionMapper : ExceptionMapper<ConstraintViolationException> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun toResponse(exception: ConstraintViolationException): Response {
        logger.warn("Validation error occurred: {}", exception.message)

        val errors = exception.constraintViolations.map { violation ->
            mapOf(
                "field" to violation.propertyPath.toString(),
                "message" to violation.message,
                "invalidValue" to violation.invalidValue?.toString()
            )
        }

        val response = mapOf(
            "error" to "Validation Error",
            "status" to Response.Status.BAD_REQUEST.statusCode,
            "violations" to errors
        )

        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(response)
            .build()
    }
} 