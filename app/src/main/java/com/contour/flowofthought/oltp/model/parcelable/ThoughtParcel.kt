package com.contour.flowofthought.oltp.model.parcelable

import android.os.Parcelable
import com.contour.flowofthought.oltp.model.Thought
import kotlinx.parcelize.Parcelize

@Parcelize
class ThoughtParcel(
    var id: Long,
    var title: String
) : Parcelable {
    constructor(thought: Thought): this(thought.id, thought.title)
}