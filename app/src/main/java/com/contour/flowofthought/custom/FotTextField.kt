package com.contour.flowofthought.custom

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import com.contour.richtext.span.MyBulletSpan
import kotlinx.coroutines.flow.collect

object FotTextField {
    @Composable
    fun fotTextField(
        modifier: Modifier = Modifier
    ) {
        var text by remember {
            mutableStateOf("")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                colors = TextFieldDefaults.textFieldColors(textColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        val spanBullet = MyBulletSpan(android.graphics.Color.BLUE, 5, 5)

        SpannableString("").apply {
            setSpan(spanBullet, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        AnnotatedString.Builder().pushStyle(SpanStyle(textGeometricTransform = TextGeometricTransform()))
        VisualTransformation.None.filter(AnnotatedString(text, ParagraphStyle()))
    }
}