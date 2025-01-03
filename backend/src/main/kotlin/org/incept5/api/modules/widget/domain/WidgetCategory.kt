package org.incept5.api.modules.widget.domain

enum class WidgetCategory {
    BASIC,
    ADVANCED,
    PREMIUM,
    CUSTOM;

    companion object {
        fun fromString(value: String): WidgetCategory = 
            values().find { it.name == value.uppercase() }
                ?: throw IllegalArgumentException("Invalid widget category: $value")
    }
} 