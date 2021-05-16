package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.PictureOfDay

//Database object called DatabasePictureOfDay

@Entity
data class DatabasePictureOfDay constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mediaType: String,
    val title: String,
    val url: String
)


//Extension function which converts from database objects to domain objects
fun DatabasePictureOfDay.asDomainModel(): PictureOfDay {
    return PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url

    )
}

//Extension function that converts from data transfer objects to database objects
fun PictureOfDay.asDatabaseModel(): DatabasePictureOfDay {
    return DatabasePictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}

