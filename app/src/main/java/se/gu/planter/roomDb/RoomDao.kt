package se.gu.planter.roomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 *  Data Access Object for interacting with the database.
 */
@Dao
interface RoomDao {

    /**
     * Inserts or updates a plant in the database.
     * @param plant The plant to be inserted or updated.
     */
    @Upsert
    suspend fun upsertPlant(plant: Plant)

    /**
     * Deletes a plant from the database.
     * @param plant The plant to be deleted.
     */
    @Delete
    suspend fun deletePlant(plant: Plant)

    /**
     * Retrieves all plants from the database.
     * @return A Flow that emits a list of all plants.
     */
    @Query("SELECT * FROM plant")
    fun getPlants(): Flow<List<Plant>>

    /**
     * Inserts or updates a plant event in the database.
     * @param event The plant event to be inserted or updated.
     */
    @Upsert
    suspend fun upsertEvent(event: PlantEvent)

    /**
     * Deletes a plant event from the database.
     * @param event The plant event to be deleted.
     */
    @Delete
    suspend fun deleteEvent(event: PlantEvent)

    /**
     * Retrieves all plant events from the database.
     * @return A Flow that emits a list of all plant events.
     */
    @Query("SELECT * FROM plantEvent")
    fun getEvents(): Flow<List<PlantEvent>>

    /**
     * Retrieves all plant events for a specific plant.
     * @param plantId The ID of the plant.
     * @return A Flow that emits a list of plant events for the specified plant.
     */
    @Query("SELECT * FROM plantEvent WHERE plantId = :plantId")
    fun getEventsForPlant(plantId: String): Flow<List<PlantEvent>>

}