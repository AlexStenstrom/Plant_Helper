package se.gu.planter.buildPlantActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import se.gu.planter.CameraHelper
import se.gu.planter.PlantBuilder
import se.gu.planter.R
import se.gu.planter.databinding.ActivityBuildPlantBinding
import se.gu.planter.roomDb.Plant

/**
 * Activity for building a plant. It can be used for adding or editing a plant.
 */
class BuildPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuildPlantBinding // View binding for the layout
    private var builder = PlantBuilder() // Builder for creating Plant objects

    private lateinit var plant: Plant
    private var imageString = Plant.DEFAULT_IMAGE // String representation of the image URI

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent> // Launcher for taking pictures
    private lateinit var cameraHelper: CameraHelper // Helper class for camera operations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        initializeBinding() // Set up view binding
        setContentView(binding.root)
        setSystemBars() // Configure system UI elements

        cameraHelper = CameraHelper(this)

        getPlant()
        setTitle()

        registerForCamera()

        // Set listeners for UI elements
        setImageButtonListener()
        setSaveButtonListener()
        setBackButtonListener()
        setWateringActivationListener()
        setNutritionActivationListener()
    }

    private fun getPlant() {
        val fetchedPlant = intent.getSerializableExtra("plant")
        if (fetchedPlant != "none") {
            plant = fetchedPlant as Plant
            setSavedValues(plant, plant.mainImage)
        } else
            plant = savePlant()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("plant", savePlant())
        outState.putSerializable("imageString", imageString)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val plant = savedInstanceState.getSerializable("plant") as Plant
        val image = savedInstanceState.getString("imageString") as String
        imageString = image
        setSavedValues(plant, image)

    }

    private fun setTitle() {
        val fetchedTitle = intent.getStringExtra("title")
        binding.title.text = fetchedTitle
    }



    /**
     * Sets the saved values of the plant in the UI, if there are any.
     */
    private fun setSavedValues(plant: Plant, imageString: String) {
        binding.plantNameInput.editText?.setText(plant.name)
        binding.scientificNameInput.editText?.setText(plant.scientificName)
        binding.descriptionInput.editText?.setText(plant.description)
        binding.wateringSliderCheckBox.isChecked = plant.wateringInterval != null
        binding.nutritionSliderCheckBox.isChecked = plant.nutritionInterval != null
        binding.wateringSlider.isEnabled = binding.wateringSliderCheckBox.isChecked
        binding.nutritionSlider.isEnabled = binding.nutritionSliderCheckBox.isChecked
        binding.wateringCommentInput.editText?.setText(plant.wateringComment)
        binding.nutritionCommentInput.editText?.setText(plant.nutritionComment)
        binding.sunlightDetailsInput.editText?.setText(plant.sunlightDetails)
        binding.addImageButton.setImageURI(Uri.parse(imageString))

        binding.wateringSlider.values =
            if (plant.wateringInterval != null) getListFromPair(plant.wateringInterval!!)
            else mutableListOf(1f, 1f)
        binding.nutritionSlider.values =
            if (plant.nutritionInterval != null) getListFromPair(plant.nutritionInterval!!)
            else mutableListOf(1f, 1f)

    }

    /**
     * Converts a pair of floats to a mutable list of floats.
     */
    private fun getListFromPair(pair: Pair<Float, Float>): MutableList<Float> {
        val list = mutableListOf<Float>()
        list.add(pair.first)
        list.add(pair.second)
        return list
    }




    /**
     * Initializes view binding for the layout.
     */
    private fun initializeBinding() {
        binding = ActivityBuildPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Configures the system UI elements (status bar and navigation bar).
     */
    private fun setSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_primary)
    }

    /**
     * Registers a launcher for taking pictures using the camera.
     * If the result is successful, the captured image is put on the screen
     * and added to the builder.
     */
    private fun registerForCamera() {
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                cameraHelper.handleActivityResult(result.data) { uri ->
                    if (uri != null) {
                        binding.addImageButton.setImageURI(null)
                        binding.addImageButton.setImageURI(uri)
                        imageString = uri.toString()
                    } else {
                        Toast.makeText(this, "Camera operation failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Sets a listener for the "Add Image" button.
     * When clicked, it calls the camera helper to take a picture.
     */
    private fun setImageButtonListener() {
        binding.addImageButton.setOnClickListener {
            cameraHelper.takePicture(takePictureLauncher) { uri ->

            }
        }
    }



    /**
     * Sets a listener for the "Watering Slider" checkbox.
     * Disables the "Watering Slider" and "Watering Comment" inputs
     * when the checkbox is unchecked.
     */
    private fun setWateringActivationListener() {
        binding.wateringSliderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.wateringSlider.isEnabled = true
                binding.wateringCommentInput.isEnabled = true
            } else {
                binding.wateringSlider.isEnabled = false

            }
        }
    }

    /**
     * Sets a listener for the "Nutrition Slider" checkbox.
     * Disables the "Nutrition Slider" and "Nutrition Comment" inputs
     * when the checkbox is unchecked.
     */
    private fun setNutritionActivationListener() {
        binding.nutritionSliderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.nutritionSlider.isEnabled = true
                binding.nutritionCommentInput.isEnabled = true
            } else {
                binding.nutritionSlider.isEnabled = false
            }
        }
    }

    /**
     * Sets a listener for the "Back" button.
     * Finishes the activity when clicked.
     */
    private fun setBackButtonListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Sets a listener for the "Save" button.
     * If the plant name is empty, the user is notified to enter a name.
     * Else, saves the plant, and finishes the activity with a result intent.
     */
    private fun setSaveButtonListener() {
        binding.saveFloatingActionButton.setOnClickListener {
            savePlant()

            val savedPlantName = binding.plantNameInput.editText?.text.toString()

            if (savedPlantName.isEmpty()) {
                val snackbar =
                    Snackbar.make(binding.root, "Please enter a plant name", Snackbar.LENGTH_LONG)
                snackbar.show()

            } else {
                val plant = savePlant()
                val resultIntent = Intent()
                resultIntent.putExtra("plant", plant)
                setResult(RESULT_OK, resultIntent)
                finish()

            }
        }
    }

    /**
     * Returns a plant object with the entered data.
     */
    private fun savePlant(): Plant {
        val plantName = binding.plantNameInput.editText?.text.toString()
        val scientificName = binding.scientificNameInput.editText?.text.toString()
        val description = binding.descriptionInput.editText?.text.toString()
        val wateringInterval =
            if (binding.wateringSliderCheckBox.isChecked) binding.wateringSlider.values else listOf()
        val wateringComment = binding.wateringCommentInput.editText?.text.toString()
        val nutritionInterval =
            if (binding.nutritionSliderCheckBox.isChecked) binding.nutritionSlider.values else listOf()
        val nutritionComment = binding.nutritionCommentInput.editText?.text.toString()
        val sunlightDetails = binding.sunlightDetailsInput.editText?.text.toString()

        val plant = builder
            .setName(plantName)
            .setScientificName(scientificName)
            .setDescription(description)
            .setWateringInterval(wateringInterval)
            .setWateringComment(wateringComment)
            .setNutritionInterval(nutritionInterval)
            .setNutritionComment(nutritionComment)
            .setSunlightDetails(sunlightDetails)
            .setImage(imageString)
            .build()

        return plant
    }


}






