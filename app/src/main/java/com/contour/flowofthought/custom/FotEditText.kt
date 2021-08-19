package com.contour.flowofthought.custom

import android.content.Context
import android.text.Editable
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.contour.flowofthought.R
import com.contour.flowofthought.custom.theme.isDarkTheme
import com.contour.flowofthought.oltp.model.Message
import com.contour.richtext.RichEditText
import com.contour.richtext.parser.HtmlParser

class FotEditText(
    context: Context
) : RichEditText(context) {
    companion object {
        @Composable
        fun compose(
            context: Context,
            modifier: Modifier = Modifier,
            onFocus: (RichEditText) -> Unit,
            afterTextChange: (Message) -> Unit,
            message: Message
        ) {
            AndroidView(
            {
                RichEditText(context).apply {
                    fromHtml(message.text)

                    onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            onFocus(this)
                        }
                    }

                    doAfterTextChanged { text ->
                        message.text = HtmlParser.toHtml(text)
                        afterTextChange(message)
                    }
                }
            },
            modifier = modifier)
        }
    }

    init {
        val density = resources.displayMetrics.density
        setPadding(
            (15 * density).toInt(),
            (5 * density).toInt(),
            (15 * density).toInt(),
            (5 * density).toInt()
        )

        setBackgroundColor(
            resources.getColor(
                R.color.transparent,
                null
            )
        )

        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.richedittext_border_light,
            null
        )

        setHintTextColor(
            resources.getColor(
                if (isDarkTheme) R.color.primary_variant_dark
                else R.color.primary_variant_light, null
            )
        )
        hint = resources.getString(R.string.enter_your_thought)

        setTextColor(
            resources.getColor(
                if (isDarkTheme) R.color.text_primary_dark
                else R.color.text_primary_light, null
            )
        )
    }
}