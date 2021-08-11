package com.contour.flowofthought.oltp.dao

import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.LiveData
import androidx.room.*
import com.contour.flowofthought.oltp.model.Thought


@Dao
interface ThoughtDAO {
    @Query("SELECT * FROM Thought")
    fun getLiveAll(): LiveData<List<Thought>>

    @Query("SELECT * FROM Thought WHERE id = :id")
    fun getLiveById(id: Long): LiveData<Thought?>

    @Query("SELECT * FROM Thought WHERE id = :id")
    fun getById(id: Long): Thought?

    @Query("SELECT * FROM Thought WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<Thought>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg thought: Thought): List<Long>

    @Update
    fun updateAll(thought: List<Thought>)

    @Query("DELETE FROM Thought WHERE id = :id")
    fun deleteById(id: Long)

    @Delete
    fun delete(vararg thought: Thought)

    @Transaction
    fun upsertAll(vararg thought: Thought) {
        val insertResult: List<Long> = insertAll(*thought)
        val updateList: MutableList<Thought> = ArrayList()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(thought[i])
            }
        }
        if (updateList.isNotEmpty()) {
            updateAll(updateList)
        }
    }
}