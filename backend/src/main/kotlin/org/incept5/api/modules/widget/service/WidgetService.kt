package org.incept5.api.modules.widget.service

import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.incept5.api.core.error.InvalidRequestException
import org.incept5.api.core.error.OptimisticLockException
import org.incept5.api.core.error.ResourceNotFoundException
import org.incept5.api.modules.widget.domain.Widget
import org.incept5.api.modules.widget.domain.WidgetCategory
import org.incept5.api.modules.widget.dto.CreateWidgetRequest
import org.incept5.api.modules.widget.dto.PagedWidgetResponse
import org.incept5.api.modules.widget.dto.UpdateWidgetRequest
import org.incept5.api.modules.widget.dto.WidgetResponse
import org.incept5.api.modules.widget.repository.WidgetRepository
import org.slf4j.LoggerFactory
import java.util.UUID

@ApplicationScoped
class WidgetService(private val widgetRepository: WidgetRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val allowedSortFields = setOf("createdAt", "updatedAt", "description", "category", "level")

    private fun parseUserId(userId: String): UUID {
        return try {
            UUID.fromString(userId)
        } catch (e: IllegalArgumentException) {
            throw InvalidRequestException("Invalid user ID format")
        }
    }

    private fun validateSortField(field: String): String {
        if (!allowedSortFields.contains(field)) {
            throw InvalidRequestException("Invalid sort field: $field. Allowed fields are: ${allowedSortFields.joinToString()}")
        }
        return field
    }

    @Transactional
    fun createWidget(userId: String, request: CreateWidgetRequest): WidgetResponse {
        logger.info("Creating widget: userId={}", userId)
        logger.debug("Widget creation details: {}", request)

        val userUUID = parseUserId(userId)
        val widget = Widget(
            userId = userUUID,
            description = request.description,
            category = request.category,
            level = request.level
        )

        widgetRepository.persist(widget)
        logger.info("Widget created successfully: id={}", widget.id)
        return WidgetResponse.from(widget)
    }

    @Transactional
    fun updateWidget(userId: String, widgetId: String, request: UpdateWidgetRequest): WidgetResponse {
        logger.info("Updating widget: userId={}, widgetId={}", userId, widgetId)
        logger.debug("Widget update details: {}", request)

        val userUUID = parseUserId(userId)
        val widget = widgetRepository.findByIdAndUserId(widgetId, userUUID)
            ?: throw ResourceNotFoundException("Widget not found")

        if (widget.version != request.version) {
            throw OptimisticLockException()
        }

        request.description?.let { widget.description = it }
        request.category?.let { widget.category = it }
        request.level?.let { widget.level = it }

        widgetRepository.persist(widget)
        logger.info("Widget updated successfully: id={}", widget.id)
        return WidgetResponse.from(widget)
    }

    fun getWidget(userId: String, widgetId: String): WidgetResponse {
        logger.info("Fetching widget: userId={}, widgetId={}", userId, widgetId)
        
        return widgetRepository.findByIdAndUserId(widgetId, parseUserId(userId))
            ?.let { WidgetResponse.from(it) }
            ?: throw ResourceNotFoundException("Widget not found")
    }

    fun listWidgets(
        userId: String,
        category: WidgetCategory?,
        page: Int,
        size: Int,
        sortField: String = "createdAt",
        sortDirection: Sort.Direction = Sort.Direction.Descending
    ): PagedWidgetResponse {
        logger.info("Listing widgets: userId={}, category={}, page={}, size={}", userId, category, page, size)

        val validatedSortField = validateSortField(sortField)
        val sort = Sort.by(validatedSortField, sortDirection)
        val userUUID = parseUserId(userId)
        
        val (widgets, total) = if (category != null) {
            logger.debug("Filtering by category: {}", category)
            Pair(
                widgetRepository.findByUserIdAndCategory(userUUID, category, page, size, sort),
                widgetRepository.countByUserIdAndCategory(userUUID, category)
            )
        } else {
            Pair(
                widgetRepository.findByUserId(userUUID, page, size, sort),
                widgetRepository.countByUserId(userUUID)
            )
        }

        val totalPages = (total + size - 1) / size
        
        return PagedWidgetResponse(
            content = widgets.map { WidgetResponse.from(it) },
            totalElements = total,
            totalPages = totalPages.toInt(),
            pageNumber = page,
            pageSize = size,
            hasNext = page < totalPages - 1,
            hasPrevious = page > 0
        )
    }

    @Transactional
    fun deleteWidget(userId: String, widgetId: String) {
        logger.info("Deleting widget: userId={}, widgetId={}", userId, widgetId)
        
        val userUUID = parseUserId(userId)
        val widget = widgetRepository.findByIdAndUserId(widgetId, userUUID)
            ?: throw ResourceNotFoundException("Widget not found")
        
        widgetRepository.delete(widget)
        logger.info("Widget deleted successfully: id={}", widget.id)
    }
} 