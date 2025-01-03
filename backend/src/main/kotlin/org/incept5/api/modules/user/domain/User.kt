package org.incept5.api.modules.user.domain

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "users", schema = "auth")
class User {
    @Id
    @Column(name = "id")
    var id: UUID = UUID.randomUUID()

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "role")
    var role: String? = null

    @Column(name = "email_confirmed_at")
    var emailConfirmedAt: Instant? = null

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()

    @Column(name = "is_super_admin")
    var isSuperAdmin: Boolean = false

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
} 