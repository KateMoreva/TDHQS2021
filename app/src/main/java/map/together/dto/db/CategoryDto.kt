package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class CategoryDto(
    @SerializedName("id")
    @Expose
    val id: Long,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("color")
    @Expose
    var color: Int,
    @SerializedName("ownerId")
    @Expose
    var ownerId: Long,
)