package se.gu.planter.plantActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import se.gu.planter.R
import se.gu.planter.roomDb.EventType
import se.gu.planter.roomDb.PlantEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying a list of plant events in a RecyclerView.
 */
class PlantEventAdapter(private var events: List<PlantEvent>) :
    RecyclerView.Adapter<PlantEventAdapter.EventViewHolder>() {

    /**
     * Interface for handling item click events.
     */
    interface OnItemClickListener {
        fun onEventLongClick(event: PlantEvent)

    }

    private var listener: OnItemClickListener? = null // Listener for item clicks

    /**
     * Sets the listener for item click events.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    /**
     * ViewHolder for a single event item.
     */
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get references to views in your event item layout
        val dateText: TextView = itemView.findViewById(R.id.date_text)
        val typeOfEventText: TextView = itemView.findViewById(R.id.type_of_event_text)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val cameraView: ImageView = itemView.findViewById(R.id.image_view)
        val photoLayout: View = itemView.findViewById(R.id.photo_layout)

    }

    /**
     * Inflates the layout for a single event item and returns a ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    /**
     * Updates the data in the RecyclerView.
     * If the event is a camera event an image is displayed.
     */
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        val dateString : String = convertDate(event.date)

        holder.dateText.text = dateString
        holder.typeOfEventText.text = event.eventType.toString().lowercase().replaceFirstChar { it.uppercase() }
        holder.commentText.text = event.comment


        if (event.eventType == EventType.CAMERA && event.image != null) {
            val imageView: ImageView = holder.itemView.findViewById(R.id.image_view)

            Glide.with(holder.itemView.context)
                .load(event.image)
                .placeholder(R.drawable.potted_plant_24px)
                .centerCrop()
                .into(imageView)
            holder.photoLayout.visibility = View.VISIBLE
        }

        setBackgroundColor(holder, event)

        holder.itemView.setOnLongClickListener {listener?.onEventLongClick(event); true}

    }

    /**
     * Converts a Long timestamp to a formatted string representing the given date.
     */
    private fun convertDate(date: Long) : String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
        val dateString = dateFormat.format(Date(date))
        return dateString

    }

    /**
     * Sets the background color of the event item based on its type.
     */
    private fun setBackgroundColor(
        holder: EventViewHolder,
        event: PlantEvent
    ) {
        val waterColor = ContextCompat.getColor(holder.itemView.context, R.color.water)
        val nutritionColor =
            ContextCompat.getColor(holder.itemView.context, R.color.nutrition)
        val statusColor = ContextCompat.getColor(holder.itemView.context, R.color.status)
        val cameraColor = ContextCompat.getColor(holder.itemView.context, R.color.camera)
        val replantColor = ContextCompat.getColor(holder.itemView.context, R.color.replant)


        val color = when (event.eventType) {
            EventType.WATER -> waterColor
            EventType.NUTRITION -> nutritionColor
            EventType.REPLANT -> replantColor
            EventType.CAMERA -> cameraColor
            EventType.STATUS -> statusColor
        }

        (holder.itemView as CardView).setCardBackgroundColor(color)
    }

    override fun getItemCount(): Int = events.size
}

