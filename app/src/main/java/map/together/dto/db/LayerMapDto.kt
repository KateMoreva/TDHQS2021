package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LayerMapDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("map_id")
        @Expose
        var mapId: Long,
        @SerializedName("layer_id")
        @Expose
        var layerId: Long,
)