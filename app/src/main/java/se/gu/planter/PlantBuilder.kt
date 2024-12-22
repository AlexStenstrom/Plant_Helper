package se.gu.planter

import se.gu.planter.roomDb.Plant

/**
 * Builder class for creating Plant objects.
 */
class PlantBuilder {

    // Properties of the Plant object
    private var name: String = ""
    private var scientificName: String = ""
    private var description: String = ""
    private var wateringComment: String = ""
    private var nutritionComment: String = ""
    private var wateringInterval: Pair<Float, Float>? = null
    private var nutritionInterval: Pair<Float, Float>? = null
    private var sunlightDetails: String = ""
    private var image: String = Plant.DEFAULT_IMAGE

    // Setter methods for each property, using apply for method chaining
    fun setName(name: String) = apply { this.name = name }

    fun setScientificName(scientificName: String) =
        apply { this.scientificName = scientificName }

    fun setDescription(description: String) =
        apply { this.description = description }

    /**
     * Sets the watering interval from a list of floats.
     * Expects a list with at least two elements (min, max).
     * If the list is smaller, the interval is set to null.
     */
    fun setWateringInterval(wateringInterval: List<Float>) = apply {
        if (wateringInterval.size >= 2) {
            this.wateringInterval = Pair(wateringInterval[0], wateringInterval[1])
        } else {
            this.wateringInterval = null // Or handle the case differently
        }
    }


    fun setWateringComment(wateringComment: String) = apply {
        this.wateringComment = wateringComment
    }

    /**
     * Sets the nutrition interval from a list of floats.
     * Expects a list with at least two elements (min, max).
     * If the list is smaller, the interval is set to null.
     */
    fun setNutritionInterval(nutritionInterval: List<Float>) = apply {
        if (nutritionInterval.size >= 2) {
            this.nutritionInterval = Pair(nutritionInterval[0], nutritionInterval[1])
        } else {
            this.nutritionInterval = null // Or handle the case differently
        }
    }

    fun setNutritionComment(nutritionComment: String) = apply {
        this.nutritionComment = nutritionComment
    }

    fun setSunlightDetails(sunlightDetails: String) = apply {
        this.sunlightDetails = sunlightDetails
    }

    fun setImage(image: String) = apply {
        this.image = image
    }

    /**
     * Builds a Plant object with the set properties.
     * Returns a Plant object.
     */
    fun build(): Plant {
        return Plant(
            name = name,
            scientificName = scientificName,
            description = description,
            wateringInterval = wateringInterval,
            nutritionInterval = nutritionInterval,
            wateringComment = wateringComment,
            nutritionComment = nutritionComment,
            sunlightDetails = sunlightDetails,
            mainImage = image,
        )
    }
}