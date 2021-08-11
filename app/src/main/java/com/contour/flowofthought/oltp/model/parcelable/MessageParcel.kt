package com.contour.flowofthought.oltp.model.parcelable

import android.os.Parcelable
import com.contour.flowofthought.oltp.model.Markdown
import com.contour.flowofthought.oltp.model.Message
import kotlinx.parcelize.Parcelize

@Parcelize
class MessageParcel(
    var id: Long,
    var thoughtId: Long,
    var text: String
): Parcelable {
    constructor(message: Message): this(message.id, message.thoughtId, message.text)
}