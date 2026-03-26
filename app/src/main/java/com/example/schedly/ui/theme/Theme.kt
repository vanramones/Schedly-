package com.example.schedly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.schedly.R

private val LightColorScheme = lightColorScheme(
    primary = SchedlyBlue,
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF49454F)
)

// Define the Font Families
val FrauncesFamily = FontFamily(
    Font(R.font.fraunces)
)

val InstrumentSansFamily = FontFamily(
    Font(R.font.instrument_sans),
    Font(R.font.instrument_sans, FontWeight.Bold)
)

// Now define the Typography object using these families
val SchedlyTypography = Typography(

    // H1/Display: Use Fraunces for the prominent title
    headlineLarge = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),

    // Body text: Use Instrument Sans for the description and button
    bodyLarge = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // Button text (will inherit from bodyLarge unless specified)
    labelLarge = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 20.sp
    )
)

@Composable
fun SchedlyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = SchedlyTypography,
        content = content
    )
}
