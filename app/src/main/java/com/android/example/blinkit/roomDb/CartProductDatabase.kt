package com.android.example.blinkit.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [CartProducts::class], version = 1, exportSchema = false) // schema creates the json file of our database
abstract class CartProductDatabase:RoomDatabase() {                          // the schema of our database creates the json file

    abstract fun cartProductDao():CartProductDao

    companion object {

        @Volatile
        var INSTANCE : CartProductDatabase?=null

        fun getDatabaseInstance(context: Context):CartProductDatabase {
            val tempInstance= INSTANCE
            if(tempInstance!=null) {
               return tempInstance
            }

            synchronized(this) {
                val roomDb = Room.databaseBuilder(context,CartProductDatabase::class.java,"CartProducts").allowMainThreadQueries().build()
                INSTANCE=roomDb
                return roomDb

            }

        }


    }



}