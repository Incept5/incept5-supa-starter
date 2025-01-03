package org.incept5.api.modules.widget.domain

import org.incept5.api.core.domain.UlidGenerator
import java.time.OffsetDateTime
import java.util.UUID
import jakarta.persistence.*

@Entity
@Table(name = "widget", schema = "public")
class Widget {
    @Column(name = "user_id", nullable = false)
    lateinit var userId: UUID

    @Column(nullable = false)
    lateinit var description: String

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit var category: WidgetCategory

    @Column(nullable = false)
    var level: Int = 0

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()

    @Version
    var version: Long = 0

    @Id
    @Column(name = "id")
    var id: String = UlidGenerator.generate()

    constructor()  // Default constructor for Hibernate

    constructor(
        userId: UUID,
        description: String,
        category: WidgetCategory,
        level: Int
    ) {
        this.userId = userId
        this.description = description
        this.category = category
        this.level = level
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = OffsetDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Widget
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
} 