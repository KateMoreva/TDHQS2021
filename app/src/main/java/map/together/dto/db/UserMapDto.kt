package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.items.UserItem


data class UserMapDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("email")
        @Expose
        var email: String,
        @SerializedName("userName")
        @Expose
        var userName: String,
        @SerializedName("photoUrl")
        @Expose
        var photoUrl: String?,
        @SerializedName("roleName")
        @Expose
        var roleName: String,
        @SerializedName("timestamp")
        @Expose
        var timestamp: Long,
        @SerializedName("canEdit")
        @Expose
        var canEdit: Boolean,
        @SerializedName("canDelete")
        @Expose
        var canDelete: Boolean,
) {
        fun toUserItem(): UserItem {
                return UserItem(id.toString(), userName, email, roleName, canEdit, canDelete)
        }
}