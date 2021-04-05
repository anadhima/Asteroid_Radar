package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.AsteroidFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class AsteroidApiStatus { LOADING, ERROR, DONE }
class MainViewModel(application: Application) : ViewModel() {

    private lateinit var asteroidListLiveData: LiveData<List<Asteroid>>
    private val database = getDatabase(application)
    private var asteroidRepository = AsteroidRepository(database)


    //This list will be observed in RecyclerView
    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList


    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val asteroidListObserver = Observer<List<Asteroid>> {
        //Update new list to RecyclerView
        _asteroidList.value = it
    }


    init {
        refreshAsteroid()
        refreshPictureOfDay()
        updateFilter(AsteroidFilter.SHOW_ALL)

    }

    val pictureOfDay = asteroidRepository.pictureOfDay

    private fun refreshAsteroid() {
        _status.value = AsteroidApiStatus.LOADING
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                asteroidRepository.refreshAsteroid()
                _status.postValue(AsteroidApiStatus.DONE)
            } catch (ex: Exception) {
                _status.postValue(AsteroidApiStatus.ERROR)
            }
        }
    }

    private fun refreshPictureOfDay() {
        _status.value = AsteroidApiStatus.LOADING
        viewModelScope.launch {
            try {
                asteroidRepository.refreshPictureOfDay()
                _status.postValue(AsteroidApiStatus.DONE)
            } catch (ex: Exception) {

                _status.value = AsteroidApiStatus.ERROR
            }
        }

    }


    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }


    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }


    fun updateFilter(filter: AsteroidFilter) {
        //Observe the new filtered LiveData
        asteroidListLiveData = asteroidRepository.getAsteroidSelection(filter)
        asteroidListLiveData.observeForever(asteroidListObserver)
    }

    override fun onCleared() {
        super.onCleared()
        //Clear observers
        asteroidListLiveData.removeObserver(asteroidListObserver)
    }


}

