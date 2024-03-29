package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity()
data class MapEntity(
        @ColumnInfo
        var name: String,
        @ColumnInfo
        var centerPlaceId: Long?,
        @ColumnInfo
        var mainLayerId: Long?,
        @ColumnInfo(name = "owner")
        var ownerId: Long?,
        @ColumnInfo
        var canDelete: Boolean,
        @ColumnInfo
        var canEdit: Boolean,
        @ColumnInfo
        var roleName: String,
        @ColumnInfo
        var participantsCount: Int,
        var serverId: Long = -1,
        @PrimaryKey(autoGenerate = true) var id: Long = 0
)