package org.incept5.api.core.error

import jakarta.ws.rs.core.Response

/**
 * Exception thrown when a requested resource is not found.
 * Results in HTTP 404 Not Found response.
 *
 * @param message Detailed message about what resource was not found
 * @param cause The underlying cause (optional)
 */
class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null
) : ApiException(message, Response.Status.NOT_FOUND, cause)

/**
 * Exception thrown when there is a conflict with the current state of a resource.
 * Results in HTTP 409 Conflict response.
 *
 * @param message Description of the conflict
 * @param cause The underlying cause (optional)
 */
class ResourceConflictException(
    message: String,
    cause: Throwable? = null
) : ApiException(message, Response.Status.CONFLICT, cause)

/**
 * Exception thrown when the request is malformed or contains invalid data.
 * Results in HTTP 400 Bad Request response.
 *
 * @param message Description of what makes the request invalid
 * @param cause The underlying cause (optional)
 */
class InvalidRequestException(
    message: String,
    cause: Throwable? = null
) : ApiException(message, Response.Status.BAD_REQUEST, cause)

/**
 * Exception thrown when optimistic locking fails due to concurrent modifications.
 * Results in HTTP 409 Conflict response.
 *
 * @param message Description of the conflict (defaults to standard message)
 * @param cause The underlying cause (optional)
 */
class OptimisticLockException(
    message: String = "Resource has been modified by another user",
    cause: Throwable? = null
) : ApiException(message, Response.Status.CONFLICT, cause)

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 * Results in HTTP 403 Forbidden response.
 *
 * @param message Description of why access was denied (defaults to standard message)
 * @param cause The underlying cause (optional)
 */
class ForbiddenException(
    message: String = "Access denied",
    cause: Throwable? = null
) : ApiException(message, Response.Status.FORBIDDEN, cause) 