package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(primaryKeys = ["placeId", "layerId"],
        foreignKeys = [
            ForeignKey(
                    entity = PlaceEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["placeId"],
                    onDelete = ForeignKey.CASCADE

            ),
            ForeignKey(
                    entity = LayerEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["layerId"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class PlaceLayerEntity(
        @ColumnInfo
        var placeId: Long,
        @ColumnInfo
        var layerId: Long,
        var serverId: Long = -1,
)