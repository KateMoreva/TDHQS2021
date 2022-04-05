package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlaceCategoryDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("place_id")
        @Expose
        var placeId: Long,
        @SerializedName("category_id")
        @Expose
        var categoryId: Long,
)