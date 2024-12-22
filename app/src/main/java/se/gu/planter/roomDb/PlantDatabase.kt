package se.gu.planter.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The Room database for the application
 */
@Database(
    entities = [Plant::class, PlantEvent:: class],
    version = 1
)

/**
 * Type converters for the database.
 */
@TypeConverters(FloatPairConverter::class, EventTypeConverter::class)

/**
 * The Data Access Object for the database.
 */
abstract class PlantDatabase: RoomDatabase() {

    abstract val dao: RoomDao

}