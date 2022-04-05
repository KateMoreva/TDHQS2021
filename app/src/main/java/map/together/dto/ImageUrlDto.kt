package map.together.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ImageUrlDto(
        @SerializedName("photoUrl")
        @Expose
        var photoUrl: String
)
