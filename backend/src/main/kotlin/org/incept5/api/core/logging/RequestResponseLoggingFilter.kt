package org.incept5.api.core.logging

import io.quarkus.logging.Log
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

@Provider
class RequestResponseLoggingFilter : ContainerRequestFilter, ContainerResponseFilter {

    override fun filter(requestContext: ContainerRequestContext) {
        val requestId = java.util.UUID.randomUUID().toString()
        requestContext.setProperty("requestId", requestId)

        Log.info("""
            |=== Incoming Request ===
            |ID: $requestId
            |Method: ${requestContext.method}
            |Path: ${requestContext.uriInfo.path}
            |Headers: ${requestContext.headers}
            |Query Parameters: ${requestContext.uriInfo.queryParameters}
        """.trimMargin())

        // Log request body if present
        if (requestContext.hasEntity()) {
            try {
                val stream = requestContext.entityStream
                val baos = ByteArrayOutputStream()
                stream.transferTo(baos)
                val body = String(baos.toByteArray(), StandardCharsets.UTF_8)
                
                // Reset the entity stream
                requestContext.entityStream = ByteArrayInputStream(baos.toByteArray())
                
                Log.info("""
                    |=== Request Body ===
                    |ID: $requestId
                    |Body: $body
                """.trimMargin())
            } catch (e: Exception) {
                Log.warn("Failed to log request body", e)
            }
        }
    }

    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        val requestId = requestContext.getProperty("requestId")?.toString() ?: "unknown"

        try {
            Log.info("""
                |=== Outgoing Response ===
                |ID: $requestId
                |Status: ${responseContext.status}
                |Headers: ${responseContext.headers}
                |Body: ${responseContext.entity ?: "no content"}
            """.trimMargin())
        } catch (e: Exception) {
            Log.warn("Failed to log response", e)
        }
    }
} 