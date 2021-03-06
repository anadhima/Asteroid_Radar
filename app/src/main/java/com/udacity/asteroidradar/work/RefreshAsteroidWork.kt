package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException


class RefreshAsteroidWork (appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params){

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {

            repository.refreshAsteroid()
            repository.refreshPictureOfDay()
            repository.deleteBeforeToday()

            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}

