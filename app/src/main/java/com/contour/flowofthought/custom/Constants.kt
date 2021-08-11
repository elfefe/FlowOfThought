package com.contour.flowofthought.custom

import androidx.fragment.app.Fragment

object State {
    const val COLLAPSED = false
    const val EXPANDED = true
}

object Argument {
    const val THOUGHT_KEY = "thought_key"
    const val MESSAGE_KEY = "message_key"
}

object Pager {
    const val SLIDE_PAGE = 100
}

val Any.name: String
    get() {

        val spliced = javaClass.canonicalName?.split(".")?: listOf("")
        return if (spliced[spliced.size - 1] == "Companion") spliced[spliced.size - 2]
        else spliced[spliced.size - 1]
    }