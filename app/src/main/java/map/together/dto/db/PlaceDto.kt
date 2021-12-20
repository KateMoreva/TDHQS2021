package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlaceDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("ownerId")
        @Expose
        var ownerId: Long,
        @SerializedName("latitude")
        @Expose
        var latitude: String,
        @SerializedName("longitude")
        @Expose
        var longitude: String,
        @SerializedName("canEdit")
        @Expose
        var canEdit: Boolean,
        @SerializedName("canDelete")
        @Expose
        var canDelete: Boolean,
)