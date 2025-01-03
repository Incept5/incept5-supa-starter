package org.incept5.api.core.error

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response

/**
 * Base exception class for all application-specific exceptions.
 * Extends WebApplicationException to provide proper REST response handling.
 *
 * @param message The error message to be returned to the client
 * @param status The HTTP status code to be returned (defaults to 500 Internal Server Error)
 * @param cause The underlying cause of the exception (optional)
 */
open class ApiException(
    message: String,
    val status: Response.Status = Response.Status.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : WebApplicationException(message, cause, Response.status(status).build()) 