package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class UserMapDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("user_id")
        @Expose
        var userId: Long,
        @SerializedName("map_id")
        @Expose
        var mapId: Long,
)