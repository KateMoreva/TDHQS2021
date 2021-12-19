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
    @SerializedName("colorRecourse")
    @Expose
    var colorRecourse: Int,
    @SerializedName("owner_id")
    @Expose
    var ownerId: Long,
)