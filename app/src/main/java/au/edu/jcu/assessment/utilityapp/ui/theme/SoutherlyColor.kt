package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.ui.graphics.Color
import au.edu.jcu.assessment.utilityapp.model.UvLevel

// Light palette – white paper with near-black ink (reference design, light variant)
val Paper = Color(0xFFFFFFFF)
val PillLight = Color(0xFFF3F4F6)
val InkLight = Color(0xFF111214)
val MutedLight = Color(0xFF8A8F98)
val OutlineLight = Color(0xFFE3E5E8)

// Dark palette – soft black with light ink (reference design, dark variant)
val Night = Color(0xFF141416)
val PillDark = Color(0xFF26272B)
val PillDarkSelected = Color(0xFF36373D)
val InkDark = Color(0xFFF4F5F7)
val MutedDark = Color(0xFF9A9FA8)
val OutlineDark = Color(0xFF34363B)

// Accents taken from the 3D weather illustrations
val StormYellow = Color(0xFFFFD84D)
val RainBlue = Color(0xFF3FA9FF)
val GlowBlue = Color(0xFF3D7BFF)
val StarMint = Color(0xFF7CF0C8)

// UV category colours (WHO / Cancer Council Australia scale)
val UvLow = Color(0xFF3DBB6B)
val UvModerate = Color(0xFFF2B705)
val UvHigh = Color(0xFFF28C28)
val UvVeryHigh = Color(0xFFE5484D)
val UvExtreme = Color(0xFF8E4EC6)

fun uvColor(level: UvLevel): Color = when (level) {
    UvLevel.LOW -> UvLow
    UvLevel.MODERATE -> UvModerate
    UvLevel.HIGH -> UvHigh
    UvLevel.VERY_HIGH -> UvVeryHigh
    UvLevel.EXTREME -> UvExtreme
}