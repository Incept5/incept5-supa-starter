package org.incept5.api.modules.widget.controller

import io.quarkus.panache.common.Sort
import io.quarkus.security.Authenticated
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.incept5.api.modules.widget.domain.WidgetCategory
import org.incept5.api.modules.widget.dto.CreateWidgetRequest
import org.incept5.api.modules.widget.dto.UpdateWidgetRequest
import org.incept5.api.modules.widget.service.WidgetService
import org.slf4j.LoggerFactory
import jakarta.validation.executable.ValidateOnExecution

@Path("/api/widgets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@ValidateOnExecution
class WidgetController(private val widgetService: WidgetService) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val maxPageSize = 100

    @POST
    fun createWidget(
        @Valid request: CreateWidgetRequest,
        @Context securityContext: SecurityContext
    ): Response {
        val userId = securityContext.userPrincipal.name
        logger.info("Creating widget: userId={}", userId)
        
        val widget = widgetService.createWidget(userId, request)
        return Response.status(Response.Status.CREATED).entity(widget).build()
    }

    @PUT
    @Path("/{widgetId}")
    fun updateWidget(
        @PathParam("widgetId") @NotBlank @Pattern(regexp = "^[0-9A-Z]{26}$", message = "Invalid ULID format") widgetId: String,
        @Valid request: UpdateWidgetRequest,
        @Context securityContext: SecurityContext
    ): Response {
        val userId = securityContext.userPrincipal.name
        logger.info("Updating widget: userId={}, widgetId={}", userId, widgetId)
        
        val widget = widgetService.updateWidget(userId, widgetId, request)
        return Response.ok(widget).build()
    }

    @GET
    @Path("/{widgetId}")
    fun getWidget(
        @PathParam("widgetId") @NotBlank @Pattern(regexp = "^[0-9A-Z]{26}$", message = "Invalid ULID format") widgetId: String,
        @Context securityContext: SecurityContext
    ): Response {
        val userId = securityContext.userPrincipal.name
        logger.info("Fetching widget: userId={}, widgetId={}", userId, widgetId)
        
        val widget = widgetService.getWidget(userId, widgetId)
        return Response.ok(widget).build()
    }

    @GET
    fun listWidgets(
        @QueryParam("category") category: WidgetCategory?,
        @QueryParam("page") @DefaultValue("0") @Min(0) page: Int,
        @QueryParam("size") @DefaultValue("20") @Min(1) size: Int,
        @QueryParam("sort") @DefaultValue("createdAt") sortField: String,
        @QueryParam("direction") @DefaultValue("DESC") sortDirection: String,
        @Context securityContext: SecurityContext
    ): Response {
        val userId = securityContext.userPrincipal.name
        logger.info("Listing widgets: userId={}, category={}, page={}, size={}", userId, category, page, size)
        
        val validatedSize = size.coerceIn(1, maxPageSize)
        val direction = when (sortDirection.uppercase()) {
            "ASC" -> Sort.Direction.Ascending
            else -> Sort.Direction.Descending
        }

        val widgets = widgetService.listWidgets(userId, category, page, validatedSize, sortField, direction)
        return Response.ok(widgets).build()
    }

    @DELETE
    @Path("/{widgetId}")
    fun deleteWidget(
        @PathParam("widgetId") @NotBlank @Pattern(regexp = "^[0-9A-Z]{26}$", message = "Invalid ULID format") widgetId: String,
        @Context securityContext: SecurityContext
    ): Response {
        val userId = securityContext.userPrincipal.name
        logger.info("Deleting widget: userId={}, widgetId={}", userId, widgetId)
        
        widgetService.deleteWidget(userId, widgetId)
        return Response.noContent().build()
    }
} 