package map.together.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class UserNameDto(
        @SerializedName("user_name")
        @Expose
        val firstName: String
)