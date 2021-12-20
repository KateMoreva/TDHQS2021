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
        @SerializedName("ownerId")
        @Expose
        var ownerId: Long,
        @SerializedName("canEdit")
        @Expose
        var canEdit: Boolean,
        @SerializedName("canDelete")
        @Expose
        var canDelete: Boolean,
        @SerializedName("places")
        @Expose
        var places: List<PlaceDto>,
)