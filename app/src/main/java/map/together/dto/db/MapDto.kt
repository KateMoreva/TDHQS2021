package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.db.entity.MapEntity

data class MapDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("name")
        @Expose
        var name: String,
        @SerializedName("centerPlaceId")
        @Expose
        var centerPlaceId: Long?,
        @SerializedName("mainLayerId")
        @Expose
        var mainLayerId: Long?,
        @SerializedName("ownerId")
        @Expose
        var ownerId: Long,
        @SerializedName("canDelete")
        @Expose
        var canDelete: Boolean,
        @SerializedName("canEdit")
        @Expose
        var canEdit: Boolean,
        @SerializedName("roleName")
        @Expose
        var roleName: String,
        @SerializedName("participantsCount")
        @Expose
        var participantsCount: Int,
) {
        fun toMapEntity(): MapEntity = MapEntity(name, centerPlaceId, mainLayerId, ownerId,
                canDelete, canEdit, roleName, participantsCount, id)
}
