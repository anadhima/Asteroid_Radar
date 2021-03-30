package com.udacity.asteroidradar.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
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
        Transformations.map(database.pictureOfDayDao().getPictureOfDay()) {
            it?.asDomainModel()
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

            /* (AsteroidFilter.SHOW_WEEK) -> Transformations.map(database.asteroidDao().getWeekAsteroid(
                  getStartDateFormatted(), getEndDateFormatted())){
                  it.asDomainModel()*/

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
        /* _status.value = Status.LOADING
         withContext(Dispatchers.IO) {
             try {*/

        val asteroid = AsteroidApi.retrofitService.getAllAsteroids(
            startDate = getStartDateFormatted(),
            endDate = getEndDateFormatted()
        ).await()
        val resultJSONObject = JSONObject(asteroid)
        val resultParsed = parseAsteroidsJsonResult(resultJSONObject)
        database.asteroidDao().insertAll(*resultParsed.asDatabaseModel())
/*
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }*/
    }

    //Method to refresh PictureOfTheDay Offline Cache
    suspend fun refreshPictureOfDay() {
        /*_status.value = Status.LOADING
        withContext(Dispatchers.IO) {
            try {*/
        val pictureOfDay = AsteroidApi.retrofitService.getImageOfDay().await()
        if (pictureOfDay.mediaType == "image") {
            database.pictureOfDayDao().clear()
            database.pictureOfDayDao().insertAll(pictureOfDay.asDatabaseModel())
            /*_status.value = Status.DONE
        }
    } catch (e: Exception) {
        _status.value = Status.ERROR
    }
}*/
        }
    }

    suspend fun deleteBeforeToday() {
        withContext(Dispatchers.IO) {
            database.asteroidDao().deleteBeforeToday(getStartDateFormatted())
        }
    }


}
