package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import au.edu.jcu.assessment.utilityapp.R

/** Poppins (SIL Open Font License) – the geometric sans used in the reference design. */
val Poppins = FontFamily(
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
)

private val base = Typography()

val SoutherlyTypography = Typography(
    headlineMedium = base.headlineMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 15.sp),
)