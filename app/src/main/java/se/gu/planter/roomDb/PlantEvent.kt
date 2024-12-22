package se.gu.planter.roomDb

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Data class representing an event associated with a plant.
 */
@Entity(foreignKeys = [ForeignKey(
    entity = Plant::class, // Foreign key references a Plant
    parentColumns = ["name"], // The column in the parent entity (Plant.name)
    childColumns = ["plantId"], // The column in this entity (PlantEvent.plantId)
    onDelete = ForeignKey.CASCADE)]) // Delete this event if the corresponding plant is deleted
data class PlantEvent(
    @PrimaryKey(autoGenerate = true) // Auto-generate the primary key
    val eventId: Int = 0, // Unique ID for the event
    val plantId: String, // ID of the plant this event is associated with
    val date: Long, // Date and time of the event (in milliseconds)
    @TypeConverters(EventTypeConverter::class) // Use EventTypeConverter for this property
    val eventType: EventType, // Type of the event (WATER, NUTRITION, etc.)
    val comment: String, // Comment for the event
    var image: String? = null // Optional image URI or path associated with the event
    )

/**
 * Type converter for converting between EventType and String for Room.
 */
class EventTypeConverter {

    /**
     * Converts an EventType to a String representation.
     * Params:
     * eventType - The EventType to convert.
     * Returns:
     * The String representation of the EventType (its name).
     */
    @TypeConverter
    fun fromEventType(eventType: EventType): String {
        return eventType.name
    }

    /**
     * Converts a String representation to an EventType.
     * Params:
     * eventTypeString - The String representation of the EventType.
     * Returns:
     * The EventType corresponding to the string.
     */
    @TypeConverter
    fun toEventType(eventTypeString: String): EventType {
        return EventType.valueOf(eventTypeString)
    }

}

/**
 * Enum class representing different types of plant events.
 */
enum class EventType {
    WATER,
    NUTRITION,
    STATUS,
    REPLANT,
    CAMERA
}