package org.incept5.api.modules.widget.dto

import jakarta.validation.constraints.*
import org.incept5.api.modules.widget.domain.Widget
import org.incept5.api.modules.widget.domain.WidgetCategory
import java.time.OffsetDateTime

data class CreateWidgetRequest(
    @field:NotBlank(message = "Description is required")
    @field:Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    val description: String,

    @field:NotNull(message = "Category is required")
    val category: WidgetCategory,

    @field:Min(value = 1, message = "Level must be at least 1")
    @field:Max(value = 100, message = "Level must not exceed 100")
    val level: Int
)

data class UpdateWidgetRequest(
    @field:Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    val description: String?,

    val category: WidgetCategory?,

    @field:Min(value = 1, message = "Level must be at least 1")
    @field:Max(value = 100, message = "Level must not exceed 100")
    val level: Int?,

    @field:NotNull(message = "Version is required for optimistic locking")
    val version: Long
)

data class WidgetResponse(
    @field:NotNull
    val id: String,
    
    @field:NotNull
    val userId: String,
    
    @field:NotNull
    @field:Size(min = 3, max = 1000)
    val description: String,
    
    @field:NotNull
    val category: WidgetCategory,
    
    @field:Min(1)
    @field:Max(100)
    val level: Int,
    
    @field:NotNull
    val createdAt: OffsetDateTime,
    
    @field:NotNull
    val updatedAt: OffsetDateTime,
    
    @field:NotNull
    val version: Long
) {
    companion object {
        fun from(widget: Widget) = WidgetResponse(
            id = widget.id,
            userId = widget.userId.toString(),
            description = widget.description,
            category = widget.category,
            level = widget.level,
            createdAt = widget.createdAt,
            updatedAt = widget.updatedAt,
            version = widget.version
        )
    }
}

data class PagedWidgetResponse(
    @field:NotNull
    val content: List<WidgetResponse>,
    
    @field:NotNull
    val totalElements: Long,
    
    @field:NotNull
    @field:Min(0)
    val totalPages: Int,
    
    @field:NotNull
    @field:Min(0)
    val pageNumber: Int,
    
    @field:NotNull
    @field:Min(1)
    val pageSize: Int,
    
    @field:NotNull
    val hasNext: Boolean,
    
    @field:NotNull
    val hasPrevious: Boolean
) 