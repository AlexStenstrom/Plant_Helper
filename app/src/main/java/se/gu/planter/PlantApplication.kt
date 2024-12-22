package se.gu.planter

import android.app.Application
import androidx.room.Room
import se.gu.planter.roomDb.PlantDatabase
import se.gu.planter.viewModel.PlantViewModel
import se.gu.planter.viewModel.Repository

/**
 * Custom Application class for initializing the database and providing dependencies.
 */
class PlantApplication : Application() {

    /**
     *  Lazy initialization of the database.
     */
    private val database by lazy {
        Room.databaseBuilder(
            this,
            PlantDatabase::class.java,
            "plant_1.db"
        ).build()
    }

    /**
     * Lazy initialization of the plant repository and view model factory.
     */
    private val plantRepository by lazy { Repository(database) }
    val plantViewModelFactory by lazy { PlantViewModel.PlantViewModelFactory(plantRepository) }
}
