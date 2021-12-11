package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LayerDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("owner_id")
        @Expose
        var ownerId: Long,
)