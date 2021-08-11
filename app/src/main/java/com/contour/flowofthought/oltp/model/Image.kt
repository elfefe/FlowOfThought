package com.contour.flowofthought.oltp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var uri: String,
    var index: Int
): Parcelable
