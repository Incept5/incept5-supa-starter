package org.incept5.api.modules.user.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import org.incept5.api.modules.user.domain.User
import java.util.*

@ApplicationScoped
class UserRepository : PanacheRepositoryBase<User, UUID> {
    fun findByEmail(email: String): User? = find("email", email).firstResult()
} 