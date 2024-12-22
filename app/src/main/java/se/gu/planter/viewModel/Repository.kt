package se.gu.planter.viewModel


import se.gu.planter.roomDb.PlantEvent
import se.gu.planter.roomDb.PlantDatabase
import se.gu.planter.roomDb.Plant

/**
 * Repository class for managing plant and plant event data. This class acts as a mediator between the ViewModel and the database.
 */
class Repository(private val db: PlantDatabase) {

    /**
     * Inserts or updates a plant in the database.
     * Params:
     * plant - The plant to be inserted or updated.
     */
    suspend fun upsertPlant(plant: Plant) {
        db.dao.upsertPlant(plant)
    }

    /**
     * Deletes a plant from the database.
     * Params:
     * plant - The plant to be deleted.
     */
    suspend fun deletePlant(plant: Plant) {
        db.dao.deletePlant(plant)
    }

    /**
     * Gets all plants from the database.
     * Returns:
     * A Flow of List.
     */
    fun getPlants() = db.dao.getPlants()

    /**
     * Inserts or updates an event in the database.
     * Params:
     * event - The event to be inserted or updated.
     */
    suspend fun upsertEvent(event: PlantEvent) {
        db.dao.upsertEvent(event)
    }

    /**
     * Deletes an event from the database.
     * Params:
     * event - The event to be deleted.
     */
    suspend fun deleteEvent(event: PlantEvent) {
        db.dao.deleteEvent(event)
    }

    /**
     * Gets all events from the database.
     * Returns:
     * A Flow of List.
     */
    fun getEvents() = db.dao.getEvents()

    /**
     * Gets all events for a specific plant from the database.
     * Params:
     * plantId - The ID of the plant.
     * Returns:
     * A Flow of List.
     */
    fun getEventsForPlant(plantId: String) = db.dao.getEventsForPlant(plantId)


}