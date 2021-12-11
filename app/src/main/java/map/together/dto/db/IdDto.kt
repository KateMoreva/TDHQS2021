package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class IdDto(
        @SerializedName("id")
        @Expose
        val id: Long
)