package com.recondex.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Tipografía redondeada / bold, tono juguetón tipo "app de lecciones"
val RecoNDexTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
)
