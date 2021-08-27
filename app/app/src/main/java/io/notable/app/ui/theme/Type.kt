package io.notable.app.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.notable.app.R

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

private val Mulish = FontFamily(
    Font(R.font.mulish_light, FontWeight.W300),
    Font(R.font.mulish_regular, FontWeight.W400),
    Font(R.font.mulish_medium, FontWeight.W500),
    Font(R.font.mulish_semibold, FontWeight.W600),
    Font(R.font.mulish_bold, FontWeight.W700),
    Font(R.font.mulish_extrabold, FontWeight.W800),
    Font(R.font.mulish_black, FontWeight.W900),
)

val MulishTypography = Typography(
    h1 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W500,
        fontSize = 32.sp,
    ),
    h2 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W500,
        fontSize = 26.sp,
    ),
    h3 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W500,
        fontSize = 22.sp,
    ),
    h4 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 20.sp,
    ),
    h5 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
    ),
    h6 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
    ),
    subtitle1 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W500,
        fontSize = 15.sp,
    ),
    subtitle2 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
    ),
    body1 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = Mulish,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        color = Color.White
    ),
    caption = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    overline = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.W400,
        fontSize = 13.sp
    )
)