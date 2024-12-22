package se.gu.planter.plantListActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import se.gu.planter.buildPlantActivity.BuildPlantActivity
import se.gu.planter.PlantApplication
import se.gu.planter.R
import se.gu.planter.databinding.ActivityPlantListBinding
import se.gu.planter.plantActivity.PlantActivity
import se.gu.planter.roomDb.Plant
import se.gu.planter.viewModel.PlantViewModel

/**
 * Main activity displaying a list of plants.
 */
class PlantListActivity : AppCompatActivity() {

    private lateinit var adapter : PlantListAdapter

    private val plantViewModel: PlantViewModel by
    viewModels { // Get a reference to the ViewModel
        (application as PlantApplication).plantViewModelFactory
    }
    private lateinit var binding: ActivityPlantListBinding // View binding for the layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeBinding() // Set up view binding
        setSystemBars() // Configure system UI elements (status bar)

        // Create an adapter for the RecyclerView
        adapter = PlantListAdapter()

        binding.plantList.adapter = adapter // Set the adapter for the RecyclerView
        binding.plantList.layoutManager = LinearLayoutManager(this) // Set the layout manager

        setPlantListener(adapter) // Set click listener for plant items
        setAddPlantButtonListener() // Set click listener for the add plant button

    }

    /**
     * Sets up the observer for the list of plants.
     * Observes the list of plants and updates the adapter when it changes.
     */
    override fun onResume() {
        super.onResume()
        plantViewModel.getPlants().observe (this) {
            adapter.submitList(it)
        }
    }

    /**
     * Removes the observer for the list of plants.
     */
    override fun onPause() {
        super.onPause()
        plantViewModel.getPlants().removeObservers(this)
    }

    /**
     * Initializes view binding for the activity.
     */
    private fun initializeBinding() {
        binding = ActivityPlantListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Configures system UI elements (status bar) for edge-to-edge compatibility.
     */
    private fun setSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_primary)
    }

    /**
     * Sets a click listener for plant items in the adapter.
     * Starts the PlantActivity when a plant item is clicked.
     * Params:
     * adapter - The adapter for the RecyclerView.
     */
    private fun setPlantListener(adapter: PlantListAdapter) {
        adapter.onPlantClickListener = object : OnPlantClickListener {
            override fun onPlantClick(plant: Plant) {
                val intent = Intent(this@PlantListActivity, PlantActivity::class.java)
                intent.putExtra("plant", plant)
                startActivity(intent)
            }
        }
    }



    /**
     * Sets a click listener for the add plant button.
     * Starts the AddPlantActivity when the button is clicked.
     */
    private fun setAddPlantButtonListener() {
        binding.addPlantButton.setOnClickListener {
            val intent = Intent(
                this, BuildPlantActivity::class.java
            )
            intent.putExtra("plant", "none")
            intent.putExtra("title", "Add plant")
            startAddForResult.launch(intent)


        }
    }

    /**
     * Registers an activity result launcher for the AddPlantActivity.
     * Saves the plant and shows a SnackBar when the result is OK.
     */
    private val startAddForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val plant = result.data?.getSerializableExtra("plant") as Plant
                plantViewModel.upsertPlant(plant)
                showPlantAddedSnackBar()
            }
        }

    /**
     * Shows a SnackBar indicating that a plant has been added.
     */
    private fun showPlantAddedSnackBar() {
        val snackBar = Snackbar.make(binding.root, "Plant added", Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

}