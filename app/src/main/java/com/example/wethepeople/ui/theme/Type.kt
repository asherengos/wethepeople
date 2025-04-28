package com.example.wethepeople.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.wethepeople.R

// Define Font Families
val ArtySignatureFontFamily = FontFamily(
    Font(R.font.arty_signature, FontWeight.Normal)
    // Add bold/italic variants if available
)

val WeThePeopleFontFamily = FontFamily(
    Font(R.font.we_the_people, FontWeight.Normal)
    // Add bold/italic variants if available
)

// Temporarily use a direct font resource reference instead of XML
val RankSignatureFontFamily = FontFamily(
    Font(R.font.rank_signature, FontWeight.Normal)
)

val VoteCardSubjectFontFamily = FontFamily(
    Font(R.font.votecard_subject_font, FontWeight.Normal)
    // Add bold/italic variants if available
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
)