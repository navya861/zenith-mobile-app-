package com.zenith26.fest.ui.theme

import androidx.compose.ui.graphics.Color

// Brand Purple Palette
val Brand50 = Color(0xFFF5F3FF)
val Brand100 = Color(0xFFEDE9FE)
val Brand200 = Color(0xFFDDD6FE)
val Brand500 = Color(0xFF8B5CF6)
val Brand600 = Color(0xFF7C3AED)
val Brand700 = Color(0xFF6D28D9)
val Brand800 = Color(0xFF5B21B6)
val Brand900 = Color(0xFF4C1D95)

// Fest Accent Palette
val FestCoral = Color(0xFFF43F5E)
val FestAmber = Color(0xFFF59E0B)
val FestEmerald = Color(0xFF10B981)
val FestCyan = Color(0xFF06B6D4)

// Slate Neutrals Palette
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)

// Status & Badge Colors
val Rose50 = Color(0xFFFFF1F2)
val Rose100 = Color(0xFFFFE4E6)
val Rose200 = Color(0xFFFECDD3)
val Rose300 = Color(0xFFFDA4AF)
val Rose600 = Color(0xFFE11D48)
val Rose700 = Color(0xFFBE123C)
val Rose800 = Color(0xFF9F1239)
val Rose900 = Color(0xFF881337)

val Emerald50 = Color(0xFFECFDF5)
val Emerald100 = Color(0xFFD1FAE5)
val Emerald200 = Color(0xFFA7F3D0)
val Emerald300 = Color(0xFF6EE7B7)
val Emerald600 = Color(0xFF059669)
val Emerald800 = Color(0xFF065F46)
val Emerald900 = Color(0xFF064E3B)

val Amber50 = Color(0xFFFFFBEB)
val Amber100 = Color(0xFFFEF3C7)
val Amber200 = Color(0xFFFDE68A)
val Amber800 = Color(0xFF92400E)

val Blue100 = Color(0xFFDBEAFE)
val Blue200 = Color(0xFFBFDBFE)
val Blue800 = Color(0xFF1E40AF)

val Pink100 = Color(0xFFFCE7F3)
val Pink200 = Color(0xFFFBCFE8)
val Pink800 = Color(0xFF9D174D)

val Purple100 = Color(0xFFF3E8FF)
val Purple200 = Color(0xFFE9D5FF)
val Purple800 = Color(0xFF6B21A8)

// Category Badge Color Palette Helper
data class CategoryColorStyle(
    val background: Color,
    val text: Color,
    val border: Color
)

fun getCategoryColorStyle(category: String): CategoryColorStyle {
    return when (category.trim()) {
        "Technical" -> CategoryColorStyle(
            background = Blue100,
            text = Blue800,
            border = Blue200
        )
        "Cultural" -> CategoryColorStyle(
            background = Pink100,
            text = Pink800,
            border = Pink200
        )
        "Sports" -> CategoryColorStyle(
            background = Emerald100,
            text = Emerald800,
            border = Emerald200
        )
        "Gaming" -> CategoryColorStyle(
            background = Purple100,
            text = Purple800,
            border = Purple200
        )
        "Workshop" -> CategoryColorStyle(
            background = Amber100,
            text = Amber800,
            border = Amber200
        )
        else -> CategoryColorStyle(
            background = Slate100,
            text = Slate800,
            border = Slate200
        )
    }
}
