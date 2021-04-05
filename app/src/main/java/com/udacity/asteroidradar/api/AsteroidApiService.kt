package com.udacity.asteroidradar.api



import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


//Create a Filter enum that defines constants to match the query values our web service expects
enum class AsteroidFilter{SHOW_ALL, SHOW_WEEK, SHOW_TODAY}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//Using Retrofit Builder to create Retrofit Object with BASE_URL
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

//Using Retrofit Builder to create Retrofit Object with BASE_URL
private val feedRetrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    fun getAllAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Deferred<String>

    @GET("planetary/apod")
    fun getImageOfDay(@Query("api_key") apiKey: String = BuildConfig.API_KEY ):  Deferred<PictureOfDay>
}


object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
    val feedRetrofitService: AsteroidApiService by lazy { feedRetrofit.create(AsteroidApiService::class.java) }
}