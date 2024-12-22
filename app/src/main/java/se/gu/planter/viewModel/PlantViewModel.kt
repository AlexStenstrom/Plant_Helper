package se.gu.planter.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.gu.planter.roomDb.Plant
import se.gu.planter.roomDb.PlantEvent

/**
 * ViewModel for managing plant and plant event data.
 */
class PlantViewModel(private val repository: Repository) : ViewModel() {

    /**
     * Gets all plants from the repository as LiveData.
     */
    fun getPlants() = repository.getPlants().asLiveData(viewModelScope.coroutineContext)

    /**
     * Inserts or updates a plant in the repository.
     * Params:
     * plant - The plant to be inserted or updated.
     */
    fun upsertPlant(plant: Plant) {
        viewModelScope.launch {
            repository.upsertPlant(plant)
        }
    }

    /**
     * Deletes a plant from the repository.
     * Params:
     * plant - The plant to be deleted.
     */
    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }

    }

    /**
     * Gets all events from the repository.
     */
    fun getEvents() = viewModelScope.launch {
        repository.getEvents()
    }

    /**
     * Inserts or updates an event in the repository.
     * Params:
     * event - The event to be inserted or updated.
     */
    fun upsertEvent(event: PlantEvent) {
        viewModelScope.launch {
            repository.upsertEvent(event)
        }
    }

    /**
     * Deletes an event from the repository.
     * Params:
     * event - The event to be deleted.
     */
    fun deleteEvent(event: PlantEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }

    }

    /**
     * Gets all events for a specific plant from the repository as LiveData.
     * Params:
     * plantId - The ID of the plant.
     */
    fun getEventsForPlant(plantId: String) = repository.getEventsForPlant(plantId).asLiveData(viewModelScope.coroutineContext)

    /**
     * Factory for creating PlantViewModel instances.
     */
    class PlantViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlantViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}