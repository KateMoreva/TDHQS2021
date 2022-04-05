package map.together.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(primaryKeys = ["userId", "mapId"],
        foreignKeys = [
            ForeignKey(
                    entity = UserEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["userId"],
                    onDelete = ForeignKey.CASCADE

            ),
            ForeignKey(
                    entity = MapEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["mapId"],
                    onDelete = ForeignKey.CASCADE
            )
        ])
data class UserMapEntity(
        @ColumnInfo
        var userId: Long,
        @ColumnInfo
        var mapId: Long,
        @ColumnInfo
        var role: Long,
        var serverId: Long = -1,
)