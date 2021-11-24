package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
        @ColumnInfo
        var userName: String,
        @ColumnInfo
        var email: String,
        @ColumnInfo
        var photoUrl: String?,
        @ColumnInfo
        var serverId: Long = -1,
        @PrimaryKey(autoGenerate = true) var id: Long = 0
)