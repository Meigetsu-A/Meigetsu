package com.meigetsu.core.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.meigetsu.core.ui.R
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val CormorantGaramond = FontFamily(
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider, weight = FontWeight.Light, style = FontStyle.Italic)
)
val Syne = FontFamily(
    Font(googleFont = GoogleFont("Syne"), fontProvider = provider, weight = FontWeight.ExtraBold)
)
val DMMono = FontFamily(
    Font(googleFont = GoogleFont("DM Mono"), fontProvider = provider, weight = FontWeight.Normal)
)
val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 32.sp, color = PrimaryText),
    headlineMedium = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 28.sp, color = PrimaryText),
    titleLarge = TextStyle(fontFamily = Syne, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, letterSpacing = 2.sp, color = PrimaryText),
    bodyLarge = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 18.sp, color = PrimaryText),
    bodyMedium = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 16.sp, color = SecondaryText),
    labelLarge = TextStyle(fontFamily = DMMono, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = SecondaryText),
    labelSmall = TextStyle(fontFamily = DMMono, fontWeight = FontWeight.Normal, fontSize = 12.sp, color = MutedText)
)
