package se.gu.planter.plantActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.gu.planter.R
import se.gu.planter.roomDb.Plant

/**
 * Fragment for displaying plant information.
 */
class  PlantInformationFragment : Fragment() {
    private lateinit var plant: Plant // Plant object to be displayed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_information, container, false)

        plant = arguments?.getSerializable("plant") as Plant // Retrieves the Plant object

        // Get references to the TextViews in the layout
        val descriptionTextView = view.findViewById<TextView>(R.id.plantDescription)
        val nutritionIntervalTextView = view.findViewById<TextView>(R.id.nutritionIntervalText)
        val wateringIntervalTextView = view.findViewById<TextView>(R.id.wateringInterval)
        val nutritionCommentTextView = view.findViewById<TextView>(R.id.nutritionComment)
        val wateringCommentTextView = view.findViewById<TextView>(R.id.wateringComment)
        val sunlightTextView = view.findViewById<TextView>(R.id.sunlightComment)

        // Set the text for the watering and nutrition intervals if they are not null.
        if (plant.wateringInterval != null) {
            setIntervalText(wateringIntervalTextView, plant.wateringInterval!!)
        }
        if (plant.nutritionInterval != null) {
            setIntervalText(nutritionIntervalTextView, plant.nutritionInterval!!)
        }

        // Set the text for the watering and nutrition comments.
        wateringCommentTextView.text = plant.wateringComment
        nutritionCommentTextView.text = plant.nutritionComment
        setDescription(descriptionTextView, plant)
        setSunlightText(sunlightTextView, plant)

        return view
    }

    /**
     * Sets the description text for the plant.
     */
    private fun setDescription(descriptionTextView: TextView?, plant: Plant) {

        if (plant.description.isNotEmpty()) {
            descriptionTextView?.text = plant.description
        }
    }

    /**
     * Sets the interval text for the given TextView.
     */
    private fun setIntervalText(textView: TextView, interval: Pair<Float, Float>) {

        if (interval.first == interval.second) {
            textView.text = "Every " + interval.first.toInt() + " days"
        } else {
            textView.text = "Every " + interval.first.toInt() + " to " + interval.second.toInt() + " days"
        }

    }

    /**
     * Sets the sunlight text for the given TextView.
     */
    private fun setSunlightText(textView: TextView, plant: Plant) {
        if (plant.sunlightDetails.isNotEmpty()) {
            textView.text = plant.sunlightDetails
        } else {
            textView.visibility = View.GONE
        }
    }

    /**
     * Creates a new instance of the fragment with the given Plant object.
     */
    companion object {
        fun newInstance(plant: Plant): PlantInformationFragment {
            val fragment = PlantInformationFragment()
            val args = Bundle()
            args.putSerializable("plant", plant)
            fragment.arguments = args
            return fragment
        }
    }
}