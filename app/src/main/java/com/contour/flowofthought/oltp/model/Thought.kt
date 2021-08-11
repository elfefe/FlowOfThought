package com.contour.flowofthought.oltp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.contour.flowofthought.oltp.model.parcelable.ThoughtParcel

@Entity
data class Thought(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long,
    @ColumnInfo(name = "title")
    var title: String
) {
    constructor(thought: ThoughtParcel): this(thought.id, thought.title)
}
