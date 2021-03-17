package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Annotate it with @Database, including entities and version
@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1)

//Create an abstract Database class that extends RoomDatabase
abstract class AsteroidDatabase : RoomDatabase() {
    abstract fun asteroidDao(): AsteroidDao
    abstract fun pictureOfDayDao(): PictureOfDayDao
}

//Define an INSTANCE variable to store the singleton
private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {

    //The code is synchronized so it’s thread safe
    synchronized(AsteroidDatabase::class.java) {

        //use ::INSTANCE.isInitialized to check if the variable has been initialized.
        // If it hasn't, then initialize it
        if (!::INSTANCE.isInitialized) {

            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroid"
            ).build()
        }
    }
    return INSTANCE
}
