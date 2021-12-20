package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.db.entity.MapEntity

data class MapInfoDto(
        @SerializedName("map")
        @Expose
        val map: MapDto,
        @SerializedName("users")
        @Expose
        var users: List<UserMapDto>,
        @SerializedName("layers")
        @Expose
        var layers: List<LayerDto>,
        @SerializedName("demonstrationLayers")
        @Expose
        var demonstrationLayers: List<Long>?,
)
