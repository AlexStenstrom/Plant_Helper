package se.gu.planter.plantActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import se.gu.planter.buildPlantActivity.BuildPlantActivity
import se.gu.planter.PlantApplication
import se.gu.planter.R
import se.gu.planter.R.id
import se.gu.planter.databinding.ActivityPlantBinding
import se.gu.planter.roomDb.Plant
import se.gu.planter.viewModel.PlantViewModel

/**
 * Activity for displaying a single plant.
 */
class PlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantBinding // View binding for the layout
    private lateinit var plant: Plant // The plant object to display

    // ViewModel for managing plants
    private val plantViewModel: PlantViewModel by viewModels {
        (application as PlantApplication).plantViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSystemBars() // Configure system UI elements

        // Retrieve the plant object from the intent
        plant = intent.getSerializableExtra("plant") as Plant

        setHighlight()
        setupAppbar()
        setMenuItemListeners() // Set listeners for menu items (Edit, Delete)

        setupViewPager(plant) // Set up the ViewPager with fragments

    }

    private fun setSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_primary)
    }

    private fun setHighlight() {
        val plantName = plant.name
        val scientificName = plant.scientificName

        binding.plantTitle.text = plantName
        binding.plantScientificTitle.text = scientificName
        binding.plantImage.setImageURI(Uri.parse(plant.mainImage))
    }

    private fun setupAppbar() {
        binding.topAppBar.inflateMenu(R.menu.plant_edit_menu)
        setBackButtonListener()
    }

    /**
     * Set the listener for the back button.
     * Finishes the activity when clicked.
     */
    private fun setBackButtonListener() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
    /**
     * Set up listeners for menu items (Edit, Delete).
     * When the edit item is clicked, start the EditPlantActivity.
     * When delete item is clicked, delete the plant and finish the activity.
     */
    private fun setMenuItemListeners() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                id.menu_edit -> {
                    startEditActivity()
                    true
                }

                id.menu_delete -> {
                    deletePlant()
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Set up the ViewPager with fragments for plant information and events.
     */
    private fun setupViewPager(plant: Plant) {
        val tabLayout = findViewById<TabLayout>(id.tabLayout)
        val viewPager = findViewById<ViewPager2>(id.viewPager)

        val adapter = PlantPagerAdapter(this, plant)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Information"
                1 -> "Events"
                else -> null
            }
        }.attach()
    }

    /**
     * Start the EditPlantActivity to edit the plant.
     */
    private fun startEditActivity() {
        val intent = Intent(this, BuildPlantActivity::class.java)
        intent.putExtra("plant", plant)
        intent.putExtra("title", "Edit plant")
        startEditForResult.launch(intent)
    }

    /**
     * Register for the result of the EditPlantActivity.
     */
    private val startEditForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedPlant = result.data?.getSerializableExtra("plant") as Plant
                plantViewModel.upsertPlant(updatedPlant)
                setupViewPager(updatedPlant)
                plant = updatedPlant
                setHighlight()
            }
        }


    private fun deletePlant() {
        plantViewModel.deletePlant(plant)
    }

    /**
     * Adapter for the ViewPager2 that holds the PlantInformationFragment and PlantEventsFragment.
     */
    class PlantPagerAdapter(fragmentActivity: FragmentActivity, private val plant: Plant) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PlantInformationFragment.newInstance(plant)
                1 -> PlantEventsFragment.newInstance(plant.name)
                else -> throw IllegalStateException("Invalid tab position")
            }
        }

    }
}



