package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = PlaceEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["centerPlaceId"],
            ),
            ForeignKey(
                    entity = LayerEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["mainLayerId"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class MapEntity(
        @ColumnInfo
        var name: String,
        @ColumnInfo
        var centerPlaceId: Long,
        @ColumnInfo
        var mainLayerId: Long,
        @ColumnInfo(name = "owner")
        var ownerId: Long,
        var serverId: Long = -1,
        @PrimaryKey(autoGenerate = true) var id: Long = 0
)