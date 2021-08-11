package com.contour.flowofthought.oltp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Markdown(
    var text: String = "",
    val images: ArrayList<Image> = arrayListOf(),
    val buttons: ArrayList<Int> = arrayListOf(),
    val quotes: ArrayList<Int> = arrayListOf()
) : Parcelable
