package com.contour.flowofthought.custom

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import com.contour.flowofthought.R
import com.contour.flowofthought.custom.theme.isDarkTheme
import com.contour.richtext.RichEditText

class FotEditText(
    context: Context
) : RichEditText(context) {
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