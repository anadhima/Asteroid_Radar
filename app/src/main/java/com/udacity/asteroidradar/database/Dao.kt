package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {

    @Query("select * from databaseasteroid ORDER BY date(closeApproachDate) DESC")
    fun getAllAsteroid(): LiveData<List<DatabaseAsteroid>>


     //TODO (01) have an other check on this later


    @Query("select * from databaseasteroid WHERE date(closeApproachDate) >  date('now', 'start of day', 'weekday 6', '-7 day') ORDER BY date(closeApproachDate) DESC")
    fun getWeekAsteroid(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid WHERE date(closeApproachDate) < date('now') ORDER BY date(closeApproachDate) DESC")
    fun getTodayAsteroid(): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM databaseasteroid WHERE date(closeApproachDate) < date('now')")
    fun deleteBeforeToday(today: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)

}


@Dao
interface PictureOfDayDao{

    @Query("select * from databasepictureofday")
    fun getPictureOfDay(): LiveData<List<DatabasePictureOfDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pictureOfDay: DatabasePictureOfDay)

    @Query("DELETE FROM  databasepictureofday")
    fun clear()

}
