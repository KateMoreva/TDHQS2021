package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MapDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("center_place_id")
        @Expose
        var centerPlaceId: Long,
        @SerializedName("main_layer_id")
        @Expose
        var mainLayerId: Long,
)