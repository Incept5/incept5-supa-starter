package org.incept5.api.modules.widget.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import org.incept5.api.modules.widget.domain.Widget
import org.incept5.api.modules.widget.domain.WidgetCategory
import java.util.UUID

@ApplicationScoped
class WidgetRepository : PanacheRepository<Widget> {
    
    fun findByUserId(userId: UUID, page: Int, size: Int, sort: Sort): List<Widget> =
        find("userId = ?1", sort, userId)
            .page(Page.of(page, size))
            .list()

    fun countByUserId(userId: UUID): Long =
        count("userId = ?1", userId)

    fun findByUserIdAndCategory(userId: UUID, category: WidgetCategory, page: Int, size: Int, sort: Sort): List<Widget> =
        find("userId = ?1 and category = ?2", sort, userId, category)
            .page(Page.of(page, size))
            .list()

    fun countByUserIdAndCategory(userId: UUID, category: WidgetCategory): Long =
        count("userId = ?1 and category = ?2", userId, category)

    fun findByIdAndUserId(id: String, userId: UUID): Widget? =
        find("id = ?1 and userId = ?2", id, userId).firstResult()
} 