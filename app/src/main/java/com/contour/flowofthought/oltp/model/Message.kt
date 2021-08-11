package com.contour.flowofthought.oltp.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.contour.flowofthought.oltp.model.parcelable.MessageParcel


@Entity(
    indices = [
        Index(
            value = ["thoughtId"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Thought::class,
            parentColumns = ["id"],
            childColumns = ["thoughtId"],
            onDelete = CASCADE
        )
    ]
)
data class Message(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long,
    @ColumnInfo(name = "thoughtId")
    var thoughtId: Long,
    @ColumnInfo(name = "message")
    var text: String
) {
    constructor(message: MessageParcel) : this(message.id, message.thoughtId, message.text)
}