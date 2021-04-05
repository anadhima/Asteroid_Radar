package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {

    @Query("select * from databaseasteroid ORDER BY closeApproachDate DESC")
    fun getAllAsteroid(): LiveData<List<DatabaseAsteroid>>


    @Query("SELECT * FROM databaseasteroid where closeApproachDate between :startDay and :endDay order by closeApproachDate DESC")
    fun getWeekAsteroid(startDay: String,endDay: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = :today")
    fun getTodayAsteroid (today:String): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate < :today")
    fun deleteBeforeToday(today: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)

}


@Dao
interface PictureOfDayDao{

    @Query("select * from databasepictureofday")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pictureOfDay: DatabasePictureOfDay)

    @Query("DELETE FROM  databasepictureofday")
    fun clear()

}
