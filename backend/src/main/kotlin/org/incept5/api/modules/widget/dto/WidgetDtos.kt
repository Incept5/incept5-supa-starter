package org.incept5.api.modules.widget.dto

import jakarta.validation.constraints.*
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.incept5.api.modules.widget.domain.Widget
import org.incept5.api.modules.widget.domain.WidgetCategory
import java.time.OffsetDateTime

@Schema(description = "Request object for creating a new widget")
data class CreateWidgetRequest(
    @field:Schema(description = "Description of the widget", example = "A high-performance widget for advanced users")
    @field:NotBlank(message = "Description is required")
    @field:Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    val description: String,

    @field:Schema(description = "Category of the widget", example = "ADVANCED", enumeration = ["BASIC", "ADVANCED", "PREMIUM", "CUSTOM"])
    @field:NotNull(message = "Category is required")
    val category: WidgetCategory,

    @field:Schema(description = "Level of the widget (1-100)", example = "50", minimum = "1", maximum = "100")
    @field:Min(value = 1, message = "Level must be at least 1")
    @field:Max(value = 100, message = "Level must not exceed 100")
    val level: Int
)

@Schema(description = "Request object for updating an existing widget")
data class UpdateWidgetRequest(
    @field:Schema(description = "New description of the widget", example = "An updated high-performance widget")
    @field:Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    val description: String?,

    @field:Schema(description = "New category of the widget", example = "PREMIUM", enumeration = ["BASIC", "ADVANCED", "PREMIUM", "CUSTOM"])
    val category: WidgetCategory?,

    @field:Schema(description = "New level of the widget (1-100)", example = "75", minimum = "1", maximum = "100")
    @field:Min(value = 1, message = "Level must be at least 1")
    @field:Max(value = 100, message = "Level must not exceed 100")
    val level: Int?,

    @field:Schema(description = "Version for optimistic locking", example = "1")
    @field:NotNull(message = "Version is required for optimistic locking")
    val version: Long
)

@Schema(description = "Response object representing a widget")
data class WidgetResponse(
    @field:Schema(description = "Unique identifier of the widget", example = "01H9XY7JVZW4QX5TZMENVSHR8K")
    @field:NotNull
    val id: String,
    
    @field:Schema(description = "ID of the user who owns the widget", example = "123e4567-e89b-12d3-a456-426614174000")
    @field:NotNull
    val userId: String,
    
    @field:Schema(description = "Description of the widget", example = "A high-performance widget for advanced users")
    @field:NotNull
    @field:Size(min = 3, max = 1000)
    val description: String,
    
    @field:Schema(description = "Category of the widget", example = "ADVANCED", enumeration = ["BASIC", "ADVANCED", "PREMIUM", "CUSTOM"])
    @field:NotNull
    val category: String,
    
    @field:Schema(description = "Level of the widget (1-100)", example = "50", minimum = "1", maximum = "100")
    @field:Min(1)
    @field:Max(100)
    val level: Int,
    
    @field:Schema(description = "Timestamp when the widget was created", example = "2023-08-15T14:30:00.000Z")
    @field:NotNull
    val createdAt: OffsetDateTime,
    
    @field:Schema(description = "Timestamp when the widget was last updated", example = "2023-08-15T14:30:00.000Z")
    @field:NotNull
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun from(widget: Widget) = WidgetResponse(
            id = widget.id,
            userId = widget.userId.toString(),
            description = widget.description,
            category = widget.category.name,
            level = widget.level,
            createdAt = widget.createdAt,
            updatedAt = widget.updatedAt
        )
    }
}

@Schema(description = "Response object for paginated widget results")
data class PagedWidgetResponse(
    @field:Schema(description = "List of widgets in the current page")
    @field:NotNull
    val content: List<WidgetResponse>,
    
    @field:Schema(description = "Total number of widgets matching the query", example = "42")
    @field:NotNull
    val totalElements: Long,
    
    @field:Schema(description = "Total number of pages available", example = "3")
    @field:NotNull
    @field:Min(0)
    val totalPages: Int,
    
    @field:Schema(description = "Current page number (0-based)", example = "0")
    @field:NotNull
    @field:Min(0)
    val pageNumber: Int,
    
    @field:Schema(description = "Number of items per page", example = "20")
    @field:NotNull
    @field:Min(1)
    val pageSize: Int,
    
    @field:Schema(description = "Whether there is a next page available", example = "true")
    @field:NotNull
    val hasNext: Boolean,
    
    @field:Schema(description = "Whether there is a previous page available", example = "false")
    @field:NotNull
    val hasPrevious: Boolean
) 