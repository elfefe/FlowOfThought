package com.contour.flowofthought.oltp

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.contour.flowofthought.activity.application.BaseApplication
import com.contour.flowofthought.oltp.dao.MessageDAO
import com.contour.flowofthought.oltp.dao.ThoughtDAO
import com.contour.flowofthought.oltp.model.Image
import com.contour.flowofthought.oltp.model.Message
import com.contour.flowofthought.oltp.model.Thought

@Database(
    entities = [
        Thought::class,
        Message::class
    ], version = 4
)
@TypeConverters(Converters::class)
abstract class MainDB: RoomDatabase() {
    abstract fun thoughtDao(): ThoughtDAO
    abstract fun messageDao(): MessageDAO

    companion object {
        private const val DB_NAME = "main_db"

        val INSTANCE by lazy { instance() }

        @Synchronized
        private fun instance(): MainDB =
            Room
                .databaseBuilder(BaseApplication.INSTANCE, MainDB::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}