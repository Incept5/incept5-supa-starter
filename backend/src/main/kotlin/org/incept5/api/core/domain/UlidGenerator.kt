package org.incept5.api.core.domain

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Utility class for generating and handling ULIDs
 */
object UlidGenerator {
    fun generate(): String = UlidCreator.getUlid().toString()
}