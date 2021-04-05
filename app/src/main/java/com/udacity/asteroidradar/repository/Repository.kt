package com.udacity.asteroidradar.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabasePictureOfDay
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject




class AsteroidRepository(private val database: AsteroidDatabase) {



    val asteroid: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao().getAllAsteroid()) {
            it.asDomainModel()
        }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(getPicture()) {
            it?.asDomainModel()
        }
    private fun getPicture(): LiveData<DatabasePictureOfDay> {
        return database.pictureOfDayDao().getPictureOfDay()
    }



    // Apply the AsteroidFilter
    fun getAsteroidSelection(filter: AsteroidFilter): LiveData<List<Asteroid>> {
        return when (filter) {
            (AsteroidFilter.SHOW_ALL) -> Transformations.map(
                database.asteroidDao().getAllAsteroid()
            ) {
                it.asDomainModel()
            }
            (AsteroidFilter.SHOW_TODAY) -> Transformations.map(
                database.asteroidDao().getTodayAsteroid(
                    getStartDateFormatted()
                )
            ) {
                it.asDomainModel()
            }
            else -> {
                Transformations.map(
                    database.asteroidDao().getWeekAsteroid(
                        getStartDateFormatted(), getEndDateFormatted()
                    )
                ) {
                    it.asDomainModel()
                }
            }
        }
    }


    //Method to refresh Asteroid Offline Cache
    suspend fun refreshAsteroid() {
        val asteroid = AsteroidApi.feedRetrofitService.getAllAsteroids(
            startDate = getStartDateFormatted(),
            endDate = getEndDateFormatted()
        ).await()
        val resultJSONObject = JSONObject(asteroid)
        val resultParsed = parseAsteroidsJsonResult(resultJSONObject)
        database.asteroidDao().insertAll(*resultParsed.asDatabaseModel())

    }

    //Method to refresh PictureOfTheDay Offline Cache
    suspend fun refreshPictureOfDay() {
        val pictureOfDay = AsteroidApi.retrofitService.getImageOfDay().await()
        if (pictureOfDay.mediaType == "image") {
            database.pictureOfDayDao().clear()
            database.pictureOfDayDao().insertAll(pictureOfDay.asDatabaseModel())

        }
    }

    suspend fun deleteBeforeToday() {
        withContext(Dispatchers.IO) {
            database.asteroidDao().deleteBeforeToday(getStartDateFormatted())
        }
    }


}
