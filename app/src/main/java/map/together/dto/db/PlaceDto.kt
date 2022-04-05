package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.db.entity.PlaceEntity
import map.together.items.LayerItem

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
        @SerializedName("timestamp")
        @Expose
        var timestamp: Long,
        @SerializedName("categoryId")
        @Expose
        var categoryId: Long,
        @SerializedName("categoryName")
        @Expose
        var categoryName: String,
        @SerializedName("categoryColor")
        @Expose
        var categoryColor: Int,
) {
        fun toPlaceEntity(): PlaceEntity =
                PlaceEntity(name, ownerId, latitude, longitude, categoryId, id, id)
}