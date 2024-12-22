package se.gu.planter.plantListActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import se.gu.planter.R
import se.gu.planter.databinding.ItemPlantBinding
import se.gu.planter.roomDb.Plant

/**
 * Adapter for displaying a list of plants in a RecyclerView.
 */
class PlantListAdapter : ListAdapter<Plant, PlantListAdapter.PlantViewHolder>(PlantDiffCallback()) {

    var onPlantClickListener: OnPlantClickListener? = null // Listener for plant item clicks

    /**
     * ViewHolder for a single plant item.
     */
    class PlantViewHolder(val binding: ItemPlantBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds plant data to the view holder.
         * Params:
         * plant - The plant data to bind.
         */
        fun bind(plant: Plant) {
            binding.plantName.text = plant.name
            binding.plantScientificTitle.text = plant.scientificName

        }

        /**
         * Sets a click listener on the view holder.
         * Params:
         * listener - The click listener to set.
         * plant - The plant data associated with the view holder.
         */
        fun setOnClickListener(listener: OnPlantClickListener, plant: Plant) {
            binding.root.setOnClickListener {
                listener.onPlantClick(plant)
            }
        }
    }

    /**
     * Creates a new PlantViewHolder. Inflates the view from the ItemPlantBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    /**
     * Binds the plant data to the view holder.
     * Adds an image.
     */
    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = getItem(position)
        holder.bind(plant)
        onPlantClickListener?.let { holder.setOnClickListener(it, plant) }

        Glide.with(holder.itemView.context)
            .load(plant.mainImage)
            .placeholder(R.drawable.potted_plant_24px)
            .centerCrop()
            .into(holder.binding.plantImage) // Load into the ImageView

    }
}

/**
 * Interface for handling plant item clicks.
 */
interface OnPlantClickListener {
    fun onPlantClick(plant: Plant)
}

/**
 * Callback for calculating the difference between two lists of plants.
 */
class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
    override fun areItemsTheSame(oldPlant: Plant, newPlant: Plant): Boolean {
        return oldPlant.name == newPlant.name
    }

    /**
     * Checks if the contents of the plants are the same.
     */
    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem == newItem
    }
}