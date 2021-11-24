package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
        primaryKeys = ["mapId", "layerId"],
        foreignKeys = [
            ForeignKey(
                    entity = MapEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["mapId"],
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
data class LayerMapEntity(
        @ColumnInfo
        var mapId: Long,
        @ColumnInfo
        var layerId: Long,
        var serverId: Long = -1,
)