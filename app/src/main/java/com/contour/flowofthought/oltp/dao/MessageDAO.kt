package com.contour.flowofthought.oltp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.contour.flowofthought.oltp.model.Message
import com.contour.flowofthought.oltp.model.Thought

@Dao
interface MessageDAO {
    @Query("SELECT * FROM Message")
    fun getLiveAll(): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE thoughtId = :id")
    fun getLiveAllByThoughtId(id: Long): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE thoughtId = :id")
    fun getAllByThoughtId(id: Long): List<Message>

    @Query("SELECT * FROM Message GROUP BY thoughtId ORDER BY thoughtId DESC, id DESC")
    fun getLiveFirstByThoughtId(): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<Message>>

    @Insert(onConflict = IGNORE)
    fun insertAll(vararg message: Message): List<Long>

    @Delete
    fun delete(vararg message: Message)

    @Update
    fun updateAll(message: List<Message>)

    @Transaction
    fun upsertAll(vararg message: Message) {
        val insertResult: List<Long> = insertAll(*message)
        val updateList: MutableList<Message> = ArrayList()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(message[i])
            }
        }
        if (updateList.isNotEmpty()) {
            updateAll(updateList)
        }
    }
}