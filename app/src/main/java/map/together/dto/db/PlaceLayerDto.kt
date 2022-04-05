package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlaceLayerDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("place_id")
        @Expose
        var placeId: Long,
        @SerializedName("layer_id")
        @Expose
        var layerId: Long,
)