package se.gu.planter.plantActivity

import android.app.AlertDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import se.gu.planter.CameraHelper
import se.gu.planter.PlantApplication
import se.gu.planter.R
import se.gu.planter.databinding.FragmentEventsBinding
import se.gu.planter.roomDb.EventType
import se.gu.planter.roomDb.Plant
import se.gu.planter.roomDb.PlantEvent
import se.gu.planter.viewModel.PlantViewModel

/**
 * Fragment for displaying and managing plant events.
 */
class PlantEventsFragment : Fragment() {
//    private lateinit var plant: Plant // The plant object associated with this fragment
    private lateinit var plantID: String // The plant object associated with this fragment

    private var isAddMenuExpanded = false // Flag for displaying add menu
    private var isFilterMenuExpanded = false // Flag for displaying filter menu

    private var eventDialog: AlertDialog? = null // Reference to the event dialog

    private lateinit var binding: FragmentEventsBinding // View binding for the layout
    private lateinit var adapter: PlantEventAdapter // Adapter for the events list

    private lateinit var cameraHelper: CameraHelper // Helper for camera operations
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var capturedImageUri: Uri? = null // URI of the captured image

    // ViewModel for managing plant data
    private val plantViewModel: PlantViewModel by viewModels {
        (requireActivity().application as PlantApplication).plantViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEventsBinding.inflate(layoutInflater)
        cameraHelper = CameraHelper(requireActivity() as AppCompatActivity)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        plantID = arguments?.getSerializable("plantID") as String // Get the plant object from arguments

        adapter = PlantEventAdapter(emptyList()) // Initialize the adapter with an empty list

        registerForCameraResult() // Register for camera result

        binding.eventsList.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager for the events list

        setupAddButtons()
        setupFilterButtons()

        return binding.root
    }

    /**
     * Called when the fragment's view is created.
     * Sets up an observer for the list of events and updates the adapter when it changes.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get events for the plant and observe changes
        plantViewModel.getEventsForPlant(plantID)
            .observe(viewLifecycleOwner) { events: List<PlantEvent> ->
                adapter = PlantEventAdapter(events.sortedByDescending { it.date })
                binding.eventsList.adapter = adapter
                setItemCardListeners(adapter)

            }

    }

    /**
     * Called when the fragment is destroyed.
     * Remove observers and dismiss the event dialog.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        eventDialog?.dismiss()
        plantViewModel.getEventsForPlant(plantID).removeObservers(viewLifecycleOwner)
    }

    /**
     * Registers a callback for handling the result of taking a picture.
     * Sets the image in the dialog when a picture is taken successfully.
     * Sets the capturedImage URI.
     */
    private fun registerForCameraResult() {
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                cameraHelper.handleActivityResult(result.data) { uri ->
                    if (uri != null) {
                        val dialogView = eventDialog?.findViewById<ImageView>(R.id.eventImage)
                        dialogView?.setImageURI(uri)
                        dialogView?.visibility = View.VISIBLE
                        capturedImageUri = uri
                    } else {
                        Toast.makeText(
                            requireContext(), "Camera operation failed", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * Sets up the add menu buttons.
     * Sets listeners for each button.
     */
    private fun setupAddButtons() {
        setAddMenuVisibility(View.GONE)
        binding.addEventButton.setOnClickListener { toggleAddMenu() }

        binding.wateringEventButton.setOnClickListener { showEventDialog(EventType.WATER) }

        binding.nutritionEventButton.setOnClickListener { showEventDialog(EventType.NUTRITION) }

        binding.statusEventButton.setOnClickListener { showEventDialog(EventType.STATUS) }

        binding.replantEventButton.setOnClickListener { showEventDialog(EventType.REPLANT) }

        binding.cameraEventButton.setOnClickListener { showEventDialog(EventType.CAMERA) }
    }

    /**
     * Toggles the visibility of the add menu.
     */
    private fun toggleAddMenu() {
        isAddMenuExpanded = !isAddMenuExpanded
        if (isAddMenuExpanded) {
            setAddMenuVisibility(View.VISIBLE)
        } else {
            setAddMenuVisibility(View.GONE)
        }

    }

    /**
     * Toggles the visibility of the filter menu.
     */
    private fun toggleFilterMenu() {
        isFilterMenuExpanded = !isFilterMenuExpanded
        if (isFilterMenuExpanded) {
            setFilterMenuVisibility(View.VISIBLE)
        } else {
            setFilterMenuVisibility(View.GONE)
        }
    }

    /**
     * Sets the visibility of the menu items.
     */
    private fun setMenuVisibility(visibility: Int, vararg views: View) {
        views.forEach { it.visibility = visibility }
    }

    /**
     * Sets the visibility of the add menu items.
     */
    private fun setAddMenuVisibility(visibility: Int) {
        setMenuVisibility(
            visibility,
            binding.wateringEventButton, binding.wateringEventText,
            binding.nutritionEventButton, binding.nutritionEventText,
            binding.statusEventButton, binding.statusEventText,
            binding.replantEventButton, binding.replantEventText,
            binding.cameraEventButton, binding.cameraEventText
        )
    }

    /**
     * Sets the visibility of the filter menu items.
     */
    private fun setFilterMenuVisibility(visibility: Int) {
        setMenuVisibility(
            visibility,
            binding.resetFilterButton, binding.resetFilterText,
            binding.wateringFilterButton, binding.wateringFilterText,
            binding.resetFilterButton, binding.resetFilterText,
            binding.wateringFilterButton, binding.wateringFilterText,
            binding.nutritionFilterButton, binding.nutritionFilterText,
            binding.statusFilterButton, binding.statusFilterText,
            binding.replantFilterButton, binding.replantFilterText,
            binding.cameraFilterButton, binding.cameraFilterText,
        )
    }

    /**
     * Sets up the filter menu buttons.
     * Adds listeners.
     */
    private fun setupFilterButtons() {
        setFilterMenuVisibility(View.GONE)
        binding.filterButton.setOnClickListener {
            toggleFilterMenu()
        }

        binding.wateringFilterButton.setOnClickListener {
            showFilteredEvents(EventType.WATER)
        }

        binding.nutritionFilterButton.setOnClickListener {
            showFilteredEvents(EventType.NUTRITION)
        }

        binding.statusFilterButton.setOnClickListener {
            showFilteredEvents(EventType.STATUS)
        }

        binding.replantFilterButton.setOnClickListener {
            showFilteredEvents(EventType.REPLANT)
        }

        binding.cameraFilterButton.setOnClickListener {
            showFilteredEvents(EventType.CAMERA)
        }

        binding.resetFilterButton.setOnClickListener {
            plantViewModel.getEventsForPlant(plantID).observe(viewLifecycleOwner) { events ->
                adapter = PlantEventAdapter(events.sortedByDescending { it.date })
                binding.eventsList.adapter = adapter
                setItemCardListeners(adapter)
            }
            isFilterMenuExpanded = false
            setFilterMenuVisibility(View.GONE)
        }
    }

    /**
     * Shows filtered events based on the given event type.
     */
    private fun showFilteredEvents(eventType: EventType) {
        toggleFilterMenu()
        plantViewModel.getEventsForPlant(plantID).observe(viewLifecycleOwner) { events ->
            val filteredEvents = events.filter { it.eventType == eventType }
            adapter = PlantEventAdapter(filteredEvents.sortedByDescending { it.date })
            binding.eventsList.adapter = adapter
            setItemCardListeners(adapter)
        }
        isFilterMenuExpanded = false
        setFilterMenuVisibility(View.GONE)
    }

    /**
     * Sets up listeners for each item in the adapter.
     * Enables long click to delete events.
     */
    private fun setItemCardListeners(adapter: PlantEventAdapter) {
        adapter.setOnItemClickListener(object : PlantEventAdapter.OnItemClickListener {
            override fun onEventLongClick(event: PlantEvent) {
                openDeletionDialog(event)
            }
        })
    }

    /**
     * Opens a dialog for deleting an event.
     * If the user confirms the deletion, the event is deleted.
     */
    private fun openDeletionDialog(event: PlantEvent) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Event?")
            .setPositiveButton("Delete") { _, _ ->
                plantViewModel.deleteEvent(event)
            }.setNegativeButton("Cancel") { _, _ -> }.create().show()
    }

    /**
     * Shows a dialog for adding a new event.
     */
    private fun showEventDialog(plantEventType: EventType) {
        toggleAddMenu()

        val builder = createEventDialog(plantEventType)

        val dialog = builder.create()
        eventDialog = dialog
        eventDialog?.setOnDismissListener {
            eventDialog = null
        }
        dialog.show()
    }

    /**
     * Creates and configures an AlertDialog for adding a new plant event.
     * Params:
     * plantEventType - The type of event to be added.
     * Returns:
     * An AlertDialog.Builder instance with the configured dialog.
     */
    private fun createEventDialog(plantEventType: EventType): AlertDialog.Builder {
        val builder = AlertDialog.Builder(requireContext())

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val todayView = dialogView.findViewById<Button>(R.id.pickTodayButton)
        val calendarView = dialogView.findViewById<CalendarView>(R.id.calendarView)
        val showCalendarButton = dialogView.findViewById<Button>(R.id.showCalendarButton)
        val commentInputLayout = dialogView.findViewById<TextInputLayout>(R.id.commentInputLayout)
        val takePhotoButton = dialogView.findViewById<Button>(R.id.addPhotoButton)

        if (plantEventType == EventType.STATUS) {
            commentInputLayout.hint = "Add status"
        }

        if (plantEventType == EventType.CAMERA) {
            takePhotoButton.visibility = View.VISIBLE
        }

        builder.setView(dialogView)
        builder.setTitle("Add Event")

        calendarView.visibility = View.GONE // Initially hide the calendar view
        showCalendarButton.setOnClickListener {
            toggleCalendarVisibility(calendarView, showCalendarButton)
        } // Toggle calendar visibility on button click

        todayView.setOnClickListener {
            calendarView.date = System.currentTimeMillis()
            val color = ContextCompat.getColor(requireContext(), R.color.light_secondary)
            todayView.setBackgroundColor(color)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.timeInMillis
            calendarView.date = selectedDate // Update calendar view with selected date
        }

        // Uses camera helper when button is clicked
        takePhotoButton.setOnClickListener {
            cameraHelper.takePicture(takePictureLauncher) { _ ->

            }
        }

        // Save event on positive button click.
        builder.setPositiveButton("OK") { _, _ ->
            val selectedDate = calendarView.date
            val comment = commentInputLayout.editText?.text.toString()

            saveEvent(selectedDate, plantEventType, comment)
            showEventAddedToast(selectedDate)

        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        return builder
    }

    /**
     * Saves a new plant event.
     */
    private fun saveEvent(
        selectedDate: Long,
        plantEventType: EventType,
        comment: String
    ) {
        val event = PlantEvent(
            plantId = plantID,
            date = selectedDate,
            eventType = plantEventType,
            comment = comment,
            image = capturedImageUri?.toString()
        )

        plantViewModel.upsertEvent(event)
    }

    /**
     * Toggles the visibility of the calendar view and the show calendar button.
     */
    private fun toggleCalendarVisibility(calendarView: CalendarView, showCalendarButton: Button) {
        if (calendarView.visibility == View.VISIBLE) {
            calendarView.visibility = View.GONE
            showCalendarButton.text = getString(R.string.pick_date)
        } else {
            calendarView.visibility = View.VISIBLE
            showCalendarButton.text = getString(R.string.hide_calendar)
        }
    }

    /**
     * Shows a toast message indicating that an event was added.
     */
    private fun showEventAddedToast(selectedDate: Long) {
        val selectedDateString = DateFormat.format("yyyy-MM-dd", selectedDate)
        Toast.makeText(
            requireContext(), "Event added on $selectedDateString", Toast.LENGTH_SHORT
        ).show()
    }


    companion object {
        /**
         * Creates a new instance of the fragment with the provided plant.
         */
        @JvmStatic
        fun newInstance(plantID: String): PlantEventsFragment {
            val fragment = PlantEventsFragment()
            val args = Bundle()
            args.putSerializable("plantID", plantID)
            fragment.arguments = args
            return fragment
        }
    }
}