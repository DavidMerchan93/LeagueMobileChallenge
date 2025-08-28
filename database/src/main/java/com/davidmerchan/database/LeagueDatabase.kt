package com.davidmerchan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davidmerchan.database.dao.PostDao
import com.davidmerchan.database.dao.UserDao
import com.davidmerchan.database.entities.PostEntity
import com.davidmerchan.database.entities.UserEntity

@Database(
    entities = [UserEntity::class, PostEntity::class],
    version = 1,
)
abstract class LeagueDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao

    companion object {
        const val DATABASE_NAME = "league_database"

        fun create(context: Context): LeagueDatabase {
            return Room.databaseBuilder(context, LeagueDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }

}
