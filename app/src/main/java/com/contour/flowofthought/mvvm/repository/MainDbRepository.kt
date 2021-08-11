package com.contour.flowofthought.mvvm.repository

import com.contour.flowofthought.oltp.MainDB
import com.contour.flowofthought.oltp.model.Image
import com.contour.flowofthought.oltp.model.Message
import com.contour.flowofthought.oltp.model.Thought

class MainDbRepository {
    private val MainDb by lazy { MainDB.INSTANCE }

    fun saveThought(vararg thought: Thought) {
        MainDb.thoughtDao().upsertAll(*thought)
    }

    fun observeThoughts() = MainDb.thoughtDao().getLiveAll()

    fun observeThoughtById(id: Long) = MainDb.thoughtDao().getLiveById(id)

    fun queryThoughtById(id: Long) = MainDb.thoughtDao().getById(id)

    fun removeThought(vararg thought: Thought) {
        MainDb.thoughtDao().delete(*thought)
    }

    fun removeThoughtById(id: Long) {
        MainDb.thoughtDao().deleteById(id)
    }

    fun saveMessage(vararg message: Message) {
        MainDb.messageDao().upsertAll(*message)
    }

    fun observeMessages() = MainDb.messageDao().getLiveAll()

    fun observeMessagesByThoughtId(id: Long) = MainDb.messageDao().getLiveAllByThoughtId(id)

    fun observeFirstMessagesByThoughtId() = MainDb.messageDao().getLiveFirstByThoughtId()

    fun removeMessage(vararg message: Message) {
        MainDb.messageDao().delete(*message)
    }
}