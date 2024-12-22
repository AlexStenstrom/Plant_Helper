package se.gu.planter.roomDb

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.Serializable

/**
 * Data class representing a plant entity in the database.
 */
@Entity
data class Plant(
    @PrimaryKey val name : String, // Unique identifier for the plant
    var scientificName: String,
    var description: String,
    var wateringComment: String, // Specific comments about watering needs
    var nutritionComment: String, // Specific comments about nutrition needs
    @TypeConverters(FloatPairConverter::class) // Use FloatPairConverter for this property
    var wateringInterval: Pair<Float, Float>?, // Watering interval represented as a pair of floats (e.g., min days, max days)
    @TypeConverters(FloatPairConverter::class) // Use FloatPairConverter for this property
    var nutritionInterval: Pair<Float, Float>?, // Nutrition/fertilizing interval
    var sunlightDetails: String, // Details about sunlight requirements
    var mainImage: String // URI or path to the main image of the plant

    ) : Serializable { // The plant is serializable to allow passing between activities.

    companion object {
        val DEFAULT_IMAGE = ("android.resource://se.gu.planter/mipmap/plant_placeholder_foreground")
        // Default image for plants
    }
}

/**
 * Type converter for converting between Pair<Float, Float> and String for Room.
 */
class FloatPairConverter {
    @TypeConverter
    fun fromPair(pair: Pair<Float, Float>?): String? {
        return pair?.let { "${it.first},${it.second}" }
    }

    @TypeConverter
    fun toPair(value: String?): Pair<Float, Float>? {
        return value?.split(",")?.let {
            if (it.size == 2) { // Check if there are two values
                try {
                    Pair(it[0].toFloat(), it[1].toFloat())
                } catch (e: NumberFormatException) {
                    null // Handle invalid number format
                }
            } else {
                null // Handle cases with less or more than two values
            }
        }
    }
}



