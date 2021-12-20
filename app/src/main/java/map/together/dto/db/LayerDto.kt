package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.items.LayerItem

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
) {
        fun toNewLayerItem(): LayerItem =
                LayerItem(id.toString(), name, true, ownerId, canEdit, false, selected = false)

        fun updateLayerItem(layerToUpdate: LayerItem): LayerItem {
                layerToUpdate.title = name
                layerToUpdate.ownerId = ownerId
                layerToUpdate.editable = canEdit
                return layerToUpdate
        }
}